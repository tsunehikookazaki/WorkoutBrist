package net.the_okazakis.workoutbrisk

import android.os.Bundle
import android.view.View

class Sukebo : BaseActivity() {

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
                        tv2.text = getString(R.string.slow_stre)
                        playSoundSingle(sndStretch)
                    }
                    in 2..4 ->   playSoundSingle(sounds[num-2])
                     5 -> {
                        tv2.text = getString(R.string.slow_bent)
                         playSoundSingle(sndBend)
                    }
                    in 6..8 ->  playSoundSingle(sounds[num-6])
                    9 -> {
                        playSoundSingle(sounds[3])
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
        setContentView(R.layout.activity_sub)
        val myExplanation =
            "椅子に腰かけ、スケボーに両足をのせて足を曲げ伸ばす。\n" +
                "つま先、かかとがスケボーから離れないように。\n\n10回で1セット。1セットが標準。" +
                "\nスケボーの代わりにタオルでも出来る"

        val standardText ="10回で1セット、1セット標準"
        val maxLimit = 99
        val maxRep =30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
        // 音声をロード
        loadAllStandardSounds()

        // YouTubeボタンを非表示
        btnYoutube.visibility = View.INVISIBLE

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
            openYoutube("https://youtu.be/sRvbL3eflz0")
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