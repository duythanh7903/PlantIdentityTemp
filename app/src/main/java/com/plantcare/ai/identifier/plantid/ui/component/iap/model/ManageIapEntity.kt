package com.plantcare.ai.identifier.plantid.ui.component.iap.model

import com.plantcare.ai.identifier.plantid.app.AppConstants.DEFAULT_VALUE_BY_DOCUMENT

data class ManageIapEntity(
    var totalScan: Int = DEFAULT_VALUE_BY_DOCUMENT,
    var countScanned: Int = 0,
    var lastTimeBought: Long = System.currentTimeMillis(),
    var countBoughtIap: Int = 0
) {
}