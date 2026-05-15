package com.vibetrip.vibetripserver.album.integration

import com.vibetrip.vibetripserver.album.business.AlbumService
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMemberEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate

class AlbumDeleteTest : SpringTest() {
    @MockitoBean
    lateinit var googleImageUploader: GoogleImageUploader

    @Autowired
    lateinit var albumService: AlbumService

    @Autowired
    lateinit var albumRepository: AlbumRepository

    @Autowired
    lateinit var albumMemberRepository: AlbumMemberRepository

    @Autowired
    lateinit var albumMusicRepository: AlbumMusicRepository

    @Autowired
    lateinit var albumLogRepository: AlbumLogRepository

    @Autowired
    lateinit var albumLogImageRepository: AlbumLogImageRepository

    private val memberKey = "test-member-key"

    @BeforeEach
    fun setUp() {
        albumLogImageRepository.deleteAll()
        albumLogRepository.deleteAll()
        albumMusicRepository.deleteAll()
        albumMemberRepository.deleteAll()
        albumRepository.deleteAll()
    }

    @Test
    fun `앨범 삭제 시 앨범이 Soft Delete된다`() {
        // given
        val album = albumRepository.save(createAlbumEntity(memberKey))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = album.id!!))

        // when
        albumService.deleteAlbum(album.id!!, memberKey)

        // then
        val deletedAlbum = albumRepository.findAll().first()
        assertThat(deletedAlbum.status).isEqualTo(EntityStatus.DELETED)
        assertThat(deletedAlbum.deletedAt).isNotNull()
    }

    @Test
    fun `앨범 삭제 시 앨범 멤버가 Soft Delete된다`() {
        // given
        val album = albumRepository.save(createAlbumEntity(memberKey))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = album.id!!))

        // when
        albumService.deleteAlbum(album.id!!, memberKey)

        // then
        val deletedMember = albumMemberRepository.findAll().first()
        assertThat(deletedMember.status).isEqualTo(EntityStatus.DELETED)
    }

    @Test
    fun `앨범 삭제 시 앨범 음악이 Soft Delete된다`() {
        // given
        val album = albumRepository.save(createAlbumEntity(memberKey))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = album.id!!))
        albumMusicRepository.save(createAlbumMusicEntity(album.id!!))

        // when
        albumService.deleteAlbum(album.id!!, memberKey)

        // then
        val deletedMusic = albumMusicRepository.findAll().first()
        assertThat(deletedMusic.status).isEqualTo(EntityStatus.DELETED)
    }

    @Test
    fun `앨범 삭제 시 앨범 로그와 로그 이미지가 Soft Delete된다`() {
        // given
        val album = albumRepository.save(createAlbumEntity(memberKey))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = album.id!!))
        val log = albumLogRepository.save(AlbumLogEntity(description = "테스트 로그", albumId = album.id!!))
        albumLogImageRepository.save(AlbumLogImageEntity(imageUrl = "https://example.com/image.jpg", albumLogId = log.id!!))

        // when
        albumService.deleteAlbum(album.id!!, memberKey)

        // then
        val deletedLog = albumLogRepository.findAll().first()
        val deletedImage = albumLogImageRepository.findAll().first()
        assertThat(deletedLog.status).isEqualTo(EntityStatus.DELETED)
        assertThat(deletedImage.status).isEqualTo(EntityStatus.DELETED)
    }

    @Test
    fun `앨범 삭제 시 앨범 멤버가 아니라면 NOT_ALBUM_MEMBER 예외가 발생한다`() {
        // given
        val album = albumRepository.save(createAlbumEntity(memberKey))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = album.id!!))

        // when & then
        val exception =
            assertThrows<AppException> {
                albumService.deleteAlbum(album.id!!, "other-member-key")
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_ALBUM_MEMBER)
    }

    private fun createAlbumEntity(memberKey: String) =
        AlbumEntity(
            memberKey = memberKey,
            coverImageUrl = "https://example.com/cover.jpg",
            region = "서울",
            comment = "테스트 코멘트",
            travelStartDate = LocalDate.now(),
            travelEndDate = LocalDate.now().plusDays(3),
        )

    @Test
    fun `앨범 삭제 시 다른 멤버의 데이터는 영향받지 않는다`() {
        // given
        val otherMemberKey = "other-member-key"
        val myAlbum = albumRepository.save(createAlbumEntity(memberKey))
        val otherAlbum = albumRepository.save(createAlbumEntity(otherMemberKey))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = myAlbum.id!!))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = otherMemberKey, albumId = otherAlbum.id!!))
        albumMusicRepository.save(createAlbumMusicEntity(myAlbum.id!!))
        albumMusicRepository.save(createAlbumMusicEntity(otherAlbum.id!!))
        albumLogRepository.save(AlbumLogEntity(description = "내 로그", albumId = myAlbum.id!!))
        albumLogRepository.save(AlbumLogEntity(description = "다른 사람 로그", albumId = otherAlbum.id!!))

        // when
        albumService.deleteAlbum(myAlbum.id!!, memberKey)

        // then
        val albums = albumRepository.findAll()
        val myDeletedAlbum = albums.find { it.id == myAlbum.id }!!
        val otherActiveAlbum = albums.find { it.id == otherAlbum.id }!!
        assertThat(myDeletedAlbum.status).isEqualTo(EntityStatus.DELETED)
        assertThat(otherActiveAlbum.status).isEqualTo(EntityStatus.ACTIVE)

        val musicList = albumMusicRepository.findAll()
        val myDeletedMusic = musicList.find { it.albumId == myAlbum.id }!!
        val otherActiveMusic = musicList.find { it.albumId == otherAlbum.id }!!
        assertThat(myDeletedMusic.status).isEqualTo(EntityStatus.DELETED)
        assertThat(otherActiveMusic.status).isEqualTo(EntityStatus.ACTIVE)

        val logs = albumLogRepository.findAll()
        val myDeletedLog = logs.find { it.albumId == myAlbum.id }!!
        val otherActiveLog = logs.find { it.albumId == otherAlbum.id }!!
        assertThat(myDeletedLog.status).isEqualTo(EntityStatus.DELETED)
        assertThat(otherActiveLog.status).isEqualTo(EntityStatus.ACTIVE)
    }

    private fun createAlbumMusicEntity(albumId: Long) =
        AlbumMusicEntity(
            title = "테스트 음악",
            musicUrl = "https://example.com/music.mp3",
            genre = GenreType.LO_FI,
            withLyrics = false,
            albumId = albumId,
            taskId = "",
        )
}
