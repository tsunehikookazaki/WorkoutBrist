package net.the_okazakis.workoutbrisk

import android.os.Bundle

class ChairSquat :  BaseActivity() {

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
                        tv2.text = getString(R.string.standup)
                        playSoundSingle(sndstand)
                    }

                    in 2..5 -> {
                        playSoundSingle(sounds.getOrNull(num - 2) ?: 0)
                        tv2.text = getString(R.string.tv_seconds_format, num - 1)
                    }

                    6 -> {
                        tv2.text = getString(R.string.sitdown)
                        playSoundSingle(sndsit)

                    }

                    in 7..11 -> {
                        playSoundSingle(sounds.getOrNull(num - 7) ?: 0)
                        tv2.text = getString(R.string.tv_seconds_format, num - 6)
                    }


                    12 -> {
                        num = 0; exTimes++
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
            "椅子に座り（腰掛ける程度）、両足を肩幅に広げつま先と膝を同じ方向に向ける、ゆっくり立つ。立ち上がったら、ゆっくり（座るイメージで）腰を下ろす。\n膝がつま先より出ないように。背中が曲がらないように。反動をつけない\n\n10回で1セット。1～2セット標準。"

        val standardText = "10回で1セット。1～2セット標準"
        val maxLimit = 99
        val maxRep = 30

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
            openYoutube("https://youtu.be/61gktuGYKfI")
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