package dev.lutergs.muse.backend.infra.config

import dev.lutergs.muse.backend.infra.config.properties.KafkaStreamsConfigProperties
import dev.lutergs.muse.backend.infra.crypto.TokenManager
import dev.lutergs.muse.backend.infra.repository.kafka.streams.internal.KafkaStreamsInternalRepository
import dev.lutergs.muse.backend.infra.rest.kafkastreams.KafkaStreamsRestHandler
import dev.lutergs.muse.backend.infra.rest.muse.UserInfoRestHandler
import dev.lutergs.muse.backend.infra.rest.muse.UserNowPlayingRestHandler
import dev.lutergs.muse.backend.service.UserNowPlayingService
import dev.lutergs.muse.backend.service.UserInfoService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RestSpringBeanConfig(
  private val kafkaStreamsConfigProperties: KafkaStreamsConfigProperties
) {

  @Bean
  fun userInfoRestHandler(
    userNowPlayingService: UserNowPlayingService,
    userInfoService: UserInfoService,
    tokenManager: TokenManager
  ): UserInfoRestHandler = UserInfoRestHandler(
    userInfoService = userInfoService,
    tokenManager = tokenManager
  )

  @Bean
  fun userNowPlayingRestHandler(
    userNowPlayingService: UserNowPlayingService,
    tokenManager: TokenManager
  ): UserNowPlayingRestHandler = UserNowPlayingRestHandler(
    userNowPlayingService = userNowPlayingService,
    tokenManager = tokenManager
  )

  @Bean
  fun kafkaStreamsRestHandler(
    internalRepository: KafkaStreamsInternalRepository
  ): KafkaStreamsRestHandler = KafkaStreamsRestHandler(
    key = this.kafkaStreamsConfigProperties.communicateKey,
    kafkaStreamsInternalRepository = internalRepository
  )
}