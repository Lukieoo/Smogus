package com.anioncode.smogu.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anioncode.smogu.BuildConfig
import com.anioncode.smogu.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject


class AboutFragment @Inject constructor() : Fragment(R.layout.fragment_about) {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //google admob init
        googleAdmob()
        //About me info
        infoFragment()
    }

    private fun googleAdmob() {
        MobileAds.initialize(activity) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun infoFragment() {
        val versionName: String = BuildConfig.VERSION_NAME
        title.text = "Wersja $versionName"
        buttonsend.setOnClickListener(View.OnClickListener { v: View? ->
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "pawkrzysciak@gmail.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Witaj")
            startActivity(Intent.createChooser(emailIntent, "Zapytanie"))
        })
    }
}