package dev.lutergs.muse.backend.infra.rest.muse

import dev.lutergs.muse.backend.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.backend.domain.entity.track.Track
import dev.lutergs.muse.backend.infra.rest.muse.dto.UserChangeTrackPlayStatusDto
import dev.lutergs.muse.backend.service.UserNowPlayingService
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class UserNowPlayingRestHandler(
  private val userNowPlayingService: UserNowPlayingService
) {

  fun changeTrack(request: ServerRequest): ServerResponse {
    return this.getToken(request)
      ?.let { it to request.body(Track::class.java) }
      ?.let { this.userNowPlayingService.changeUserTrack(it.first, it.second) }
      ?.let { ServerResponse.ok().body(it) }
      ?: ServerResponse.badRequest().build()
  }

  fun changeTrackPlayStatus(request: ServerRequest): ServerResponse {
    return this.getToken(request)
      ?.let { it to request.body(UserChangeTrackPlayStatusDto::class.java) }
      ?.let { this.userNowPlayingService.changeTrackPlayStatus(it.first, PlaybackStatus.valueOf(it.second.status)) }
      ?.let { ServerResponse.ok().body(it) }
      ?: ServerResponse.badRequest().build()
  }

  private fun getToken(request: ServerRequest): String? {
    return request.cookies().getFirst("project_muse")?.value
  }
}