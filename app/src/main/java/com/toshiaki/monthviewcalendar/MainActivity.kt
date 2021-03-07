package com.toshiaki.monthviewcalendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.toshiaki.monthlyschedule.Data
import com.toshiaki.monthlyschedule.Initialization
import com.toshiaki.monthviewcalendar.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val map = hashMapOf<String, Data<Event>>()
        val currMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        for (i in 0..30) {
            val iString = if (i + 1 < 10) "0${i + 1}" else "${i + 1}"
            val currMonthString = if (currMonth < 10) "0$currMonth" else "$currMonth"
            map["2021-$currMonthString-$iString"] = Data(
                    // PUT YOUR CUSTOM LAYOUT
                    R.layout.event_item_list,
                    // PUT YOUR CUSTOM CLASS
                    Event("Maths", "Lesson ${i + 1}"),
                    // INITIALIZE YOUR VIEW HERE INSIDE DAY SCHEDULE VIEW BASED ON CUSTOM LAYOUT
                    object : Initialization<Event> {
                        override fun onInitUI(view: View, data: Event) {
                            var text = "${data.text}\n${data.time}"
                            text = text.replace("null", "")
                            view.findViewById<TextView>(R.id.tv_event_date).text = text
                        }
                    }
            )
        }

//        binding.msvSchedule.setOnUpdateSchedule(object : MonthlyScheduleView.Update {
//            override fun onUpdateSchedule(year: Int, month: Int) {
//                val mapUpdated = hashMapOf<String, Data<Event>>()
//                for (i in 0..30) {
//                    val iString = if (i + 1 < 10) "0${i + 1}" else "${i + 1}"
//                    val currMonthString = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
//                    mapUpdated["$year-$currMonthString-$iString"] = Data(
//                            // PUT YOUR CUSTOM LAYOUT
//                            R.layout.event_item_list,
//                            // PUT YOUR DATA CLASS
//                            Event("Maths", "Lesson ${i + 1}"),
//                            // INITIALIZE YOUR VIEW HERE INSIDE DAY SCHEDULE VIEW BASED ON CUSTOM LAYOUT
//                            object : Initialization<Event> {
//                                override fun onInitUI(view: View, data: Event) {
//                                    var text = "${data.text}\n${data.time}"
//                                    text = text.replace("null", "")
//                                    view.findViewById<TextView>(R.id.tv_event_date).text = text
//                                }
//                            }
//                    )
//                    updateSchedule(mapUpdated)
//                }
//            }
//        })
//
        binding.msvSchedule.setSchedules(map)
    }

//    private fun updateSchedule(map: HashMap<String, Data<Event>>) {
//        binding.msvSchedule.updateScheduleList(map)
//    }

    data class Event(
            var text: String,
            var time: String
    )
}