package com.plantcare.ai.identifier.plantid.domains

import com.plantcare.ai.identifier.plantid.R

data class SoundDomain(
    var id: Long = 0,
    var name: String = "",
    var src: Int = R.raw.voice_air_horn
) {
}