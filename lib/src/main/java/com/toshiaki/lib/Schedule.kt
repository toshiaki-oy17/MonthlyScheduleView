package com.toshiaki.lib

data class Schedule<T>(
    var date: String,
    var isToday: Boolean,
    var isMonth: Boolean,
    var data: T?
)
