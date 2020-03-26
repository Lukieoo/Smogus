package com.anioncode.smogu.Activity

import android.Manifest
import android.animation.Animator
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.CONST.MyVariables
import com.anioncode.smogu.CONST.MyVariables.Companion.modelIndexList
import com.anioncode.smogu.CONST.MyVariables.Companion.sizedApplication
import com.anioncode.smogu.CONST.MyVariables.Companion.stationList
import com.anioncode.smogu.Fragments.ChartFragment
import com.anioncode.smogu.Fragments.InfoFragment
import com.anioncode.smogu.Fragments.MapFragment
import com.anioncode.smogu.Fragments.StatsFragment
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.R
import com.google.android.material.navigation.NavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dash.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)

        MyVariables.modelIndexList = ArrayList<ModelIndex>()//inicjalizacja danych mapy
        MyVariables.sensorIDList = ArrayList<String>()//inicjalizacja danych mapy

        val service = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)
        setPermission()

        service.findAllRX().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ findall ->

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
            }, { t ->
                t.message
            })


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
                    }, { t ->
                        t.message
                    })
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
        }

        drawer.closeDrawer(GravityCompat.START)
    }

}
