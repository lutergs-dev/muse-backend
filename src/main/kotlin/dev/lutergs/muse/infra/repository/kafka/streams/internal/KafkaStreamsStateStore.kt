package dev.lutergs.muse.infra.repository.kafka.streams.internal

import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.infra.config.properties.KafkaStreamsConfigProperties
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.LoggerFactory
import org.springframework.kafka.config.StreamsBuilderFactoryBean

class KafkaStreamsStateStore(
  private val kafkaStreamsConfig: KafkaStreamsConfigProperties,
  builder: StreamsBuilderFactoryBean
): StreamsBuilderFactoryBean.Listener {
  private val logger = LoggerFactory.getLogger(this::class.java)

  lateinit var userTrackStore: ReadOnlyKeyValueStore<Long, NowPlaying>

  init { builder.addListener(this) }

  override fun streamsAdded(id: String, streams: KafkaStreams) {
    this.userTrackStore = streams.store(
      StoreQueryParameters.fromNameAndType(
        this.kafkaStreamsConfig.store.userNowPlayingStoreName,
        QueryableStoreTypes.keyValueStore()
      )
    )

    this.logger.info("Kafka Streams statestore successfully initiated!")
  }

}