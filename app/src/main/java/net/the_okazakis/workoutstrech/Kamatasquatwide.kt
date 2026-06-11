package net.the_okazakis.workoutstrech

import android.os.Bundle

class Kamatasquatwide :  BaseActivity() {


    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "$extimes/$maxextimes 回"
                        tv2.text = "ゆっくり沈んで"
                        soundPool.play(sndsizunde, countVolume, countVolume, 0, 0, 1.0f)
                    }

                    in 2..4 -> {
                        tv2.text = "${num-1}"
                        playSoundSingle(sounds[num - 2])
                    }
                    in 5..7 -> {
                        tv2.text = "耐えて"
                        playSoundSingle(sndtaete)
                    }
                    8 -> {
                        tv2.text = getString(R.string.slow_up)
                        playSoundSingle(sndslowup)
                    }

                    in 9..11 -> {
                        tv2.text = "${num-8}"
                        playSoundSingle(sounds[num - 9])

                    }
                    12 ->{
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

        val myExplanation =
            "両手を胸で組む。足は肩幅より10cm程度広げ、つま先を45度程度外に向ける。" +
                "\n息を吐きながら(吸いながら)、真下に沈み込むように、ゆっくり腰を下げる。" +
                "\n内ももが張るのに耐える。ゆっくり息を吸いながら(吐きながら)腰を上げる。" +
                "\n太ももの内側が張るような感じが大事。背中が曲がらないように。" +
                "膝が内側に入らないように気を付ける。" +
                "呼吸を止めない\n\n10回で1セット。1セット標準\n\n"

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
            openYoutube("https://youtu.be/S3DJ0ke9624")
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