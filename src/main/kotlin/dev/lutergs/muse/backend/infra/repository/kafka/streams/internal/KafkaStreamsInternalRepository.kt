package dev.lutergs.muse.backend.infra.repository.kafka.streams.internal

import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.backend.infra.repository.kafka.streams.KafkaStreamsRepository
import org.apache.kafka.streams.errors.InvalidStateStoreException
import org.slf4j.LoggerFactory

class KafkaStreamsInternalRepository(
  private val kafkaStreamsStateStore: KafkaStreamsStateStore
): KafkaStreamsRepository {
  private val logger = LoggerFactory.getLogger(this::class.java)

  override fun getUserNowPlaying(userId: Long): NowPlaying? {
    return try {
      this.kafkaStreamsStateStore.userTrackStore.get(userId)
    } catch (e: NullPointerException) {
      null
    } catch (e: InvalidStateStoreException) {
      this.logger.warn("Statestore is initialized and proceed to RUNNING mode...")
      // statestore is initiating...
      null
    }
  }
}