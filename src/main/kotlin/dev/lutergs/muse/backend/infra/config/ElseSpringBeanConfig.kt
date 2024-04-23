package dev.lutergs.muse.backend.infra.config

import dev.lutergs.muse.backend.infra.config.properties.CryptoConfigProperties
import dev.lutergs.muse.backend.infra.crypto.KeyPair
import dev.lutergs.muse.backend.infra.crypto.RsaCrypter
import dev.lutergs.muse.backend.infra.repository.rdb.DbEntityConvertService
import dev.lutergs.muse.backend.infra.repository.rdb.DbUserEntityRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [
  CryptoConfigProperties::class
])
class ElseSpringBeanConfig(
  private val cryptoConfigProperties: CryptoConfigProperties
) {

  @Bean
  fun keyPair(): KeyPair = KeyPair(
    publicKeyPath = this.cryptoConfigProperties.public,
    privateKeyPath = this.cryptoConfigProperties.private
  )

  @Bean
  fun rsaCrypter(
    keyPair: KeyPair
  ): RsaCrypter = RsaCrypter(
    keyPair = keyPair
  )

  @Bean
  fun dbEntityConvertService(
    repository: DbUserEntityRepository
  ): DbEntityConvertService = DbEntityConvertService(
    repository = repository
  )
}