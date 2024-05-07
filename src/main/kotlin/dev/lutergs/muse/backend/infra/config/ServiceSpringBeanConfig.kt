package dev.lutergs.muse.backend.infra.config

import dev.lutergs.muse.backend.domain.repository.NowPlayingNotifier
import dev.lutergs.muse.backend.domain.repository.UserInfoRepository
import dev.lutergs.muse.backend.service.UserNowPlayingService
import dev.lutergs.muse.backend.service.UserInfoService
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
    notifier: NowPlayingNotifier
  ): UserNowPlayingService = UserNowPlayingService(
    userInfoRepository = userInfoRepository,
    notifier = notifier
  )
}