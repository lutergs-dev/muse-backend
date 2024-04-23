package dev.lutergs.muse.backend.infra.repository.rdb

import dev.lutergs.muse.backend.domain.entity.User
import jakarta.persistence.*


@Entity
@Table(name = "MUSE_USER")
class DbUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MUSE_USER_SEQ_GENERATOR")
  @SequenceGenerator(name = "MUSE_USER_SEQ_GENERATOR", sequenceName = "MUSE_USER_SEQ", allocationSize = 1)
  @Column(name = "ID")
  var id: Long? = null

  @Column(name = "NAME", nullable = true)
  var name: String? = null

  @Column(name = "AUTH_VENDOR", nullable = false)
  var vendor: String = ""

  @Column(name = "AUTH_UID", nullable = false)
  var vendorUid: String = ""

  @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
  var relations: MutableList<DbUserRelationEntity> = mutableListOf()

  fun updateValues(user: User) {
    this.name = user.info.name
    this.vendor = user.info.auth!!.vendor.toString()
    this.vendorUid = user.info.auth.id
  }
}