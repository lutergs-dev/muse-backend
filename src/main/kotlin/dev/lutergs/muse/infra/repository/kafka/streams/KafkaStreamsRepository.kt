package dev.lutergs.muse.infra.repository.kafka.streams

import dev.lutergs.muse.domain.entity.userInfo.NowPlaying

interface KafkaStreamsRepository {
  fun getUserNowPlaying(userId: Long): NowPlaying?
}