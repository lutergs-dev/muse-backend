package dev.lutergs.muse.backend.infra.config

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.muse.backend.infra.config.properties.KafkaHttpUrlConfigProperties
import dev.lutergs.muse.backend.infra.config.properties.KafkaStreamsConfigProperties
import dev.lutergs.muse.backend.infra.repository.kafka.streams.KafkaStreamsQueryService
import dev.lutergs.muse.backend.infra.repository.kafka.streams.external.KafkaStreamsExternalRepository
import dev.lutergs.muse.backend.infra.repository.kafka.streams.external.KafkaStreamsRestClient
import dev.lutergs.muse.backend.infra.repository.kafka.streams.internal.KafkaStreamsInternalRepository
import dev.lutergs.muse.backend.infra.repository.kafka.streams.internal.KafkaStreamsStateStore
import dev.lutergs.muse.backend.infra.repository.kafka.streams.internal.KafkaStreamsTopology
import org.apache.kafka.streams.StreamsBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.config.StreamsBuilderFactoryBean

@Configuration
@EnableKafkaStreams
@EnableConfigurationProperties(value = [
  KafkaHttpUrlConfigProperties::class,
  KafkaStreamsConfigProperties::class
])
class KafkaStreamsSpringBeanConfig(
  private val kafkaHttpUrlConfigProperties: KafkaHttpUrlConfigProperties,
  private val kafkaStreamsConfigProperties: KafkaStreamsConfigProperties,
) {

  @Bean
  fun kafkaStreamsRestClient(
    redisTemplate: StringRedisTemplate
  ): KafkaStreamsRestClient = KafkaStreamsRestClient(
    kafkaHttpUrlConfigs = this.kafkaHttpUrlConfigProperties,
    kafkaStreamsConfigProperties = this.kafkaStreamsConfigProperties,
    redisClient = redisTemplate
  )

  @Bean
  fun kafkaStreamsExternalRepository(
    restClient: KafkaStreamsRestClient
  ): KafkaStreamsExternalRepository = KafkaStreamsExternalRepository(
    kafkaStreamsRestClient = restClient
  )

  @Bean
  fun kafkaStreamsTopology(
    redisTemplate: StringRedisTemplate,
    objectMapper: ObjectMapper,
    kafkaStreamsBuilder: StreamsBuilder
  ): KafkaStreamsTopology = KafkaStreamsTopology(
    kafkaStreamsConfig = this.kafkaStreamsConfigProperties,
    httpUrlConfigs = this.kafkaHttpUrlConfigProperties,
    kafkaStreamsBuilder = kafkaStreamsBuilder,
    redisClient = redisTemplate,
    objectMapper = objectMapper
  )

  @Bean
  fun kafkaStreamsStateStore(
    builder: StreamsBuilderFactoryBean
  ) = KafkaStreamsStateStore(
    kafkaStreamsConfig = this.kafkaStreamsConfigProperties,
    builder = builder
  )

  @Bean
  fun kafkaStreamsInternalRepository(
    kafkaStreamsStateStore: KafkaStreamsStateStore
  ): KafkaStreamsInternalRepository = KafkaStreamsInternalRepository(
    kafkaStreamsStateStore = kafkaStreamsStateStore
  )

  @Bean
  fun kafkaStreamsQueryService(
    externalRepository: KafkaStreamsExternalRepository,
    internalRepository: KafkaStreamsInternalRepository
  ): KafkaStreamsQueryService = KafkaStreamsQueryService(
    external = externalRepository,
    internal = internalRepository
  )
}