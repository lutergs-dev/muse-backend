package dev.lutergs.muse.infra.repository.kafka.streams.processor

import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import java.time.Duration

class TTLProcessorSupplier(
  private val maxAge: Duration,
  private val scanFrequency: Duration,
  private val ttlStoreName: String,
  private val userTrackStoreName: String
): ProcessorSupplier<Long, NowPlaying?, Long, NowPlaying?> {
  override fun get(
  ): Processor<Long, NowPlaying?, Long, NowPlaying?> {
    return TTLProcessor(this.maxAge, this.scanFrequency, this.ttlStoreName, this.userTrackStoreName)
  }
}