package dev.lutergs.muse.infra.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom.kafka.http.url")
data class KafkaHttpUrlConfigProperties (
  val protocolType: ProtocolType,
  val hostName: String,
  val serviceName: String,
  val namespace: String,
  val port: Int
) {

  fun buildUrl(): String {
    return if (this.hostName == "localhost") {
      "${this.protocolType.name.lowercase()}://localhost:${this.port}"
    } else{
      "${this.protocolType.name.lowercase()}://${this.hostName}.${this.serviceName}.${this.namespace}.svc.cluster.local:${this.port}"
    }
  }
}


enum class ProtocolType {
  HTTP, HTTPS
}