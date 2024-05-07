package dev.lutergs.muse.backend.infra.rest.muse

import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthVendor
import dev.lutergs.muse.backend.infra.crypto.TokenManager
import dev.lutergs.muse.backend.infra.rest.muse.dto.UserChangeNickNameDto
import dev.lutergs.muse.backend.infra.rest.muse.dto.UserLoginDto
import dev.lutergs.muse.backend.service.UserInfoService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.paramOrNull

class UserInfoRestHandler(
  private val userInfoService: UserInfoService,
  private val tokenManager: TokenManager
) {
  private val logger: Logger = LoggerFactory.getLogger(UserInfoRestHandler::class.java)

  fun getUser(request: ServerRequest): ServerResponse {
    return this.validateAccessToken(request) { _ ->
      val id = request.paramOrNull("id")?.toLong()
      if (id != null) {
        this.userInfoService.getUser(id)
          .let { ServerResponse.ok().body(it) }
      } else {
        ServerResponse.badRequest().build()
      }
    }
  }

  fun getUserFriends(request: ServerRequest): ServerResponse {
    return this.validateAccessToken(request) { userId ->
      this.userInfoService.getUserFriends(userId)
        .let { ServerResponse.ok().body(it) }
    }
  }

  fun login(request: ServerRequest): ServerResponse {
    return request.body(UserLoginDto::class.java)
      .let { this.userInfoService.userLogin(AuthVendor.valueOf(it.type), it.uid) }
      .let { user ->
        ServerResponse.ok()
          .body(
            mapOf(
              "user" to user,
              "token" to this.tokenManager.issueTokens(user.id!!)
            )
          )
      }
  }

  fun changeNickname(request: ServerRequest): ServerResponse {
    return this.validateAccessToken(request) { userId ->
      request.body(UserChangeNickNameDto::class.java)
        .let { this.userInfoService.changeNickname(userId, it.name) }
        .let { ServerResponse.ok().body(it) }
    }
  }

  fun addFriend(request: ServerRequest): ServerResponse {
    return this.validateAccessToken(request) { userId ->
      val friendId = request.paramOrNull("id")?.toLong()
      if (friendId != null) {
        this.userInfoService.addFriend(userId, friendId)
          .let { ServerResponse.ok().body(it) }
      } else {
        ServerResponse.badRequest().build()
      }
    }
  }

  fun removeFriend(request: ServerRequest): ServerResponse {
    return this.validateAccessToken(request) { userId ->
      val friendId = request.paramOrNull("id")?.toLong()
      if (friendId != null) {
        this.userInfoService.removeFriend(userId, friendId)
          .let { ServerResponse.ok().body(it) }
      } else {
        ServerResponse.badRequest().build()
      }
    }
  }

  fun refreshToken(request: ServerRequest): ServerResponse {
    return this.validateRefreshToken(request) { userId ->
      ServerResponse.ok()
        .body(this.tokenManager.issueTokens(userId))
    }
  }

  private fun validateAccessToken(request: ServerRequest, processFunc: (Long) -> ServerResponse): ServerResponse {
    return request.headers().firstHeader("Authorization")
      ?.let { it.split(" ")[1] }
      ?.let {
        println("access token is $it")
        this.tokenManager.getUserIdFromAccessToken(it) }
      ?.let(processFunc)
      ?: ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
  }

  private fun validateRefreshToken(request: ServerRequest, processFunc: (Long) -> ServerResponse): ServerResponse {
    return request.headers().firstHeader("Authorization")
      ?.let { it.split(" ")[1] }
      ?.let { this.tokenManager.getUserIdFromRefreshToken(it) }
      ?.let(processFunc)
      ?: ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
  }
}