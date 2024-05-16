package dev.lutergs.muse.infra.config

import dev.lutergs.muse.infra.config.properties.KafkaStreamsConfigProperties
import dev.lutergs.muse.infra.crypto.TokenManager
import dev.lutergs.muse.infra.repository.kafka.streams.internal.KafkaStreamsInternalRepository
import dev.lutergs.muse.infra.rest.kafkastreams.KafkaStreamsRestHandler
import dev.lutergs.muse.infra.rest.muse.UserInfoRestHandler
import dev.lutergs.muse.infra.rest.muse.UserNowPlayingRestHandler
import dev.lutergs.muse.service.UserNowPlayingService
import dev.lutergs.muse.service.UserInfoService
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