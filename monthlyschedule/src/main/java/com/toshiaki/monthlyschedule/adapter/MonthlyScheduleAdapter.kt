package com.toshiaki.monthlyschedule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toshiaki.monthlyschedule.databinding.MonthViewItemListBinding
import com.toshiaki.monthlyschedule.model.Schedule
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * CREATED BY yosualeonardo ON 3/18/21
 *
 */

class MonthlyScheduleAdapter<T>(
    private val context: Context,
    private val colors: List<Int>,
    private val viewHeight: Int,
    private var schedules: List<Schedule<T>>
) : RecyclerView.Adapter<MonthlyScheduleAdapter.ViewHolder>() {

    /***
     * Notes for Colors
     * [0] = Today Color
     * [1] = If the date is inside current month
     * [2] = If the date is outside current month
     */

    private lateinit var binding: MonthViewItemListBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = MonthViewItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[holder.adapterPosition]

        with (holder) {
            binding.vwHighlight.visibility = if (schedule.isToday) View.VISIBLE else View.INVISIBLE
            binding.vwHighlight.setBackgroundColor(colors[0])

            val date = dateFormat.parse(schedule.date)
            val calendar = Calendar.getInstance()
            calendar.time = date!!

            binding.tvDate.text = "${calendar.get(Calendar.DATE)}"
            binding.tvDate.setTextColor(
                    when {
                        schedule.isToday -> colors[0]
                        schedule.isMonth -> colors[1]
                        else -> colors[2]
                    }
            )

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            if (schedule.data != null) {
                val view = inflater.inflate(schedule.data!!.resId, null)
                binding.rlCustomView.addView(view)
                if (schedule.data!!.init != null) {
                    schedule.data!!.init!!.onInitUI(view, schedule.data!!.data!!)
                }
            }

            val param = binding.root.layoutParams as GridLayoutManager.LayoutParams
            param.height = viewHeight
            binding.root.layoutParams = param
        }
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = MonthViewItemListBinding.bind(itemView)
    }
}