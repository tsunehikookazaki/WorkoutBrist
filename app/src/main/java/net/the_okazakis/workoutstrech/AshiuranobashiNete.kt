package net.the_okazakis.workoutstrech

import android.os.Bundle

class AshiuranobashiNete : BaseActivity() {

      private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "$extimes /$maxextimes セット"//表示用回数 extimesは0から
                        tv2.text = getString(R.string.noba)
                        playSoundSingle(sndstretch)
                    }
                    2 -> {
                        tv2.text = getString(R.string.keep30s)
                        playSoundSingle(sndkeep30s)
                    }

                    in 4..33 -> {
                        if (count) {
                            tv2.text = "${num - 3} 秒  👣"
                        } else {
                            tv2.text = "${num - 3} 秒"
                        }
                        playSoundSingle(sounds[num - 4])
                    }
                    34 -> {
                        if (count) {   //false →true 足が2回変わったら
                            extimes++   //回数を増やす
                            count = false    //
                        } else {
                            count = true
                        }
                        if (extimes  <= maxextimes) {
                            tv2.text = "足を変えて"
                            playSoundSingle(sndchangleg)
                            num = -4    //タオルをかける時間
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

        //説明文
        val myExplanation=
            "膝の裏伸ばし（寝る）\n仰向けに寝て、片方の足のモモを両手でゆっくり自分の方に引き寄せる。足を延ばして膝の裏が伸びているのを感じる。30秒間キープ。反対の足も同様に。1回（左右1回ずつ）で1セット。1セット標準。"

        val StandardText = "1回（左右1回ずつ）で1セット。\n1セット（回）標準。最大99回"
        val masxlimit = 99
        val maxRep =30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation)
        // 音声をロード
        loadAllStandardSounds()

        // 各種クリックリスナー
        btnstart.setOnClickListener {
            setUIForStarting(runnable,-1,btnback, btnChangeTimes, btnyoutube)
        }


        btnstop.setOnClickListener {
            setUIForStopping(btnback, btnChangeTimes, btnyoutube)
            handler.removeCallbacks(runnable)
        }

        btnrerstart.setOnClickListener {
            restartTraining(runnable,btnback,btnChangeTimes,btnyoutube)
        }

        btnspeed.setOnClickListener {
            setUIForSpeedStarting(runnable, -1, btnback,btnyoutube, btnChangeTimes)
        }

        btnback.setOnClickListener {
            finish()
        }
        //Youtubeのリンクを開く
        btnyoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/R0cIFc6S32U?t=12")
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