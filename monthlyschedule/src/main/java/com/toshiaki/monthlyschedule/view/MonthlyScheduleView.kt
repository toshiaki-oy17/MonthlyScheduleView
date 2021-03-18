package com.toshiaki.monthlyschedule.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.toshiaki.monthlyschedule.R
import com.toshiaki.monthlyschedule.adapter.MonthlyPagerAdapter
import com.toshiaki.monthlyschedule.databinding.MainMonthViewCalendarBinding
import com.toshiaki.monthlyschedule.model.Data
import java.text.SimpleDateFormat
import java.util.*


@Suppress("SameParameterValue")
class MonthlyScheduleView<T>(context: Context, attrs: AttributeSet?) :
        ConstraintLayout(context, attrs) {

    /***
     * Calendar Day Initialization
     */
    private var startDayView = Calendar.SUNDAY
    private var startMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var startYear = Calendar.getInstance().get(Calendar.YEAR)
    private var isThreeLettersDay = false
    private val minMonth = 0
    private var minYear = 1900
    private val maxMonth = 11
    private var maxYear = 2100

    /**
     * Colors for:
     * 1.) Today highlights,
     * 2.) Day inside current month,
     * 3.) Day outside current month
     */
    private var todayColor = 0
    private var dayInMonthColor = 0
    private var dayOutMonthColor = 0

    /**
     * INSERT CUSTOM VIEW'S HEIGHT
     */
    private var viewHeight = 0
    private val defaultHeight = 120

    // Calendar List for Initialization
    private val myList = mutableListOf<MonthYear>()

    // Calendar handler
    private val currCalendar = Calendar.getInstance()

    // Simple Date Format
    private val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.US)

    // View Binding
    private val binding = MainMonthViewCalendarBinding.inflate(LayoutInflater.from(context), this, true)

    // Map Object
    private var currMap = hashMapOf<String, Data<T>>()

    // Initialize Fragment List
    private val bundles = arrayListOf<Bundle>()

    // Set Fix Today MonthYear Position
    private var fixedPosition = 0

    init {
        getAttrs(attrs)
    }

    @SuppressLint("Recycle")
    fun getAttrs(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MonthlyScheduleView)
        val c = Calendar.getInstance()
        startDayView = a.getInt(R.styleable.MonthlyScheduleView_start_day_view, Calendar.SUNDAY)
        startDayView = if (startDayView in 1..7) startDayView else 1
        startMonth =
                a.getInt(R.styleable.MonthlyScheduleView_start_month_view, c.get(Calendar.MONTH))
        startYear = a.getInt(R.styleable.MonthlyScheduleView_start_year_view, c.get(Calendar.YEAR))
        isThreeLettersDay = a.getBoolean(
                R.styleable.MonthlyScheduleView_three_letters_day,
                !context.resources.getBoolean(R.bool.isTablet)
        )
        todayColor = a.getColor(
                R.styleable.MonthlyScheduleView_today_color,
                ContextCompat.getColor(context, android.R.color.holo_red_light)
        )
        dayInMonthColor = a.getColor(
                R.styleable.MonthlyScheduleView_day_in_month_color,
                ContextCompat.getColor(context, android.R.color.black)
        )
        dayOutMonthColor = a.getColor(
                R.styleable.MonthlyScheduleView_day_out_month_color,
                ContextCompat.getColor(context, android.R.color.darker_gray)
        )
        viewHeight = a.getDimensionPixelSize(
                R.styleable.MonthlyScheduleView_height_custom_view,
                getPxFromDp(defaultHeight)
        )
        minYear = a.getInt(R.styleable.MonthlyScheduleView_min_year, 1900)
        maxYear = a.getInt(R.styleable.MonthlyScheduleView_max_year, 2100)
        a.recycle()
        init()
    }

    private fun init() {
        val c = Calendar.getInstance()
        c.set(Calendar.MONTH, minMonth)
        c.set(Calendar.YEAR, minYear)
        var index = 0
        while (c.get(Calendar.MONTH) != maxMonth && c.get(Calendar.YEAR) != maxYear) {
            myList.add(MonthYear(index, c.get(Calendar.YEAR), c.get(Calendar.MONTH)))
            c.add(Calendar.MONTH, 1)
            index++
        }

        updateCalendar(MonthYear(-1, startYear, startMonth))
        listeners()
    }

    private fun listeners() {
        binding.btnMonth.setOnClickListener {
            pickMonthYear().show()
        }
    }

    fun setSchedules(map: HashMap<String, Data<T>>) {
        this.currMap = map

        val colors = arrayListOf(todayColor, dayInMonthColor, dayOutMonthColor)

        for (i in myList.indices) {
            val bundle = Bundle()
            bundle.putInt(MonthlyPagerAdapter.iStartMonth, myList[i].month)
            bundle.putInt(MonthlyPagerAdapter.iStartYear, myList[i].year)
            bundle.putInt(MonthlyPagerAdapter.iStartDayView, startDayView)
            bundle.putBoolean(MonthlyPagerAdapter.iIsThreeLettersDay, isThreeLettersDay)
            bundle.putInt(MonthlyPagerAdapter.iViewHeight, viewHeight)
            bundle.putIntegerArrayList(MonthlyPagerAdapter.iColors, colors)
            bundle.putSerializable(MonthlyPagerAdapter.iMap, map)
            bundles.add(bundle)
        }

        val adapter = MonthlyPagerAdapter<T>(context, bundles)
        binding.vpDaysLayout.adapter = adapter

        val my = MonthYear(0, startYear, startMonth)
        fixedPosition = myPosition(myList, my, 0, myList.size)
        binding.vpDaysLayout.setCurrentItem(fixedPosition, false)

        binding.vpDaysLayout.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val myGet = getMonthYear(myList, position, 0, myList.size)
                updateCalendar(myGet)
            }
        })
    }

    private fun updateCalendar(my: MonthYear) {
        currCalendar.set(Calendar.YEAR, my.year)
        currCalendar.set(Calendar.MONTH, my.month)
        binding.btnMonth.text = monthYearFormat.format(currCalendar.time)
    }

    private fun getPxFromDp(dp: Int): Int = dp * resources.displayMetrics.density.toInt()

    // MonthYear Position
    private fun myPosition(myList: List<MonthYear>, my: MonthYear, left: Int, right: Int): Int {
        val idx = (left + right) / 2
        if (idx >= myList.size) {
            return -1
        }
        val pivot: MonthYear = myList[idx]

        if (left > right) return -1

        return when {
            my.getIntValue() < pivot.getIntValue() -> {
                myPosition(myList, my, left, idx - 1)
            }
            my.getIntValue() > pivot.getIntValue() -> {
                myPosition(myList, my, idx + 1, right)
            }
            else -> {
                pivot.position
            }
        }
    }

    // Get MonthYear by Position
    private fun getMonthYear(myList: List<MonthYear>, position: Int, left: Int, right: Int): MonthYear {
        val idx = (left + right) / 2
        if (idx >= myList.size) {
            return MonthYear()
        }
        val pivot: MonthYear = myList[idx]

        if (left > right) return MonthYear()

        return when {
            position < pivot.position -> {
                getMonthYear(myList, position, left, idx - 1)
            }
            position > pivot.position -> {
                getMonthYear(myList, position, idx + 1, right)
            }
            else -> {
                pivot
            }
        }
    }

    private fun pickMonthYear(): DatePickerDialog {
        val dpd = DatePickerDialog(
                context, { _, year, month, _ ->
            val my = MonthYear(-1, year, month)
            val position = myPosition(myList, my, 0, myList.size)
            binding.vpDaysLayout.setCurrentItem(position, false)
        },
                currCalendar.get(Calendar.YEAR),
                currCalendar.get(Calendar.MONTH),
                currCalendar.get(Calendar.DAY_OF_MONTH))

        try {
            val datePickerDialogFields = dpd.javaClass.declaredFields
            for (datePickerDialogField in datePickerDialogFields) {
                if (datePickerDialogField.name == "mDatePicker") {
                    datePickerDialogField.isAccessible = true
                    val datePicker = datePickerDialogField[dpd] as DatePicker
                    val datePickerFields = datePickerDialogField.type.declaredFields
                    for (datePickerField in datePickerFields) {
                        if ("mDaySpinner" == datePickerField.name) {
                            datePickerField.isAccessible = true
                            val dayPicker = datePickerField[datePicker]
                            (dayPicker as View).visibility = View.GONE
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }

        return dpd
    }

    private data class MonthYear(
            var position: Int = -1,
            var year: Int = 0,
            var month: Int = 0
    ) {
        fun getIntValue(): Int = year * 12 + month

        override fun toString(): String {
            return "MonthYear(position=$position, year=$year, month=$month)"
        }
    }
}