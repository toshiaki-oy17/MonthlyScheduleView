package com.toshiaki.lib

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

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

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.month_view_item_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[holder.adapterPosition]

        holder.vwHighlight.visibility = if (schedule.isToday) View.VISIBLE else View.INVISIBLE
        holder.vwHighlight.setBackgroundColor(colors[0])

        val date = dateFormat.parse(schedule.date)
        val calendar = Calendar.getInstance()
        calendar.time = date!!

        holder.tvDate.text = "${calendar.get(Calendar.DATE)}"
        holder.tvDate.setTextColor(
            when {
                schedule.isToday -> colors[0]
                schedule.isMonth -> colors[1]
                else -> colors[2]
            }
        )

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (schedule.data != null) {
            val view = inflater.inflate(schedule.data!!.resId, null)
            holder.customView.addView(view)
            if (schedule.data!!.init != null) {
                schedule.data!!.init!!.onInitUI(view, schedule.data!!.data!!)
            }
        }

        val param = holder.itemView.layoutParams as GridLayoutManager.LayoutParams
        param.height = viewHeight
        holder.itemView.layoutParams = param
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    fun updateSchedule(schedules: List<Schedule<T>>) {
        this.schedules = schedules
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vwHighlight: View = itemView.findViewById(R.id.vw_highlight)
        var tvDate: TextView = itemView.findViewById(R.id.tv_date)
        var customView: RelativeLayout = itemView.findViewById(R.id.rl_custom_view)
    }
}