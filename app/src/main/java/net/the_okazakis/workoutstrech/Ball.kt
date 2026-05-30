package net.the_okazakis.workoutstrech

import android.os.Bundle
class Ball : BaseActivity() {

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (extimes <= maxextimes) {
                tv.text = "$extimes/$maxextimes 回"//表示用回数 extimesは0から
                when(num) {
                    0->{tv2.text = "つぶして"
                        playSoundSingle(sndtsubu)
                    }
                    in 0..10-> {
                        if(num in 1..10) {
                            playSoundSingle(sounds[num - 1])
                        }

                        tv2.text = "$num 秒"
                    }
                    11 -> {
                        playSoundSingle(sndloosen)
                        extimes++   //回数を増やす
                        tv2.text = "ゆるめて"
                        num = -1
                    }
                    else ->{}
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
        _helper = DatabaseHelper(applicationContext)  // ←これ追加
        setContentView(R.layout.activity_sub)

        //説明文
        val myExplanation =
            "膝裏ボール潰し\n椅子に座り、膝の裏に小さめのボール(または丸めたタオル)を挟み、ぎゅーっと10秒間押しつぶす。10秒3回で1セット。1セット標準。"

        val StandardText = "10秒3回で1セット。1セット標準。\n運動回数　1回標準　最大99回"
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
            openYoutube("https://youtu.be/MVzREF4j1lI?t=4")
        }

        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(StandardText, masxlimit,maxRep)
        }
        loadSettingsTick()
    }
    override fun onDestroy() {
        _helper.close()
        soundPool.release()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/${maxextimes} セット"   // ← UIも更新
    }
}