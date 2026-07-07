package net.the_okazakis.workoutbrisk

import android.os.Bundle

class Legraise : BaseActivity() {

     private val defaultTimes = 3
     private val defaultReps = 10

     private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (exTimes <= maxExTimes) {

                tv.text = getString(R.string.tv_sets_format, exTimes, maxExTimes)

                when (num) {
                    in -10..-4 ->{
                        if(!isStart){
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                        }
                    }
                    in -3..-1 ->{

                        if(!isStart){
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sounds[num*(-1)-1])
                        }
                    }
                    0 ->{
                        if(!isStart){
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sndpi)
                        }
                    }

                    in 1..20 -> {

                        if(isUp) {
                            nn++
                            tv2.text = getString(R.string.progress_action_10_format, getString(R.string.up), nn)
                            playSoundSingle(sndup)
                            isUp = false
                        }else{
                            tv2.text = getString(R.string.progress_action_10_format, getString(R.string.down), nn)
                            playSoundSingle(snddown)
                            isUp = true

                        }
                    }
                    21 -> {

                        if (nn >=10) {
                            num = -(11) // 10s relax
                            nn=0
                            exTimes++
                            if(exTimes <= maxExTimes) {
                                tv2.text = getString(R.string.relax10)
                                playSoundSingle(snd10re)
                                isStart = false
                            }
                            }

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

        val myExplanation =
            "仰向けに寝て、手は身体の横に置く\n足はなるべく伸ばす（きつい場合は曲げてもよい.）\n足をそろえてゆっくり上げて(出来れば90度位に)ゆっくりおろす\n" +
                    "下したとき足は床に付けない。\n\n10回で1セット、3セットが標準"

        val standardText ="10回で1セット。\n3セット（回）標準。"
        val maxLimit = 99
        val maxRep = 30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
        // 音声をロード
        loadAllStandardSounds()

        // 各種クリックリスナー
        btnStart.setOnClickListener {
            setUIForStarting(runnable, -2, btnBack, btnChangeTimes, btnYoutube)
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

        btnYoutube.setOnClickListener {
            openYoutube("https://youtu.be/JmG5MLaDS38")
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

    // 👇ここに書く（onCreateの下）
    override fun onResume() {
        super.onResume()
        loadSettingsTick(defaultTimes, defaultReps)
        updateHeaderUI()
    }
}