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


class HipaddactionBall : BaseActivity() {

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            if (extimes <= maxextimes) {
                num++
                tv.text = "${extimes}/$maxextimes セット"
                when (num) {
                    -5 -> {
                        if (!isStart) {
                            tv2.text = "ちょっと休憩"
                            playSoundSingle(sndbreak)
                        }
                    }

                    -4 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                        }
                    }

                    in -3..-1 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sounds[num * (-1) - 1])
                        }
                    }

                    0 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sndpi)
                        }
                    }

                    in 1..50 -> {
                        tv2.text = getString(R.string.tubu) + "  $num/50 回 "
                        playSoundSingle(sndtsubushite)
                    }

                    51 -> {
                        isStart = false
                        num = -6
                        extimes++
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

         //説明文
         val myExplanation =
            "椅子に座って(寝ながらやってもOK)、膝の間にボール(枕、クッションでもOK)をはさむ。\nモモに力を入れてボールを挟んでつぶす。力をぬいて緩める。再度つぶして、力を抜く" +
                    "これをリズミカルに繰り返す。\n\n50回で1セット　3セット標準" +
                    "\n\nボールは100均一で"

         val StandardText = "50回で1セット。3セット標準"
         val masxlimit = 99
         val maxRep =30

         // すべての共通初期化を実行
         initializeStandardSettings(myExplanation)
         // 音声をロード
         loadAllStandardSounds()

         // 各種クリックリスナー
         btnstart.setOnClickListener {
             setUIForStarting(runnable,-1,btnback, btnChangeTimes, btnyoutube)
         }


         btnstop.setOnClickListener {
             setUIForStopping(btnback, btnChangeTimes, btnyoutube)
             handler.removeCallbacks(runnable)
         }

         btnrerstart.setOnClickListener {
             restartTraining(runnable,btnback,btnChangeTimes,btnyoutube)
         }

         btnspeed.setOnClickListener {
             setUIForSpeedStarting(runnable, -1, btnback,btnyoutube, btnChangeTimes)
         }

         btnback.setOnClickListener {
             finish()
         }
         //Youtubeのリンクを開く
         btnyoutube.setOnClickListener {
             // 固有のURLを渡すだけ
             openYoutube("https://youtu.be/n7Munc_C_Xo?t=3")
         }


         btnChangeTimes.setOnClickListener {
             // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
             openChangeTimes(StandardText, masxlimit,maxRep)
         }
         loadSettingsTick()
     }
    override fun onDestroy() {

        soundPool.release()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/${maxextimes} 回"   // ← UIも更新
    }
}