package com.vibetrip.vibetripserver.alarm.dataaccess.repository

import com.vibetrip.vibetripserver.alarm.dataaccess.entity.AlarmEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AlarmRepository :
    JpaRepository<AlarmEntity, Long>,
    CustomAlarmRepository
