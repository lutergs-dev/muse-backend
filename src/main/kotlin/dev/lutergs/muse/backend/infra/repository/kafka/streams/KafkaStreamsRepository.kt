package dev.lutergs.muse.backend.infra.repository.kafka.streams

import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying

interface KafkaStreamsRepository {
  fun getUserNowPlaying(userId: Long): NowPlaying?
}