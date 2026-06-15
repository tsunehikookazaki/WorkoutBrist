package net.the_okazakis.workoutstrech

import android.os.Bundle
class Momomae : BaseActivity() {

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "${extimes}/$maxextimes セット"
                    }
                    2 ->{
                        tv2.text = "足をお尻に引き付け前ももを伸ばす"
                        playSoundSingle(sndhikitsuke)
                    }
                    3 -> {
                        tv2.text = getString(R.string.keep30s)
                        playSoundSingle(sndkeep30s)
                    }
                    in 4..33 -> {
                        tv2.text = "${num - 3}"
                        playSoundSingle(sounds[num - 4])
                    }

                    34 ->{ extimes++}

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
            "前モモ伸ばしし\n片方の足を後ろに引き、足を持ってかかとをおしりに引き付ける。前モモが伸びているのを感じる。30秒間キープ。反対の足も同様に。1回（左右1回ずつ）で1セット。1セット標準。" +
                    "足が持てない場合はタオルなどを使って引き付ける。モモを後ろに引くとより効果的。　足が横に出ないように、前に出ないように、腰が反らないように注意する。"

        val StandardText ="左右1回づつで1セット。1セット標準。"
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
        // soundPool.release() // サービスで共有しているため、Activityでは解放しない
        super.onDestroy()
    }

    // 👇ここに書く（onCreateの下）
    override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/$maxextimes 回"   // ← UIも更新
    }
}