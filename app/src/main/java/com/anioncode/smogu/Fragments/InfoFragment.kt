package com.anioncode.smogu.Fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.anioncode.smogu.R
import kotlinx.android.synthetic.main.fragment_info.view.*

/**
 * A simple [Fragment] subclass.
 */
class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view:View= inflater.inflate(R.layout.fragment_info, container, false)
        // Inflate the layout for this fragment

//        view.more.setOnClickListener {
//            val url = "https://powietrze.gios.gov.pl"
//            val i = Intent(Intent.ACTION_VIEW)
//            i.data = Uri.parse(url)
//            startActivity(i)
//        }
        return view
    }


}
