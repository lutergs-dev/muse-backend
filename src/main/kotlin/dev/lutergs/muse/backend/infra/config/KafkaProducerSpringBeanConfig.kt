package dev.lutergs.muse.backend.infra.config

import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.backend.infra.config.properties.KafkaStreamsConfigProperties
import dev.lutergs.muse.backend.infra.repository.kafka.producer.KafkaStreamsTopicProducer
import dev.lutergs.muse.backend.infra.repository.kafka.producer.WebsocketTopicProducer
import dev.lutergs.muse.backend.infra.repository.kafka.serialization.LongListSerializer
import dev.lutergs.muse.backend.infra.repository.kafka.serialization.LongSerializer
import dev.lutergs.muse.backend.infra.repository.kafka.serialization.NowPlayingSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
@EnableKafka
class KafkaProducerSpringBeanConfig(
  private val kafkaProperties: KafkaProperties,
  private val kafkaStreamsConfigProperties: KafkaStreamsConfigProperties
) {

  @Bean
  fun streamsProducerFactory(): ProducerFactory<Long, NowPlaying> {
    return this.kafkaProperties.buildProducerProperties(null)
      .apply {
        this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = NowPlayingSerializer::class.java
      }.let { DefaultKafkaProducerFactory(it) }
  }

  @Bean
  fun streamsKafkaTemplate(
    streamsProducerFactory: ProducerFactory<Long, NowPlaying>
  ): KafkaTemplate<Long, NowPlaying> {
    return KafkaTemplate(streamsProducerFactory)
  }


  @Bean
  fun websocketTopicProducerFactory(): ProducerFactory<NowPlaying, List<Long>> {
    return this.kafkaProperties.buildProducerProperties(null)
      .apply {
        this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = NowPlayingSerializer::class.java
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = LongListSerializer::class.java
      }.let { DefaultKafkaProducerFactory(it) }
  }

  @Bean
  fun wsServerProduceKafkaTemplate(
    websocketTopicProducerFactory: ProducerFactory<NowPlaying, List<Long>>
  ): KafkaTemplate<NowPlaying, List<Long>> {
    return KafkaTemplate(websocketTopicProducerFactory)
  }


  @Bean
  fun websocketTopicProducer(
    kafkaTemplate: KafkaTemplate<NowPlaying, List<Long>>
  ): WebsocketTopicProducer = WebsocketTopicProducer(
    kafkaTemplate = kafkaTemplate
  )

  @Bean
  fun kafkaStreamsTopicProducer(
    kafkaTemplate: KafkaTemplate<Long, NowPlaying>
  ): KafkaStreamsTopicProducer = KafkaStreamsTopicProducer(
    kafkaTemplate = kafkaTemplate,
    topicName = this.kafkaStreamsConfigProperties.inputTopicName
  )

}