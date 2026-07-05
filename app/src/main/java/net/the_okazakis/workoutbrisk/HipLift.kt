package net.the_okazakis.workoutbrisk

import android.os.Bundle
import android.view.View

class HipLift : BaseActivity() {

    private val defaultTimes = 2
    private val defaultReps = 1

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++

            if (extimes <= maxextimes) {

                tv.text = "${extimes}/$maxextimes セット"

                when (num) {

                    -10 -> {
                        if (!isStart) {
                            speedTime = normalspeedTime
                            tv2.text = "10秒リラックス"
                            playSoundSingle(snd10re)
                        }
                    }
                    -3 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sounds[2])
                        }
                    }
                    -2 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sounds[1])
                        }
                    }
                    -1 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            playSoundSingle(sounds[0])
                        }
                    }
                    0 -> {
                        if (!isStart) {
                            tv2.text = "0"
                            playSoundSingle(sndpi)
                            if (isSpeed)speedTime = speedspeedTime
                        }
                    }

                    1 -> {
                        tv2.text = getString(R.string.up) + " ${nn + 1}回"
                        playSoundSingle(sndup)
                    }
                    2 -> {

                        tv2.text = getString(R.string.down) + " ${nn + 1}回"
                        playSoundSingle(snddown)
                        if (nn < 9) {
                            num = 0
                            nn++
                        } else {
                            isStart = false
                            nn = 0
                            num = -11
                            extimes++
                        }
                    }
                }
                handler.postDelayed(this, speedTime)

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

        btnspeed = findViewById(R.id.btspeed)
        btnspeed.visibility = View.VISIBLE

        val myExplanation =
            "床に寝て、膝を曲げる。足はべた足。お尻に力を入れ足を踏ん張るように腰を上げ下げする。\nおしりを下げた時床に着けない。" +
                    "おしりを上げた時に足が浮かないように。腰で上げないよう、お尻を触って、力が入っていることを確認。" +
                    "\nレベル1：両手を体側につく\nレベル2：手は胸におく" +
                    "\nレベル３：片足を上げて、反対の足に載せ、手はつく\nレベル４：片足を上げ伸ばし、両手は胸におく。" +
                    "上げた足は反対の足と同じ高さをキープ。両手を伸ばし、上にあげて掌をつけるのが最高。" +
                    "\nSPEEDボタンでスピードアップ"+
                    "\n\n左右それぞれ10回が1セット。合計２セットが標準　　片足を上げた場合はそれぞれ１セットずつ、合計２セット\n\n"

        val StandardText = "10回が1セット。左右それぞれ1セット合計２セット（回）が標準\n片足を上げた場合はそれぞれ１セットずつ、合計２セット(回)が標準\n最大99回\nSPEEDボタンでも同じ"
        val masxlimit = 99
        val maxRep =30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation, defaultTimes, defaultReps)
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
            openYoutube("https://youtu.be/trf2Ph_WWPQ")
        }


        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(StandardText, masxlimit,maxRep)
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
            tv.text = "1/$maxextimes 回"   // ← UIも更新
        }
    }

