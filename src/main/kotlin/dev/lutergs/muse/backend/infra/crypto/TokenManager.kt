package dev.lutergs.muse.backend.infra.crypto

import dev.lutergs.muse.backend.infra.config.properties.WebConfigProperties
import dev.lutergs.muse.backend.util.generateRandomString
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwe
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration

class TokenManager(
  private val jwtManager: JwtManager,
  private val redisTemplate: StringRedisTemplate,
  private val webConfigProperties: WebConfigProperties
) {
  private val idKey = "${this.webConfigProperties.url}/user"
  private val accessTokenName = "ACCESS_TOKEN"
  private val refreshTokenName = "REFRESH_TOKEN"
  private val redisUserKey = "USER_TOKEN"

  private val logger = LoggerFactory.getLogger(TokenManager::class.java)

  fun issueTokens(userId: Long): Map<String, String> {
    return mapOf(
      this.accessTokenName to this.issueAccessToken(userId),
      this.refreshTokenName to this.issueRefreshToken(userId)
    )
  }

  private fun issueAccessToken(userId: Long): String {
    val data = mapOf(
      "type" to this.accessTokenName,
      this.idKey to userId.toString()
    )
    return this.jwtManager.createToken(
      data,
      Duration.ofHours(this.webConfigProperties.tokenExpire.accessToken.toLong())
    )
  }

  private fun issueRefreshToken(userId: Long): String {
    val password = generateRandomString(length = 10)
    val data = mapOf(
      "type" to this.refreshTokenName,
      "password" to password,
      this.idKey to userId.toString()
    )

    this.redisTemplate.opsForHash<String, String>()
      .put(this.redisUserKey, userId.toString(), password)

    return this.jwtManager.createToken(
      data,
      Duration.ofHours(this.webConfigProperties.tokenExpire.refreshToken.toLong())
    )
  }

  fun getUserIdFromAccessToken(token: String): Long? {
    val claims = this.jwtManager.retrieveDataFromToken(token)
    return if (this.isValidToken(claims, this.accessTokenName)) {
      (claims.payload[this.idKey] as String).toLong()
    } else {
      null
    }
  }

  fun getUserIdFromRefreshToken(token: String): Long? {
    val claims = this.jwtManager.retrieveDataFromToken(token)
    return if (this.isValidToken(claims, this.refreshTokenName)) {
      val userId = claims.payload[this.idKey] as String

      // redis 의 refresh token 값과도 비교
      val redisPassword = this.redisTemplate.opsForHash<String, String>()
        .get(this.redisUserKey, userId)
      val tokenPassword = claims.payload["password"] as String

      if (redisPassword == tokenPassword) {
        userId.toLong()
      } else {
        null
      }
    } else {
      null
    }
  }

  private fun isValidToken(claims: Jwe<Claims>, tokenType: String): Boolean {
    if (claims.payload["type"] as String == tokenType) {
      try {
        this.jwtManager.validateTokenByTime(claims)
        return true
      } catch (e: IllegalStateException) {
        this.logger.error("userId ${claims.payload[this.idKey]} 의 $tokenType 이 expired 되었습니다!")
        return false
      }
    } else {
      this.logger.error("올바른 token 이 아닙니다! ${claims.payload}")
      return false
    }
  }
}