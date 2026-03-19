package com.vibetrip.vibetripserver.common.enums

enum class IpHeader(
    val value: String
) {
    X_FORWARDED_FOR("X-Forwarded-For"),
    X_REAL_IP("X-Real-IP"),
    PROXY_CLIENT_IP("Proxy-Client-IP"),
    WL_PROXY_CLIENT_IP("WL-Proxy-Client-IP"),
    HTTP_CLIENT_IP("HTTP_CLIENT_IP"),
    HTTP_X_FORWARDED_FOR("HTTP_X_FORWARDED_FOR"),
}