package com.toshiaki.monthlyschedule

import android.view.View

interface Initialization<T> {
    fun onInitUI(view: View, data: T)
}