package com.toshiaki.monthlyschedule.model

import android.view.View

interface Initialization<T> {
    fun onInitUI(view: View, data: T)
}