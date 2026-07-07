package net.the_okazakis.workoutbrisk

import android.os.Bundle

class NaiGaiten : BaseActivity() {

    private val defaultTimes = 1
    private val defaultReps = 10
    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (exTimes <= maxExTimes) {  //続ける条件　　maxExTimesまで

                when (num) {

                    2 -> {
                        tv.text = getString(R.string.tv_times_format, exTimes, maxExTimes)
                        tv2.text = getString(R.string.slow_open)
                        playSoundSingle(sndSlowOpen)
                    }
                    5 -> {
                        tv2.text = getString(R.string.slow_close)
                        playSoundSingle(sndSlowClose)
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
            "内もも(内転筋)のストレッチ。\n床に座り、足を前に出し足裏を合わせる。または、床に寝て両膝を曲げて揃える。" +
                    "\n両足を外に倒し足を広げる。広げた時に少し手で押さえ、力を加え足を開く。" +
                    "\n\n10回が1セット。1セットが標準"

        val standardText ="10回で1セット、1セット（回）標準　　最大99回"
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
            openYoutube("https://youtu.be/kM_M8TeN1qY?t=155")
        }
        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(standardText, maxLimit,maxRep)
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