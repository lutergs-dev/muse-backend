package dev.lutergs.muse.infra.repository.kafka.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer

class LongListSerializer(
  private val objectMapper: ObjectMapper = ObjectMapper()
): Serializer<List<Long>> {

  override fun serialize(topicName: String, data: List<Long>): ByteArray {
    return this.objectMapper.writeValueAsBytes(data)
  }
}