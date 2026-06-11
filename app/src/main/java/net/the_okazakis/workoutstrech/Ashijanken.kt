package net.the_okazakis.workoutstrech

import android.os.Bundle
class Ashijanken :BaseActivity() {


    private val runnable = object : Runnable {
        override fun run() {

            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "${extimes}/$maxextimes 回"
                        tv2.text = getString(R.string.goo)
                        playSoundSingle(sndgo)
                    }

                    2 -> {
                        if (choki) {
                            choki = false
                            tv2.text = getString(R.string.choki)
                            playSoundSingle(sndchoki)
                        } else {
                            tv2.text = getString(R.string.urachoki)
                            playSoundSingle(sndurachoki)
                            choki = true
                        }
                    }

                    3 -> {
                        tv2.text = getString(R.string.pa)
                        playSoundSingle(sndpa)
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
        val myExplanation =
             "足の指でグー、チョキ(裏)　パーをする。" +
                "\nグー指はなるべく深く曲げる。チョキ、パーはなるべく大きく開く." +
                     " グーのこぶしが出るように" +
                "\n\nグー　チョキ　パーを16回標準"

        val StandardText = "グー）チョキ　パー、グー（裏）チョキ　パーを各8回　16回標準\n最大99"
        val maxLimit = 99
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
            openYoutube("https://youtu.be/p6agyQN2gco?t=102")
        }

        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(StandardText, maxLimit, maxRep )
        }
        loadSettingsTick()
    }
    override fun onDestroy() {

        soundPool.release()
        super.onDestroy()
    }

      override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/$maxextimes 回"   // ← UIも更新
    }
}