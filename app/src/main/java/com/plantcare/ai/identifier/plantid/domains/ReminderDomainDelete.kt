package com.plantcare.ai.identifier.plantid.domains

data class ReminderDomainDelete(
    var id: Long = 0L,
    var daysNotification: String = "", // Ví dụ thông báo được phát vào thứ 2, 5 hằng tuần -> value: 25
    var title: String = "",
    var des: String = "",
    var hour: Int = 0,
    var minute: Int = 0,
    var sound: Int = 0,
    var isVibration: Boolean = true,
    var isActiveRemind: Boolean = true,
    var createdAt: Long = System.currentTimeMillis(),
    var isSelected: Boolean = false
) {
}