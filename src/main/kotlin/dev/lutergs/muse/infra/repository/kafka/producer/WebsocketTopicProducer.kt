package dev.lutergs.muse.infra.repository.kafka.producer

import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate

class WebsocketTopicProducer(
  private val kafkaTemplate: KafkaTemplate<NowPlaying, ProduceValueDto>
) {

  fun produce(socketServerName: String, nowPlaying: NowPlaying, userId: Long, notifyUserIds: List<Long>) {
    val value = ProduceValueDto(
      userIds = notifyUserIds,
      friendId = userId
    )
    this.kafkaTemplate.send(ProducerRecord(socketServerName, nowPlaying, value))
  }
}