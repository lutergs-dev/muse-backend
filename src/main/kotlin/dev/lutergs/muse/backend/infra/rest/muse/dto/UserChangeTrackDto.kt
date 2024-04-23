package dev.lutergs.muse.backend.infra.rest.muse.dto

import dev.lutergs.muse.backend.domain.entity.track.Track
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthVendor

data class UserChangeTrackDto (
  val userId: Long,
  val track: Track
)

data class UserLoginDto(
  val type: String,
  val uid: String
)

data class UserChangeNickNameDto (
  val name: String
)