package dev.lutergs.muse.backend.service

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.backend.domain.entity.track.Track
import dev.lutergs.muse.backend.domain.repository.NowPlayingNotifier
import dev.lutergs.muse.backend.domain.repository.UserAuthRepository
import dev.lutergs.muse.backend.domain.repository.UserInfoRepository

class UserNowPlayingService(
  private val userInfoRepository: UserInfoRepository,
  private val userAuthRepository: UserAuthRepository,
  private val notifier: NowPlayingNotifier
) {

  // user 가 다음 곡으로 넘김
  fun changeUserTrack(token: String, track: Track) {
    this.userAuthRepository.getUserFromToken(token)
      ?.let { this.modify(it) { user -> user.changeTrack(track) } }
      ?: throw RuntimeException("존재하지 않는 user 입니다.")
  }

  // user 가 곡을 일시정지함
  fun pauseTrack(token: String) {
    this.userAuthRepository.getUserFromToken(token)
      ?.let { this.modify(it) { user -> user.setPlaybackStatus(PlaybackStatus.PAUSED) } }
      ?: throw RuntimeException("존재하지 않는 user 입니다.")
  }

  // user 가 곡을 다시 재생함
  fun playTrack(token: String) {
    this.userAuthRepository.getUserFromToken(token)
      ?.let { this.modify(it) { user -> user.setPlaybackStatus(PlaybackStatus.PLAYING) } }
      ?: throw RuntimeException("존재하지 않는 user 입니다.")
  }

  private fun modify(user: User, userChangeFunc: (User) -> User): User {
    val res = userChangeFunc(user)
    println(res)

    return userChangeFunc
      .invoke(user)
      .also { println(user) }
      .let { this.userInfoRepository.saveUser(it) }
      .also { this.notifier.notify(it) }
  }
}