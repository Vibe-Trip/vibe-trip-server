package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AlbumMemberRepository : JpaRepository<AlbumMemberEntity, Long>, CustomAlbumMemberRepository