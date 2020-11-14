package com.anioncode.smogu.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.anioncode.retrofit2.ApiService
import com.anioncode.smogu.adapter.SensorAdapter
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorIDList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorbyIDList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorsNameList
import com.anioncode.smogu.model.ModelIndex.ModelIndex
import com.anioncode.smogu.model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import com.anioncode.smogu.events.NavEvent
import com.anioncode.smogu.presenters.StatsFragmentPresenter
import com.anioncode.smogu.utils.SystemUtils
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dash.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class StatsFragment @Inject constructor() : Fragment(R.layout.fragment_dash),
    StatsFragmentPresenter.View {

    @Inject
    lateinit var navEvents: PublishProcessor<NavEvent>

    @Inject
    lateinit var sensorEvent: PublishProcessor<SensorbyID>

    @Inject
    lateinit var service: ApiService

    @Inject
    lateinit var adapterSensor: SensorAdapter

    private lateinit var modelIndex: ModelIndex
    private lateinit var myPreference: MyPreference
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var disposable: Disposable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Get data saved from SharedPreference
        getSharedPreferenceData()
        //Check possibility connection with internet
        fetchSingleData()
        //Get Sensor Data From Station
        getDataStationSensor()
        //Init Ui events
        initView()

    }

    override fun initView() {
        maps.setOnClickListener(View.OnClickListener {
            navEvents.onNext(NavEvent(NavEvent.Destination.MapFragment))
        })
        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = adapterSensor
        }
        disposable = sensorEvent
            .subscribeOn(Schedulers.io())
            .subscribe({
                partJakosc.text =
                    "Ocena jakości z ${it.key}:"
                time.text =
                    "${it.values.get(0).date.substring(
                        0,
                        10
                    )} "

                getDataStation(it.key)
            }, {})
    }

    override fun fetchSingleData() {
        if (SystemUtils(requireActivity()).isOnline())
            getDataStation("ST")
    }

    override fun getSharedPreferenceData() {
        myPreference = MyPreference(requireContext())
        if (!myPreference.getID().equals("")) {
            nameStation.text = myPreference.getSTATION_NAME()
            nameStreet.text = myPreference.getSTATION_STREET()
            nameProvince.text = myPreference.getSTATION_PROVINCE()
        }
    }

    private fun getDataStation(Id: String) {
        jakosc.visibility = View.INVISIBLE
        compositeDisposable.add(
            service.getIndexRX(if (myPreference.getID() != "") myPreference.getID() else "114")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var color: Int =
                        R.color.colorbdb
                    modelIndex = it
                    var nameLevel = ""

                    when (Id) {

                        "PM10" ->
                            if (modelIndex.pm10IndexLevel != null) {
                                color = pairColor(modelIndex.pm10IndexLevel.id)
                                nameLevel = modelIndex.pm10IndexLevel.indexLevelName
                            }
                        "NO2" ->
                            if (modelIndex.no2IndexLevel != null) {
                                color = pairColor(modelIndex.no2IndexLevel.id)
                                nameLevel = modelIndex.no2IndexLevel.indexLevelName
                            }
                        "O3" ->
                            if (modelIndex.o3IndexLevel != null) {
                                color = pairColor(modelIndex.o3IndexLevel.id)
                                nameLevel = modelIndex.o3IndexLevel.indexLevelName
                            }
                        "SO2" ->
                            if (modelIndex.so2IndexLevel != null) {
                                color = pairColor(modelIndex.so2IndexLevel.id)
                                nameLevel = modelIndex.so2IndexLevel.indexLevelName
                            }
                        "C6H6" ->
                            if (modelIndex.c6h6IndexLevel != null) {
                                color = pairColor(modelIndex.c6h6IndexLevel.id)
                                nameLevel = modelIndex.c6h6IndexLevel.indexLevelName
                            }
                        "ST" ->
                            if (modelIndex.stIndexLevel != null) {
                                color = pairColor(modelIndex.stIndexLevel.id)
                                nameLevel = modelIndex.stIndexLevel.indexLevelName
                            }
                        "PM2.5" ->
                            if (modelIndex.pm25IndexLevel != null) {
                                color = pairColor(modelIndex.pm25IndexLevel.id)
                                nameLevel = modelIndex.pm25IndexLevel.indexLevelName
                            }


                        else -> {
                            if (modelIndex.stIndexLevel != null) {
                                color = pairColor(modelIndex.stIndexLevel.id)
                                nameLevel = modelIndex.stIndexLevel.indexLevelName

                            }
                        }

                    }

                    jakosc.text = nameLevel
                    jakosc.setTextColor(resources.getColor(color, null))
                    jakosc.visibility = View.VISIBLE


                }, {

                    Log.d(requireActivity().packageName, "Call  ${it.message}")
                    jakosc.text = "Brak indeksu"
                    jakosc.visibility = View.VISIBLE

                })
        )
    }

    fun pairColor(model: Int): Int {
        var color1: Int
        when (model) {
            0 -> {
                color1 =
                    R.color.colorbdb
            }
            1 -> {
                color1 =
                    R.color.colordb
            }
            2 -> {
                color1 =
                    R.color.colordst
            }
            3 -> {
                color1 =
                    R.color.colordop
            }
            4 -> {
                color1 =
                    R.color.colorndst
            }
            else -> {
                color1 =
                    R.color.colorndst
            }
        }
        return color1
    }

    override fun getDataStationSensor() {
        sensorbyIDList = ArrayList<SensorbyID>()

        compositeDisposable.add(
            service.getDataRX(if (myPreference.getID() != "") myPreference.getID() else "14")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sensorsNameList = it
                    sensorIDList.clear()
                    for (sensorsName in sensorsNameList) {
                        sensorIDList.add(sensorsName.id.toString())
                        compositeDisposable.add(
                            service.getSensorRX(sensorsName.id.toString()).subscribeOn(
                                Schedulers.io()
                            )
                                .observeOn(AndroidSchedulers.mainThread()).subscribe({ sensorByID ->
                                    sensorbyIDList.add(sensorByID)
                                    if (sensorbyIDList.size > 0) {
                                        adapterSensor.setData(sensorbyIDList)
                                    }
                                }, { t ->
                                    t.printStackTrace()
                                })
                        )
                    }

                }, { t ->
                    t.printStackTrace()
                })
        )

    }

    override fun onDestroy() {
        compositeDisposable.add(disposable)
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
