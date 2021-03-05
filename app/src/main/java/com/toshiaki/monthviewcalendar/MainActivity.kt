package com.toshiaki.monthviewcalendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.toshiaki.lib.MonthlyScheduleView
import com.toshiaki.lib.Schedule
import com.toshiaki.monthviewcalendar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val map = hashMapOf<String, Event>()
        map["2021-03-12"] = Event("Maths", "13:30")
        map["2021-03-13"] = Event("Biology", "14:30")
        map["2021-03-14"] = Event("Computer Science", "15:30")
        map["2021-03-15"] = Event("Physics", "16:30")
        map["2021-03-16"] = Event("Chemistry", "17:30")

        with(binding) {
            map["2021-03-12"] = Event("Maths", "13:30")
            map["2021-03-13"] = Event("Biology", "14:30")
            map["2021-03-14"] = Event("Computer Science", "15:30")
            map["2021-03-15"] = Event("Physics", "16:30")
            map["2021-03-16"] = Event("Chemistry", "17:30")

            msvSchedule.setSchedules(
                R.layout.event_item_list,
                map,
                object : MonthlyScheduleView.IInitialize<Event> {
                    override fun onInitUI(view: View, schedule: Schedule<Event>) {
                        val text = "${schedule.data?.text} ${schedule.data?.time}"
                        view.findViewById<TextView>(R.id.tv_event_date).text = text.replace("null", "")
                    }
                })
        }
    }

    data class Event(
        var text: String,
        var time: String
    )
}