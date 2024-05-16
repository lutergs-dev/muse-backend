package dev.lutergs.muse.infra.repository.kafka.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.muse.infra.repository.kafka.producer.ProduceValueDto
import org.apache.kafka.common.serialization.Serializer

class ProduceValueDtoSerializer: Serializer<ProduceValueDto> {
  private val objectMapper = ObjectMapper()

  override fun serialize(topic: String, data: ProduceValueDto): ByteArray {
    return this.objectMapper.writeValueAsBytes(data)
  }
}