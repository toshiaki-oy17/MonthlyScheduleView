package com.toshiaki.monthlyschedule

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.toshiaki.monthlyschedule.databinding.LayoutDatesViewBinding
import java.text.SimpleDateFormat
import java.util.*

class MonthlyPagerAdapter<T> (private val context: Context, private var bundles: List<Bundle>
) : RecyclerView.Adapter<MonthlyPagerAdapter<T>.ViewHolder>() {

    private lateinit var binding: LayoutDatesViewBinding

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val iStartMonth = "I_START_MONTH"
        const val iStartYear = "I_START_YEAR"
        const val iStartDayView = "I_START_DAY_VIEW"
        const val iIsThreeLettersDay = "I_IS_THREE_LETTERS_DAY"
        const val iViewHeight = "I_VIEW_HEIGHT"
        const val iColors = "I_COLORS"
        const val iMap = "I_MAP"
        const val numberOfWeeks = 6
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LayoutDatesViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bundle = bundles[holder.adapterPosition]

        val currCalendar = Calendar.getInstance()
        val dayTexts = context.resources.getStringArray(R.array.list_of_days)
        val startMonth = bundle.getInt(iStartMonth)
        val startYear = bundle.getInt(iStartYear)
        val startDayView = bundle.getInt(iStartDayView)
        val isThreeLettersDay = bundle.getBoolean(iIsThreeLettersDay)
        val viewHeight = bundle.getInt(iViewHeight)
        val colors = bundle.getIntegerArrayList(iColors)!!
        val map = bundle.getSerializable(iMap) as HashMap<String, Data<T>>
        
        val dayTextViews: List<TextView> = listOf(
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

        val adapter = MonthlyScheduleAdapter(
            context,
            colors,
            viewHeight,
            getScheduleList(map, currCalendar, startDayView)
        )

        binding.rvDaySchedule.adapter = adapter
    }

    private fun getScheduleList(
        map: HashMap<String, Data<T>>,
        currCalendar: Calendar,
        startDayView: Int
    ): ArrayList<Schedule<T>> {
        val tempCurrCalendar = currCalendar.clone() as Calendar
        val tempCalendar = findFirstDate(tempCurrCalendar, startDayView)
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

    private fun findFirstDate(calendar: Calendar, startDayView: Int): Calendar {
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        while (calendar.get(Calendar.DAY_OF_WEEK) != startDayView) {
            calendar.add(Calendar.DATE, -1)
        }

        return calendar
    }

    override fun getItemCount(): Int {
        return bundles.size
    }
}