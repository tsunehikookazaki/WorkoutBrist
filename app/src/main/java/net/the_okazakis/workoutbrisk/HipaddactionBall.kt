package net.the_okazakis.workoutbrisk


import android.os.Bundle



class HipaddactionBall : BaseActivity() {

    private val defaultTimes = 3
    private val defaultReps = 50

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            if (exTimes <= maxExTimes) {
                num++
                tv.text = getString(R.string.tv_sets_format, exTimes, maxExTimes)
                when (num) {
                    -5 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.msg_rest_brief)
                            playSoundSingle(sndbreak)
                        }
                    }

                    -4 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                        }
                    }

                    in -3..-1 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sounds[num * (-1) - 1])
                        }
                    }

                    0 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sndpi)
                        }
                    }

                    in 1..50 -> {
                        tv2.text = getString(R.string.progress_action_50_format, getString(R.string.tubu), num)
                        playSoundSingle(sndtsubushite)
                    }

                    51 -> {
                        isStart = false
                        num = -6
                        exTimes++
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

         //説明文
         val myExplanation =
            "椅子に座って(寝ながらやってもOK)、膝の間にボール(枕、クッションでもOK)をはさむ。\nモモに力を入れてボールを挟んでつぶす。力をぬいて緩める。再度つぶして、力を抜く" +
                    "これをリズミカルに繰り返す。\n\n50回で1セット　3セット標準" +
                    "\n\nボールは100均一で"

         val standardText = "50回で1セット。3セット標準"
         val maxLimit = 99
         val maxRep = 99 // 上限を標準より高く設定

         // すべての共通初期化を実行
         initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
         // 音声をロード
         loadAllStandardSounds()

         // 各種クリックリスナー
         btnStart.setOnClickListener {
             setUIForStarting(runnable, -1, btnBack, btnChangeTimes, btnYoutube)
         }


         btnStop.setOnClickListener {
             setUIForStopping(btnBack, btnChangeTimes, btnYoutube)
             handler.removeCallbacks(runnable)
         }

         btnRestart.setOnClickListener {
             restartTraining(runnable, btnBack, btnChangeTimes, btnYoutube)
         }

         btnSpeed.setOnClickListener {
             setUIForSpeedStarting(runnable, -1, btnBack, btnYoutube, btnChangeTimes)
         }

         btnBack.setOnClickListener {
             finish()
         }
         //Youtubeのリンクを開く
         btnYoutube.setOnClickListener {
             // 固有のURLを渡すだけ
             openYoutube("https://youtu.be/n7Munc_C_Xo?t=3")
         }


         btnChangeTimes.setOnClickListener {
             // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
             openChangeTimes(standardText, maxLimit, maxRep)
         }
         // 初回起動時の初期値を設定
         loadSettingsTick(defaultTimes, defaultReps)
     }
    override fun onDestroy() {
        // soundPool.release() // サービスで共有しているため、Activityでは解放しない
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        loadSettingsTick(defaultTimes, defaultReps)
        updateHeaderUI()
    }
}