package dev.lutergs.muse.backend.domain.entity.userInfo.auth

import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64
import javax.crypto.Cipher

class AuthKey(
  val public: PublicKey,
  val private: PrivateKey
) {
  companion object {
    private val defaultEncode = Charsets.UTF_8
    private val keyGen = KeyPairGenerator.getInstance("RSA")
      .apply { this.initialize(2048) }

    fun createAuthKey(): AuthKey = keyGen.genKeyPair().let { AuthKey(it.public, it.private) }
  }

  fun encryptString(original: String): String {
    return Cipher.getInstance("RSA")
      .apply { this.init(Cipher.ENCRYPT_MODE, public) }
      .doFinal(original.toByteArray(defaultEncode))
      .let { Base64.getEncoder().encodeToString(it) }
  }
}