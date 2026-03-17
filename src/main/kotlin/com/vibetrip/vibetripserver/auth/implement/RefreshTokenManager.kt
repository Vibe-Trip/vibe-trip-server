package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.dataaccess.repository.RefreshTokenRepository
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.data.repository.findByIdOrNull
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

    fun update(id: Long, refreshToken: String) =
        refreshTokenRepository.findByIdOrNull(id)?.update(refreshToken) ?: throw AppException(ErrorType.NOT_FOUND_DATA)


    fun delete(id: Long) =
        refreshTokenRepository.deleteById(id)

}