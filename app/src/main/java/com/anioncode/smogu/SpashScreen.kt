package com.anioncode.smogu

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_spash_screen.*

class SpashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spash_screen)

//
        var intent: Intent = Intent(this@SpashScreen, DashActivity::class.java)
        startActivity(intent)
        finish()

        splash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
//                var intent: Intent = Intent(this@SpashScreen, DashActivity::class.java)
//                startActivity(intent)
//                finish()
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }

        })
        splash.playAnimation()

//        if (splash.progress==100f){
//            Toast.makeText(this@SpashScreen,"OK",Toast.LENGTH_LONG).show()
//        }
    }
}


//
//public void addAnimatorListener(Animator.AnimatorListener listener) {
//    lottieDrawable.addAnimatorListener(listener);
//}
//public void addAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener updateListener) {
//    lottieDrawable.addAnimatorUpdateListener(updateListener);
//}
