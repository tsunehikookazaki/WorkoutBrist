package net.the_okazakis.workoutbrisk

import android.os.Bundle

class Kamatakakato : BaseActivity() {

    private val defaultTimes = 10
    private val defaultReps = 1
    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (exTimes <= maxExTimes) {  //続ける条件　　maxExTimesまで
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.tv_times_format, exTimes, maxExTimes)
                        tv2.text = getString(R.string.msg_toes_up)
                        playSoundSingle(sndToesUp)
                    }

                    3 -> {
                        tv2.text = getString(R.string.msg_toes_stand)
                        playSoundSingle(sndStandToes)

                    }
                    4 -> {
                        tv2.text = getString(R.string.msg_stretch_further)
                    }

                    5 -> {
                        tv2.text = getString(R.string.msg_drop_heels)
                        playSoundSingle(sndDropDown)
                    }
                    6 ->{
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

        val myExplanation =
            "鎌田式 かかと落とし\n背筋を伸ばし、両足を肩幅に広げ、ゆっくりつま先立ちになる。ゆっくりかかとを落として、すとんと衝撃を与える。膝を痛めないように少しだけ曲げる。10回で1セット。1セット標準。目指せ1日3セット"

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
            openYoutube("https://youtu.be/gEdSC2LGc10?t=36")
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