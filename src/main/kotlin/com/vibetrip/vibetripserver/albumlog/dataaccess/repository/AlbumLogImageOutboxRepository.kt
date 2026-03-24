package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageOutbox
import org.springframework.data.jpa.repository.JpaRepository

interface AlbumLogImageOutboxRepository : JpaRepository<AlbumLogImageOutbox, Long>
