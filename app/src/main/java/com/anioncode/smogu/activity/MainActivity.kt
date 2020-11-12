package com.anioncode.smogu.activity

import android.animation.Animator
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController

import androidx.navigation.findNavController
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.CONST.MyVariables
import com.anioncode.smogu.CONST.MyVariables.Companion.modelIndexList
import com.anioncode.smogu.CONST.MyVariables.Companion.sensorIDList
import com.anioncode.smogu.CONST.MyVariables.Companion.sizedApplication
import com.anioncode.smogu.CONST.MyVariables.Companion.stationList
import com.anioncode.smogu.model.ModelIndex.ModelIndex
import com.anioncode.smogu.R
import com.anioncode.smogu.events.NavEvent
import com.anioncode.smogu.presenters.MainActivityPresenter
import com.anioncode.smogu.utils.SystemUtils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dashboard.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    MainActivityPresenter.View {

    @Inject
    lateinit var navEvents: PublishProcessor<NavEvent>

    @Inject
    lateinit var service : ApiService

    private lateinit var navController: NavController

    var compositeDisposable: CompositeDisposable = CompositeDisposable()


    var currentFragment = R.id.statsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //Set NavController with RxJava Subscriber
        initNavigationController()
        //Check permission about internet connection and  Location
        systemPrivilegesCheck()
        //Show Dialog if it is first time
        firstTimeAction()
        //Download Data from Api
        fetchDataFromServer()
        //Set event and init view
        initView()


    }

    override fun initView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawer.setScrimColor(resources.getColor(android.R.color.transparent, null))
        } //Invisible background

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
            sync.isClickable = false
        }

        sync.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {

                stationList = emptyList()
                sizedApplication = 0
                modelIndexList.clear()

                compositeDisposable.add(
                    service
                        .findAllRX()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { stationData ->
                            stationList = stationData
                            sizedApplication = stationList.size
                            var k: Int = 0;
                            for (stationData in stationList) {
                                compositeDisposable.add(
                                    service.getIndexRX(stationData.id.toString())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ model ->
                                            modelIndexList.add(model)
                                            k++
                                            if (k == stationList.size) {
                                                navController =
                                                    findNavController(R.id.main_fragment_container)
                                                navController.popBackStack()
                                                navController.navigate(currentFragment)
                                                sync.pauseAnimation()
                                                sync.isClickable = true
                                                sync.frame = 0
                                            }
                                        }, {
                                            it.printStackTrace()
                                            sync.isClickable = true
                                        })
                                )

                            }
                        })
            }

        })
    }

    override fun initNavigationController() {
        navController = findNavController(R.id.main_fragment_container)

        navEvents.subscribe {
            when (it.destination) {
                NavEvent.Destination.StatsFragment -> {
                    if (navController.currentDestination?.id != R.id.statsFragment) {
                        navController.popBackStack()
                        navController.navigate(R.id.statsFragment)
                        currentFragment = R.id.statsFragment
                    }
                }
                NavEvent.Destination.ChartFragment -> {
                    if (navController.currentDestination?.id != R.id.chartFragment) {
                        navController.popBackStack()
                        navController.navigate(R.id.chartFragment)
                        currentFragment = R.id.chartFragment
                    }
                }
                NavEvent.Destination.MapFragment -> {

                    if (navController.currentDestination?.id != R.id.mapFragment) {
                        navController.popBackStack()
                        navController.navigate(R.id.mapFragment)
                        currentFragment = R.id.mapFragment
                    }
                }
                NavEvent.Destination.InfoFragment -> {
                    if (navController.currentDestination?.id != R.id.infoFragment) {
                        navController.popBackStack()
                        navController.navigate(R.id.infoFragment)
                        currentFragment = R.id.infoFragment
                    }
                }
                NavEvent.Destination.AboutFragment -> {
                    if (navController.currentDestination?.id != R.id.aboutFragment) {
                        navController.popBackStack()
                        navController.navigate(R.id.aboutFragment)
                        currentFragment = R.id.aboutFragment
                    }
                }
            }
        }
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun firstTimeAction() {
        var syPreference = MyPreference(applicationContext)
        if (syPreference.getFIRST_LOGED()) {
            val builder = AlertDialog.Builder(this@MainActivity)
            syPreference.setFIRST_LOGED(false)
            builder.setTitle("Smoguś")
            builder.setIcon(resources.getDrawable(R.drawable.draw, null))
            builder.setMessage("Witaj w aplikacji 'Smoguś'  przejdź do mapy i wybierz swoją stację \uD83D\uDE00")
            builder.setNegativeButton("Później") { dialog, _ ->
                dialog.cancel()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    override fun systemPrivilegesCheck() {
        var permission: SystemUtils = SystemUtils(activity = this)
        permission.setPermission()
        if (!permission.isOnline()) {
            Snackbar.make(
                window.decorView, "Brak połączenia",
                Snackbar.LENGTH_LONG
            ).setAction("Action", null).show()
        }
    }

    override fun fetchDataFromServer() {
        modelIndexList = ArrayList<ModelIndex>()
        sensorIDList = ArrayList<String>()

        compositeDisposable.add(service.findAllRX().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dataFromService ->
                stationList = dataFromService
                sizedApplication = stationList.size
                for (stationData in stationList) {
                    compositeDisposable.add(
                        service.getIndexRX(stationData.id.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ model ->
                                modelIndexList.add(model)
                            }, {
                                it.printStackTrace()
                            })
                    )
                }
            })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        selectItemDrawer(item)
        return true
    }

    private fun selectItemDrawer(item: MenuItem) {
        val id = item.itemId
        if (id == R.id.main) {
            navEvents.onNext(
                NavEvent(
                    NavEvent.Destination.StatsFragment
                )
            )

        } else if (id == R.id.maps) {
            navEvents.onNext(
                NavEvent(
                    NavEvent.Destination.MapFragment
                )
            )

        } else if (id == R.id.info) {
            navEvents.onNext(
                NavEvent(
                    NavEvent.Destination.InfoFragment
                )
            )
        } else if (id == R.id.stats) {
            navEvents.onNext(
                NavEvent(
                    NavEvent.Destination.ChartFragment
                )
            )
        } else if (id == R.id.about) {
            navEvents.onNext(
                NavEvent(
                    NavEvent.Destination.AboutFragment
                )
            )
        }

        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onDestroy() {

        compositeDisposable.dispose()
        super.onDestroy()
    }
}
