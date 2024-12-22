package com.plantcare.ai.identifier.plantid.domains

import android.util.SparseBooleanArray
import com.plantcare.ai.identifier.plantid.app.AppConstants.SOUND_DEFAULT

data class AlarmDomainDelete(
    var id: Long = 0L,
    var time: Long = System.currentTimeMillis(),
    var label: String = "",
    var description: String = "",
    var allDays: SparseBooleanArray = SparseBooleanArray(),
    var soundRes: Int = SOUND_DEFAULT,
    var isEnabled: Boolean = true,
    var isVibration: Boolean = true,
    var isSelected: Boolean = false
) {
}