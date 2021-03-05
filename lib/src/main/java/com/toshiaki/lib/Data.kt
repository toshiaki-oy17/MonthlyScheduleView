package com.toshiaki.lib

data class Data<T> (
        var resId: Int = 0,
        var data: T? = null,
        var init:OnInitialization<T>? = null
) {
    constructor() : this(0, null, null)
}
