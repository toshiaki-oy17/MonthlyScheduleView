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

        /**
         * INITIALIZE MAP
         * STRING -> dateString with format "yyyy-MM-dd"
         * DATA -> INSERT LAYOUT, INITIALIZE LAYOUT, AND CUSTOM OBJECT
         */
        val map = hashMapOf<String, Data<Event>>()

        // Just an example data to be included
        val currMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        // Just an example data to be included
        for (i in 0..30) {
            /**
             * INITIALIZE DATA CLASS WITH YOUR CUSTOM OBJECT
             */
            val data = Data<Event>()

            /**
             * INSERT YOUR CUSTOM LAYOUT
             */
            data.resId = R.layout.event_item_list

            /**
             * INITIALIZE YOUR CUSTOM LAYOUT
             */
            data.init = object : Initialization<Event> {
                override fun onInitUI(view: View, data: Event) {
                    var text = "${data.text}\n${data.time}"
                    text = text.replace("null", "")
                    view.findViewById<TextView>(R.id.tv_event_date).text = text
                }
            }

            /**
             * INPUT YOUR CUSTOM OBJECT
             */
            data.data = Event("Maths", "Lesson ${i + 1}")

            /**
             * INSERT YOUR CUSTOM OBJECT (CLASS)
             * TO YOUR MAP WITH DATE FORMAT:
             * "yyyy-MM-dd"
             */
            val iString = if (i + 1 < 10) "0${i + 1}" else "${i + 1}"
            val currMonthString = if (currMonth < 10) "0$currMonth" else "$currMonth"
            map["2021-$currMonthString-$iString"] = data
        }

        binding.msvSchedule.setSchedules(map)
    }

    /**
     * CREATE YOUR CUSTOM OBJECT TO PUT IN "DATA" OBJECT
     */
    data class Event(
        var text: String,
        var time: String
    )
}