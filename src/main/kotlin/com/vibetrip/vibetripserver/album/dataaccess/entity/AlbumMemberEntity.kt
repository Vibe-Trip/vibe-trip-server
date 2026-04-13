package com.vibetrip.vibetripserver.album.dataaccess.entity

import com.vibetrip.vibetripserver.album.domain.AlbumMember
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "album_member")
@Entity
class AlbumMemberEntity(
    @Column(nullable = false)
    var memberKey: String,
    @Column(nullable = false)
    var albumId: Long,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_member_id")
    var id: Long? = null,
) : BaseEntity() {
    fun toDomain() =
        AlbumMember(
            albumId = albumId,
            memberKey = memberKey,
        )
}
