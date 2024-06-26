package dev.lutergs.muse.domain.entity

import dev.lutergs.muse.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.domain.entity.track.Track
import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.domain.entity.userInfo.UserInfo

data class User (
  val id: Long?,
  val info: UserInfo,
  val nowPlaying: NowPlaying
) {

  fun changeTrack(track: Track, playbackStatus: PlaybackStatus): User {
    return User(
      id = this.id,
      info = this.info,
      nowPlaying = this.nowPlaying.changeTrack(track, playbackStatus)
    )
  }

  fun setPlaybackStatus(playbackStatus: PlaybackStatus): User {
    return User(
      id = this.id,
      info = this.info,
      nowPlaying = this.nowPlaying.setPlaybackStatus(playbackStatus)
    )
  }

  fun changeNickName(name: String): User {
    return User(
      id = this.id,
      info = this.info.changeNickname(name),
      nowPlaying = this.nowPlaying
    )
  }

  fun addFriend(friendId: Long): User {
    return User(
      id = this.id,
      info = this.info.addFriend(friendId),
      nowPlaying = this.nowPlaying
    )
  }

  fun removeFriend(friendId: Long): User {
    return User(
      id = this.id,
      info = this.info.removeFriend(friendId),
      nowPlaying = this.nowPlaying
    )
  }

  fun cleansing(): User {
    return User(
      id = this.id,
      info = this.info.cleansing(),
      nowPlaying = this.nowPlaying
    )
  }
}