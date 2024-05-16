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
  val userNowPlayingStoreName: String
)

data class KafkaStreamsTimeConfig(
  val pauseTimeoutSecond: Long,
  val playingTimeoutSecond: Long,
  val scanFrequencySecond: Long
) {
  val pauseTimeout: Duration = Duration.ofSeconds(this.pauseTimeoutSecond)
  val playingTimeout: Duration = Duration.ofSeconds(this.playingTimeoutSecond)
  val scanFrequency: Duration = Duration.ofSeconds(this.scanFrequencySecond)
}