package dev.lutergs.muse.infra.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.time.Duration

@ConfigurationProperties(prefix = "custom.web")
data class WebConfigProperties(
  val protocolType: ProtocolType,
  val domain: String,
  @NestedConfigurationProperty val keyLocation: WebCryptKeyLocationConfig,
  @NestedConfigurationProperty val tokenExpire: WebTokenExpireSecondConfig
) {
  val url = "${this.protocolType.name.lowercase()}://${this.domain}"
}


data class WebCryptKeyLocationConfig(
  val public: String,
  val private: String
)

data class WebTokenExpireSecondConfig(
  val accessToken: Int,
  val refreshToken: Int
) {
  val accessTokenExpireDuration: Duration = Duration.ofSeconds(this.accessToken.toLong())
  val refreshTokenExpireDuration: Duration = Duration.ofSeconds(this.refreshToken.toLong())
}