package net.the_okazakis.workoutbrisk

import android.os.Bundle
class Ashijanken :BaseActivity() {

    private val defaultTimes = 16
    private val defaultReps = 1


    private val runnable = object : Runnable {
        override fun run() {

            timeCount++
            num++
            if (exTimes <= maxExTimes) {
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.tv_times_format, exTimes, maxExTimes)
                        tv2.text = getString(R.string.goo)
                        playSoundSingle(sndgo)
                    }

                    2 -> {
                        if (choki) {
                            choki = false
                            tv2.text = getString(R.string.choki)
                            playSoundSingle(sndchoki)
                        } else {
                            tv2.text = getString(R.string.urachoki)
                            playSoundSingle(sndurachoki)
                            choki = true
                        }
                    }

                    3 -> {
                        tv2.text = getString(R.string.pa)
                        playSoundSingle(sndpa)
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
        val myExplanation =
             "足の指でグー、チョキ(裏)　パーをする。" +
                "\nグー指はなるべく深く曲げる。チョキ、パーはなるべく大きく開く." +
                     " グーのこぶしが出るように" +
                "\n\nグー　チョキ　パーを16回標準"

        val standardText = "グー）チョキ　パー、グー（裏）チョキ　パーを各8回　16回標準\n最大99"
        val maxLimit = 99
        val maxRep =30
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
            openYoutube("https://youtu.be/p6agyQN2gco?t=102")
        }

        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(standardText, maxLimit, maxRep )
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