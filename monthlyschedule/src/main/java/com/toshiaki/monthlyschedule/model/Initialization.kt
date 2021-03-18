package com.toshiaki.monthlyschedule.model

import android.view.View

/**
 *
 * CREATED BY yosualeonardo ON 3/18/21
 *
 */

interface Initialization<T> {
    fun onInitUI(view: View, data: T)
}