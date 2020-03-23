package com.anioncode.smogu.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anioncode.smogu.R
import kotlinx.android.synthetic.main.fragment_dash.*

/**
 * A simple [Fragment] subclass.
 */
class DashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_dash, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        maps.setOnClickListener(View.OnClickListener {
            //var intent: Intent = Intent(activity, MainActivity::class.java)
           // startActivity(intent)
            getFragmentManager()?.beginTransaction()
                ?.replace(R.id.fragment,MapFragment(),"SOMETAG")?.commit();
        })
    }
}
