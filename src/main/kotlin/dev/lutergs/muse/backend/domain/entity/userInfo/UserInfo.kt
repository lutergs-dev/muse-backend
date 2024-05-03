package dev.lutergs.muse.backend.domain.entity.userInfo

import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthInfo
import dev.lutergs.muse.backend.util.generateRandomString
import java.util.*

data class UserInfo (
  val name: String,      //
  val auth: AuthInfo?,    // 외부로 나갈 땐 공개되지 않음
  val friends: List<Long>
) {
  fun changeNickname(name: String): UserInfo {
    if (name.length > 30) {
      throw RuntimeException("닉네임은 30자를 초과할 수 없습니다.")
    }
    return UserInfo(
      name = name,
      auth = this.auth,
      friends = this.friends
    )
  }

  fun addFriend(friend: Long): UserInfo {
    if (this.friends.contains(friend)) {
      throw RuntimeException("이미 친구로 등록되어 있는 유저입니다.")
    }
    return UserInfo(
      name = this.name,
      auth = this.auth,
      friends = this.friends.plus(friend)
    )
  }

  fun removeFriend(friend: Long): UserInfo {
    if (!this.friends.contains(friend)) {
      throw RuntimeException("친구로 등록된 유저가 아님에도 친구 삭제를 요청했습니다.")
    }
    return UserInfo (
      name = this.name,
      auth = this.auth,
      friends = this.friends.minus(friend)
    )
  }

  fun cleansing(): UserInfo {
    return UserInfo(
      name = this.name,
      auth = null,
      friends = listOf()
    )
  }

  companion object {
    fun fromAuthInfo(auth: AuthInfo): UserInfo {
      return UserInfo(
        name = generateRandomString(),
        auth = auth,
        friends = listOf()
      )
    }
  }
}