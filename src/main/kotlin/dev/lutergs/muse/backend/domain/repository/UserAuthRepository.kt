package dev.lutergs.muse.backend.domain.repository

import dev.lutergs.muse.backend.domain.entity.User

interface UserAuthRepository {
  fun getUserFromToken(token: String): User?
  fun getTokenFromUser(user: User): String
}