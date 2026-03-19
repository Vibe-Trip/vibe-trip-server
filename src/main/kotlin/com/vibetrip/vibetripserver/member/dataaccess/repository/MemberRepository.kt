package com.vibetrip.vibetripserver.member.dataaccess.repository

import com.vibetrip.vibetripserver.member.dataaccess.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, Long> {

    fun findByMemberKey(memberKey: String): MemberEntity?
}