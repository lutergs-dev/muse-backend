package dev.lutergs.muse.backend.infra.repository.kafka.serialization

import org.apache.kafka.common.serialization.Deserializer

class LongDeserializer: Deserializer<Long> {
  override fun deserialize(topic: String, data: ByteArray): Long {
    return data.toString(Charsets.UTF_8).toLong()
  }
}

/*

{
  "track": {
    "vendor": "APPLE",
    "uid": "1234"
  },
  "status": "PLAYING",
  "timestamp": 1713605712
}


**/