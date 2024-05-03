package dev.lutergs.muse.backend.infra.rest.muse

import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthVendor
import dev.lutergs.muse.backend.infra.rest.muse.dto.UserChangeNickNameDto
import dev.lutergs.muse.backend.infra.rest.muse.dto.UserLoginDto
import dev.lutergs.muse.backend.service.UserInfoService
import jakarta.servlet.http.Cookie
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.paramOrNull

class UserInfoRestHandler(
  private val userInfoService: UserInfoService,
  private val logger: Logger = LoggerFactory.getLogger(UserInfoRestHandler::class.java)
) {
  fun getUser(request: ServerRequest): ServerResponse {
    val token = this.getToken(request)
    val id = request.paramOrNull("id")?.toLong()

    return when {
      token != null && id != null -> this.userInfoService.getUser(token, id)
        .let { ServerResponse.ok().body(it) }
      else -> ServerResponse.badRequest().build()
    }
  }

  fun getUserFriends(request: ServerRequest): ServerResponse {
    return this.getToken(request)
      ?.let { this.userInfoService.getUserFriends(it) }
      ?.let { ServerResponse.ok().body(it) }
      ?: ServerResponse.badRequest().build()
  }

  fun login(request: ServerRequest): ServerResponse {
    return request.body(UserLoginDto::class.java)
      .let { this.userInfoService.userLogin(AuthVendor.valueOf(it.type), it.uid) }
      .let {
        ServerResponse.ok()

          // TODO : domain 이름을 configuration variable 로 주입받을 수 있도록 설정 필요
          .cookie(Cookie("project_muse", it.second).apply {
            this.domain = "192.168.9.8"
            this.path = "/"
          })
          .body(it.first)
      }
  }

  fun changeNickname(request: ServerRequest): ServerResponse {
    return this.getToken(request)
      ?.let { it to  request.body(UserChangeNickNameDto::class.java) }
      ?.let { this.userInfoService.changeNickname(it.first, it.second.name) }
      ?.let { ServerResponse.ok().body(it) }
      ?: ServerResponse.badRequest().build()
  }


  fun addFriend(request: ServerRequest): ServerResponse {
    val token = this.getToken(request)
    val id = request.paramOrNull("id")?.toLong()

    return when {
      token != null && id != null -> this.userInfoService.addFriend(token, id)
        .let { ServerResponse.ok().body(it) }
      else -> ServerResponse.badRequest().build()
    }
  }


  fun removeFriend(request: ServerRequest): ServerResponse {
    val token = this.getToken(request)
    val id = request.paramOrNull("id")?.toLong()

    return when {
      token != null && id != null -> this.userInfoService.removeFriend(token, id)
        .let { ServerResponse.ok().body(it) }
      else -> ServerResponse.badRequest().build()
    }
  }


  private fun getToken(request: ServerRequest): String? {
    return request.cookies().getFirst("project_muse")?.value
  }
}