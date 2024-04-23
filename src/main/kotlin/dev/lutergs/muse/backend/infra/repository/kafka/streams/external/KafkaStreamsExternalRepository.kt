package dev.lutergs.muse.backend.infra.repository.kafka.streams.external

import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.backend.infra.repository.kafka.streams.KafkaStreamsRepository

class KafkaStreamsExternalRepository(
  private val kafkaStreamsRestClient: KafkaStreamsRestClient
): KafkaStreamsRepository {

  override fun getUserNowPlaying(userId: Long): NowPlaying? {
    return this.kafkaStreamsRestClient.getUserTrackAndLastStatus(userId)
  }
}