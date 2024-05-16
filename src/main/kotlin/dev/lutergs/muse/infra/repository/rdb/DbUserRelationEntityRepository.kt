package dev.lutergs.muse.infra.repository.rdb

import org.springframework.data.jpa.repository.JpaRepository

interface DbUserRelationEntityRepository: JpaRepository<DbUserRelationEntity, Long> {
  fun findAllByFriendId(friendId: Long): List<DbUserRelationEntity>
}