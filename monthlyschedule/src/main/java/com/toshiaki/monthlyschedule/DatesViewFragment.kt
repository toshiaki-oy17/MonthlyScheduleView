package com.toshiaki.monthlyschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.toshiaki.monthlyschedule.databinding.FragmentDatesViewBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DatesViewFragment<T> : Fragment() {

    private var colors = ArrayList<Int>()
    private var startMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var startYear = Calendar.getInstance().get(Calendar.YEAR)
    private var startDayView = 1
    private var numberOfWeeks = 6
    private var isThreeLettersDay = false
    private var viewHeight = 0
    private lateinit var map: HashMap<String, Data<T>>

    // List of days
    private var dayTexts = context?.resources?.getStringArray(R.array.list_of_days)
    // List of Text View of Days
    private var dayTextViews: List<TextView> = emptyList()

    private lateinit var binding: FragmentDatesViewBinding

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private lateinit var currCalendar: Calendar
    private lateinit var adapter: MonthlyScheduleAdapter<T>

    companion object {
        const val iStartMonth = "I_START_MONTH"
        const val iStartYear = "I_START_YEAR"
        const val iStartDayView = "I_START_DAY_VIEW"
        const val iIsThreeLettersDay = "I_IS_THREE_LETTERS_DAY"
        const val iViewHeight = "I_VIEW_HEIGHT"
        const val iColors = "I_COLORS"
        const val iMap = "I_MAP"

        fun <T> getInstance(bundle: Bundle) : DatesViewFragment<T> {
            val fragment = DatesViewFragment<T>()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currCalendar = Calendar.getInstance()
        dayTexts = context?.resources?.getStringArray(R.array.list_of_days)
        startMonth = arguments!!.getInt(iStartMonth)
        startYear = arguments!!.getInt(iStartYear)
        startDayView = arguments!!.getInt(iStartDayView)
        isThreeLettersDay = arguments!!.getBoolean(iIsThreeLettersDay)
        viewHeight = arguments!!.getInt(iViewHeight)
        colors = arguments!!.getIntegerArrayList(iColors)!!
        map = arguments!!.getSerializable(iMap) as HashMap<String, Data<T>>

        binding = FragmentDatesViewBinding.inflate(layoutInflater)
        init()
        return binding.root
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

            val text = dayTexts?.get((i + startDayView - 1) % dayTextViews.size)
            dayTextViews[i].text = if (isThreeLettersDay) text!!.take(3) else text
        }

        /***
         * Initialize Calendar
         * Based on start_year and start_month
         */
        currCalendar.set(Calendar.YEAR, startYear)
        currCalendar.set(Calendar.MONTH, startMonth)

        adapter = MonthlyScheduleAdapter(
            context!!,
            colors,
            viewHeight,
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
}