package com.toshiaki.monthlyschedule.model

data class Schedule<T>(
    var date: String = "",
    var isToday: Boolean = false,
    var isMonth: Boolean = false,
    var data: Data<T>? = null
) {
    constructor(): this("", false, false, null)
}
