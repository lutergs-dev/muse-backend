package dev.lutergs.muse.backend.service

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.backend.domain.entity.track.Track
import dev.lutergs.muse.backend.domain.repository.NowPlayingNotifier
import dev.lutergs.muse.backend.domain.repository.UserInfoRepository

class UserNowPlayingService(
  private val userInfoRepository: UserInfoRepository,
  private val notifier: NowPlayingNotifier
) {

  // user 가 다음 곡으로 넘김
  fun changeUserTrack(userId: Long, track: Track) {
    this.userInfoRepository.getUser(userId)
      ?.let { this.modify(it) { user -> user.changeTrack(track)} }
      ?: throw IllegalStateException("존재하지 않는 user 입니다.")
  }

  fun changeTrackPlayStatus(userId: Long, status: PlaybackStatus) {
    this.userInfoRepository.getUser(userId)
      ?.let { this.modify(it) { user -> user.setPlaybackStatus(status) } }
      ?: throw IllegalStateException("존재하지 않는 user 입니다.")
  }

  private fun modify(user: User, userChangeFunc: (User) -> User): User {
    return userChangeFunc
      .invoke(user)
      .let { this.userInfoRepository.saveUser(it) }
      .also { this.notifier.notify(it) }
  }
}