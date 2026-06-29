package net.the_okazakis.workoutstrech

import android.os.Bundle

class Ashiage : BaseActivity() {

    private val defaultTimes = 1   //セット数
    private val defaultReps = 3   //セット当たり回数秒数　

    private val runnable = object : Runnable {
        override fun run() {

            timeCount++

            num++
            if (extimes <= maxextimes) {
                tv.text = "$extimes/$maxextimes 回"
                when (num) {
                    1 -> {
                        tv2.text = getString(R.string.up)
                        playSoundSingle(sndup)
                    }

                    in 2..11 -> {
                        tv2.text = "${num - 1} 秒"
                        val soundId = sounds.getOrNull(num - 2) ?: 0
                        playSoundSingle(soundId)

                    }

                    12 -> {
                        if (count) {
                            extimes++; count = false
                        } else {
                            count = true
                        }
                        if (extimes <= maxextimes) {
                            tv2.text = "足を変えて上げて"
                            playSoundSingle(sndchangleg)
                            num = 0
                        }
                    }
                }
                handler.postDelayed(this, 1000)
            } else {
                handleTrainingComplete(tv2, btnback, btnChangeTimes, btnyoutube) {
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

        val StandardText = "左右10秒ずつ 3回（1セット）"
        val masxlimit = 99
        val maxRep =30
        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation)


        btnstart.setOnClickListener {
            setUIForStarting(
                runnable,
                -3,
                btnback,
                btnChangeTimes,
                btnyoutube
            )
        }

        btnstop.setOnClickListener {
            setUIForStopping(btnback, btnChangeTimes, btnyoutube)
            handler.removeCallbacks(runnable)
        }

        btnrerstart.setOnClickListener {
            setUIForStarting(
                runnable,
                -3,
                btnback,
                btnChangeTimes,
                btnyoutube
            )
        }

        btnback.setOnClickListener {
            finish()
        }

        btnyoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/suG47Nx_H5A?t=42")
        }
        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(StandardText, masxlimit,maxRep)
        }
       loadAllStandardSounds()
       loadSettingsTick(defaultTimes, defaultReps)
    }
        override fun onResume() {
            super.onResume()
            loadSettingsTick(defaultTimes, defaultReps)
        }
    }


