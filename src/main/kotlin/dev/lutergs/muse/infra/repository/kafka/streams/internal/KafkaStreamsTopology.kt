package dev.lutergs.muse.infra.repository.kafka.streams.internal

import dev.lutergs.muse.infra.config.properties.KafkaStreamsConfigProperties
import dev.lutergs.muse.infra.repository.kafka.serialization.LongDeserializer
import dev.lutergs.muse.infra.repository.kafka.serialization.LongSerializer
import dev.lutergs.muse.infra.repository.kafka.streams.processor.TTLProcessorSupplier
import dev.lutergs.muse.infra.repository.kafka.serialization.NowPlayingDeserializer
import dev.lutergs.muse.infra.repository.kafka.serialization.NowPlayingSerializer
import dev.lutergs.muse.service.UserNowPlayingService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.state.Stores
import org.springframework.data.redis.core.StringRedisTemplate

class KafkaStreamsTopology(
  private val userNowPlayingService: UserNowPlayingService,
  private val kafkaStreamsConfig: KafkaStreamsConfigProperties,
  private val httpUrlConfigs: dev.lutergs.muse.infra.config.properties.KafkaHttpUrlConfigProperties,
  private val kafkaStreamsBuilder: StreamsBuilder,
  private val redisClient: StringRedisTemplate
) {
  private val nowPlayingSerde = Serdes.serdeFrom(
    NowPlayingSerializer(),
    NowPlayingDeserializer()
  )
  private val customLongSerde = Serdes.serdeFrom(
    LongSerializer(),
    LongDeserializer()
  )

  init { this.build() }

  @PostConstruct
  private fun registerKafkaStreams() {
    this.redisClient.opsForHash<String, String>().put(
      "KafkaStreamsClient",
      this.kafkaStreamsConfig.currentMachineKey,
      this.httpUrlConfigs.buildUrl()
    )
  }

  @PreDestroy
  private fun unregisterKafkaStreams() {
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

    val userTrackKVStore = Stores.keyValueStoreBuilder(
      Stores.persistentKeyValueStore(this.kafkaStreamsConfig.store.userNowPlayingStoreName),
      this.customLongSerde,
      this.nowPlayingSerde
    )

    this.kafkaStreamsBuilder.addStateStore(userTrackKVStore)

    userAndTrackStream
      .process(
        TTLProcessorSupplier(
          pausedTimeout = this.kafkaStreamsConfig.time.pauseTimeout,
          playingTimeout = this.kafkaStreamsConfig.time.playingTimeout,
          scanFrequency = this.kafkaStreamsConfig.time.scanFrequency,
          userTrackStoreName = this.kafkaStreamsConfig.store.userNowPlayingStoreName,
          userNowPlayingService = this.userNowPlayingService
        ),
        this.kafkaStreamsConfig.store.userNowPlayingStoreName
      )
  }
}

