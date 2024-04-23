package dev.lutergs.muse.backend.infra.repository

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.repository.NowPlayingNotifier
import dev.lutergs.muse.backend.infra.repository.kafka.producer.WebsocketTopicProducer
import org.springframework.data.redis.core.StringRedisTemplate

class NowPlayingNotifierImpl(
  redisTemplate: StringRedisTemplate,
  private val websocketServerProducer: WebsocketTopicProducer
): NowPlayingNotifier {
  private val redisValueOp = redisTemplate.opsForValue()

  override fun notify(user: User) {
    this.findWebsocketServerOfConnectedFriends(user)
      .forEach { (key, value) ->
        this.websocketServerProducer.produce(
          socketServerName = key,
          nowPlaying = user.nowPlaying,
          notifyUserIds = value
        )
      }
  }

  private fun findWebsocketServerOfConnectedFriends(user: User): Map<String, List<Long>> {
    return user.info.friends.mapNotNull {
      this.findWebsocketServerNameOfConnectedUser(it)
    }.fold(mutableMapOf<String, MutableList<Long>>()) { resultMap, userServerPair ->
      val (websocketConnectedUserId, serverName) = userServerPair
      if (resultMap[serverName] == null) {
        resultMap[serverName] = mutableListOf(websocketConnectedUserId)
      } else {
        resultMap[serverName]?.add(websocketConnectedUserId)
      }
      resultMap
    }
  }

  private fun findWebsocketServerNameOfConnectedUser(userId: Long): Pair<Long, String>? {
    return this.redisValueOp.get(userId.toString())?.let { userId to it }
  }
}