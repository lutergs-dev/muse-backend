package dev.lutergs.muse.backend.infra.repository.rdb

import dev.lutergs.muse.backend.domain.entity.User
import dev.lutergs.muse.backend.domain.entity.userInfo.auth.AuthInfo
import org.springframework.data.repository.findByIdOrNull

class DbEntityConvertService(
  private val repository: DbUserEntityRepository
) {
  fun getDbEntity(userId: Long): DbUserEntity? {
    return this.repository.findByIdOrNull(userId)
  }

  fun saveDbEntity(user: User): DbUserEntity {
    // 여기서 relation 을 업데이트하지 않음
    return if (user.id != null) {
      // user persists
      this.repository.findByIdOrNull(user.id)
        ?.apply { this.updateValues(user) }
        ?.let { this.repository.save(it) }
        ?: throw RuntimeException("user 가 존재하지 않습니다!")
    } else {
      DbUserEntity()
        .apply {
          this.name = user.info.name
          this.vendor = user.info.auth!!.vendor.name
          this.vendorUid = user.info.auth.id
        }
        .let { this.repository.save(it) }
    }
  }

  fun addFriendFromDbEntity(user: User, friendId: Long): DbUserEntity {
    return this.repository.findByIdOrNull(user.id!!)
      ?.apply {
        val dbEntity = this
        println("user ${this.id} ${this.name} will add friend ${friendId}")
        DbUserRelationEntity().apply {
          this.user = dbEntity
          this.friendId = friendId
        }.let { this.relations.add(it) }
      }
      ?.let { this.repository.save(it) }
      ?: throw RuntimeException("user 가 존재하지 않습니다!")
  }

  fun removeFriendFromDbEntity(user: User, friendId: Long): DbUserEntity {
    return this.repository.findByIdOrNull(user.id!!)
      ?.apply { this.relations.removeIf { it.friendId == friendId } }
      ?.let { this.repository.save(it) }
      ?: throw RuntimeException("user 가 존재하지 않습니다!")
  }

  fun getDbEntityByAuth(auth: AuthInfo): DbUserEntity? {
    return this.repository.findByVendorAndVendorUid(auth.vendor.name, auth.id)
  }
}