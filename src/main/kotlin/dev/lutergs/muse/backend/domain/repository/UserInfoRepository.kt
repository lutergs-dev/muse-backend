package dev.lutergs.muse.backend.domain.repository

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthInfo

interface UserInfoRepository {
  fun getUser(id: Long): User?
  fun saveUser(user: User): User
  fun getUserByAuth(auth: AuthInfo): User?

  // 1:N relation 상, 자식 엔티티를 도메인 엔티티로 변환해버리게 되면,
  // 자식 엔티티의 추가 / 삭제가 일어났을 때 불필요한 검색 및 추가 / 삭제된 자식 확인 및 생성 / 삭제 과정이 포함된다.
  // 그러지 않도록, 부득이하게 repository 단에서 추가 / 삭제 함수를 구현한다.
  fun addFriend(user: User, friendId: Long): User
  fun removeFriend(user: User, friendId: Long): User
}