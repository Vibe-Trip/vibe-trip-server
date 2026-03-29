package com.vibetrip.vibetripserver.member.integration

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.member.business.MemberService
import com.vibetrip.vibetripserver.member.dataaccess.entity.MemberEntity
import com.vibetrip.vibetripserver.member.dataaccess.repository.MemberRepository
import com.vibetrip.vibetripserver.member.domain.MemberRole
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate

class MemberWithdrawTest : SpringTest() {
    @MockitoBean
    lateinit var googleImageUploader: GoogleImageUploader

    @Autowired
    lateinit var memberService: MemberService

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var albumRepository: AlbumRepository

    @Autowired
    lateinit var albumLogRepository: AlbumLogRepository

    private val memberKey = "test-member-key"

    @BeforeEach
    fun setUp() {
        albumLogRepository.deleteAll()
        albumRepository.deleteAll()
        memberRepository.deleteAll()
    }

    @Test
    fun `회원 탈퇴 시 멤버가 Soft Delete된다`() {
        // given
        memberRepository.save(createMemberEntity(memberKey))

        // when
        memberService.withdraw(memberKey)

        // then
        val deletedMember = memberRepository.findAll().first()

        assertThat(deletedMember.status).isEqualTo(EntityStatus.DELETED)
        assertThat(deletedMember.deletedAt).isNotNull()
    }

    @Test
    fun `회원 탈퇴 시 해당 멤버의 앨범이 Soft Delete된다`() {
        // given
        memberRepository.save(createMemberEntity(memberKey))
        albumRepository.save(createAlbumEntity(memberKey))
        albumRepository.save(createAlbumEntity(memberKey))

        // when
        memberService.withdraw(memberKey)

        // then
        val deletedAlbums = albumRepository.findAll()

        assertThat(deletedAlbums).hasSize(2)
        assertThat(deletedAlbums).allMatch { it.status == EntityStatus.DELETED }
        assertThat(deletedAlbums).allMatch { it.deletedAt != null }
    }

    @Test
    fun `회원 탈퇴 시 해당 멤버의 앨범 로그가 Soft Delete된다`() {
        // given
        memberRepository.save(createMemberEntity(memberKey))
        val album = albumRepository.save(createAlbumEntity(memberKey))
        albumLogRepository.save(createAlbumLogEntity(album.id!!))
        albumLogRepository.save(createAlbumLogEntity(album.id!!))
        albumLogRepository.save(createAlbumLogEntity(album.id!!))

        // when
        memberService.withdraw(memberKey)

        // then
        val deletedAlbumLogs = albumLogRepository.findAll()

        assertThat(deletedAlbumLogs).hasSize(3)
        assertThat(deletedAlbumLogs).allMatch { it.status == EntityStatus.DELETED }
        assertThat(deletedAlbumLogs).allMatch { it.deletedAt != null }
    }

    @Test
    fun `회원 탈퇴 시 멤버, 앨범, 앨범로그가 모두 Soft Delete된다`() {
        // given
        memberRepository.save(createMemberEntity(memberKey))
        val album1 = albumRepository.save(createAlbumEntity(memberKey))
        val album2 = albumRepository.save(createAlbumEntity(memberKey))
        albumLogRepository.save(createAlbumLogEntity(album1.id!!))
        albumLogRepository.save(createAlbumLogEntity(album1.id!!))
        albumLogRepository.save(createAlbumLogEntity(album2.id!!))

        // when
        memberService.withdraw(memberKey)

        // then
        val deletedMember = memberRepository.findAll().first()
        val deletedAlbums = albumRepository.findAll()
        val deletedAlbumLogs = albumLogRepository.findAll()

        assertThat(deletedMember.status).isEqualTo(EntityStatus.DELETED)
        assertThat(deletedAlbums).hasSize(2)
        assertThat(deletedAlbums).allMatch { it.status == EntityStatus.DELETED }
        assertThat(deletedAlbumLogs).hasSize(3)
        assertThat(deletedAlbumLogs).allMatch { it.status == EntityStatus.DELETED }
    }

    @Test
    fun `회원 탈퇴 시 다른 멤버의 데이터는 영향받지 않는다`() {
        // given
        val otherMemberKey = "other-member-key"
        memberRepository.save(createMemberEntity(memberKey))
        memberRepository.save(createMemberEntity(otherMemberKey))

        val myAlbum = albumRepository.save(createAlbumEntity(memberKey))
        val otherAlbum = albumRepository.save(createAlbumEntity(otherMemberKey))

        albumLogRepository.save(createAlbumLogEntity(myAlbum.id!!))
        albumLogRepository.save(createAlbumLogEntity(otherAlbum.id!!))

        // when
        memberService.withdraw(memberKey)

        // then
        val members = memberRepository.findAll()
        val myMember = members.find { it.memberKey == memberKey }!!
        val otherMember = members.find { it.memberKey == otherMemberKey }!!

        assertThat(myMember.status).isEqualTo(EntityStatus.DELETED)
        assertThat(otherMember.status).isEqualTo(EntityStatus.ACTIVE)

        val albums = albumRepository.findAll()
        val deletedAlbum = albums.find { it.memberKey == memberKey }!!
        val activeAlbum = albums.find { it.memberKey == otherMemberKey }!!

        assertThat(deletedAlbum.status).isEqualTo(EntityStatus.DELETED)
        assertThat(activeAlbum.status).isEqualTo(EntityStatus.ACTIVE)

        val albumLogs = albumLogRepository.findAll()
        val deletedAlbumLog = albumLogs.find { it.albumId == myAlbum.id }!!
        val activeAlbumLog = albumLogs.find { it.albumId == otherAlbum.id }!!

        assertThat(deletedAlbumLog.status).isEqualTo(EntityStatus.DELETED)
        assertThat(activeAlbumLog.status).isEqualTo(EntityStatus.ACTIVE)
    }

    private fun createMemberEntity(memberKey: String) =
        MemberEntity(
            memberKey = memberKey,
            name = "테스트유저",
            email = "test@test.com",
            profileImageUrl = "https://example.com/profile.jpg",
            roles = mutableSetOf(MemberRole.ROLE_USER),
        )

    private fun createAlbumEntity(memberKey: String) =
        AlbumEntity(
            memberKey = memberKey,
            title = "테스트 앨범",
            coverImageUrl = "https://example.com/cover.jpg",
            region = "서울",
            comment = "테스트 코멘트",
            travelStartDate = LocalDate.now(),
            travelEndDate = LocalDate.now().plusDays(3),
        )

    private fun createAlbumLogEntity(albumId: Long) =
        AlbumLogEntity(
            description = "테스트 로그",
            albumId = albumId,
        )
}
