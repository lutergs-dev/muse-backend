package dev.lutergs.muse.backend.infra.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "custom.web")
data class WebConfigProperties(
  val protocolType: ProtocolType,
  val domain: String,
  @NestedConfigurationProperty val keyLocation: WebCryptKeyLocationConfig,
  @NestedConfigurationProperty val tokenExpire: WebTokenConfig
) {
  val url = "${this.protocolType.name.lowercase()}://${this.domain}"
}


data class WebCryptKeyLocationConfig(
  val public: String,
  val private: String
)

data class WebTokenConfig(
  val accessToken: Int,
  val refreshToken: Int
)