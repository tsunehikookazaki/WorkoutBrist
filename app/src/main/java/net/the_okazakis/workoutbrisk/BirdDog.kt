package net.the_okazakis.workoutbrisk

import android.os.Bundle

class BirdDog :  BaseActivity() {

    private val defaultTimes = 3  //１セット秒数
    private val defaultReps = 5   //セット当たり回数
    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (exTimes <= maxExTimes) {
                when (num) {
                    1 -> {  //上げて
                        tv.text = getString(R.string.tv_times_format, exTimes, maxExTimes)
                        if (isStart) {  //初めてなら　上げて
                            tv2.text =  getString(R.string.up)
                            playSoundSingle(sndup)
                            isStart = false   //初めてじゃない
                        }
                        else {  //初めてじゃない　変えてあげて
                            tv2.text =  getString(R.string.change4)
                            playSoundSingle(sndkaeteagete)
                        }
                    }
                    2 -> {     //n秒キープ
                        tv2.text = getString(R.string.unit_seconds_keep_format, maxReps)
                        playSoundSingle(soundskeep[maxReps - 1])
                    }
                    in 3..maxReps + 2 -> {   // 1,2,3,4
                        tv2.text = getString(R.string.unit_seconds_format, num - 2)
                        playSoundSingle(sounds[num - 3])
                    }
                    maxReps + 3 -> {   //戻して
                        tv2.text =  getString(R.string.modo)
                        playSoundSingle(sndmodo)
                    }

                    maxReps + 4  -> { //変えてあげて
                        tv2.text =  getString(R.string.change4)
                        playSoundSingle(sndkaeteagete)
                    }

                    maxReps + 5 -> {      //n秒キープ
                        tv2.text = getString(R.string.unit_seconds_keep_format, maxReps)
                        playSoundSingle(soundskeep[maxReps - 1])
                    }
                    in maxReps + 6..maxReps + maxReps + 5 -> {   //1,2,3
                        tv2.text = getString(R.string.unit_seconds_format, num - maxReps - 5)
                        playSoundSingle(sounds[num - maxReps - 6])
                    }
                    maxReps+maxReps + 6  -> {   //戻して
                        tv2.text = getString(R.string.modo)
                        playSoundSingle(sndmodo)
                        num = 0
                        exTimes ++
                    }

                    else -> {}
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
            "脊柱起立筋の筋トレ（バードドッグ）\n四つ這いになり、両手と両足は肩幅に開きます。" +
                    "\n右手は肘を伸ばし、左足は膝をつたまま足先を上げる。または足を水平に上げ、" +
                    "そのままの姿勢をキープ（キープは３秒が標準　最大10秒）。\n手足を元に戻し、反対側も同じようにする。" +
                    "\n手足を上げるときに、腰を反ったり、姿勢が崩れないように注意する。" +
                    "\n\n左右5回ずつが1セット。1セット標準" +
                    "\n膝を付かずに水平に伸ばすのが普通のバードドックですが、運動の強度が高いので、膝をついて行います。力がついたら足を水平に上げます\n"

        val standardText = "手足を上げる秒数は3秒（運動回数　最大30秒）、左右5回づつ（１セットの回数）ずつが標準\n運動回数　最大99回　１セットの秒数　最大30秒"
        val maxLimit = 99 // 👈【追加】上限値を決める
        val maxRep = 30 // 👈【追加】もう一つの値（秒数など）の上限
        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
        // 音声をロード
        loadAllStandardSounds()

        // 各種クリックリスナー
        btnStart.setOnClickListener {
            setUIForStarting(runnable, -3, btnBack, btnChangeTimes, btnYoutube)
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
        //Youtubeのリンクを開く
        btnYoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/7laXS5K4KcU")
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