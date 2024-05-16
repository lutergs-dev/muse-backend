package dev.lutergs.muse.domain.entity.userInfo

import dev.lutergs.muse.domain.entity.track.MusicVendor
import dev.lutergs.muse.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.domain.entity.track.Track
import java.time.Instant

data class NowPlaying(
  val track: Track,
  val status: PlaybackStatus,
  val timestamp: Long
) {
  fun setPlaybackStatus(playbackStatus: PlaybackStatus): NowPlaying = NowPlaying(
    track = if (playbackStatus == PlaybackStatus.STOPPED) Track.nullTrack() else this.track,
    status = playbackStatus,
    timestamp = Instant.now().epochSecond
  )

  fun changeTrack(track: Track, playbackStatus: PlaybackStatus): NowPlaying {
    val status = when (track.vendor) {
      MusicVendor.Else -> PlaybackStatus.STOPPED
      else -> playbackStatus
    }
    return NowPlaying(
      track = track,
      status = status,
      timestamp = Instant.now().epochSecond
    )
  }

  companion object {
    fun empty(): NowPlaying {
      return NowPlaying(
        track = Track.nullTrack(),
        status = PlaybackStatus.STOPPED,
        timestamp = Instant.now().epochSecond
      )
    }
  }
}
