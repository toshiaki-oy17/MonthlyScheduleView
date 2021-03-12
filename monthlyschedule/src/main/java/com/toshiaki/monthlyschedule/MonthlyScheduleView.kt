package com.toshiaki.monthlyschedule

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.toshiaki.monthlyschedule.databinding.MainMonthViewCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class MonthlyScheduleView<T>(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    /***
     * Calendar Day Initialization
     */
    private var startDayView = Calendar.SUNDAY
    private var startMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var startYear = Calendar.getInstance().get(Calendar.YEAR)
    private var isThreeLettersDay = false

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

    // Calendar handler
    private var currCalendar = Calendar.getInstance()

    // Simple Date Format
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.US)

    // View Binding
    private var binding: MainMonthViewCalendarBinding =
        MainMonthViewCalendarBinding.inflate(LayoutInflater.from(context), this, true)

    // Map Object
    private var currMap = hashMapOf<String, Data<T>>()

    // Initialize Fragment List
    private val bundles = arrayListOf<Bundle>()

    private lateinit var update: Update

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
            getPxFromDp(120)
        )
        a.recycle()
        init()
    }

    private fun init() {
        updateCalendar()
        listeners()
    }

    private fun listeners() {
        binding.tvDayCurrentMonth.setOnClickListener {

        }

        binding.btnDayToday.setOnClickListener {
            currCalendar = Calendar.getInstance()
            updateCalendar()
        }

        binding.ivDayNextMonth.setOnClickListener {
            currCalendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        binding.ivDayPreviousMonth.setOnClickListener {
            currCalendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }
    }

    private fun updateCalendar() {
        binding.tvDayCurrentMonth.text = monthYearFormat.format(currCalendar.time)
    }

    fun setSchedules(map: HashMap<String, Data<T>>) {
        this.currMap = map

        val colors = arrayListOf(todayColor, dayInMonthColor, dayOutMonthColor)

        for (i in -2..2) {
            val bundle = Bundle()
            bundle.putInt(MonthlyPagerAdapter.iStartMonth, startMonth + i)
            bundle.putInt(MonthlyPagerAdapter.iStartYear, startYear)
            bundle.putInt(MonthlyPagerAdapter.iStartDayView, startDayView)
            bundle.putBoolean(MonthlyPagerAdapter.iIsThreeLettersDay, isThreeLettersDay)
            bundle.putInt(MonthlyPagerAdapter.iViewHeight, viewHeight)
            bundle.putIntegerArrayList(MonthlyPagerAdapter.iColors, colors)
            bundle.putSerializable(MonthlyPagerAdapter.iMap, map)
            bundles.add(bundle)
        }

        val adapter = MonthlyPagerAdapter<T>(context, bundles)
        binding.vpDaysLayout.adapter = adapter
        binding.vpDaysLayout.currentItem = (bundles.size - 1) / 2

        binding.vpDaysLayout.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("VIEW PAGER POSITION", position.toString())
            }
        })
    }

    fun setOnUpdateSchedule(update: Update) {
        this.update = update
    }

    private fun getPxFromDp(dp: Int): Int {
        return dp * resources.displayMetrics.density.toInt()
    }

    interface Update {
        fun onUpdateSchedule(year: Int, month: Int)
    }
}