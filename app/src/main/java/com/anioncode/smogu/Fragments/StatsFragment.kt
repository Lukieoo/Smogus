package com.anioncode.smogu.Fragments


import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.Adapter.SensorAdapter
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables
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

            getDataStation()



        getDataStationSensor()
        maps.setOnClickListener(View.OnClickListener {
            //var intent: Intent = Intent(activity, MainActivity::class.java)
            // startActivity(intent)

            getFragmentManager()?.beginTransaction()
                ?.replace(R.id.fragment, MapFragment(), "SOMETAG")?.commit();


        })

    }

    private fun getDataStation() {
        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)

        api.getIndex(if (!myPreference.getID().toString().equals("")) myPreference.getID().toString() else "14")
            .enqueue(object : Callback<ModelIndex> {

                override fun onResponse(
                    call: Call<ModelIndex>,
                    response: Response<ModelIndex>
                ) {
                    var color: Int =
                        R.color.colorbdb
                    modelIndex = response.body()!!

                    if (modelIndex.pm10IndexLevel != null) {
                        when (modelIndex.pm10IndexLevel.id) {
                            0 -> {
                                color =
                                    R.color.colorbdb
                            }
                            1 -> {
                                color =
                                    R.color.colordb
                            }
                            2 -> {
                                color =
                                    R.color.colordst
                            }
                            3 -> {
                                color =
                                    R.color.colordop
                            }
                            4 -> {
                                color =
                                    R.color.colorndst
                            }
                            else -> {
                                color =
                                    R.color.colorndst
                            }
                        }
                    }

                    activity?.runOnUiThread {
                        jakosc.text = modelIndex.stIndexLevel.indexLevelName
                        jakosc.setTextColor(resources.getColor(color))
                    }


                }

                override fun onFailure(call: Call<ModelIndex>, t: Throwable) {
                    Log.d("MainActivity1313x: ", "Call  ${t.message}")

                }

            })
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
                                                        SensorAdapter(sensorbyIDList, activity!!);
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
