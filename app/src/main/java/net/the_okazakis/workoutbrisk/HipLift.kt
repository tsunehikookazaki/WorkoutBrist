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

            if (exTimes <= maxExTimes) {

                tv.text = getString(R.string.tv_sets_format, exTimes, maxExTimes)

                when (num) {

                    -10 -> {
                        if (!isStart) {
                            speedTime = normalspeedTime
                            tv2.text = getString(R.string.relax10)
                            playSoundSingle(snd10re)
                        }
                    }
                    -3 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sounds[2])
                        }
                    }
                    -2 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sounds[1])
                        }
                    }
                    -1 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, num * (-1))
                            playSoundSingle(sounds[0])
                        }
                    }
                    0 -> {
                        if (!isStart) {
                            tv2.text = getString(R.string.unit_seconds_format, 0)
                            playSoundSingle(sndpi)
                            if (isSpeed)speedTime = speedspeedTime
                        }
                    }

                    1 -> {
                        tv2.text = getString(R.string.progress_action_simple_format, getString(R.string.up), nn + 1)
                        playSoundSingle(sndup)
                    }
                    2 -> {

                        tv2.text = getString(R.string.progress_action_simple_format, getString(R.string.down), nn + 1)
                        playSoundSingle(snddown)
                        if (nn < 9) {
                            num = 0
                            nn++
                        } else {
                            isStart = false
                            nn = 0
                            num = -11
                            exTimes++
                        }
                    }
                }
                handler.postDelayed(this, speedTime)

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

        btnSpeed = findViewById(R.id.btspeed)
        btnSpeed.visibility = View.VISIBLE

        val myExplanation =
            "床に寝て、膝を曲げる。足はべた足。お尻に力を入れ足を踏ん張るように腰を上げ下げする。\nおしりを下げた時床に着けない。" +
                    "おしりを上げた時に足が浮かないように。腰で上げないよう、お尻を触って、力が入っていることを確認。" +
                    "\nレベル1：両手を体側につく\nレベル2：手は胸におく" +
                    "\nレベル３：片足を上げて、反対の足に載せ、手はつく\nレベル４：片足を上げ伸ばし、両手は胸におく。" +
                    "上げた足は反対の足と同じ高さをキープ。両手を伸ばし、上にあげて掌をつけるのが最高。" +
                    "\nSPEEDボタンでスピードアップ"+
                    "\n\n左右それぞれ10回が1セット。合計２セットが標準　　片足を上げた場合はそれぞれ１セットずつ、合計２セット\n\n"

        val standardText = "10回が1セット。左右それぞれ1セット合計２セット（回）が標準\n片足を上げた場合はそれぞれ１セットずつ, 合計２セット(回)が標準\n最大99回\nSPEEDボタンでも同じ"
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
            openYoutube("https://youtu.be/trf2Ph_WWPQ")
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

