package dev.lutergs.muse.backend.infra.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom.crypto.key.path")
data class CryptoConfigProperties (
  val public: String,
  val private: String
)