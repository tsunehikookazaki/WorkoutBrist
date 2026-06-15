package net.the_okazakis.workoutstrech

import android.os.Bundle

class KamatasquatTable :  BaseActivity() {
    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "$extimes/$maxextimes 回"
                        tv2.text = getString(R.string.down)
                        playSoundSingle(snddown)
                    }
                    2 -> {
                        tv2.text = getString(R.string.keep5s)
                        playSoundSingle(sndkeep5)
                    }
                    in 3..7 -> playSoundSingle(sounds[num - 3])
                   8 -> {
                        tv2.text = getString(R.string.up)
                       playSoundSingle(sndup)
                    }
                    9 -> {
                        num = 0; extimes++
                    }
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
              "両手をテーブルにのせ、足は肩幅に広げる。" +
                "息を吐きながら(吸いながら)、お尻を突き出すように腰を下げる。" +
                "\n下げたまま5秒間キープして腰を上げる。" +
                "\n膝がつま先より出ないように。背中が曲がらないように。" +
                "呼吸を止めない\n\n10回で1セット。1セット標準　目指せ1日3セット！"

          val StandardText ="10回で1セット。1セット標準。"
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
              openYoutube("https://youtu.be/e9SqQ71tSX4?t=38")
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