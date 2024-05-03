package dev.lutergs.muse.backend.infra.rest

import dev.lutergs.muse.backend.infra.rest.kafkastreams.KafkaStreamsRestHandler
import dev.lutergs.muse.backend.infra.rest.muse.UserInfoRestHandler
import dev.lutergs.muse.backend.infra.rest.muse.UserNowPlayingRestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.*

@Configuration
class RestRouter(
  private val userInfoRestHandler: UserInfoRestHandler,
  private val userNowPlayingRestHandler: UserNowPlayingRestHandler,
  private val kafkaStreamsRestHandler: KafkaStreamsRestHandler
) {

  @Bean
  fun buildRouter() = router {
    accept(MediaType.APPLICATION_JSON).nest {

      // userInfoRestHandler
      GET("/user", userInfoRestHandler::getUser)
      POST("/user", userInfoRestHandler::changeNickname)
      POST("/user/login", userInfoRestHandler::login)
      GET("/user/friends", userInfoRestHandler::getUserFriends)
      PUT("/user/friends", userInfoRestHandler::addFriend)
      DELETE("/user/friends", userInfoRestHandler::removeFriend)

      // userNowPlayingRestHandler
      POST("/track", userNowPlayingRestHandler::changeTrack)
      POST("/track/status", userNowPlayingRestHandler::changeTrackPlayStatus)

      // kafkaStreamsRestHandler
      GET("/", userInfoRestHandler::getUser)
      GET("/kstreams/{userId}", kafkaStreamsRestHandler::getNowPlaying)
    }
  }
}