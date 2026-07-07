package net.the_okazakis.workoutbrisk

import android.os.Bundle

class Kamatasquatwide :  BaseActivity() {

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
                        tv2.text = getString(R.string.msg_sink_slowly)
                        playSoundSingle(sndsizunde)
                    }

                    in 2..4 -> {
                        tv2.text = getString(R.string.tv_seconds_format, num - 1)
                        playSoundSingle(sounds[num - 2])
                    }
                    in 5..7 -> {
                        tv2.text = getString(R.string.msg_endure)
                        playSoundSingle(sndtaete)
                    }
                    8 -> {
                        tv2.text = getString(R.string.slow_up)
                        playSoundSingle(sndSlowUp)
                    }

                    in 9..11 -> {
                        tv2.text = getString(R.string.tv_seconds_format, num - 8)
                        playSoundSingle(sounds[num - 9])

                    }
                    12 ->{
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

        val myExplanation =
            "両手を胸で組む。足は肩幅より10cm程度広げ、つま先を45度程度外に向ける。" +
                "\n息を吐きながら(吸いながら)、真下に沈み込むように、ゆっくり腰を下げる。" +
                "\n内ももが張るのに耐える。ゆっくり息を吸いながら(吐きながら)腰を上げる。" +
                "\n太ももの内側が張るような感じが大事。背中が曲がらないように。" +
                "膝が内側に入らないように気を付ける。" +
                "呼吸を止めない\n\n10回で1セット。1セット標準\n\n"

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
            openYoutube("https://youtu.be/S3DJ0ke9624")
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