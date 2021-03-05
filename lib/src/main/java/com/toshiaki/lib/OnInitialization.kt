package com.toshiaki.lib

import android.view.View

interface OnInitialization<T> {
    fun onInitUI(view: View, data: T)
}