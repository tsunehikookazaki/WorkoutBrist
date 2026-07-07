package net.the_okazakis.workoutbrisk

import android.os.Bundle

class Ashiage : BaseActivity() {

    private val defaultTimes = 3   //セット当たり回数秒数
    private val defaultReps = 1   //セット数　

    private val runnable = object : Runnable {
        override fun run() {

            timeCount++

            num++
            if (exTimes <= maxExTimes) {
                tv.text = getString(R.string.tv_times_format, exTimes, maxExTimes)
                when (num) {
                    1 -> {
                        tv2.text = getString(R.string.up)
                        playSoundSingle(sndup)
                    }

                    in 2..11 -> {
                        tv2.text = getString(R.string.tv_seconds_format, num - 1)
                        val soundId = sounds.getOrNull(num - 2) ?: 0
                        playSoundSingle(soundId)

                    }

                    12 -> {
                        count = if (count) {
                            exTimes++; false
                        } else {
                            true
                        }
                        if (exTimes <= maxExTimes) {
                            tv2.text = getString(R.string.msg_change_leg)
                            playSoundSingle(sndChangeLeg)
                            num = 0
                        }
                    }
                }
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
            "出来るだけ浅く座り、背もたれにもたれない。身体を後ろに倒さないで、脚をまっすぐにしたまま、" +
                    "つま先が10cm以上になるよう、足(モモ)を上げる。\n膝を曲げないで、踵から上げる気持ちで。" +
                    "\n\n左右10秒ずつ３回が１セット。1セットが標準"

        val standardText = "左右10秒ずつ 3回（1セット）"
        val maxLimit = 99
        val maxRep = 30
        // すべての共通初期化を実行（初期値を渡す）
        initializeStandardSettings(myExplanation, defaultTimes, defaultReps)


        btnStart.setOnClickListener {
            setUIForStarting(
                runnable,
                -3,
                btnBack,
                btnChangeTimes,
                btnYoutube,
            )
        }

        btnStop.setOnClickListener {
            setUIForStopping(btnBack, btnChangeTimes, btnYoutube)
            handler.removeCallbacks(runnable)
        }

        btnRestart.setOnClickListener {
            setUIForStarting(
                runnable,
                -3,
                btnBack,
                btnChangeTimes,
                btnYoutube
            )
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnYoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/suG47Nx_H5A?t=42")
        }
        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(standardText, maxLimit, maxRep)
        }
       loadAllStandardSounds()
    }
        override fun onResume() {
            super.onResume()
            loadSettingsTick(defaultTimes, defaultReps)
            updateHeaderUI()
        }
    }


