package com.example.weatherforecast

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.androdocs.httprequest.HttpRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var selectedItems: String? = null

    private lateinit var CITY: String
    private lateinit var API: String
    private lateinit var URL: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinLoc.setSelection(0)
        API = "82fb6878ff8a4017c578766ccc7ed3eb"

        spinLoc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0?.let {
                    selectedItems = it.selectedItem.toString()
                    CITY = selectedItems as String
                    URL =
                        "https://api.openweathermap.org/data/2.5/weather?q=${CITY}&units=metric&appid=${API}"
                    Weather(
                        this@MainActivity,
                        URL
                    ).execute()
                }
            }
        }


    }


    class Weather(
        private val mainActivity: MainActivity,
        val URL: String
    ) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String {
            return HttpRequest.excuteGet(URL)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                //Object
                val jsonObject = JSONObject(result)
                val coord = jsonObject.getJSONObject("coord")
                val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
                val main = jsonObject.getJSONObject("main")
                val wind = jsonObject.getJSONObject("wind")
                val sys = jsonObject.getJSONObject("sys")

                //String
                val lon = coord.getString("lon")
                val lat = coord.getString("lat")
                val cloud = weather.getString("description")
                val temp = main.getString("temp") + "°C"
                val feels = main.getString("feels_like") + "°C"
                val min = main.getString("temp_min")
                val max = main.getString("temp_max")
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val speed = wind.getString("speed")

                val sunrise = sys.getLong("sunrise")
                val sunset = sys.getLong("sunset")

                //long to date
                val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                val sunrise_date = Date(sunrise * 1000)
                val sunset_date = Date(sunset * 1000)

                //UI
                mainActivity.apply {
                    degree.text = temp
                    clouds.text = cloud
                    tvmin.text = min
                    tvmax.text = max
                    tv_sunrise.text = sdf.format(sunrise_date)
                    tv_sunset.text = sdf.format(sunset_date)
                    tv_feels.text = feels
                    tv_wind.text = speed
                    tv_pressure.text = pressure
                    tv_humidity.text = humidity
                    tv_lon.text = lon
                    tv_lat.text = lat
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }


    }
    
}