package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AlbumLogRepository : JpaRepository<AlbumLogEntity, Long> {

    @Query("""
        SELECT COUNT(al)
        FROM AlbumLogEntity al
        JOIN AlbumEntity a ON al.albumId = a.id
        WHERE a.memberKey = :memberKey
        AND al.status = 'ACTIVE'
        AND a.status = 'ACTIVE'
    """)
    fun countByMemberKey(memberKey: String): Long
}