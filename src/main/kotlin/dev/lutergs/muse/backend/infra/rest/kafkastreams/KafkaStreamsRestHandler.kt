package dev.lutergs.muse.backend.infra.rest.kafkastreams

import dev.lutergs.muse.backend.infra.repository.kafka.streams.internal.KafkaStreamsInternalRepository
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class KafkaStreamsRestHandler(
  private val key: String,
  private val kafkaStreamsInternalRepository: KafkaStreamsInternalRepository
) {

  fun getNowPlaying(request: ServerRequest): ServerResponse {
    return if (this.isValidRequest(request)) {
      request.pathVariable("userId").toLong()
        .let { this.kafkaStreamsInternalRepository.getUserNowPlaying(it) }
        ?.let { ServerResponse.ok().body(it) }
        ?: ServerResponse.notFound().build()
    } else {
      ServerResponse.status(403).build()
    }
  }

  private fun isValidRequest(request: ServerRequest): Boolean {
    return request.headers().header("key").first() == this.key
  }
}