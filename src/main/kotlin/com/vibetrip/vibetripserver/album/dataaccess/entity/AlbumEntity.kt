package com.vibetrip.vibetripserver.album.dataaccess.entity

import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "album")
class AlbumEntity(
    @Column(nullable = false)
    var memberKey: String,
    @Column(nullable = false)
    var title: String? = null,
    @Column(nullable = false)
    var coverImageUrl: String,
    @Column(nullable = false, length = 15)
    var region: String,
    @Column(columnDefinition = "TEXT")
    var comment: String?,
    @Column(nullable = false)
    var travelStartDate: LocalDate,
    @Column(nullable = false)
    var travelEndDate: LocalDate,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    var id: Long? = null,
) : BaseEntity() {
    companion object {
        fun from(newAlbum: NewAlbum) =
            AlbumEntity(
                memberKey = newAlbum.memberKey,
                region = newAlbum.regionValue,
                comment = newAlbum.commentValue,
                travelStartDate = newAlbum.travelStartDate,
                travelEndDate = newAlbum.travelEndDate,
                coverImageUrl = newAlbum.coverImageUrl,
            )
    }

    fun toDomain() =
        Album.of(
            albumId = id!!,
            memberKey = memberKey,
            title = title,
            coverImageUrl = coverImageUrl,
            region = region,
            comment = comment,
            travelStartDate = travelStartDate,
            travelEndDate = travelEndDate,
        )

    fun updateTitle(newTitle: String) {
        title = newTitle
    }
}