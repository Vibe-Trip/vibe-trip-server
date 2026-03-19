package com.vibetrip.vibetripserver.album.dataaccess.entity

import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "album")
class AlbumEntity(
    @Column(nullable = false)
    var memberKey: String,

    @Column(nullable = false, length = 15)
    var title: String,

    @Column(length = 255)
    var coverImageUrl: String,

    @Column(nullable = false, length = 15)
    var region: String,

    @Column(columnDefinition = "TEXT", length = 500)
    var comment: String?,

    @Column(nullable = false)
    var travelStartDate: LocalDate,

    @Column(nullable = false)
    var travelEndDate: LocalDate,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    var id: Long? = null
) : BaseEntity(){
    companion object{
        fun from(
            memberKey: String,
            title: String,
            coverImageUrl: String,
            region: String,
            comment: String,
            travelStartDate: LocalDate,
            travelEndDate: LocalDate,
        ) = AlbumEntity(
            memberKey = memberKey,
            title = title,
            coverImageUrl = coverImageUrl,
            region = region,
            comment = comment,
            travelStartDate = travelStartDate,
            travelEndDate = travelEndDate,
        )
    }

    fun toDomain() = Album(
        id = id,
        memberKey = memberKey,
        title = title,
        coverImageUrl = coverImageUrl,
        region = Region(region),
        comment = comment?.let { Comment(it) },
        travelStartDate = travelStartDate,
        travelEndDate = travelEndDate,
    )
}