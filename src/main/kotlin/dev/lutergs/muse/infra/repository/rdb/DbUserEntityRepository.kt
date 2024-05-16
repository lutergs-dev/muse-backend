package dev.lutergs.muse.infra.repository.rdb

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DbUserEntityRepository: JpaRepository<DbUserEntity, Long> {
  fun findByVendorAndVendorUid(vendor: String, vendorUid: String): DbUserEntity?
  fun findByName(name: String): DbUserEntity?
}