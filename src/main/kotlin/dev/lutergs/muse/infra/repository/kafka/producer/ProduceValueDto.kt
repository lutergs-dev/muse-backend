package dev.lutergs.muse.infra.repository.kafka.producer

data class ProduceValueDto (
  val userIds: List<Long>,
  val friendId: Long
)