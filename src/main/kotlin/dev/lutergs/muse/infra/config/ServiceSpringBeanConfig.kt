package dev.lutergs.muse.infra.config

import dev.lutergs.muse.domain.repository.UserInfoRepository
import dev.lutergs.muse.service.UserNowPlayingService
import dev.lutergs.muse.service.UserInfoService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceSpringBeanConfig {

  @Bean
  fun userInfoService(
    userInfoRepository: UserInfoRepository
  ): UserInfoService = UserInfoService(
    userInfoRepository = userInfoRepository
  )

  @Bean
  fun userNowPlayingService(
    userInfoRepository: UserInfoRepository,
    notifier: dev.lutergs.muse.domain.repository.NowPlayingNotifier
  ): UserNowPlayingService = UserNowPlayingService(
    userInfoRepository = userInfoRepository,
    notifier = notifier
  )
}