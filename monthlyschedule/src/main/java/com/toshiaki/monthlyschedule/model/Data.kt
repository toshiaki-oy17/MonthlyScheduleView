package com.toshiaki.monthlyschedule.model

data class Data<T> (
        var resId: Int = 0,
        var data: T? = null,
        var init: Initialization<T>? = null
) {
    constructor() : this(0, null, null)
}
