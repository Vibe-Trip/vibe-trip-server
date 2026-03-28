package com.vibetrip.vibetripserver.member.dataaccess.repository

interface CustomMemberRepository {
    fun deleteByMemberKey(memberKey: String)
}
