package com.vibetrip.vibetripserver.common.entity

import com.vibetrip.vibetripserver.common.enums.EntityStatus
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var modifiedAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var status: EntityStatus = EntityStatus.ACTIVE

    @Column
    var deletedAt: LocalDateTime? = null

    fun delete() {
        status = EntityStatus.DELETED
        deletedAt = LocalDateTime.now()
    }
}