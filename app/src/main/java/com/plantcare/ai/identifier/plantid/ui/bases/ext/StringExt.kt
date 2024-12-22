package com.plantcare.ai.identifier.plantid.ui.bases.ext

fun String.isVideoFast() = videoExtensions.any { endsWith(it, true) }

fun String.isImageFast() = photoExtensions.any { endsWith(it, true) }
fun String.isAudioFast() = audioExtensions.any { endsWith(it, true) }
fun String.isRawFast() = rawExtensions.any { endsWith(it, true) }