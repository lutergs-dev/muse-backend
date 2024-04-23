package dev.lutergs.muse.backend.infra.crypto

import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

class KeyPair(
  publicKeyPath: String,
  privateKeyPath: String
) {
  val publicKey: PublicKey
  val privateKey: PrivateKey

  init {
    this.publicKey = this.readPublicKey(publicKeyPath)
    this.privateKey = this.readPrivateKey(privateKeyPath)
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