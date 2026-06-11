package net.the_okazakis.workoutstrech

import android.os.Bundle

class Hukkin :  BaseActivity() {

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num){
                    in -10..-4 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                        }
                    }
                    in -3..-1 ->{
                        if(!isStart){
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sounds[num*(-1)-1])
                        }
                    }
                    0 ->{
                        if(!isStart){
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sndpi)
                        }
                    }
                    1 -> {
                        tv.text = "$extimes/$maxextimes セット"

                        tv2.text = getString(R.string.slow_up)
                        playSoundSingle(sndslowup)
                    }
                    2 -> {
                        tv2.text = "20秒キープ"
                        playSoundSingle(sndkeep20s)

                    }
                    in 4..23 -> {  //3から23まで
                        tv2.text = "${num-3}"
                        playSoundSingle(sounds[num-4])
                    }

                    24 -> {
                        tv2.text = getString(R.string.slow_down)
                        playSoundSingle(sndslowdown)
                    }
                    25 -> {
                        extimes++
                        if (extimes <= maxextimes) {
                            num = -(11) // 10s relax
                            tv2.text = getString(R.string.relax10)
                            playSoundSingle(snd10re)
                            isStart = false
                        } else {
                            // Done
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

        val myExplanation =
            "床に寝て、両足を床につかないよう10cm位上げ、20秒キープ\n\n高く上げると効果が薄い" +
                    "\n\n20秒で1セット、３セット標準"

        val StandardText ="20秒で1セット、\n3セット（回）標準。 最大99回"
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
            openYoutube("https://youtu.be/npIGK0blwyk")
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
        tv.text = "1/$maxextimes セット"   // ← UIも更新
    }
}
