package net.the_okazakis.workoutstrech

import android.os.Bundle

class AshiuranobashiTachi : BaseActivity() {

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "${extimes}/$maxextimes セット"//表示用回数 extimesは0から
                        tv2.text = getString(R.string.taoshite)
                        playSoundSingle(sndtaoshite)
                    }

                    2 ->{
                        tv2.text = getString(R.string.keep30s)
                        playSoundSingle(sndkeep30s)
                    }
                    in 4..33 ->{

                        if (count) {tv2.text = "${num - 3} 秒"}
                        else {tv2.text = "${num - 3} 秒"}

                        playSoundSingle(sounds[num-4])
                    }
                    34 -> {
                        if(count){   //false →true 足が2回変わったら
                            extimes++   //回数を増やす
                            count =false    //
                        }
                        else{count =true}
                        if(extimes  <= maxextimes  ){
                            tv2.text = "足を変えて"
                            playSoundSingle(sndchangleg)
                            num = -1
                        }
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

        setContentView(R.layout.activity_sub)

        //説明文
        val myExplanation ="片方の脚をベット椅子などに載せ延ばす。つま先は上向きにし。骨盤で身体を前に倒し、" +
                "30秒キープ \n片足交互1回で1セット １セット標準"

        val StandardText = "片足交互1回で1セット １セット標準\n運動回数　1回標準　最大99回"
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
            openYoutube("https://youtu.be/8d647I5J4wY?t=14")
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
        tv.text = "1/${maxextimes} セット"   // ← UIも更新
    }
}