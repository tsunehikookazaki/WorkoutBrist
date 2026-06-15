package net.the_okazakis.workoutstrech

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView


class Tsumasakidachi :  BaseActivity() {

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "${extimes}/$maxextimes 回"
                        tv2.text = getString(R.string.slow_up)
                        playSoundSingle(sndslowup)
                    }
                    2 -> {}
                    3 -> {
                        tv2.text = getString(R.string.keep3s)
                        playSoundSingle(sndkeepmama)
                    }
                    4 -> {}
                    5 ->{
                        playSoundSingle(sounds[1])
                    }
                    6 ->{
                        playSoundSingle(sounds[2])
                    }
                    7 -> {tv2.text =  getString(R.string.slow_down)
                        playSoundSingle(sndslowdown)
                    }
                    8 ->  {
                        extimes ++ ;num = 0
                    }
                    else -> {}
                }
                // ← これ追加
                handler.postDelayed(this, 1000)

            } else {
                handleTrainingComplete(tv2, btnback, btnChangeTimes, btnyoutube) {
                    isSaved = true
                    playSoundSingle(sndend)
                }
            }
        }
    }


      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sub)

          val myExplanation =
            "椅子または壁に手をつき、背筋を伸ばし、ゆっくりつま先立ちになる。ゆっくりかかとを下ろす。かかとは床につけないと効果的。息を止めない。15回標準。"

          val StandardText ="運動回数15回標準　最大99回"
          val masxlimit = 99
          val maxRep =30

          // すべての共通初期化を実行
          initializeStandardSettings(myExplanation)
          // 音声をロード
          loadAllStandardSounds()

          // 各種クリックリスナー
          btnstart.setOnClickListener {
              setUIForStarting(runnable,-2,btnback, btnChangeTimes, btnyoutube)
          }


          btnstop.setOnClickListener {
              setUIForStopping(btnback, btnChangeTimes, btnyoutube)
              handler.removeCallbacks(runnable)
          }

          btnrerstart.setOnClickListener {
              restartTraining(runnable,btnback,btnChangeTimes,btnyoutube)
          }

          btnspeed.setOnClickListener {
              setUIForSpeedStarting(runnable, -3, btnback,btnyoutube, btnChangeTimes)
          }

          btnback.setOnClickListener {
              finish()
          }

          btnyoutube.setOnClickListener {
              openYoutube("https://youtu.be/pVUqFOD_1M0?t=63")
          }
          btnChangeTimes.setOnClickListener {
              // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
              openChangeTimes(StandardText, masxlimit,maxRep)
          }

          loadSettingsTick()

      }
    override fun onDestroy() {
        // soundPool.release() // サービスで共有しているため、Activityでは解放しない
        super.onDestroy()
    }

    // 👇ここに書く（onCreateの下）
    override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/$maxextimes 回"   // ← UIも更新
    }
}