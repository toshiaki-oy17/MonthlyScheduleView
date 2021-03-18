package com.toshiaki.monthlyschedule.model

/**
 *
 * CREATED BY yosualeonardo ON 3/18/21
 *
 */

data class Data<T> (
        var resId: Int = 0,
        var data: T? = null,
        var init: Initialization<T>? = null
) {
    constructor() : this(0, null, null)
}
