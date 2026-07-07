package net.the_okazakis.workoutbrisk

import android.os.Bundle
class Momomae : BaseActivity() {

    private val defaultTimes = 1
    private val defaultReps = 1

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (exTimes <= maxExTimes) {
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.tv_sets_format, exTimes, maxExTimes)
                    }
                    2 ->{
                        tv2.text = getString(R.string.msg_pull_heels)
                        playSoundSingle(sndhikitsuke)
                    }
                    3 -> {
                        tv2.text = getString(R.string.keep30s)
                        playSoundSingle(sndkeep30s)
                    }
                    in 4..33 -> {
                        tv2.text = getString(R.string.unit_seconds_format, num - 3)
                        playSoundSingle(sounds[num - 4])
                    }

                    34 ->{ exTimes++}

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
            "前モモ伸ばしし\n片方の足を後ろに引き、足を持ってかかとをおしりに引き付ける。前モモが伸びているのを感じる。30秒間キープ。反対の足も同様に。1回（左右1回ずつ）で1セット。1セット標準。" +
                    "足が持てない場合はタオルなどを使って引き付ける。モモを後ろに引くとより効果的。　足が横に出ないように、前に出ないように、腰が反らないように注意する。"

        val standardText ="左右1回づつで1セット。1セット標準。"
        val maxLimit = 99
        val maxRep = 30

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

        btnYoutube.setOnClickListener {
            openYoutube("https://youtu.be/sRvbL3eflz0")
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