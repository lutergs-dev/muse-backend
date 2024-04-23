package dev.lutergs.muse.backend.infra.repository.kafka.streams.internal

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.muse.backend.infra.config.properties.HttpUrlConfigProperties
import dev.lutergs.muse.backend.infra.config.properties.KafkaStreamsConfigProperties
import dev.lutergs.muse.backend.infra.repository.kafka.serialization.LongDeserializer
import dev.lutergs.muse.backend.infra.repository.kafka.serialization.LongSerializer
import dev.lutergs.muse.backend.infra.repository.kafka.streams.processor.TTLProcessorSupplier
import dev.lutergs.muse.backend.infra.repository.kafka.serialization.NowPlayingDeserializer
import dev.lutergs.muse.backend.infra.repository.kafka.serialization.NowPlayingSerializer
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.Stores
import org.springframework.data.redis.core.StringRedisTemplate

class KafkaStreamsTopology(
  private val kafkaStreamsConfig: KafkaStreamsConfigProperties,
  private val httpUrlConfigs: HttpUrlConfigProperties,
  private val kafkaStreamsBuilder: StreamsBuilder,
  private val objectMapper: ObjectMapper,
  private val redisClient: StringRedisTemplate,
  private val serverPort: String
) {
  private val nowPlayingSerde = Serdes.serdeFrom(
    NowPlayingSerializer(this.objectMapper),
    NowPlayingDeserializer(this.objectMapper)
  )
  private val customLongSerde = Serdes.serdeFrom(
    LongSerializer(),
    LongDeserializer()
  )

  init { this.build() }

  @PostConstruct
  fun registerKafkaStreams() {
    this.redisClient.opsForHash<String, String>().put(
      "KafkaStreamsClient",
      this.kafkaStreamsConfig.currentMachineKey,
      this.httpUrlConfigs.buildHttpUrl(this.serverPort.toInt())
    )
  }

  @PreDestroy
  fun unregisterKafkaStreams() {
    this.redisClient.opsForHash<String, String>().delete(
      "KafkaStreamsClient",
      this.kafkaStreamsConfig.currentMachineKey
    )
  }


  private fun build() {
    val userAndTrackStream = this.kafkaStreamsBuilder.stream(
      this.kafkaStreamsConfig.inputTopicName,
      Consumed.with(
        this.customLongSerde,
        this.nowPlayingSerde,
      ).withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST)
    )

    val ttlStateStore = Stores.keyValueStoreBuilder(
      Stores.persistentKeyValueStore(this.kafkaStreamsConfig.store.ttlStoreName),
      this.customLongSerde,    // input topic key
      this.customLongSerde     // input topic message's timestamp
    )

    val userTrackKVStore = Stores.keyValueStoreBuilder(
      Stores.persistentKeyValueStore(this.kafkaStreamsConfig.store.userNowPlayingStoreName),
      this.customLongSerde,
      this.nowPlayingSerde
    )

    this.kafkaStreamsBuilder.addStateStore(ttlStateStore)
    this.kafkaStreamsBuilder.addStateStore(userTrackKVStore)

    userAndTrackStream
      .process(
        TTLProcessorSupplier(
          maxAge = this.kafkaStreamsConfig.time.stopTimeout,
          scanFrequency = this.kafkaStreamsConfig.time.scanFrequency,
          ttlStoreName = this.kafkaStreamsConfig.store.ttlStoreName,
          userTrackStoreName = this.kafkaStreamsConfig.store.userNowPlayingStoreName
        ),
        this.kafkaStreamsConfig.store.ttlStoreName, this.kafkaStreamsConfig.store.userNowPlayingStoreName
      )
      .to(this.kafkaStreamsConfig.inputTopicName, Produced.with(this.customLongSerde, this.nowPlayingSerde))
  }
}

