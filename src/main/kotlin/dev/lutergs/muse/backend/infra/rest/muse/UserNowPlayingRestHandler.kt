package dev.lutergs.muse.backend.infra.rest.muse

import dev.lutergs.muse.backend.domain.entity.track.Track
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

  fun playTrack(request: ServerRequest): ServerResponse {
    return this.getToken(request)
      ?.let { this.userNowPlayingService.playTrack(it) }
      ?.let { ServerResponse.ok().body(it) }
      ?: ServerResponse.badRequest().build()
  }

  fun pauseTrack(request: ServerRequest): ServerResponse {
    return this.getToken(request)
      ?.let { this.userNowPlayingService.pauseTrack(it) }
      ?.let { ServerResponse.ok().body(it) }
      ?: ServerResponse.badRequest().build()
  }

  private fun getToken(request: ServerRequest): String? {
    return request.cookies().getFirst("project_muse")?.value
  }
}