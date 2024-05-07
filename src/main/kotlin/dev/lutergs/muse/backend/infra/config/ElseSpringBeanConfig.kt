package dev.lutergs.muse.backend.infra.config

import dev.lutergs.muse.backend.infra.config.properties.WebConfigProperties
import dev.lutergs.muse.backend.infra.crypto.DefaultKeyPair
import dev.lutergs.muse.backend.infra.crypto.JwtManager
import dev.lutergs.muse.backend.infra.crypto.TokenManager
import dev.lutergs.muse.backend.infra.repository.rdb.DbEntityConvertService
import dev.lutergs.muse.backend.infra.repository.rdb.DbUserEntityRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
@EnableConfigurationProperties(value = [
  WebConfigProperties::class
])
class ElseSpringBeanConfig(
  private val webConfigProperties: WebConfigProperties
) {

  @Bean
  fun keyPair(): DefaultKeyPair = DefaultKeyPair(
    publicKeyPath = this.webConfigProperties.keyLocation.public,
    privateKeyPath = this.webConfigProperties.keyLocation.private
  )

  @Bean
  fun jwtManager(
    keyPair: DefaultKeyPair
  ): JwtManager = JwtManager(
    defaultKeyPair = keyPair
  )

  @Bean
  fun tokenManager(
    jwtManager: JwtManager,
    redisTemplate: StringRedisTemplate
  ): TokenManager = TokenManager(
    jwtManager = jwtManager,
    redisTemplate = redisTemplate,
    webConfigProperties = this.webConfigProperties
  )

  @Bean
  fun dbEntityConvertService(
    repository: DbUserEntityRepository
  ): DbEntityConvertService = DbEntityConvertService(
    repository = repository
  )
}