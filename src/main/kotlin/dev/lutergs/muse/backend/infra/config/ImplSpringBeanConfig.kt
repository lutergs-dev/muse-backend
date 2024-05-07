package dev.lutergs.muse.backend.infra.config

import dev.lutergs.muse.backend.domain.repository.UserInfoRepository
import dev.lutergs.muse.backend.infra.repository.NowPlayingNotifierImpl
import dev.lutergs.muse.backend.infra.repository.UserInfoRepositoryImpl
import dev.lutergs.muse.backend.infra.repository.kafka.producer.KafkaStreamsTopicProducer
import dev.lutergs.muse.backend.infra.repository.kafka.producer.WebsocketTopicProducer
import dev.lutergs.muse.backend.infra.repository.kafka.streams.KafkaStreamsQueryService
import dev.lutergs.muse.backend.infra.repository.rdb.DbEntityConvertService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class ImplSpringBeanConfig {

  @Bean
  fun nowPlayingNotifierImpl(
    redisTemplate: StringRedisTemplate,
    websocketServerProducer: WebsocketTopicProducer
  ): NowPlayingNotifierImpl = NowPlayingNotifierImpl(
    redisTemplate = redisTemplate,
    websocketServerProducer = websocketServerProducer
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