package dev.lutergs.muse.infra.repository.rdb

import dev.lutergs.muse.domain.entity.User
import dev.lutergs.muse.domain.entity.userInfo.auth.AuthInfo
import dev.lutergs.muse.util.generateRandomString
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull

class DbEntityConvertService(
  private val repository: DbUserEntityRepository
) {
  fun getDbEntitiesByPage(page: Int, size: Int): List<DbUserEntity> {
    val pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))
    return this.repository.findAll(pageRequest).toList()
  }

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
      val dbUser = DbUserEntity()
        .apply {
          this.name = user.info.name
          this.vendor = user.info.auth!!.vendor.name
          this.vendorUid = user.info.auth.id
        }
      // user nickname 이 DB 에 존재하지 않을 때까지 search
      while (true) {
        this.repository.findByName(dbUser.name)
          ?.run { dbUser.name = generateRandomString() }
          ?: break
      }
      this.repository.save(dbUser)
    }
  }

  fun addFriendFromDbEntity(user: User, friendId: Long): DbUserEntity {
    return this.repository.findByIdOrNull(user.id!!)
      ?.apply {
        val dbEntity = this
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