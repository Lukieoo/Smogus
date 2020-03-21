package com.anioncode.smogu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_dash.*

class DashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)
        maps.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(this@DashActivity, MainActivity::class.java)
            startActivity(intent)
        })

    }
}
