package dev.lutergs.muse.backend.infra.repository.kafka.producer

import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate

class WebsocketTopicProducer(
  private val kafkaTemplate: KafkaTemplate<NowPlaying, List<Long>>
) {

  fun produce(socketServerName: String, nowPlaying: NowPlaying, notifyUserIds: List<Long>) {
    this.kafkaTemplate.send(
      ProducerRecord(socketServerName, nowPlaying, notifyUserIds)
    )
  }
}