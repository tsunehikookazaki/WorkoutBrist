package net.the_okazakis.workoutbrisk

import android.os.Bundle

class Hukkin :  BaseActivity() {

    private val defaultTimes = 3
    private val defaultReps = 1

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (exTimes <= maxExTimes) {
                when (num){
                    in -10..-4 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                        }
                    }
                    in -3..-1 ->{
                        if(!isStart){
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sounds[num*(-1)-1])
                        }
                    }
                    0 ->{
                        if(!isStart){
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sndpi)
                        }
                    }
                    1 -> {
                        tv.text = getString(R.string.tv_sets_format, exTimes, maxExTimes)

                        tv2.text = getString(R.string.slow_up)
                        playSoundSingle(sndSlowUp)
                    }
                    2 -> {
                        tv2.text = getString(R.string.msg_keep_20s)
                        playSoundSingle(sndkeep20s)

                    }
                    in 4..23 -> {  //3から23まで
                        tv2.text = getString(R.string.unit_seconds_format, num - 3)
                        playSoundSingle(sounds[num-4])
                    }

                    24 -> {
                        tv2.text = getString(R.string.slow_down)
                        playSoundSingle(sndSlowDown)
                    }
                    25 -> {
                        exTimes++
                        if (exTimes <= maxExTimes) {
                            num = -(11) // 10s relax
                            tv2.text = getString(R.string.relax10)
                            playSoundSingle(snd10re)
                            isStart = false
                        } else {
                            // Done
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

        val myExplanation =
            "床に寝て、両足を床につかないよう10cm位上げ、20秒キープ\n\n高く上げると効果が薄い" +
                    "\n\n20秒で1セット、３セット標準"

        val standardText = "20秒で1セット、\n3セット（回）標準。 最大99回"
        val maxLimit = 99
        val maxRep = 30

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
            openYoutube("https://youtu.be/npIGK0blwyk")
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
