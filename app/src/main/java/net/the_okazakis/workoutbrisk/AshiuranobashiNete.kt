package net.the_okazakis.workoutbrisk

import android.os.Bundle

class AshiuranobashiNete : BaseActivity() {
    private val defaultTimes = 1
    private val defaultReps = 1
      private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (exTimes <= maxExTimes) {
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.tv_sets_format, exTimes, maxExTimes)
                        tv2.text = getString(R.string.noba)
                        playSoundSingle(sndStretch)
                    }
                    2 -> {
                        tv2.text = getString(R.string.keep30s)
                        playSoundSingle(sndkeep30s)
                    }

                    in 4..33 -> {
                        if (count) {
                            tv2.text = getString(R.string.tv_seconds_feet_format, num - 3)
                        } else {
                            tv2.text = getString(R.string.tv_seconds_format, num - 3)
                        }
                        playSoundSingle(sounds[num - 4])
                    }
                    34 -> {
                        if (count) {   //false →true 足が2回変わったら
                            exTimes++   //回数を増やす
                            count = false    //
                        } else {
                            count = true
                        }
                        if (exTimes <= maxExTimes) {
                            tv2.text = getString(R.string.msg_change_leg_simple)
                            playSoundSingle(sndChangeLeg)
                            num = -4    //タオルをかける時間
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
        val myExplanation=
            "膝の裏伸ばし（寝る）\n仰向けに寝て、片方の足のモモを両手でゆっくり自分の方に引き寄せる。足を延ばして膝の裏が伸びているのを感じる。30秒間キープ。反対の足も同様に。1回（左右1回ずつ）で1セット。1セット標準。"

        val standardText = "1回（左右1回ずつ）で1セット。\n1セット（回）標準。最大99回"
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
            openYoutube("https://youtu.be/R0cIFc6S32U?t=12")
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