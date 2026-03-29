package com.vibetrip.vibetripserver.album.integration

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMemberEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import com.vibetrip.vibetripserver.support.integration.SpringTest
import com.vibetrip.vibetripserver.support.paging.Cursorable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean

class AlbumRepositoryTest : SpringTest() {
    @MockitoBean
    lateinit var googleImageUploader: GoogleImageUploader

    @Autowired
    lateinit var albumRepository: AlbumRepository

    @Autowired
    lateinit var albumMemberRepository: AlbumMemberRepository

    @BeforeEach
    fun setUp() {
        albumRepository.deleteAll()
        albumMemberRepository.deleteAll()
    }

    @Test
    fun `앨범 목록은 최신순으로 반환된다`() {
        // given
        val memberKey = "member-key-123"
        val saved1 = albumRepository.save(AlbumFixture.albumEntity())
        val saved2 = albumRepository.save(AlbumFixture.albumEntity())
        val saved3 = albumRepository.save(AlbumFixture.albumEntity())
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = saved1.id!!))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = saved2.id!!))
        albumMemberRepository.save(AlbumMemberEntity(memberKey = memberKey, albumId = saved3.id!!))
        val cursorable = Cursorable<Long>(cursor = null, limit = 10)

        // when
        val result = albumRepository.findAllByMemberKey(memberKey, cursorable)

        // then
        assertThat(result.content[0].id).isEqualTo(saved3.id)
        assertThat(result.content[1].id).isEqualTo(saved2.id)
        assertThat(result.content[2].id).isEqualTo(saved1.id)
    }
}
