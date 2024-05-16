package dev.lutergs.muse.infra.repository.kafka.streams.processor

import dev.lutergs.muse.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.domain.entity.track.Track
import dev.lutergs.muse.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.service.UserNowPlayingService
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import java.time.Duration


class TTLProcessor(
  private val pausedTimeout: Duration,
  private val playingTimeout: Duration,
  private val scanFrequency: Duration,
  private val userTrackStoreName: String,
  private val userNowPlayingService: UserNowPlayingService
): Processor<Long, NowPlaying?, Long, NowPlaying?> {
  private lateinit var context: ProcessorContext<Long, NowPlaying?>
  private lateinit var userTrackStateStore: KeyValueStore<Long, NowPlaying>

  override fun init(context: ProcessorContext<Long, NowPlaying?>) {
    this.context = context
    this.userTrackStateStore = this.context.getStateStore(this.userTrackStoreName)

    this.context.schedule(this.scanFrequency, PunctuationType.WALL_CLOCK_TIME) { timestamp ->
      val pausedCutoff = timestamp - this.pausedTimeout.toMillis()
      val playingCutoff = timestamp - this.playingTimeout.toMillis()

      this.userTrackStateStore.all().use { all ->
        all.forEachRemaining { kv ->
          when (kv.value.status) {
            PlaybackStatus.PAUSED -> {
              if (kv.value.timestamp < pausedCutoff) {
                this.userNowPlayingService.changeUserTrack(kv.key, Track.nullTrack(), PlaybackStatus.STOPPED)
              }
            }
            PlaybackStatus.PLAYING -> {
              if (kv.value.timestamp < playingCutoff) {
                this.userNowPlayingService.changeUserTrack(kv.key, Track.nullTrack(), PlaybackStatus.STOPPED)
              }
            }
            PlaybackStatus.STOPPED -> {/* DO NOTHING WHEN STOPPED */}
          }
        }
      }
    }
  }

  override fun process(record: Record<Long, NowPlaying?>) {
    if (record.value() != null) {
      this.userTrackStateStore.put(record.key(), record.value())
    }
  }

}