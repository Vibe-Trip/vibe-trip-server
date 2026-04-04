package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.SunoMusicDataEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SunoMusicDataRepository : JpaRepository<SunoMusicDataEntity, Long>
