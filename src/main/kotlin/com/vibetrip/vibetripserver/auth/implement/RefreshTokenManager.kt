package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.dataaccess.entity.RefreshTokenEntity
import com.vibetrip.vibetripserver.auth.dataaccess.repository.RefreshTokenRepository
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class RefreshTokenManager(
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    @Transactional(readOnly = true)
    fun findByMemberKey(memberKey: String) =
        refreshTokenRepository.findByMemberKey(memberKey)?.toDomain() ?: throw AppException(ErrorType.NOT_FOUND_DATA)

    fun update(refreshToken: String, memberKey: String) =
        refreshTokenRepository.findByMemberKey(memberKey)?.update(refreshToken) ?: refreshTokenRepository.save(
            RefreshTokenEntity(
                refreshToken,
                memberKey
            )
        )


    fun delete(id: Long) =
        refreshTokenRepository.deleteById(id)

}