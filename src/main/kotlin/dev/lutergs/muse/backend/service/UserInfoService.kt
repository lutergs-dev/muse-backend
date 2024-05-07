package dev.lutergs.muse.backend.service

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.backend.domain.entity.userInfo.UserInfo
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthInfo
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthVendor
import dev.lutergs.muse.backend.domain.repository.UserInfoRepository

class UserInfoService (
  private val userInfoRepository: UserInfoRepository
) {

  fun getUser(userId: Long): User {
    return this.userInfoRepository.getUser(userId)
      ?.cleansing()
      ?: throw IllegalStateException("존재하지 않은 user 의 조회를 요청했습니다.")
  }

  fun getUserFriends(userId: Long): List<User> {
    return this.userInfoRepository.getUser(userId)
      ?.let { user -> user.info.friends.mapNotNull { this.userInfoRepository.getUser(it)?.cleansing() } }
      ?: throw IllegalStateException("존재하지 않는 user 입니다.")
  }

  // 유저 최초 or 그냥 로그인
  // controller 단에선 응답을 다르게 주는거로 구분해야 할듯?
  fun userLogin(type: AuthVendor, uid: String): User {
    return AuthInfo(type, uid)
      .let { this.userInfoRepository.getUserByAuth(it) ?: this.userSignup(it) }
  }

  // 유저 닉네임 변경
  fun changeNickname(userId: Long, name: String) {
    this.userInfoRepository.getUser(userId)
      ?.changeNickName(name)
      ?.let { this.userInfoRepository.saveUser(it) }
      ?: throw IllegalStateException("존재하지 않는 user 입니다.")
  }

  // 유저 친구추가
  fun addFriend(userId: Long, friendId: Long) {
    this.userInfoRepository.getUser(userId)
      ?.also { this.userInfoRepository.getUser(friendId) ?: throw IllegalStateException("존재하지 않는 user 의 추가를 요청했습니다.") }
      ?.let { this.userInfoRepository.addFriend(it, friendId) }
      ?: throw IllegalStateException("존재하지 않는 user 입니다.")
  }

  // 유저 친구제거
  fun removeFriend(userId: Long, friendId: Long) {
    this.userInfoRepository.getUser(userId)
      ?.also { if (!it.info.friends.contains(friendId)) throw IllegalStateException("해당 User 에 존재하지 않는 friend 삭제를 요청했습니다.") }
      ?.let { this.userInfoRepository.removeFriend(it, friendId) }
      ?: throw IllegalStateException("존재하지 않는 user 입니다.")
  }

  private fun userSignup(authInfo: AuthInfo): User {
    return User(
      id = null,
      info = UserInfo.fromAuthInfo(authInfo),
      nowPlaying = NowPlaying.empty()
    ).let { this.userInfoRepository.saveUser(it) }
  }
}