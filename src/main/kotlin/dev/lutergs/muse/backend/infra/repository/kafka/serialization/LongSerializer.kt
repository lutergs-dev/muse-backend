package dev.lutergs.muse.backend.infra.repository.kafka.serialization

import org.apache.kafka.common.serialization.Serializer

class LongSerializer: Serializer<Long> {
  override fun serialize(topic: String, data: Long): ByteArray {
    return data.toString()
//      .also { println("before: $it") }
      .toByteArray(Charsets.UTF_8)
//      .also { println("final : ${it.toString(Charsets.UTF_8)}") }
  }
}