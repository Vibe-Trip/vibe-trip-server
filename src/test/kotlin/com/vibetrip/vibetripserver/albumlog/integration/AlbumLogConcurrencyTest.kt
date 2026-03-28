package com.vibetrip.vibetripserver.albumlog.integration

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.albumlog.business.AlbumLogService
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.albumlog.domain.EditAlbumLog
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@ExtendWith(MockitoExtension::class)
class AlbumLogConcurrencyTest : SpringTest() {
    @MockitoBean
    lateinit var albumMemberRepository: AlbumMemberRepository

    @MockitoBean
    lateinit var googleImageUploader: GoogleImageUploader

    @Autowired
    lateinit var albumLogService: AlbumLogService

    @Autowired
    lateinit var albumLogRepository: AlbumLogRepository

    private val albumId = 1L
    private val memberKey = "test-member-key"

    @BeforeEach
    fun setUp() {
        albumLogRepository.deleteAll()
    }

    @Test
    fun `같은 AlbumLog를 동시에 업데이트 시 OptimisticLockingFailureException이 발생한다`() {
        // given
        val count = 100
        val executor: ExecutorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(count)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        val savedAlbumLog =
            albumLogRepository.save(
                AlbumLogEntity(
                    description = "원본 설명",
                    albumId = albumId,
                ),
            )
        val albumLogId = savedAlbumLog.id!!

        given(albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey)).willReturn(true)

        // when
        for (i in 0 until count) {
            executor.submit {
                try {
                    val editAlbumLog =
                        EditAlbumLog.of(
                            id = albumLogId,
                            description = "수정된 설명 $i",
                            albumId = albumId,
                            newImages = emptyList(),
                            removeImageIds = emptyList(),
                        )
                    albumLogService.updateAlbumLog(editAlbumLog, memberKey)
                    successCount.incrementAndGet()
                } catch (e: ObjectOptimisticLockingFailureException) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // then
        val updatedAlbumLog = albumLogRepository.findById(albumLogId).get()

        assertThat(successCount.get() + failCount.get()).isEqualTo(count)
        assertThat(successCount.get()).isGreaterThanOrEqualTo(1)
        assertThat(failCount.get()).isGreaterThan(0)
        assertThat(updatedAlbumLog.version).isEqualTo(successCount.get().toLong())
    }

    @Test
    fun `@Retryable이 적용되어 동시 업데이트 시 최소 1회 이상 성공한다`() {
        // given
        val count = 50
        val executor: ExecutorService = Executors.newFixedThreadPool(16)
        val latch = CountDownLatch(count)

        val savedAlbumLog =
            albumLogRepository.save(
                AlbumLogEntity(
                    description = "원본 설명",
                    albumId = albumId,
                ),
            )
        val albumLogId = savedAlbumLog.id!!
        val initialVersion = savedAlbumLog.version

        given(albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey)).willReturn(true)

        // when
        for (i in 0 until count) {
            executor.submit {
                try {
                    val editAlbumLog =
                        EditAlbumLog.of(
                            id = albumLogId,
                            description = "재시도 테스트 $i",
                            albumId = albumId,
                            newImages = emptyList(),
                            removeImageIds = emptyList(),
                        )
                    albumLogService.updateAlbumLog(editAlbumLog, memberKey)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        // then
        val updatedAlbumLog = albumLogRepository.findById(albumLogId).get()
        val finalVersion = updatedAlbumLog.version
        val successfulUpdates = finalVersion - initialVersion

        assertThat(successfulUpdates).isGreaterThanOrEqualTo(1)
        assertThat(successfulUpdates).isLessThanOrEqualTo(count.toLong())
        assertThat(updatedAlbumLog.description).startsWith("재시도 테스트")
    }

    @Test
    fun `순차적으로 업데이트하면 모든 업데이트가 성공한다`() {
        // given
        val savedAlbumLog =
            albumLogRepository.save(
                AlbumLogEntity(
                    description = "원본 설명",
                    albumId = albumId,
                ),
            )
        val albumLogId = savedAlbumLog.id!!
        val initialVersion = savedAlbumLog.version
        val updateCount = 5

        given(albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey)).willReturn(true)

        // when
        repeat(updateCount) { index ->
            val editAlbumLog =
                EditAlbumLog.of(
                    id = albumLogId,
                    description = "순차 수정 $index",
                    albumId = albumId,
                    newImages = emptyList(),
                    removeImageIds = emptyList(),
                )
            albumLogService.updateAlbumLog(editAlbumLog, memberKey)
        }

        // then
        val updatedAlbumLog = albumLogRepository.findById(albumLogId).get()

        assertThat(updatedAlbumLog.version).isEqualTo(initialVersion + updateCount)
        assertThat(updatedAlbumLog.description).isEqualTo("순차 수정 ${updateCount - 1}")
    }
}
