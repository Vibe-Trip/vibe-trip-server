package com.vibetrip.vibetripserver.common.domain

import java.io.Closeable
import java.io.InputStream

data class ImageData(
    val data: InputStream,
    val contentType: String,
    val originalFilename: String,
) : Closeable {
    override fun close() {
        data.close()
    }
}
