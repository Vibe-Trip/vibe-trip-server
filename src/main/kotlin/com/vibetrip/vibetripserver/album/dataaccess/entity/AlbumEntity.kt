package com.vibetrip.vibetripserver.album.dataaccess.entity

import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.album.domain.EditAlbum
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDate

@Entity
@Table(name = "album")
class AlbumEntity(
    @Column(nullable = false)
    var memberKey: String,
    @Column(nullable = false, length = 15)
    var title: String = "",
    @Column(nullable = false, columnDefinition = "TEXT")
    var coverImageUrl: String = "",
    @Column(nullable = false, length = 20)
    var region: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var comment: String = "",
    @Column(nullable = false)
    var travelStartDate: LocalDate,
    @Column(nullable = false)
    var travelEndDate: LocalDate,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    var id: Long? = null,
    @Version
    var version: Long = 0,
) : BaseEntity() {
    companion object {
        fun from(
            newAlbum: NewAlbum,
            coverImageUrl: String,
        ) = AlbumEntity(
            memberKey = newAlbum.memberKey,
            coverImageUrl = coverImageUrl,
            region = newAlbum.region.value,
            comment = newAlbum.comment.value,
            travelStartDate = newAlbum.travelDate.startDate,
            travelEndDate = newAlbum.travelDate.endDate,
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

    fun updateAlbum(
        editAlbum: EditAlbum,
        coverImageUrl: String,
    ) {
        region = editAlbum.region.value
        comment = editAlbum.comment.value
        travelStartDate = editAlbum.travelDate.startDate
        travelEndDate = editAlbum.travelDate.endDate
        this.coverImageUrl = coverImageUrl
    }
}
