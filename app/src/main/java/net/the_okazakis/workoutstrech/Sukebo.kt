package net.the_okazakis.workoutstrech

import android.os.Bundle
import android.view.View

class Sukebo : BaseActivity() {

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "$extimes/$maxextimes 回"
                        tv2.text = getString(R.string.slow_stre)
                        playSoundSingle(sndstretch)
                    }
                    in 2..4 ->   playSoundSingle(sounds[num-2])
                     5 -> {
                        tv2.text = getString(R.string.slow_bent)
                         playSoundSingle(sndbend)
                    }
                    in 6..8 ->  playSoundSingle(sounds[num-6])
                    9 -> {
                        playSoundSingle(sounds[3])
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
        setContentView(R.layout.activity_sub)
        val myExplanation =
            "椅子に腰かけ、スケボーに両足をのせて足を曲げ伸ばす。\n" +
                "つま先、かかとがスケボーから離れないように。\n\n10回で1セット。1セットが標準。" +
                "\nスケボーの代わりにタオルでも出来る"

        val StandardText ="10回で1セット、1セット標準"
        val masxlimit = 99
        val maxRep =30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation)
        // 音声をロード
        loadAllStandardSounds()

        // YouTubeボタンを非表示
        btnyoutube.visibility = View.INVISIBLE

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
            openYoutube("https://youtu.be/sRvbL3eflz0")
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