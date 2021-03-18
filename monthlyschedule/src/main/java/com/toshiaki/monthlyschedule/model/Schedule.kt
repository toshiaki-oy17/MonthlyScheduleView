package com.toshiaki.monthlyschedule.model

/**
 *
 * CREATED BY yosualeonardo ON 3/18/21
 *
 */

data class Schedule<T>(
    var date: String = "",
    var isToday: Boolean = false,
    var isMonth: Boolean = false,
    var data: Data<T>? = null
) {
    constructor(): this("", false, false, null)
}
