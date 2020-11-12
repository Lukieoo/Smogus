package com.anioncode.smogu.fragments


import android.os.Bundle
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.adapter.SensorChartAdapter
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorIDList
import com.anioncode.smogu.model.ChartPart
import com.anioncode.smogu.model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.EntryXComparator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class ChartFragment @Inject constructor(): Fragment(R.layout.fragment_chart) {

    lateinit var myPreference: MyPreference


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        myPreference = MyPreference(requireContext())

        if (!myPreference.getID().equals("")) {
            nameStation.text = myPreference.getSTATION_NAME()
            nameStreet.text = myPreference.getSTATION_STREET()
            nameProvince.text = myPreference.getSTATION_PROVINCE()
        }

        getDataRecyclerStation()

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
