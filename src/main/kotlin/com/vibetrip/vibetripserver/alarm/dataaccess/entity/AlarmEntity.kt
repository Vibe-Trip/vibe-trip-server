package com.vibetrip.vibetripserver.alarm.dataaccess.entity

import com.vibetrip.vibetripserver.alarm.domain.AlarmType
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "alarm")
@Entity
class AlarmEntity(
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val description: String,
    @Column(nullable = false)
    val memberKey: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val alarmType: AlarmType,
    @Column
    val albumId: Long? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    var id: Long? = null,
) : BaseEntity()