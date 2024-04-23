package dev.lutergs.muse.backend.infra.repository.kafka.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import org.apache.kafka.common.serialization.Deserializer

class NowPlayingDeserializer(
  private val objectMapper: ObjectMapper = ObjectMapper()
): Deserializer<NowPlaying?> {

  override fun deserialize(topicName: String, value: ByteArray): NowPlaying? {
    return if (value.isEmpty()) null
    else this.objectMapper.readValue(value, NowPlaying::class.java)
  }
}