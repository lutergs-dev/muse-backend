package dev.lutergs.muse.infra.repository.kafka.producer

import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate

class KafkaStreamsTopicProducer(
  private val kafkaTemplate: KafkaTemplate<Long, NowPlaying>,
  private val topicName: String
) {
  fun produce(userId: Long, nowPlaying: NowPlaying) {
    this.kafkaTemplate.send(ProducerRecord(this.topicName, userId, nowPlaying))
      .get()
  }
}