package com.anioncode.smogu.Fragments


import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.Adapter.SensorAdapter
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorIDList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorbyIDList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorsNameList
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.Model.ModelSensor.SensorsName
import com.anioncode.smogu.Model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import kotlinx.android.synthetic.main.fragment_dash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : Fragment() {
    lateinit var modelIndex: ModelIndex
    lateinit var myPreference: MyPreference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Preferences
        myPreference = MyPreference(requireContext())

        if (!myPreference.getID().equals("")) {
            nameStation.text = myPreference.getSTATION_NAME()
            nameStreet.text = myPreference.getSTATION_STREET()
            nameProvince.text = myPreference.getSTATION_PROVINCE()
        }

        if (isOnline(requireContext()))

            getDataStation("ST")



        getDataStationSensor()
        maps.setOnClickListener(View.OnClickListener {
            //var intent: Intent = Intent(activity, MainActivity::class.java)
            // startActivity(intent)

            getFragmentManager()?.beginTransaction()
                ?.replace(R.id.fragment, MapFragment(), "SOMETAG")?.commit();


        })

    }

    private fun getDataStation(Id: String) {
        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
        jakosc.visibility=View.INVISIBLE
        api.getIndex(if (!myPreference.getID().toString().equals("")) myPreference.getID().toString() else "114")
            .enqueue(object : Callback<ModelIndex> {

                override fun onResponse(
                    call: Call<ModelIndex>,
                    response: Response<ModelIndex>
                ) {
                    var color: Int =
                        R.color.colorbdb
                    modelIndex = response.body()!!
                    var nameLevel:String=""

                    when (Id) {

                        "PM10" ->
                            if (modelIndex.pm10IndexLevel != null) {
                                color = pairColor(modelIndex.pm10IndexLevel.id)
                                nameLevel=modelIndex.pm10IndexLevel.indexLevelName
                            }
                        "NO2" ->
                            if (modelIndex.no2IndexLevel != null) {
                                color = pairColor(modelIndex.no2IndexLevel.id)
                                nameLevel=modelIndex.no2IndexLevel.indexLevelName
                            }
                        "O3" ->
                            if (modelIndex.o3IndexLevel != null) {
                                color = pairColor(modelIndex.o3IndexLevel.id)
                                nameLevel=modelIndex.o3IndexLevel.indexLevelName
                            }
                        "SO2" ->
                            if (modelIndex.so2IndexLevel != null) {
                                color = pairColor(modelIndex.so2IndexLevel.id)
                                nameLevel=modelIndex.so2IndexLevel.indexLevelName
                            }
                        "C6H6" ->
                            if (modelIndex.c6h6IndexLevel != null) {
                                color = pairColor(modelIndex.c6h6IndexLevel.id)
                                nameLevel=modelIndex.c6h6IndexLevel.indexLevelName
                            }
                        "ST" ->
                            if (modelIndex.stIndexLevel != null) {
                                color = pairColor(modelIndex.stIndexLevel.id)
                                nameLevel=modelIndex.stIndexLevel.indexLevelName
                            }
                        "PM2.5" ->
                            if (modelIndex.pm25IndexLevel != null) {
                                color = pairColor(modelIndex.pm25IndexLevel.id)
                                nameLevel=modelIndex.pm25IndexLevel.indexLevelName
                            }


                        else -> {
                            if (modelIndex.stIndexLevel != null) {
                                color = pairColor(modelIndex.stIndexLevel.id)
                                nameLevel=modelIndex.stIndexLevel.indexLevelName

                            }
                        }

                    }

                    activity?.runOnUiThread {
                        jakosc.text =nameLevel
                        jakosc.setTextColor(resources.getColor(color))
                        jakosc.visibility=View.VISIBLE
                    }


                }

                override fun onFailure(call: Call<ModelIndex>, t: Throwable) {
                    Log.d("MainActivity1313x: ", "Call  ${t.message}")

                }

            })
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

    private fun getDataStationSensor() {
        sensorbyIDList = ArrayList<SensorbyID>()//inicjalizacja danych mapy
        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)

        api.getData(if (!myPreference.getID().equals("")) myPreference.getID() else "14")
            .enqueue(object : Callback<List<SensorsName>> {

                override fun onResponse(
                    call: Call<List<SensorsName>>,
                    response: Response<List<SensorsName>>
                ) {
                    sensorsNameList = response.body()!!
                    activity?.runOnUiThread {
                        sensorIDList.clear()
                        for (sensorsName in sensorsNameList) {

                            sensorIDList.add(sensorsName.id.toString())

//                            if (sensorsName.param.paramCode.equals("PM10")) {
//
//                                myPreference.setSENSORID(sensorsName.id.toString())
//                                println("${sensorsName.id} MS897")
//
//                            }
                            api.getSensor(sensorsName.id.toString())
                                .enqueue(object : Callback<SensorbyID> {

                                    override fun onResponse(
                                        call: Call<SensorbyID>,
                                        response: Response<SensorbyID>
                                    ) {
                                        sensorbyIDList.add(response.body()!!)
                                        if (sensorbyIDList.size > 0) {
                                            print(sensorbyIDList)
                                            activity?.runOnUiThread {
                                                RecyclerView.apply {
                                                    layoutManager = LinearLayoutManager(
                                                        activity,
                                                        LinearLayoutManager.HORIZONTAL,
                                                        false
                                                    )
                                                    adapter =
                                                        SensorAdapter(
                                                            sensorbyIDList,
                                                            activity!!,
                                                            clickListner = object :
                                                                SensorAdapter.OnItemClickListner {
                                                                override fun onItemClick(model: SensorbyID) {
                                                                    partJakosc.text =
                                                                        "Ocena jakości ${model.key}:"
                                                                    getDataStation(model.key)

                                                                }

                                                            }
                                                        );
                                                }
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<SensorbyID>, t: Throwable) {

                                    }

                                })
                        }
                    }
                }

                override fun onFailure(call: Call<List<SensorsName>>, t: Throwable) {

                }

            })
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
