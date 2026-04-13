package com.vibetrip.vibetripserver.common.util

import com.vibetrip.vibetripserver.common.enums.IpHeader
import jakarta.servlet.http.HttpServletRequest

private const val UNKNOWN = "unknown"

fun getClientIp(request: HttpServletRequest): String =
    IpHeader.entries.firstNotNullOfOrNull { header ->
        request
            .getHeader(header.value)
            ?.takeIf { isValidIp(it) }
            ?.let { getFirstIp(it) }
    } ?: request.remoteAddr

private fun getFirstIp(ip: String) = ip.substringBefore(",").trim()

private fun isValidIp(ip: String) = ip.isNotBlank() && !ip.equals(UNKNOWN, ignoreCase = true)
