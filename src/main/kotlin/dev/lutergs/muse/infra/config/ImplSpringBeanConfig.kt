package dev.lutergs.muse.infra.config

import dev.lutergs.muse.infra.repository.NowPlayingNotifierImpl
import dev.lutergs.muse.infra.repository.UserInfoRepositoryImpl
import dev.lutergs.muse.infra.repository.kafka.producer.KafkaStreamsTopicProducer
import dev.lutergs.muse.infra.repository.kafka.producer.WebsocketTopicProducer
import dev.lutergs.muse.infra.repository.kafka.streams.KafkaStreamsQueryService
import dev.lutergs.muse.infra.repository.rdb.DbEntityConvertService
import dev.lutergs.muse.infra.repository.rdb.DbUserRelationEntityRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class ImplSpringBeanConfig {

  @Bean
  fun nowPlayingNotifierImpl(
    redisTemplate: StringRedisTemplate,
    websocketServerProducer: WebsocketTopicProducer,
    dbUserRelationEntityRepository: DbUserRelationEntityRepository
  ): NowPlayingNotifierImpl = NowPlayingNotifierImpl(
    redisTemplate = redisTemplate,
    websocketServerProducer = websocketServerProducer,
    dbUserRelationEntityRepository =  dbUserRelationEntityRepository
  )

  @Bean
  fun userInfoRepositoryImpl(
    kafkaStreamsQueryService: KafkaStreamsQueryService,
    kafkaStreamsTopicProducer: KafkaStreamsTopicProducer,
    dbEntityConvertService: DbEntityConvertService
  ): UserInfoRepositoryImpl = UserInfoRepositoryImpl(
    kafkaStreamsQueryService = kafkaStreamsQueryService,
    kafkaStreamsTopicProducer = kafkaStreamsTopicProducer,
    dbEntityConvertService = dbEntityConvertService
  )
}