package com.anioncode.smogu.Fragments


import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.Model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.EntryXComparator
import kotlinx.android.synthetic.main.fragment_chart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class ChartFragment : Fragment() {

    lateinit var myPreference: MyPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        myPreference = MyPreference(requireContext())

        if (!myPreference.getID().equals("")) {
            nameStation.text = myPreference.getSTATION_NAME()
            nameStreet.text = myPreference.getSTATION_STREET()
            nameProvince.text = myPreference.getSTATION_PROVINCE()
        }


        getDataStation()




//        xAxis.valueFormatter = object : ValueFormatter() {
//
//            override fun getFormattedValue(value: Float): String {
//
//                val date = Date(hours[value.toInt()].time.toInt() * 1000L)
//                // format of the date
//                val jdf = SimpleDateFormat("h a", Locale.getDefault())
//                jdf.timeZone = TimeZone.getTimeZone(date.timezone)
//
//                return jdf.format(date)
//            }
//        }

//        lineChartView.axisLeft.mAxisMaximum = 1f
//        lineChartView.axisLeft.mAxisMinimum = -1f
//        lineChartView.axisLeft.mAxisRange = 2f


        lineChartView.invalidate()


    }

    private fun getDataStation() {

        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)

        api.getSensor("92")
            .enqueue(object : Callback<SensorbyID> {

                override fun onResponse(
                    call: Call<SensorbyID>,
                    response: Response<SensorbyID>
                ) {


                    var modelIndexs: SensorbyID? =response.body()

//                    modelIndexs= response.body()!!

                    activity?.runOnUiThread {
                        //Part1
                        val entries = ArrayList<Entry>()
                        var i=0;
                        for(value in modelIndexs?.values!!){
                            var date:Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.date)
//                            i("455F","${date.time}")
                            var setlong:Float=if(value.value!=null)value.value.toInt().toFloat()else 0f
                            entries.add(Entry(date.time.toFloat()/1000,setlong ))
                            i++
                            if (i==5){
                                break;
                            }
                        }
                        Collections.sort(entries, EntryXComparator())





                        val lineDataSet1 = LineDataSet(entries, "")
                        lineDataSet1.apply {
                            color = ContextCompat.getColor(requireContext(), R.color.colorbdb)

                            disableDashedLine()
                            setDrawFilled(true)

                            lineWidth = 2f
                            setCircleColor(R.color.colorGreen)
                            fillColor = ContextCompat.getColor(requireContext(), R.color.colorbdb)
                            setDrawValues(false)
                            setAxisDependency(YAxis.AxisDependency.RIGHT)
                        }

                        lineChartView.xAxis.labelRotationAngle = 0f

                        lineChartView.getAxisLeft().setDrawGridLines(false);
                        lineChartView.getXAxis().setDrawGridLines(false);

                        // lineChartView.getAxisLeft().setDrawLabels(false);
                        lineChartView.getAxisRight().setDrawLabels(false);
                        //  lineChartView.getXAxis().setDrawLabels(false);
                        lineChartView.getLegend().setEnabled(false);
                        // lineChartView.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorbdb))
                        lineChartView.data = LineData(lineDataSet1)
                        lineChartView.setTouchEnabled(false)
                        lineChartView.setPinchZoom(false)
                        lineChartView.description.text = ""
                        lineChartView.animateX(1000, Easing.EaseInExpo)
                        val xAxis: XAxis = lineChartView.getXAxis()
                        xAxis.position = XAxis.XAxisPosition.BOTTOM

                      //  xAxis.setLabelRotationAngle(-45f);

//                        xAxis.valueFormatter = object : ValueFormatter() {
//
//                            override fun getFormattedValue(value: Float): String {
//
//                                i("MSSG34","${value.toInt()}")
//                                val date = Date(value.toLong())
//                                // format of the date
//                                val jdf = SimpleDateFormat("HH:MM", Locale.getDefault())
//                                jdf.timeZone = TimeZone.getDefault()
//
//                                return jdf.format(date)
//                            }
//                        }

                    }
                }

                override fun onFailure(call: Call<SensorbyID>, t: Throwable) {
                    Log.d("MainActivity1313x: ", "Call  ${t.message}")

                }

            })
    }

}
