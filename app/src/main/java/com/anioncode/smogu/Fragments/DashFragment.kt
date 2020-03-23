package com.anioncode.smogu.Fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.R
import kotlinx.android.synthetic.main.fragment_dash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class DashFragment : Fragment() {
    lateinit var modelIndex: ModelIndex

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_dash, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDataStation()
        maps.setOnClickListener(View.OnClickListener {
            //var intent: Intent = Intent(activity, MainActivity::class.java)
            // startActivity(intent)

            getFragmentManager()?.beginTransaction()
                ?.replace(R.id.fragment, MapFragment(), "SOMETAG")?.commit();


        })
    }

    private fun getDataStation() {
        val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)

        api.getIndex("14").enqueue(object : Callback<ModelIndex> {

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

                println("${response.body()} ttttttttttttttttttt")
            }

            override fun onFailure(call: Call<ModelIndex>, t: Throwable) {
                Log.d("MainActivity1313x: ", "Call  ${t.message}")

            }

        })
    }
}
