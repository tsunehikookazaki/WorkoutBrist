package net.the_okazakis.workoutstrech

import android.os.Bundle
class Hihukukinn : BaseActivity() {

    private val defaultTimes = 2
    private val defaultReps = 1



    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (extimes <= maxextimes) {
                tv.text = "${extimes}/$maxextimes セット"//表示用回数 extimesは0から
                when (num) {
                    0 -> {
                        tv2.text = getString(R.string.noba)
                        playSoundSingle(sndstretch)
                    }

                    in 0..15 -> {
                        if (num in 1..16) {
                            playSoundSingle(sounds[num - 1])
                        }

                        tv2.text = "$num 秒"
                    }

                    17 -> {
                        if (count) {   //false →true 足が2回変わったら
                            extimes++   //回数を増やす
                            count = false    //
                        } else {
                            count = true
                        }
                        if (extimes  <= maxextimes) {
                            tv2.text = "足を変えて"
                            playSoundSingle(sndchangleg)
                            num = -3
                        }
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
            "腓腹筋伸ばし\n両手を壁に付けて身体を斜めにし、伸ばしたい方の足をゆっくり後ろに引き、踵を付ける。" +
                    "腰、背中を曲げない。踵が浮かないように。\n\n左右15秒づつが1セット。２セット標準"

        val StandardText = "左右15秒づつが1セット。２セット標準\n（運動回数２回標準  最大99回）"
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
        //Youtubeのリンクを開く
        btnyoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/suG47Nx_H5A?t=226")
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
        tv.text = "1/${maxextimes} 回"   // ← UIも更新
    }
}