package com.vibetrip.vibetripserver.member.dataaccess.entity

import com.vibetrip.vibetripserver.common.entity.BaseEntity
import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.member.domain.MemberRole
import com.vibetrip.vibetripserver.member.domain.NewMember
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Table(name = "member")
@Entity
class MemberEntity(
    @Column(nullable = false)
    var memberKey: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var email: String,
    @Column(nullable = false)
    var profileImageUrl: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    var roles: MutableSet<MemberRole>,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var id: Long? = null,
) : BaseEntity() {
    companion object {
        fun from(member: NewMember) =
            MemberEntity(
                memberKey = UUID.randomUUID().toString(),
                name = member.nameValue,
                email = member.emailValue,
                profileImageUrl = member.profileImageUrlValue,
                roles = mutableSetOf(MemberRole.ROLE_USER),
            )
    }

    fun toDomain() =
        Member.of(
            memberKey = memberKey,
            name = name,
            email = email,
            profileImageUrl = profileImageUrl,
            roles = roles.toSet(),
        )
}
