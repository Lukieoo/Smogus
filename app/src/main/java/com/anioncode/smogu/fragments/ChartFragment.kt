package com.anioncode.smogu.fragments


import android.os.Bundle
import android.util.Log.i
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anioncode.retrofit2.ApiService
import com.anioncode.smogu.adapter.SensorChartAdapter
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorIDList
import com.anioncode.smogu.model.ChartPart
import com.anioncode.smogu.model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import com.anioncode.smogu.presenters.ChartsFragmentPresenter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.EntryXComparator
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ChartFragment @Inject constructor() : Fragment(R.layout.fragment_chart),
    ChartsFragmentPresenter.View {

    @Inject
    lateinit var service: ApiService

    @Inject
    lateinit var adapterChart: SensorChartAdapter

    private lateinit var myPreference: MyPreference

    var disposable: CompositeDisposable = CompositeDisposable()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //My Saved Data in SharedPreferences
        mySavedData()
        ///Show data on charts
        getDataRecyclerStation()

    }

    override fun mySavedData() {
        myPreference = MyPreference(requireContext())

        if (!myPreference.getID().equals("")) {
            nameStation.text = myPreference.getSTATION_NAME()
            nameStreet.text = myPreference.getSTATION_STREET()
            nameProvince.text = myPreference.getSTATION_PROVINCE()
        }
    }


    override fun getDataRecyclerStation() {

        var chartPart = ArrayList<ChartPart>()

        myChartsRec.apply {

            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = adapterChart

        }
        for (local in sensorIDList) {
            disposable.add(
                service.getSensorRX(local).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ allSensor ->

                        var sensorData: SensorbyID? = allSensor
                        val entries = ArrayList<Entry>()
                        var i = 0
                        for (value in sensorData?.values!!) {
                            var date: Date =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.date)
                            var setlong: Float =
                                if (value.value != null) value.value.toInt().toFloat() else 0f
                            entries.add(Entry(date.time.toFloat() / 1000, setlong))
                            i++
                            if (i == 7) {
                                break;
                            }
                        }
                        if (entries.size > 0) {
                            Collections.sort(entries, EntryXComparator())
                            var k = 0

                            for (single in entries) {
                                if (single.y == 0f) {
                                    entries.removeAt(k)

                                } else {
                                    k++
                                }
                            }

                            chartPart.add(ChartPart(sensorData.key, entries))

                            adapterChart.setData(chartPart)

                        }
                    }, { t ->
                        t.message
                    })
            )
        }

    }

    override fun onStop() {
        disposable.dispose()
        super.onStop()
    }
}
