package net.the_okazakis.workoutbrisk

import android.os.Bundle

class AshiuranobashiTachi : BaseActivity() {

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
                        tv2.text = getString(R.string.taoshite)
                        playSoundSingle(sndtaoshite)
                    }

                    2 ->{
                        tv2.text = getString(R.string.keep30s)
                        playSoundSingle(sndkeep30s)
                    }
                    in 4..33 ->{

                        tv2.text = getString(R.string.tv_seconds_format, num - 3)

                        playSoundSingle(sounds[num-4])
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
                            num = -1
                        }
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
        val myExplanation ="片方の脚をベット椅子などに載せ延ばす。つま先は上向きにし。骨盤で身体を前に倒し、" +
                "30秒キープ \n片足交互1回で1セット １セット標準"

        val standardText = "片足交互1回で1セット １セット標準\n運動回数　1回標準　最大99回"
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
            openYoutube("https://youtu.be/8d647I5J4wY?t=14")
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