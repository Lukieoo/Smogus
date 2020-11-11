package com.anioncode.smogu.activity

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables
import com.anioncode.smogu.CONST.MyVariables.Companion.modelIndexList
import com.anioncode.smogu.CONST.MyVariables.Companion.sizedApplication
import com.anioncode.smogu.CONST.MyVariables.Companion.stationList
import com.anioncode.smogu.fragments.*
import com.anioncode.smogu.model.ModelIndex.ModelIndex
import com.anioncode.smogu.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dash.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)

         modelIndexList = ArrayList<ModelIndex>()//inicjalizacja danych mapy
        MyVariables.sensorIDList = ArrayList<String>()//inicjalizacja danych mapy
        var syPreference = MyPreference(applicationContext)
        val service = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
        setPermission()

        if (!isOnline(applicationContext)) {
            Snackbar.make(
                getWindow().getDecorView(), "Brak połączenia",
                Snackbar.LENGTH_LONG
            ).setAction("Action", null).show()
        }

        if (syPreference.getFIRST_LOGED()) {

            val builder = AlertDialog.Builder(this@MainActivity)
            syPreference.setFIRST_LOGED(false)
            // Set the alert dialog title
            builder.setTitle("Smoguś")
            builder.setIcon(resources.getDrawable(R.drawable.draw, null))
            // Display a message on alert dialog
            builder.setMessage("Witaj w aplikacji 'Smoguś'  przejdź do mapy i wybierz swoją stację \uD83D\uDE00")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("Mapa") { dialog, which ->
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment,
                    MapFragment(), "SOMETAG"
                ).commit()
                dialog.dismiss()
            }

            // Display a negative button on alert dialog
            builder.setNegativeButton("Później") { dialog, which ->
                dialog.cancel()
            }


            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()
        }

        service.findAllRX().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { findall ->
                stationList = findall
                sizedApplication = stationList.size
                for (findAllModel in stationList) {
                    service.getIndexRX(findAllModel.id.toString()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ model ->
                            modelIndexList.add(model)
                            println("moveIN ${modelIndexList.size}")
                        }, {

                        })
                }
            }


        getSupportFragmentManager().beginTransaction().replace(
            R.id.fragment,
            StatsFragment(), "SOMETAG"
        ).commit()

        nav_view.setNavigationItemSelectedListener(this);

        drawer.setScrimColor(resources.getColor(android.R.color.transparent)) //Niewidzialne tło przesuwania

        val toggle: ActionBarDrawerToggle =
            object : ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            ) {
                override fun onDrawerSlide(
                    drawerView: View,
                    slideOffset: Float
                ) {
                    super.onDrawerSlide(drawerView, slideOffset)

                    if (drawer.isDrawerVisible(Gravity.RIGHT)) {
                        val slideX = -1 * (drawerView.width * slideOffset)
                        moveRelative.translationX = slideX
                    } else {
                        val slideX = drawerView.width * slideOffset
                        moveRelative.translationX = slideX
                    }
                }
            }

        drawer.addDrawerListener(toggle)
        toggle.syncState()


        sync.setOnClickListener {
            sync.playAnimation()
        }

        sync.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {

            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {
                //   spinner.setSelection(0)
                val service =
                    RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
                stationList = emptyList()
                sizedApplication = 0
                modelIndexList.clear()

                service.findAllRX().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ findall ->

                        stationList = findall
                        sizedApplication = stationList.size
                        var k: Int = 0;
                        for (findAllModel in stationList) {
                            service.getIndexRX(findAllModel.id.toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ model ->
                                    modelIndexList.add(model)
                                    k++
                                    if (k == stationList.size) {

                                        var frg: Fragment? = null
                                        frg = supportFragmentManager.findFragmentByTag("SOMETAG")
                                        val ft: FragmentTransaction =
                                            supportFragmentManager.beginTransaction()
                                        frg?.let { ft.detach(it) }
                                        frg?.let { ft.attach(it) }
                                        ft.commit()
                                        sync.pauseAnimation()

                                    }
                                    println("moveIN $k ${modelIndexList.size}")
                                }, {

                                })

                        }
                    } )
            }

        })
    }

    private fun setPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        selectItemDrawer(item)

        return true
    }

    private fun selectItemDrawer(item: MenuItem) {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.main) {
            getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment,
                StatsFragment(), "SOMETAG"
            ).commit()
        } else if (id == R.id.maps) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment,
                MapFragment(), "SOMETAG"
            ).commit()
        } else if (id == R.id.info) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment,
                InfoFragment(), "SOMETAG"
            ).commit()
        } else if (id == R.id.stats) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment,
                ChartFragment(), "SOMETAG"
            ).commit()
        } else if (id == R.id.about) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment,
                AboutFragment(), "SOMETAG"
            ).commit()
        }

        drawer.closeDrawer(GravityCompat.START)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
