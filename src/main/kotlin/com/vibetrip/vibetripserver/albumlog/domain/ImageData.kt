package com.vibetrip.vibetripserver.albumlog.domain

import java.nio.file.Path

data class ImageData(
    val tempFilePath: Path,
    val contentType: String,
    val originalFilename: String,
)