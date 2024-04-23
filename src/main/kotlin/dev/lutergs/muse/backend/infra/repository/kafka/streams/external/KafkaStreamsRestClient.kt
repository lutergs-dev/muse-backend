package dev.lutergs.muse.backend.infra.repository.kafka.streams.external

import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.backend.infra.config.properties.HttpUrlConfigProperties
import dev.lutergs.muse.backend.infra.config.properties.KafkaStreamsConfigProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.client.RestClient

class KafkaStreamsRestClient(
  private val httpUrlConfigs: HttpUrlConfigProperties,
  private val kafkaStreamsConfigProperties: KafkaStreamsConfigProperties,
  private val redisClient: StringRedisTemplate
) {

  private val restClient: RestClient = RestClient.create()

  fun getUserTrackAndLastStatus(userId: Long): NowPlaying? {
    return runBlocking {
      getOtherClients()
        .map { otherHost ->
          async(Dispatchers.IO) {
            requestToOtherClient(otherHost)
          }
        }.mapNotNull { it.await() }
        .let { lists -> if (lists.isEmpty()) null else lists.maxBy { it.timestamp } }
    }
  }

  private fun requestToOtherClient(host: String): NowPlaying? {
    return this.restClient.get().uri { it.host(host).build() }
      .retrieve()
      .body(NowPlaying::class.java)
  }

  private fun getOtherClients(): List<String> {
    return this.redisClient.opsForHash<String, String>().entries(this.kafkaStreamsConfigProperties.currentMachineKey)
      .filterNot { it.key == this.httpUrlConfigs.hostName }
      .map { it.value }
  }
}