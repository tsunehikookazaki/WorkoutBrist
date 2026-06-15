package net.the_okazakis.workoutstrech

import android.os.Bundle

class BirdDog :  BaseActivity() {


    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {  //上げて
                        tv.text = "$extimes/$maxextimes 回"
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
                        tv2.text = "$maxReps キープ"
                        playSoundSingle(soundskeep[maxReps-1])
                    }
                    in 3..maxReps+2 -> {   // 1,2,3,4
                        tv2.text = "${num -2}"
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
                        tv2.text = "$maxReps キープ"
                        playSoundSingle(soundskeep[maxReps-1])
                    }
                    in maxReps + 6..maxReps+maxReps + 5 -> {   //1,2,3
                        tv2.text ="${num - maxReps -5}"
                        playSoundSingle(sounds[num - maxReps -6])
                    }
                    maxReps+maxReps + 6  -> {   //戻して
                        tv2.text = getString(R.string.modo)
                        playSoundSingle(sndmodo)
                        num = 0
                        extimes ++
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
            "脊柱起立筋の筋トレ（バードドッグ）\n四つ這いになり、両手と両足は肩幅に開きます。" +
                    "\n右手は肘を伸ばし、左足は膝をつたまま足先を上げる。または足を水平に上げ、" +
                    "そのままの姿勢をキープ（キープは３秒が標準　最大10秒）。\n手足を元に戻し、反対側も同じようにする。" +
                    "\n手足を上げるときに、腰を反ったり、姿勢が崩れないように注意する。" +
                    "\n\n左右5回ずつが1セット。1セット標準" +
                    "\n膝を付かずに水平に伸ばすのが普通のバードドックですが、運動の強度が高いので、膝をついて行います。力がついたら足を水平に上げます\n"

        val StandardText = "1セットの秒数は3秒（最大30秒）、左右5回（運動回数５）ずつが標準\n運動回数　最大99回　１セットの秒数　最大30秒"
        val maxLimit = 99 // 👈【追加】上限値を決める
        val maxRep = 30 // 👈【追加】もう一つの値（秒数など）の上限
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
            openYoutube("https://youtu.be/7laXS5K4KcU")
        }


        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(StandardText, maxLimit,maxRep)
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
        tv.text = "1/${maxextimes} セット"   // ← UIも更新
    }
}