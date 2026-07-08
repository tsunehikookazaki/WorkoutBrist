package net.the_okazakis.workoutbrisk

import android.os.Bundle
class Ball : BaseActivity() {

    private val defaultTimes = 3   //セット当たり回数秒数
    private val defaultReps = 1   //セット数　
    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (exTimes <= maxExTimes) {
                tv.text = getString(R.string.tv_times_format, exTimes, maxExTimes)
                when(num) {
                    0->{tv2.text = getString(R.string.tubu)
                        playSoundSingle(sndtsubu)
                    }
                    in 1..10-> {
                        playSoundSingle(sounds[num - 1])
                        tv2.text = getString(R.string.unit_seconds_format, num)
                    }
                    11 -> {
                        playSoundSingle(sndloosen)
                        exTimes++   //回数を増やす
                        tv2.text = getString(R.string.yuru)
                        num = -1
                    }
                    else ->{}
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
            "膝裏ボール潰し\n椅子に座り、膝の裏に小さめのボール(または丸めたタオル)を挟み、ぎゅーっと10秒間押しつぶす。10秒3回で1セット。1セット標準。"

        val standardText = "10秒3回で1セット。1セット標準。\n運動回数　1回標準　最大99回"
        val maxLimit = 99
        val maxRep = 30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
        // 音声をロード
        loadAllStandardSounds()

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
        //Youtubeのリンクを開く
        btnYoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/MVzREF4j1lI?t=4")
        }

        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(standardText, maxLimit, maxRep)
        }
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