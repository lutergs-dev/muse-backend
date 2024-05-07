package dev.lutergs.muse.backend.infra.crypto

import java.io.File
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

class DefaultKeyPair(
  publicKeyPath: String,
  privateKeyPath: String
) {
  val default: KeyPair

  init {
    this.default = KeyPair(this.readPublicKey(publicKeyPath), this.readPrivateKey(privateKeyPath))
  }

  private fun readPublicKey(filePath: String): PublicKey {
    return File(filePath).readText().replace("\\n".toRegex(), "")
      .replace("-----BEGIN PUBLIC KEY-----", "")
      .replace("-----END PUBLIC KEY-----", "")
      .trim()
      .let { Base64.getDecoder().decode(it) }
      .let { X509EncodedKeySpec(it) }
      .let { KeyFactory.getInstance("RSA").generatePublic(it) }
  }

  private fun readPrivateKey(filePath: String): PrivateKey {
    return File(filePath).readText().replace("\\n".toRegex(), "")
      .replace("-----BEGIN PRIVATE KEY-----", "")
      .replace("-----END PRIVATE KEY-----", "")
      .trim()
      .let { Base64.getDecoder().decode(it) }
      .let { PKCS8EncodedKeySpec(it) }
      .let { KeyFactory.getInstance("RSA").generatePrivate(it) }
  }
}