package dev.lutergs.muse.backend.infra.rest.muse

import dev.lutergs.muse.backend.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.backend.domain.entity.track.Track
import dev.lutergs.muse.backend.infra.crypto.TokenManager
import dev.lutergs.muse.backend.infra.rest.muse.dto.UserChangeTrackPlayStatusDto
import dev.lutergs.muse.backend.service.UserNowPlayingService
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class UserNowPlayingRestHandler(
  private val userNowPlayingService: UserNowPlayingService,
  private val tokenManager: TokenManager
) {

  fun changeTrack(request: ServerRequest): ServerResponse {
    return this.validateAccessToken(request) { userId ->
      request.body(Track::class.java)
        .let { this.userNowPlayingService.changeUserTrack(userId, it) }
        .let { ServerResponse.ok().body(it) }
    }
  }

  fun changeTrackPlayStatus(request: ServerRequest): ServerResponse {
    return this.validateAccessToken(request) { userId ->
      request.body(UserChangeTrackPlayStatusDto::class.java)
        .let { this.userNowPlayingService.changeTrackPlayStatus(userId, PlaybackStatus.valueOf(it.status)) }
        .let { ServerResponse.ok().body(it) }
    }
  }

  private fun validateAccessToken(request: ServerRequest, processFunc: (Long) -> ServerResponse): ServerResponse {
    return request.headers().firstHeader("Authorization")
      ?.let { it.split(" ")[1] }
      ?.let { this.tokenManager.getUserIdFromAccessToken(it) }
      ?.let(processFunc)
      ?: ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
  }
}