package net.the_okazakis.workoutbrisk

import android.os.Bundle

class KamatasquatTable :  BaseActivity() {

    private val defaultTimes = 10
    private val defaultReps = 1

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (exTimes <= maxExTimes) {
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.tv_times_format, exTimes, maxExTimes)
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
                        num = 0; exTimes++
                    }
                }
                // ← これ追加
                handler.postDelayed(this, 1000)

            } else {
                handleTrainingComplete(tv2, btnBack, btnChangeTimes, btnYoutube) {
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

          val standardText ="10回で1セット。1セット標準。"
          val maxLimit = 99
          val maxRep =30

          // すべての共通初期化を実行
          initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
          // 音声をロード
          loadAllStandardSounds()

          // 各種クリックリスナー
          btnStart.setOnClickListener {
              setUIForStarting(runnable, -2, btnBack, btnChangeTimes, btnYoutube)
          }


          btnStop.setOnClickListener {
              setUIForStopping(btnBack, btnChangeTimes, btnYoutube)
              handler.removeCallbacks(runnable)
          }

          btnRestart.setOnClickListener {
              restartTraining(runnable, btnBack, btnChangeTimes, btnYoutube)
          }

          btnSpeed.setOnClickListener {
              setUIForSpeedStarting(runnable, -3, btnBack, btnYoutube, btnChangeTimes)
          }

          btnBack.setOnClickListener {
              finish()
          }

          btnYoutube.setOnClickListener {
              openYoutube("https://youtu.be/e9SqQ71tSX4?t=38")
          }
          btnChangeTimes.setOnClickListener {
              // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
              openChangeTimes(standardText, maxLimit, maxRep)
          }

          loadSettingsTick(defaultTimes, defaultReps)

      }
    override fun onDestroy() {
        // soundPool.release() // サービスで共有しているため、Activityでは解放しない
        super.onDestroy()
    }

    // 👇ここに書く（onCreateの下）
    override fun onResume() {
        super.onResume()
        loadSettingsTick(defaultTimes, defaultReps)
        updateHeaderUI()
    }
}