package dev.lutergs.muse.infra.repository

import dev.lutergs.muse.domain.entity.User
import dev.lutergs.muse.domain.repository.NowPlayingNotifier
import dev.lutergs.muse.infra.repository.kafka.producer.WebsocketTopicProducer
import dev.lutergs.muse.infra.repository.rdb.DbUserRelationEntityRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate

class NowPlayingNotifierImpl(
  redisTemplate: StringRedisTemplate,
  private val dbUserRelationEntityRepository: DbUserRelationEntityRepository,
  private val websocketServerProducer: WebsocketTopicProducer
): NowPlayingNotifier {
  private val redisValueOp = redisTemplate.opsForValue()
  private val logger = LoggerFactory.getLogger(NowPlayingNotifierImpl::class.java)

  override fun notify(user: User) {
    this.dbUserRelationEntityRepository.findAllByFriendId(user.id!!)
      .mapNotNull { this.findWebsocketServerNameOfConnectedUser(it.user!!.id!!) }
      .fold(mutableMapOf<String, MutableList<Long>>()) { map, pair ->
        val (websocketConnectedUserId, serverName) = pair
        if (map[serverName] == null) {
          map[serverName] = mutableListOf(websocketConnectedUserId)
        } else {
          map[serverName]?.add(websocketConnectedUserId)
        }
        map
      }.forEach { (key, value) ->
        this.logger.debug("user {}'s friend is connected to.. {}, {}", user.id, key, value)
        this.websocketServerProducer.produce(
          socketServerName = key,
          nowPlaying = user.nowPlaying,
          userId = user.id,
          notifyUserIds = value
        )
      }
  }

  private fun findWebsocketServerNameOfConnectedUser(userId: Long): Pair<Long, String>? {
    return this.redisValueOp.get(userId.toString())?.let { userId to it }
  }
}