package dev.lutergs.muse.infra.config

import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.infra.config.properties.KafkaStreamsConfigProperties
import dev.lutergs.muse.infra.repository.kafka.producer.KafkaStreamsTopicProducer
import dev.lutergs.muse.infra.repository.kafka.producer.ProduceValueDto
import dev.lutergs.muse.infra.repository.kafka.producer.WebsocketTopicProducer
import dev.lutergs.muse.infra.repository.kafka.serialization.LongSerializer
import dev.lutergs.muse.infra.repository.kafka.serialization.NowPlayingSerializer
import dev.lutergs.muse.infra.repository.kafka.serialization.ProduceValueDtoSerializer
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
  fun kafkaStreamsTopicProducer(
    kafkaTemplate: KafkaTemplate<Long, NowPlaying>
  ): KafkaStreamsTopicProducer = KafkaStreamsTopicProducer(
    kafkaTemplate = kafkaTemplate,
    topicName = this.kafkaStreamsConfigProperties.inputTopicName
  )


  @Bean
  fun websocketTopicProducerFactory(): ProducerFactory<NowPlaying, ProduceValueDto> {
    return this.kafkaProperties.buildProducerProperties(null)
      .apply {
        this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = NowPlayingSerializer::class.java
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ProduceValueDtoSerializer::class.java
      }.let { DefaultKafkaProducerFactory(it) }
  }

  @Bean
  fun wsServerProduceKafkaTemplate(
    websocketTopicProducerFactory: ProducerFactory<NowPlaying, ProduceValueDto>
  ): KafkaTemplate<NowPlaying, ProduceValueDto> {
    return KafkaTemplate(websocketTopicProducerFactory)
  }


  @Bean
  fun websocketTopicProducer(
    kafkaTemplate: KafkaTemplate<NowPlaying, ProduceValueDto>
  ): WebsocketTopicProducer = WebsocketTopicProducer(
    kafkaTemplate = kafkaTemplate
  )

}