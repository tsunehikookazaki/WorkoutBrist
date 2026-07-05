package net.the_okazakis.workoutbrisk

import android.os.Bundle

class ChairSquat :  BaseActivity() {

    private val defaultTimes = 10
    private val defaultReps = 1
    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "$extimes/$maxextimes 回"
                        tv2.text = getString(R.string.standup)
                        playSoundSingle(sndstand)
                    }

                    in 2..5 -> {
                        if (num in 2..5) {
                            playSoundSingle(sounds[num - 2])
                        }

                        tv2.text = "${num - 1} 秒"
                    }

                    6 -> {
                        tv2.text = getString(R.string.sitdown)
                        playSoundSingle(sndsit)

                    }

                    in 7..11 -> {
                        if (num in 7..11) {
                            playSoundSingle(sounds[num - 7])
                        }
                        tv2.text = "${num - 6} 秒"
                    }


                    12 -> {
                        num = 0; extimes++
                    }
                    else -> {}
                }
                // ← これ追加
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

        //説明文
        val myExplanation =
            "椅子に座り（腰掛ける程度）、両足を肩幅に広げつま先と膝を同じ方向に向ける、ゆっくり立つ。立ち上がったら、ゆっくり（座るイメージで）腰を下ろす。\n膝がつま先より出ないように。背中が曲がらないように。反動をつけない\n\n10回で1セット。1～2セット標準。"

        val StandardText = "10回で1セット。1～2セット標準"
        val masxlimit = 99
        val maxRep =30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
        // 音声をロード
        loadAllStandardSounds()

        // 各種クリックリスナー
        btnstart.setOnClickListener {
            setUIForStarting(runnable,-1,btnback, btnChangeTimes, btnyoutube)
        }


        btnstop.setOnClickListener {
            setUIForStopping(btnback, btnChangeTimes, btnyoutube)
            handler.removeCallbacks(runnable)
        }

        btnrerstart.setOnClickListener {
            restartTraining(runnable,btnback,btnChangeTimes,btnyoutube)
        }

        btnspeed.setOnClickListener {
            setUIForSpeedStarting(runnable, -1, btnback,btnyoutube, btnChangeTimes)
        }

        btnback.setOnClickListener {
            finish()
        }
        //Youtubeのリンクを開く
        btnyoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/61gktuGYKfI")
        }
        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(StandardText, masxlimit,maxRep)
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
        tv.text = "1/${maxextimes} セット"   // ← UIも更新
    }
}