package dev.lutergs.muse.infra.repository.kafka.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.muse.domain.entity.track.MusicVendor
import dev.lutergs.muse.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.domain.entity.track.Track
import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import org.apache.kafka.common.serialization.Deserializer

class NowPlayingDeserializer: Deserializer<NowPlaying?> {
  private val objectMapper: ObjectMapper = ObjectMapper()

  override fun deserialize(topicName: String, value: ByteArray): NowPlaying? {
    return if (value.isEmpty()) null
    else this.objectMapper.readTree(value)
      .let { rootNode ->
        NowPlaying(
          track = rootNode.get("track").let { trackNode ->
            Track(
              vendor = MusicVendor.valueOf(trackNode.get("vendor").asText()),
              uid = trackNode.get("uid").asText()
            )
          },
          status = PlaybackStatus.valueOf(rootNode.get("status").asText()),
          timestamp = rootNode.get("timestamp").asLong()
        )
      }
  }
}