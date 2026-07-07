package net.the_okazakis.workoutbrisk

import android.os.Bundle

class HipabdactionBelt : BaseActivity() {

    private val defaultTimes = 3
    private val defaultReps = 30

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (exTimes <= maxExTimes) {
                when (num) {
                    -5 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.msg_rest_brief)
                            playSoundSingle(sndbreak)
                        }
                    }

                    -4 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                        }
                    }

                    in -3..-1 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sounds[num * (-1) - 1])
                        }
                    }

                    0 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sndpi)
                        }
                    }

                    in 1..90 -> {    //%3
                        if ((num % 3) == 1) {
                            tv.text = getString(R.string.tv_sets_format, exTimes, maxExTimes)
                            tv2.text = getString(R.string.progress_action_30_format, getString(R.string.open), nn)
                            playSoundSingle(sndopen)
                        }
                        if ((num % 3) == 2) {
                            tv2.text = getString(R.string.progress_action_30_format, getString(R.string.keep1s), nn)
                            playSoundSingle(sndkeep1s)
                        }
                        if ((num % 3) == 0) {
                            tv2.text = getString(R.string.progress_action_30_format, getString(R.string.slow_close), nn)
                            playSoundSingle(sndSlowClose)

                            nn++
                        }
                    }

                    91 -> {
                        isStart = false
                        num = -6 // num++があるので　-5にするには -6
                        nn = 1
                        exTimes++
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

        //説明文
        val myExplanation =
            "椅子に座って足を少し開き、膝にゴムベルトを巻く。\n手をお尻の横に添えて、お尻に力を入れ膝をギュッと開く。" +
                    "\n1秒間キープして、ゆっくり閉じる" +
                    "\nこれを繰り返す。\n\n30回で1セット　3セット標準" +
                    "\n\nベルトは100均一で"

        val standardText = "30回で1セット。3セット標準"
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
            openYoutube("https://youtu.be/xZGLV-_eOEA")
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