package net.the_okazakis.workoutstrech

import android.os.Bundle

class HipabdactionBelt : BaseActivity() {

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    -5 -> {
                        if (!isStart) {
                            tv2.text = "ちょっと休憩"
                            playSoundSingle(sndbreak)
                        }
                    }

                    -4 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                        }
                    }

                    in -3..-1 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sounds[num * (-1) - 1])
                        }
                    }

                    0 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sndpi)
                        }
                    }

                    in 1..90 -> {    //%3
                        if ((num % 3) == 1) {
                            tv.text = "$extimes /$maxextimes セット"
                            tv2.text = getString(R.string.open) + " $nn/30"
                            playSoundSingle(sndopen)
                        }
                        if ((num % 3) == 2) {
                            tv2.text = getString(R.string.keep1s) + " $nn/30"
                            playSoundSingle(sndkeep1s)
                        }
                        if ((num % 3) == 0) {
                            tv2.text = getString(R.string.slow_close) + " $nn/30"
                            playSoundSingle(sndslowclose)

                            nn++
                        }
                    }

                    91 -> {
                        isStart = false
                        num = -6 // num++があるので　-5にするには -6
                        nn = 1
                        extimes++
                    }
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
            "椅子に座って足を少し開き、膝にゴムベルトを巻く。\n手をお尻の横に添えて、お尻に力を入れ膝をギュッと開く。" +
                    "\n1秒間キープして、ゆっくり閉じる" +
                    "\nこれを繰り返す。\n\n30回で1セット　3セット標準" +
                    "\n\nベルトは100均一で"

        val StandardText = "30回で1セット。3セット標準"
        val masxlimit = 99
        val maxRep =30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation)
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
            openYoutube("https://youtu.be/xZGLV-_eOEA")
        }


        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(StandardText, masxlimit,maxRep)
        }
        loadSettingsTick()
    }
    override fun onDestroy() {
        // soundPool.release() // サービスで共有しているため、Activityでは解放しない
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/${maxextimes} 回"   // ← UIも更新
    }
}