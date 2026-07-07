package net.the_okazakis.workoutbrisk


import android.os.Bundle



class Tsumasakidachi :  BaseActivity() {

    private val defaultTimes = 15
    private val defaultReps = 1

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (exTimes <= maxExTimes) {
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.tv_times_format, exTimes, maxExTimes)
                        tv2.text = getString(R.string.slow_up)
                        playSoundSingle(sndSlowUp)
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
                        playSoundSingle(sndSlowDown)
                    }
                    8 ->  {
                        exTimes ++ ;num = 0
                    }
                    else -> {}
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
            "椅子または壁に手をつき、背筋を伸ばし、ゆっくりつま先立ちになる。ゆっくりかかとを下ろす。かかとは床につけないと効果的。息を止めない。15回標準。"

          val standardText ="運動回数15回標準　最大99回"
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
              openYoutube("https://youtu.be/pVUqFOD_1M0?t=63")
          }
          btnChangeTimes.setOnClickListener {
              // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
              openChangeTimes(standardText, maxLimit,maxRep)
          }
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