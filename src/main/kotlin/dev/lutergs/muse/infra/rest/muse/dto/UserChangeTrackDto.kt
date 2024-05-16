package dev.lutergs.muse.infra.rest.muse.dto

import dev.lutergs.muse.domain.entity.track.Track

data class UserChangeTrackDto (
  val track: Track,
  val playbackStatus: String
)

data class UserLoginDto(
  val type: String,
  val uid: String
)

data class UserChangeNickNameDto (
  val name: String
)

data class UserChangeTrackPlayStatusDto (
  val status: String
)