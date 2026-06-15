package net.the_okazakis.workoutstrech

import android.os.Bundle

class AshikubiKushin : BaseActivity() {


    private val runnable = object : Runnable {
        override fun run() {

            timeCount++
            num++
            if (extimes < maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "${extimes + 1}/$maxextimes 回"
                        tv2.text = getString(R.string.foot_bent)
                        playSoundSingle(sndbent)
                    }

                    2 -> {
                        tv2.text = getString(R.string.foot_stre)
                        playSoundSingle(sndstre)
                        num = 0; extimes++
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
        val myExplanation = "膝を伸ばして座るか、床に寝る。足首をゆっくり手前に曲げ、次に向こう側に伸ばす。\n" +
                "ふくらはぎ、足の甲が伸びているのを感じる。\n\n15回標準。"

        val StandardText = "15回標準　最大99回"
        val masxlimit = 99
        val maxRep =30
        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation)
        // 音声をロード
        loadAllStandardSounds()

        // 各種クリックリスナー
        btnstart.setOnClickListener {
            setUIForStarting(runnable,-3,btnback, btnChangeTimes, btnyoutube)
        }


        btnstop.setOnClickListener {
            setUIForStopping(btnback, btnChangeTimes, btnyoutube)
            handler.removeCallbacks(runnable)
        }

        btnrerstart.setOnClickListener {
            restartTraining(runnable,btnback,btnChangeTimes,btnyoutube)
        }

        btnspeed.setOnClickListener {
            setUIForSpeedStarting(runnable, -3, btnback,btnyoutube, btnChangeTimes)
        }

        btnback.setOnClickListener {
            finish()
        }

        btnyoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/IxC41pWD2Iw")
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
        tv.text = "1/$maxextimes 回"   // ← UIも更新
    }
}