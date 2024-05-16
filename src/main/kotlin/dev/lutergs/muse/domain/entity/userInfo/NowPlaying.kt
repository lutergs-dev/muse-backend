package dev.lutergs.muse.domain.entity.userInfo

import dev.lutergs.muse.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.domain.entity.track.Track
import java.time.Instant

data class NowPlaying(
  val track: Track?,
  val status: PlaybackStatus,
  val timestamp: Long
) {
  fun setPlaybackStatus(playbackStatus: PlaybackStatus): NowPlaying = NowPlaying(
    track = this.track,
    status = playbackStatus,
    timestamp = Instant.now().epochSecond
  )

  fun changeMusic(track: Track, timestamp: Long): NowPlaying = NowPlaying(
    track = track,
    status = this.status,
    timestamp = Instant.now().epochSecond
  )

  companion object {
    fun fromTrack(track: Track): NowPlaying {
      return NowPlaying(
        track = track,
        status = PlaybackStatus.PLAYING,
        timestamp = Instant.now().epochSecond
      )
    }

    fun empty(): NowPlaying {
      return NowPlaying(
        track = null,
        status = PlaybackStatus.STOPPED,
        timestamp = Instant.now().epochSecond
      )
    }
  }
}
