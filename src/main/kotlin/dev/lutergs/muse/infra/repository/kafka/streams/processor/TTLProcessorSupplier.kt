package dev.lutergs.muse.infra.repository.kafka.streams.processor

import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.service.UserNowPlayingService
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import java.time.Duration

class TTLProcessorSupplier(
  private val pausedTimeout: Duration,
  private val playingTimeout: Duration,
  private val scanFrequency: Duration,
  private val userTrackStoreName: String,
  private val userNowPlayingService: UserNowPlayingService
): ProcessorSupplier<Long, NowPlaying?, Long, NowPlaying?> {
  override fun get(
  ): Processor<Long, NowPlaying?, Long, NowPlaying?> {
    return TTLProcessor(this.pausedTimeout, this.playingTimeout, this.scanFrequency, this.userTrackStoreName, this.userNowPlayingService)
  }
}