package net.the_okazakis.workoutbrisk

import android.os.Bundle
class Hihukukinn : BaseActivity() {

    private val defaultTimes = 2
    private val defaultReps = 1



    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (exTimes <= maxExTimes) {
                tv.text = getString(R.string.tv_sets_format, exTimes, maxExTimes)
                when (num) {
                    0 -> {
                        tv2.text = getString(R.string.noba)
                        playSoundSingle(sndStretch)
                    }

                    in 1..15 -> {
                        playSoundSingle(sounds[num - 1])
                        tv2.text = getString(R.string.tv_seconds_format, num)
                    }

                    17 -> {
                        if (count) {   //false →true 足が2回変わったら
                            exTimes++   //回数を増やす
                            count = false    //
                        } else {
                            count = true
                        }
                        if (exTimes <= maxExTimes) {
                            tv2.text = getString(R.string.msg_change_leg_simple)
                            playSoundSingle(sndChangeLeg)
                            num = -3
                        }
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
            "腓腹筋伸ばし\n両手を壁に付けて身体を斜めにし、伸ばしたい方の足をゆっくり後ろに引き、踵を付ける。" +
                    "腰、背中を曲げない。踵が浮かないように。\n\n左右15秒づつが1セット。２セット標準"

        val standardText = "左右15秒づつが1セット。２セット標準\n（運動回数２回標準  最大99回）"
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
        //Youtubeのリンクを開く
        btnYoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/suG47Nx_H5A?t=226")
        }

        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(standardText, maxLimit, maxRep)
        }
        loadSettingsTick(defaultTimes, defaultReps)
        updateHeaderUI()
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