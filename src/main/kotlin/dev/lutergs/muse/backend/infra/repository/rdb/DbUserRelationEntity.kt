package dev.lutergs.muse.backend.infra.repository.rdb

import jakarta.persistence.*

@Entity
@Table(name = "MUSE_USER_RELATION")
class DbUserRelationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MUSE_USER_RELATION_SEQ_GENERATOR")
  @SequenceGenerator(name = "MUSE_USER_RELATION_SEQ_GENERATOR", sequenceName = "MUSE_USER_RELATION_SEQ", allocationSize = 1)
  @Column(name = "ID")
  var id: Long? = null

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID", nullable = false)
  var user: DbUserEntity? = null

  @Column(name = "FRIEND_ID", nullable = false)
  var friendId: Long = 0
}