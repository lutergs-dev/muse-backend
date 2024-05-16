package dev.lutergs.muse.infra.crypto

import dev.lutergs.muse.infra.config.properties.WebConfigProperties
import dev.lutergs.muse.util.generateRandomString
import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate

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
      this.webConfigProperties.tokenExpire.accessTokenExpireDuration
    )
  }

  private fun issueRefreshToken(userId: Long): String {
    val password = generateRandomString(length = 10)
    val data = mapOf(
      "type" to this.refreshTokenName,
      "password" to password,
      this.idKey to userId.toString()
    )

    this.logger.info("generating refresh token... password : $password")

    this.redisTemplate.opsForHash<String, String>()
      .put(this.redisUserKey, userId.toString(), password)

    return this.jwtManager.createToken(
      data,
      this.webConfigProperties.tokenExpire.refreshTokenExpireDuration
    )
  }

  fun getUserIdFromAccessToken(token: String): Long? {
    return runCatching { this.jwtManager.retrieveDataFromToken(token) }
      .mapCatching { it.payload[this.idKey] as String }
      .onFailure {
        if (it is ExpiredJwtException) {
          this.logger.warn("[Access Token] 만료된 token 입니다.")
        }
      }
      .getOrNull()
      ?.toLong()
  }

  fun getUserIdFromRefreshToken(token: String): Long? {
    return runCatching { this.jwtManager.retrieveDataFromToken(token) }
      .mapCatching { claims ->
        val userId = claims.payload[this.idKey] as String

        // redis 의 refresh token 값과도 비교
        val redisPassword = this.redisTemplate.opsForHash<String, String>()
          .get(this.redisUserKey, userId)
        val tokenPassword = claims.payload["password"] as String

        if (redisPassword == tokenPassword) {
          userId.toLong()
        } else {
          throw IllegalStateException("User $userId 의 refresh token 값이 일치하지 않습니다. redis : $redisPassword, token : $tokenPassword")
        }
      }
      .onFailure {
        when (it) {
          is ExpiredJwtException -> this.logger.warn("[Refresh Token] token 이 만료되었습니다.")
          is IllegalStateException -> this.logger.warn("[Refresh Token] ${it.localizedMessage}")
          else -> this.logger.warn("[Refresh Token] token 검증 시 에러가 발생했습니다. ${it.javaClass.name} | ${it.localizedMessage}")
        }
      }
      .getOrNull()
  }
}