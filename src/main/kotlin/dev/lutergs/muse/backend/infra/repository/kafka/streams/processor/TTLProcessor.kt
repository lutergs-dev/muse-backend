package dev.lutergs.muse.backend.infra.repository.kafka.streams.processor

import dev.lutergs.muse.backend.domain.entity.track.PlaybackStatus
import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import java.time.Duration


class TTLProcessor(
  private val maxAge: Duration,
  private val scanFrequency: Duration,
  private val ttlStoreName: String,
  private val userTrackStoreName: String
): Processor<Long, NowPlaying?, Long, NowPlaying?> {
  private lateinit var context: ProcessorContext<Long, NowPlaying?>
  private lateinit var ttlStateStore: KeyValueStore<Long, Long>
  private lateinit var userTrackStateStore: KeyValueStore<Long, NowPlaying>

  override fun init(context: ProcessorContext<Long, NowPlaying?>) {
    this.context = context
    this.ttlStateStore = this.context.getStateStore(this.ttlStoreName)
    this.userTrackStateStore = this.context.getStateStore(this.userTrackStoreName)

    this.context.schedule(this.scanFrequency, PunctuationType.WALL_CLOCK_TIME) { timestamp ->
      val cutOff = timestamp - this.maxAge.toMillis()

      this.ttlStateStore.all().use { all ->
        all.forEachRemaining { kv ->
          if (kv.value != null && kv.value < cutOff) {
            this.context.forward(Record(
              kv.key,
              this.userTrackStateStore
                .get(kv.key)
                .setPlaybackStatus(PlaybackStatus.STOPPED),
              timestamp)
            )
          }
        }
      }
    }
  }

  override fun process(record: Record<Long, NowPlaying?>) {
    if (record.value() != null) {
      when (record.value()!!.status) {
        PlaybackStatus.PLAYING, PlaybackStatus.PAUSED -> {
          this.ttlStateStore.put(record.key(), record.timestamp())
        }
        PlaybackStatus.STOPPED -> {
          this.ttlStateStore.delete(record.key())
        }
      }
      this.userTrackStateStore.put(record.key(), record.value())
    }
  }

  override fun close() {
    super.close()
  }
}