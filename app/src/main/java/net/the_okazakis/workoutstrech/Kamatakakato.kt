package net.the_okazakis.workoutstrech

import android.os.Bundle

class Kamatakakato : BaseActivity() {
    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (extimes <= maxextimes) {  //続ける条件　　maxextimeまで
                when (num) {
                    1 -> {
                        tv.text = "$extimes/$maxextimes 回"
                        tv2.text = "つま先を上げて"
                        playSoundSingle(sndtoesup)
                    }

                    3 -> {
                        tv2.text = "つま先立ちをして"
                        playSoundSingle(sndstandtoes)

                    }
                    4 -> {
                        tv2.text = "さらに伸びをして"
                    }

                    5 -> {
                        tv2.text = "かかとを落とす"
                        playSoundSingle(snddropdown)
                    }
                    6 ->{
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

        val myExplanation =
            "鎌田式 かかと落とし\n背筋を伸ばし、両足を肩幅に広げ、ゆっくりつま先立ちになる。ゆっくりかかとを落として、すとんと衝撃を与える。膝を痛めないように少しだけ曲げる。10回で1セット。1セット標準。目指せ1日3セット"

        val StandardText ="10回で1セット。1セット標準。"
        val masxlimit = 99
        val maxRep =30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation)
        // 音声をロード
        loadAllStandardSounds()

        // 各種クリックリスナー
        btnstart.setOnClickListener {
            setUIForStarting(runnable,-2,btnback, btnChangeTimes, btnyoutube)
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
            openYoutube("https://youtu.be/gEdSC2LGc10?t=36")
        }
        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(StandardText, masxlimit,maxRep)
        }

        loadSettingsTick()

    }
    override fun onDestroy() {

        soundPool.release()
        super.onDestroy()
    }

    // 👇ここに書く（onCreateの下）
    override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/$maxextimes 回"   // ← UIも更新
    }
}