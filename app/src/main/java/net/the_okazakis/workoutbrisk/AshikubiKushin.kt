package net.the_okazakis.workoutbrisk

import android.os.Bundle

class AshikubiKushin : BaseActivity() {

    private val defaultTimes = 15
    private val defaultReps = 1
    private val runnable = object : Runnable {
        override fun run() {

            timeCount++
            num++
            if (exTimes < maxExTimes) {
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.tv_times_format, exTimes + 1, maxExTimes)
                        tv2.text = getString(R.string.foot_bent)
                        playSoundSingle(sndBend)
                    }

                    2 -> {
                        tv2.text = getString(R.string.foot_stre)
                        playSoundSingle(sndStretch)
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

        //説明文
        val myExplanation = "膝を伸ばして座るか、床に寝る。足首をゆっくり手前に曲げ、次に向こう側に伸ばす。\n" +
                "ふくらはぎ、足の甲が伸びているのを感じる。\n\n15回標準。"

        val standardText = "15回標準　最大99回"
        val maxLimit = 99
        val maxRep = 30
        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
        // 音声をロード
        loadAllStandardSounds()

        // 各種クリックリスナー
        btnStart.setOnClickListener {
            setUIForStarting(runnable, -3, btnBack, btnChangeTimes, btnYoutube)
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
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/IxC41pWD2Iw")
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

    override fun onResume() {
        super.onResume()
        loadSettingsTick(defaultTimes, defaultReps)
        updateHeaderUI()
    }
}