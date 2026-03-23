package com.vibetrip.vibetripserver.common.exception

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(
    val status: HttpStatus,
    val errorCode: ErrorCode,
    val message: String,
    val logLevel: LogLevel,
) {
    INVALID_ACCESS_PATH(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 접근 경로입니다.", LogLevel.WARN),
    REQUIRED_AUTH(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "리소스에 접근하기 위한 인증이 필요합니다.", LogLevel.WARN),
    FAILED_AUTH(HttpStatus.FORBIDDEN, ErrorCode.E403, "인증에 실패했습니다.", LogLevel.WARN),
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.E403, "해당 리소스에 대한 권한이 없습니다.", LogLevel.WARN),
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, ErrorCode.E404, "해당 데이터를 찾을 수 없습니다.", LogLevel.WARN),
    CONFLICT(HttpStatus.CONFLICT, ErrorCode.E409, "요청이 충돌했습니다. 다시 시도해주세요.", LogLevel.WARN),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.E429, "너무 많은 요청을 보냈습니다.", LogLevel.WARN),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "알 수 없는 오류가 발생했습니다.", LogLevel.ERROR),

    FILE_GENERATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "새 파일을 생성할 수 없습니다.", LogLevel.ERROR),
    FILE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "파일을 찾을 수 없습니다.", LogLevel.ERROR),

    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "이미지 업로드에 실패했습니다.", LogLevel.ERROR),

    // Member
    INVALID_MEMBER_KEY(HttpStatus.BAD_REQUEST, ErrorCode.E1000, "멤버 key가 유효하지 않습니다.", LogLevel.WARN),
    INVALID_NAME_LENGTH(HttpStatus.BAD_REQUEST, ErrorCode.E1001, "이름이 너무 깁니다.", LogLevel.WARN),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, ErrorCode.E1002, "이메일이 유효하지 않습니다.", LogLevel.WARN),
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, ErrorCode.E1003, "이미지 URL이 유효하지 않습니다.", LogLevel.WARN),

    // Security
    MALFORMED_JWT(HttpStatus.BAD_REQUEST, ErrorCode.E2000, "JWT가 손상되었습니다.", LogLevel.WARN),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, ErrorCode.E2001, "지원하지 않는 JWT 형식입니다.", LogLevel.WARN),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, ErrorCode.E2002, "JWT 기한이 만료되었습니다.", LogLevel.WARN),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, ErrorCode.E2003, "JWT Signature 검증에 실패했습니다.", LogLevel.WARN),
    INVALID_JWT(HttpStatus.BAD_REQUEST, ErrorCode.E2004, "JWT가 유효하지 않습니다.", LogLevel.WARN),
    INVALID_TOKEN_METHOD(HttpStatus.BAD_REQUEST, ErrorCode.E2005, "토큰 방식이 올바르지 않습니다.", LogLevel.WARN),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST, ErrorCode.E2006, "토큰 타입이 올바르지 않습니다.", LogLevel.WARN),
    INVALID_OAUTH_USER(HttpStatus.BAD_REQUEST, ErrorCode.E2007, "존재하지 않는 OAuth 유저입니다.", LogLevel.WARN),
    FAILED_REQUEST_APPLE_KEYS(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E2008, "Apple 공개키를 가져올 수 없습니다.", LogLevel.WARN),
    INVALID_APPLE_IDENTITY_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E2009, "Apple Identity Token이 유효하지 않습니다.", LogLevel.WARN),
    INVALID_APPLE_KEY(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E2010, "Apple 키에 kid가 없습니다.", LogLevel.WARN),

    // Album
    INVALID_ALBUM_REGION(HttpStatus.BAD_REQUEST, ErrorCode.E3000, "여행지가 너무 깁니다.", LogLevel.WARN),
    INVALID_ALBUM_COMMENT(HttpStatus.BAD_REQUEST, ErrorCode.E3001, "코멘트가 너무 갑나다.", LogLevel.WARN),
    NOT_FOUND_ALBUM(HttpStatus.NOT_FOUND, ErrorCode.E3002, "앨범을 찾을 수 없습니다.", LogLevel.WARN),
    FORBIDDEN_ALBUM(HttpStatus.FORBIDDEN, ErrorCode.E3003, "해당 앨범에 대한 권한이 없습니다.", LogLevel.WARN),
    MUSIC_GENERATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E3004, "음악 생성에 실패했습니다.", LogLevel.ERROR),
    INVALID_VOCAL_GENDER(HttpStatus.BAD_REQUEST, ErrorCode.E3005, "가사가 있을 경우 보컬 성별을 선택해야 합니다.", LogLevel.WARN),
    INVALID_ALBUM_TRAVEL_DATE(HttpStatus.BAD_REQUEST, ErrorCode.E3006, "여행 종료일은 시작일 이후여야 합니다.", LogLevel.WARN),
    INVALID_GENRE_LYRICS_MISMATCH(HttpStatus.BAD_REQUEST, ErrorCode.E3007, "가사 여부와 장르 조합이 맞지 않습니다.", LogLevel.WARN),
}
