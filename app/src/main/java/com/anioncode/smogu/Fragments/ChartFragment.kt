package com.anioncode.smogu.Fragments


import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.Adapter.SensorAdapter
import com.anioncode.smogu.Adapter.SensorChartAdapter
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorIDList
import com.anioncode.smogu.Model.ChartPart
import com.anioncode.smogu.Model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.EntryXComparator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

//        lineChartView.setNoDataTextTypeface(Typeface.SANS_SERIF)
//        lineChartView.setNoDataText("");
//        lineChartView.setTouchEnabled(true)
//        lineChartView.setPinchZoom(false)
//        lineChartView.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.colorbdb))
//        lineChartView.getLegend().setEnabled(false);
//        lineChartView.getAxisRight().setDrawLabels(false);
//        lineChartView.getAxisLeft().setDrawGridLines(false);
//        lineChartView.getXAxis().setDrawGridLines(false);
//        lineChartView.description.text = ""
//        lineChartView.setExtraBottomOffset(35f);

        //getDataStation()


        getDataRecyclerStation()

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


        // lineChartView.invalidate()


    }


    private fun getDataRecyclerStation() { ///Wypełnianie Recycler Viewa wykresami z danymi

        val service = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
        var chartPart = ArrayList<ChartPart>()

        myChartsRec.apply {

            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter =
                SensorChartAdapter(chartPart, activity!!);
        }
        for (local in sensorIDList) {
            service.getSensorRX(local).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ findall ->

                    var modelIndexs: SensorbyID? = findall

                    val entriess = ArrayList<Entry>()
                    var i = 0;

                    for (value in modelIndexs?.values!!) {
                        var date: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.date)
//                            i("455F","${date.time}")
                        var setlong: Float =
                            if (value.value != null) value.value.toInt().toFloat() else 0f
                        entriess.add(Entry(date.time.toFloat() / 1000, setlong))
                        i++
                        if (i == 7) {
                            break;
                        }
                    }

                    if (entriess.size>0){
                    Collections.sort(entriess, EntryXComparator())
                        var k=0

                    for (adek in entriess){
                        if (adek.y==0f){
                            entriess.removeAt(k)

                        }else{
                            k++
                        }
                        i("8kssid $local", "${adek.x}")
                    }


                    chartPart.add(ChartPart(modelIndexs.key, entriess))

                    myChartsRec.adapter!!.notifyDataSetChanged()



                    }
                }, { t ->
                    t.message
                })
        }

    }
}
//    private fun getDataStation() {
//
//        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
//
//        api.getSensor(if (!myPreference.getSENSORID().toString().equals("")) myPreference.getSENSORID().toString() else "92")
//            .enqueue(object : Callback<SensorbyID> {
//
//                override fun onResponse(
//                    call: Call<SensorbyID>,
//                    response: Response<SensorbyID>
//                ) {
//
//
//                    var modelIndexs: SensorbyID? = response.body()
//
//                    activity?.runOnUiThread {
//
//
//                        //Part1
//                        val entries = ArrayList<Entry>()
//                        var i = 0;
//                        for (value in modelIndexs?.values!!) {
//                            var date: Date =
//                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.date)
////                            i("455F","${date.time}")
//                            var setlong: Float =
//                                if (value.value != null) value.value.toInt().toFloat() else 0f
//                            entries.add(Entry(date.time.toFloat() / 1000, setlong))
//                            i++
//                            if (i == 7) {
//                                break;
//                            }
//                        }
//                        Collections.sort(entries, EntryXComparator())
//
//
//                        val lineDataSet1 = LineDataSet(entries, "")
//                        lineDataSet1.apply {
//                            color = ContextCompat.getColor(requireContext(), R.color.colorbdb)
//                            disableDashedLine()
//                            setDrawFilled(true)
//
//                            lineWidth = 2f
//                            setCircleColor(R.color.colorGreen)
//                            fillColor = ContextCompat.getColor(requireContext(), R.color.colorbdb)
//                            setDrawValues(false)
//                            setAxisDependency(YAxis.AxisDependency.RIGHT)
//                            mode = LineDataSet.Mode.CUBIC_BEZIER
//                        }
//
//
//                        lineChartView.data = LineData(lineDataSet1)
//
//                        lineChartView.animateX(
//                            1000,
//                            Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseInExpo)
//                        )
//                        lineChartView.invalidate()
//                        val xAxis: XAxis = lineChartView.getXAxis()
//                        xAxis.position = XAxis.XAxisPosition.BOTTOM
//
//                        val xAxisFormatter = object : IndexAxisValueFormatter() {
//                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
//                                val format = SimpleDateFormat("HH:mm")
//                                return format.format(Date(value.toLong() * 1000L))
//                            }
//
//                        }
//
//                        with(xAxis) {
//                            valueFormatter = xAxisFormatter
//                            setLabelCount(5)
//                        }
//
//                        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.colorbdb)
//                        //  xAxis.setLabelRotationAngle(-90f);
//
//
//                    }
//                }
//
//                override fun onFailure(call: Call<SensorbyID>, t: Throwable) {
//                    Log.d("MainActivity1313x: ", "Call  ${t.message}")
//
//                }
//
//            })
//    }
