package dev.lutergs.muse.backend.infra.repository

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.entity.userInfo.NowPlaying
import dev.lutergs.muse.backend.domain.entity.userInfo.UserInfo
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthInfo
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthVendor
import dev.lutergs.muse.backend.domain.repository.UserInfoRepository
import dev.lutergs.muse.backend.infra.repository.kafka.producer.KafkaStreamsTopicProducer
import dev.lutergs.muse.backend.infra.repository.kafka.streams.KafkaStreamsQueryService
import dev.lutergs.muse.backend.infra.repository.rdb.DbEntityConvertService
import dev.lutergs.muse.backend.infra.repository.rdb.DbUserEntity

class UserInfoRepositoryImpl(
  private val kafkaStreamsQueryService: KafkaStreamsQueryService,
  private val kafkaStreamsTopicProducer: KafkaStreamsTopicProducer,
  private val dbEntityConvertService: DbEntityConvertService
): UserInfoRepository {

  override fun getUser(id: Long): User? {
    return this.dbEntityConvertService.getDbEntity(id)
      ?.let { userInfo ->
        val nowPlaying = this.kafkaStreamsQueryService.getUserNowPlayingFromInternalAndExternal(id)
        this.toUser(userInfo, nowPlaying)
      }
  }

  override fun saveUser(user: User): User {
    println("this is saveUserFromRepository : $user")
    val dbEntity = this.dbEntityConvertService.saveDbEntity(user)
    this.kafkaStreamsTopicProducer.produce(dbEntity.id!!, user.nowPlaying)
    return this.toUser(dbEntity, user.nowPlaying)
  }

  override fun getUserByAuth(auth: AuthInfo): User? {
    return this.dbEntityConvertService.getDbEntityByAuth(auth)
      ?.let { userInfo ->
        val nowPlaying = this.kafkaStreamsQueryService.getUserNowPlayingFromInternalAndExternal(userInfo.id!!)
        this.toUser(userInfo, nowPlaying)
      }
  }

  override fun addFriend(user: User, friendId: Long): User {
    val dbEntity = this.dbEntityConvertService.addFriendFromDbEntity(user, friendId)
    return this.toUser(dbEntity, user.nowPlaying)
  }

  override fun removeFriend(user: User, friendId: Long): User {
    val dbEntity = this.dbEntityConvertService.removeFriendFromDbEntity(user, friendId)
    return this.toUser(dbEntity, user.nowPlaying)
  }

  private fun toUser(dbUserEntity: DbUserEntity, nowPlaying: NowPlaying?): User {
    return User(
      id = dbUserEntity.id,
      info = UserInfo(
        name = dbUserEntity.name,
        auth = AuthInfo(
          vendor = dbUserEntity.vendor.let { AuthVendor.valueOf(it) },
          id = dbUserEntity.vendorUid
        ),
        friends = dbUserEntity.relations.map { it.friendId }
      ),
      nowPlaying = nowPlaying ?: NowPlaying.empty()
    )
  }
}