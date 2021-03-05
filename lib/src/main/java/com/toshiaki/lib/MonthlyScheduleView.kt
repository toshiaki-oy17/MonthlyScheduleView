package com.toshiaki.lib

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.toshiaki.lib.databinding.MainMonthViewCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class MonthlyScheduleView<T> (context: Context, attrs: AttributeSet?) :
        FrameLayout(context, attrs) {

    /***
     * Calendar Day Initialization
     */
    private var startDayView = Calendar.SUNDAY
    private var startMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var startYear = Calendar.getInstance().get(Calendar.YEAR)
    private var numberOfWeeks = 6
    private var isThreeLettersDay = false

    /***
     * Colors for:
     * 1.) Today highlights,
     * 2.) Day inside current month,
     * 3.) Day outside current month
     */
    private var todayColor = 0
    private var dayInMonthColor = 0
    private var dayOutMonthColor = 0

    // List of days
    private var dayTexts = context.resources.getStringArray(R.array.list_of_days)
    // List of Text View of Days
    private var dayTextViews: List<TextView> = emptyList()
    // Calendar handler
    private var currCalendar = Calendar.getInstance()
    // Simple Date Format
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // View Binding
    private var binding: MainMonthViewCalendarBinding = MainMonthViewCalendarBinding.inflate(LayoutInflater.from(context), this, true)

    private var currMap = hashMapOf<String, Data<T>>()

    private lateinit var update: Update
    private lateinit var adapter: MonthlyScheduleAdapter<T>

    init {
        getAttrs(attrs)
    }

    @SuppressLint("Recycle")
    fun getAttrs(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MonthlyScheduleView)
        val c = Calendar.getInstance()
        startDayView = a.getInt(R.styleable.MonthlyScheduleView_start_day_view, Calendar.SUNDAY)
        startDayView = if (startDayView in 1..7) startDayView else 1
        startMonth = a.getInt(R.styleable.MonthlyScheduleView_start_month_view, c.get(Calendar.MONTH))
        startYear = a.getInt(R.styleable.MonthlyScheduleView_start_year_view, c.get(Calendar.YEAR))
        isThreeLettersDay = a.getBoolean(R.styleable.MonthlyScheduleView_three_letters_day, !context.resources.getBoolean(R.bool.isTablet))
        todayColor = a.getColor(R.styleable.MonthlyScheduleView_today_color, ContextCompat.getColor(context, android.R.color.holo_red_light))
        dayInMonthColor = a.getColor(R.styleable.MonthlyScheduleView_day_in_month_color, ContextCompat.getColor(context, android.R.color.black))
        dayOutMonthColor = a.getColor(R.styleable.MonthlyScheduleView_day_out_month_color, ContextCompat.getColor(context, android.R.color.darker_gray))
        a.recycle()
        init()
    }

    private fun init() {
        dayTextViews = listOf(
                binding.tvDay1,
                binding.tvDay2,
                binding.tvDay3,
                binding.tvDay4,
                binding.tvDay5,
                binding.tvDay6,
                binding.tvDay7,
        )

        /***
         * Initialize Day Text Views
         * Based on start_day_view's input
         */
        for (i in dayTextViews.indices) {
            dayTextViews[i].maxLines = 1

            val text = dayTexts[(i + startDayView - 1) % dayTextViews.size]
            dayTextViews[i].text = if (isThreeLettersDay) text.take(3) else text
        }

        /***
         * Initialize Calendar
         * Based on start_year and start_month
         */
        currCalendar.set(Calendar.YEAR, startYear)
        currCalendar.set(Calendar.MONTH, startMonth)

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
        if (!this.currMap.isNullOrEmpty()) update.onUpdateSchedule(
                currCalendar.get(Calendar.YEAR),
                currCalendar.get(Calendar.MONTH)
        )
    }

    fun setSchedules(map: HashMap<String, Data<T>>) {
        this.currMap = map

        val colors = listOf(todayColor, dayInMonthColor, dayOutMonthColor)
        adapter = MonthlyScheduleAdapter(
                context,
                colors,
                getScheduleList(map)
        )
        binding.rvDaySchedule.adapter = adapter
    }

    private fun getScheduleList(map: HashMap<String, Data<T>>): ArrayList<Schedule<T>> {
        val tempCurrCalendar = currCalendar.clone() as Calendar
        val tempCalendar = findFirstDate(tempCurrCalendar)
        val calendar = Calendar.getInstance()
        val schedules = arrayListOf<Schedule<T>>()
        for (i in 0 until numberOfWeeks * 7) {
            val schedule = Schedule<T>()
            schedule.date = dateFormat.format(tempCalendar.time)
            val tempMonth = tempCalendar.get(Calendar.MONTH)
            val currMonth = currCalendar.get(Calendar.MONTH)
            schedule.isMonth = tempMonth == currMonth
            schedule.isToday = tempCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                    tempCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    tempCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)
            schedule.data = if (map.containsKey(schedule.date)) map[schedule.date] else null
            schedules.add(schedule)
            tempCalendar.add(Calendar.DATE, 1)
        }
        return schedules
    }

    private fun findFirstDate(calendar: Calendar): Calendar {
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        while (calendar.get(Calendar.DAY_OF_WEEK) != startDayView) {
            calendar.add(Calendar.DATE, -1)
        }

        return calendar
    }

    fun setOnUpdateSchedule(update: Update) {
        this.update = update
    }

    fun updateScheduleList(map: HashMap<String, Data<T>>) {
        adapter.updateSchedule(getScheduleList(map))
    }

    interface Update {
        fun onUpdateSchedule(year: Int, month: Int)
    }
}