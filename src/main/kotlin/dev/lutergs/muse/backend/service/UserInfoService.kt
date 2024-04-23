package dev.lutergs.muse.backend.service

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.backend.domain.entity.userInfo.UserInfo
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthInfo
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthVendor
import dev.lutergs.muse.backend.domain.repository.UserAuthRepository
import dev.lutergs.muse.backend.domain.repository.UserInfoRepository

class UserInfoService (
  private val userInfoRepository: UserInfoRepository,
  private val userAuthRepository: UserAuthRepository
) {

  fun getUser(token: String, userId: Long): User {
    return this.userAuthRepository.getUserFromToken(token)
      ?.let {
        this.userInfoRepository.getUser(userId)
          ?.cleansing()
          ?: throw RuntimeException("존재하지 않은 user 의 조회를 요청했습니다.")
      }
      ?: throw RuntimeException("token 에 해당하는 user 가 존재하지 않습니다.")
  }

  fun getUserFriends(token: String): List<User> {
    return this.userAuthRepository.getUserFromToken(token)
      ?.let { user ->
        user.info.friends.mapNotNull { this.userInfoRepository.getUser(it)?.cleansing() }
      }
      ?: throw RuntimeException("token 에 해당하는 user 가 존재하지 않습니다.")
  }

  // 유저 최초 or 그냥 로그인
  // controller 단에선 응답을 다르게 주는거로 구분해야 할듯?
  fun userLogin(type: AuthVendor, uid: String): Pair<User, String> {
    return AuthInfo(type, uid)
      .let { this.userInfoRepository.getUserByAuth(it) ?: this.userSignup(it) }
      .let { it to this.userAuthRepository.getTokenFromUser(it) }
  }

  // 유저 닉네임 변경
  fun changeNickname(token: String, name: String) {
    this.userAuthRepository.getUserFromToken(token)
      ?.changeNickName(name)
      ?.let { this.userInfoRepository.saveUser(it) }
      ?: throw RuntimeException("token 에 해당하는 user 가 존재하지 않습니다.")
  }

  // 유저 친구추가
  fun addFriend(token: String, friendId: Long) {
    this.userAuthRepository.getUserFromToken(token)
      ?.also { this.userInfoRepository.getUser(friendId) ?: throw RuntimeException("존재하지 않는 user 의 추가를 요청했습니다.") }
      ?.let { this.userInfoRepository.addFriend(it, friendId) }
      ?: throw RuntimeException("token 에 해당하는 user 가 존재하지 않습니다.")
  }

  // 유저 친구제거
  fun removeFriend(token: String, friendId: Long) {
    this.userAuthRepository.getUserFromToken(token)
      ?.also { if (!it.info.friends.contains(friendId)) throw RuntimeException("해당 User 에 존재하지 않는 friend 삭제를 요청했습니다.") }
      ?.let { this.userInfoRepository.removeFriend(it, friendId) }
      ?: throw RuntimeException("token 에 해당하는 user 가 존재하지 않습니다.")
  }

  private fun userSignup(authInfo: AuthInfo): User {
    return User(
      id = null,
      info = UserInfo.fromAuthInfo(authInfo),
      nowPlaying = NowPlaying.empty()
    ).let { this.userInfoRepository.saveUser(it) }
  }
}