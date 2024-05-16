package dev.lutergs.muse.service

import dev.lutergs.muse.domain.entity.User
import dev.lutergs.muse.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.domain.entity.track.Track
import dev.lutergs.muse.domain.repository.NowPlayingNotifier
import dev.lutergs.muse.domain.repository.UserInfoRepository
import jakarta.transaction.Transactional

open class UserNowPlayingService(
  private val userInfoRepository: UserInfoRepository,
  private val notifier: NowPlayingNotifier
) {

  // user 가 다음 곡으로 넘김
  @Transactional
  open fun changeUserTrack(userId: Long, track: Track, playbackStatus: PlaybackStatus) {
    this.userInfoRepository.getUser(userId)
      ?.let { this.modify(it) { user -> user.changeTrack(track, playbackStatus)} }
      ?: throw IllegalStateException("존재하지 않는 user 입니다.")
  }

  @Transactional
  open fun changeTrackPlayStatus(userId: Long, status: PlaybackStatus) {
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