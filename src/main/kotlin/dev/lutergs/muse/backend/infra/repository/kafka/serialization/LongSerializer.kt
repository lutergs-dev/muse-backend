package dev.lutergs.muse.backend.infra.repository.kafka.serialization

import org.apache.kafka.common.serialization.Serializer

class LongSerializer: Serializer<Long> {
  override fun serialize(topic: String, data: Long): ByteArray {
    return data.toString()
      .toByteArray(Charsets.UTF_8)
  }
}