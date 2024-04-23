package dev.lutergs.muse.backend.infra.crypto

import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

class RsaCrypter(
  private val keyPair: KeyPair
) {
  private val defaultEncodeCharset = Charsets.UTF_8
  private val base64Encoder = Base64.getEncoder()
  private val base64Decoder = Base64.getDecoder()

  fun encryptString(original: String): String {
    return Cipher.getInstance("RSA")
      .apply { this.init(Cipher.ENCRYPT_MODE, keyPair.publicKey) }
      .doFinal(original.toByteArray(this.defaultEncodeCharset))
      .let { this.base64Encoder.encodeToString(it) }
  }

  fun decryptString(encrypted: String): String {
    return Cipher.getInstance("RSA")
      .apply { this.init(Cipher.DECRYPT_MODE, keyPair.privateKey) }
      .doFinal(this.base64Decoder.decode(encrypted))
      .let { String(it) }
  }
}