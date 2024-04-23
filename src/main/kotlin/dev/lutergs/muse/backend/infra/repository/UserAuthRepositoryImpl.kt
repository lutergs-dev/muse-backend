package dev.lutergs.muse.backend.infra.repository

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.repository.UserAuthRepository
import dev.lutergs.muse.backend.domain.repository.UserInfoRepository
import dev.lutergs.muse.backend.infra.crypto.RsaCrypter

class UserAuthRepositoryImpl(
  private val rsaCrypter: RsaCrypter,
  private val userInfoRepository: UserInfoRepository
): UserAuthRepository {
  override fun getUserFromToken(token: String): User? {
    return this.rsaCrypter.decryptString(token).toLong()
      .let { this.userInfoRepository.getUser(it) }
  }

  override fun getTokenFromUser(user: User): String {
    if (user.id == null) throw RuntimeException("user 가 persisted 되지 않았습니다.")
    return this.rsaCrypter.encryptString(user.id.toString())
  }
}