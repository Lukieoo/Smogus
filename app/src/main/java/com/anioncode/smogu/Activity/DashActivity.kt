package com.anioncode.smogu.Activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.Fragments.StatsFragment
import com.anioncode.smogu.Fragments.MapFragment
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.CONST.MyVariables
import com.anioncode.smogu.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_dash.*

class DashActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)

        MyVariables.modelIndexList = ArrayList<ModelIndex>()//inicjalizacja danych mapy




        getSupportFragmentManager().beginTransaction().replace(
            R.id.fragment,
            StatsFragment(),"SOMETAG").commit()

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

                }
            }

        drawer.addDrawerListener(toggle)
        toggle.syncState()


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
                StatsFragment(),"SOMETAG").commit()
        }else if (id == R.id.maps){
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment,
                MapFragment(),"SOMETAG").commit()
        }else if (id == R.id.info){

        }

        drawer.closeDrawer(GravityCompat.START)
    }

}
