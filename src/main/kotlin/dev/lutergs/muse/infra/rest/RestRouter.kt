package dev.lutergs.muse.infra.rest

import dev.lutergs.muse.infra.rest.kafkastreams.KafkaStreamsRestHandler
import dev.lutergs.muse.infra.rest.muse.UserInfoRestHandler
import dev.lutergs.muse.infra.rest.muse.UserNowPlayingRestHandler
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
      GET("/users", userInfoRestHandler::getUsers)
      GET("/users/{id}", userInfoRestHandler::getUser)
      POST("/user", userInfoRestHandler::changeNickname)
      POST("/user/login", userInfoRestHandler::login)
      GET("/user/friends", userInfoRestHandler::getUserFriends)
      PUT("/user/friends", userInfoRestHandler::addFriend)
      DELETE("/user/friends", userInfoRestHandler::removeFriend)
      POST("/user/token", userInfoRestHandler::refreshToken)

      // for websocket server (get userid from accessToken)
      GET("/user/id", userInfoRestHandler::getUserIdFromAccessToken)

      // userNowPlayingRestHandler
      POST("/track", userNowPlayingRestHandler::changeTrack)
      POST("/track/status", userNowPlayingRestHandler::changeTrackPlayStatus)

      // kafkaStreamsRestHandler
      GET("/kstreams/{userId}", kafkaStreamsRestHandler::getNowPlaying)
    }
  }
}