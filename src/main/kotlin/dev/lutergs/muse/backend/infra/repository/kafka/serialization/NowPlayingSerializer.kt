package dev.lutergs.muse.backend.infra.repository.kafka.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import org.apache.kafka.common.serialization.Serializer

class NowPlayingSerializer(
  private val objectMapper: ObjectMapper = ObjectMapper()
): Serializer<NowPlaying?> {

  override fun serialize(topicName: String, value: NowPlaying?): ByteArray {
    return if (value == null) byteArrayOf()
    else this.objectMapper.writeValueAsBytes(value)
  }
}