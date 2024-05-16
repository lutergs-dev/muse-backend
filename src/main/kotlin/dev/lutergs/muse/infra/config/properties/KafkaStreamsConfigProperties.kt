package dev.lutergs.muse.infra.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.time.Duration

@ConfigurationProperties(prefix = "custom.kafka.streams")
class KafkaStreamsConfigProperties (
  val communicateKey: String,
  currentMachineKey: String,
  val inputTopicName: String,
  @NestedConfigurationProperty val store: KafkaStreamsStoreConfig,
  @NestedConfigurationProperty val time: KafkaStreamsTimeConfig
) {
  val currentMachineKey = currentMachineKey.takeLast(10)
}

data class KafkaStreamsStoreConfig(
  val ttlStoreName: String,
  val userNowPlayingStoreName: String
)

data class KafkaStreamsTimeConfig(
  val stopTimeoutSecond: Long,
  val scanFrequencySecond: Long
) {
  val stopTimeout: Duration = Duration.ofSeconds(this.stopTimeoutSecond)
  val scanFrequency: Duration = Duration.ofSeconds(this.scanFrequencySecond)
}