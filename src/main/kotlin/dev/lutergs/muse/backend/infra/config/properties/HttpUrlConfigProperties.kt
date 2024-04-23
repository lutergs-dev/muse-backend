package dev.lutergs.muse.backend.infra.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom.http.url")
data class HttpUrlConfigProperties (
  val protocolType: ProtocolType,
  val hostName: String,
  val serviceName: String,
  val namespace: String
) {
  fun buildHttpUrl(port: Int): String {
    if (this.hostName == "localhost") {
      return "http://localhost:${port}"
    }

    return "${this.protocolType}://${this.hostName}.${this.serviceName}.${this.namespace}.svc.cluster.local:${port}"
  }
}


enum class ProtocolType {
  HTTP, HTTPS
}