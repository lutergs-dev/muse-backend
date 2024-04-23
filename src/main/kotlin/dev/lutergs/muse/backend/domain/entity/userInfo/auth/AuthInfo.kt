package dev.lutergs.muse.backend.domain.entity.userInfo.auth

data class AuthInfo (
  val vendor: AuthVendor,
  val id: String
) {
}