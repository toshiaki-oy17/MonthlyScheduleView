package com.toshiaki.lib

import android.content.Context
import android.view.View

class MonthlyScheduleViewAdapter <T>(
    private val layoutId: Int,
    context: Context,
    colors: List<Int>,
    schedules: List<Schedule<T>>,
    private val iInitialize: IInitialize<T>
) : MonthlyScheduleAdapter<T>(context, colors, schedules) {

    override fun getView(): Int {
        return layoutId
    }

    override fun initUI(view: View, schedule: Schedule<T>) {
        iInitialize.onInitUI(view, schedule)
    }

    interface IInitialize<T> {
        fun onInitUI(view: View, schedule: Schedule<T>)
    }
}