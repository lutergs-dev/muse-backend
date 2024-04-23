package dev.lutergs.muse.backend.infra.repository.kafka.streams

import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.backend.infra.repository.kafka.streams.external.KafkaStreamsExternalRepository
import dev.lutergs.muse.backend.infra.repository.kafka.streams.internal.KafkaStreamsInternalRepository
import kotlinx.coroutines.*

class KafkaStreamsQueryService(
  private val external: KafkaStreamsExternalRepository,
  private val internal: KafkaStreamsInternalRepository
) {

  fun getUserNowPlayingFromInternalAndExternal(userId: Long): NowPlaying? {
    return runBlocking {
      val externalJob = async(Dispatchers.IO) {
        external.getUserNowPlaying(userId)
      }
      val internalJob = async(Dispatchers.IO) {
        internal.getUserNowPlaying(userId)
      }
      externalJob.await() to internalJob.await()
    }.let {
      when {
        it.first == null && it.second == null -> null
        it.first != null && it.second == null -> it.first
        it.first == null && it.second != null -> it.second
        it.first != null && it.second != null -> it.toList().maxBy { d -> d!!.timestamp }
        else -> throw RuntimeException("도달할 수 없는 상태입니다.")
      }
    }
  }
}