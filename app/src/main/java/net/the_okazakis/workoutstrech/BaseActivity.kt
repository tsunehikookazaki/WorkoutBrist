package net.the_okazakis.workoutstrech

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton

open class BaseActivity : AppCompatActivity() {

    protected lateinit var tv: TextView
    protected lateinit var tv2: TextView
    protected lateinit var textmenu: TextView
    protected lateinit var tvexpla: TextView
    protected lateinit var btnback: Button
    protected lateinit var btnstart: Button
    protected lateinit var btnstop: Button
    protected lateinit var btnrerstart: Button
    //protected lateinit var btnyoutube: Button
    protected lateinit var btnChangeTimes: Button
    protected lateinit var btnspeed: Button
    protected lateinit var btnyoutube: ImageButton

    // --- 共通の変数 ---
    protected  val handler = Handler(Looper.getMainLooper())
    protected lateinit var soundPool: SoundPool
    protected var _workoutId = 0
    protected var timeCount = 0
    protected var workmenu: String = ""
    protected var countVolume: Float = 1.0f
    protected var maxextimes = 2
    protected var maxReps = 5
    protected var extimes = 0
    protected var num = 0
    protected var nn = 0
    protected var speedTime = 1000L
    protected var softspeedTime = 1200L
    protected var normalspeedTime = 1000L
    protected var speedspeedTime = 700L
    protected var isSpeed = false
    protected var count = false
    protected var isSaved = false
    protected var isUp = true
    protected var isFirsttime = true
    protected var choki = true
    protected var isFirstleg = false
    protected var isStart = true
    // --- 共通のサウンドID ---
    protected var sndstr = 0
    protected var sndend = 0
    protected var sndup = 0
    protected var snddown = 0
    protected var sndchangleg = 0
    protected var sndstand =0
    protected var sndsit =0
    protected var sndgo =0
    protected var sndchoki =0
    protected var sndurachoki =0
    protected var sndpa =0
    protected var sndstre =0
    protected var sndbent =0
    protected var sndnon =0
    protected var sndtaoshite =0
    protected var sndkeep30s =0
    protected var sndkeep = 0
    protected var sndloosen = 0
    protected var sndtsubu = 0
    protected var sndmodo = 0
    protected var sndkeep3s = 0
    protected var sndkaeteagete = 0
    protected var sndbreak = 0
    protected var sndright=0
    protected var sndleft=0
    protected var sndback=0
    protected var sndkeep1s = 0
    protected var sndtsubushite = 0
    protected var snd10re = 0
    protected var sndkeep20s = 0
    protected var sndpi = 0
    protected var sndtoesup = 0
    protected var sndstandtoes = 0
    protected var snddropdown = 0
    protected var sndslowup = 0
    protected var sndslowdown = 0
    protected var sndkeep5 = 0
    protected var sndsizunde = 0
    protected var sndtaete = 0
    protected var sndhikitsuke = 0
    protected var sndslowopen = 0
    protected var sndslowclose = 0
    protected var sndkeepmama = 0
    protected var sndopen = 0
    protected var sndholdknee = 0
    protected var sndstretch = 0
    protected var sndbend = 0
    // 秒数用
    // 秒数用はリストにまとめる（個別の snd1..15 は削除！）
    // 音源IDを保持するリスト（Ashiage.ktなどで使う用）
    protected val sounds: MutableList<Int> = mutableListOf()
    protected val soundskeep: MutableList<Int> = mutableListOf()

    // 音源を一括ロードする共通メソッド
    protected fun loadAllStandardSounds() {
        // 固定音のロード
        sndstr = soundPool.load(this, R.raw.start, 1)
        sndend = soundPool.load(this, R.raw.goodjob, 1)
        sndup = soundPool.load(this, R.raw.up, 1)
        sndchangleg = soundPool.load(this, R.raw.changeleg, 1)
        sndstand = soundPool.load(this, R.raw.slowstand, 1)
        sndsit = soundPool.load(this, R.raw.sitslow, 1)
        sndgo = soundPool.load(this, R.raw.gu, 1)
        sndchoki = soundPool.load(this, R.raw.choki, 1)
        sndurachoki = soundPool.load(this, R.raw.urachoki, 1)
        sndpa = soundPool.load(this, R.raw.pa, 1)
        sndstre = soundPool.load(this, R.raw.stretch, 1)
        sndbent = soundPool.load(this, R.raw.bend, 1)
        sndnon = soundPool.load(this, R.raw.nosound, 1)
        sndtaoshite = soundPool.load(this, R.raw.taoshite, 1)
        sndkeep30s = soundPool.load(this, R.raw.keep30s, 1)
        sndkeep = soundPool.load(this, R.raw.v10, 1) // keep10sec was missing, using v10 as fallback
        sndloosen = soundPool.load(this, R.raw.loosen, 1)
        sndtsubu = soundPool.load(this, R.raw.tsubushite, 1)
        sndup = soundPool.load(this, R.raw.up, 1)
        sndmodo = soundPool.load(this, R.raw.modo, 1)
        sndkeep3s = soundPool.load(this, R.raw.keep3s, 1)
        sndkaeteagete = soundPool.load(this, R.raw.kaeteagete, 1)
        sndbreak = soundPool.load(this, R.raw.takeabreak, 1)
        sndright = soundPool.load(this, R.raw.leftlegforward, 1) // Note: original labels might be swapped
        sndleft = soundPool.load(this, R.raw.rightlegforward, 1)
        sndback = soundPool.load(this, R.raw.goback, 1)
        sndopen = soundPool.load(this, R.raw.open, 1)
        sndslowclose = soundPool.load(this, R.raw.slowclose, 1)
        sndkeep1s = soundPool.load(this, R.raw.keep1sec, 1)
        sndtsubushite = soundPool.load(this, R.raw.tsubushite, 1)
        snd10re = soundPool.load(this, R.raw.relax10sec, 1)
        snddown = soundPool.load(this, R.raw.down, 1)
        sndkeep20s = soundPool.load(this, R.raw.keep20s, 1)
        sndpi = soundPool.load(this, R.raw.pi, 1)
        sndtoesup = soundPool.load(this, R.raw.toesup, 1)
        sndstandtoes = soundPool.load(this, R.raw.standtoes, 1)
        snddropdown = soundPool.load(this, R.raw.dropdown, 1)
        sndslowup = soundPool.load(this,R.raw.slowup,1)
        sndslowdown = soundPool.load(this,R.raw.slowdown,1)
        sndkeep5 = soundPool.load(this, R.raw.keep5sec, 1)
        sndsizunde = soundPool.load(this, R.raw.sizunde, 1)
        sndtaete = soundPool.load(this, R.raw.taete, 1)
        sndhikitsuke = soundPool.load(this, R.raw.hikitsuke, 1)
        sndslowopen = soundPool.load(this, R.raw.slowopen, 1)
        sndkeepmama = soundPool.load(this, R.raw.keepmama, 1)
        sndslowup = soundPool.load(this, R.raw.slowup, 1)
        sndslowdown = soundPool.load(this, R.raw.slowdown, 1)
        sndholdknee = soundPool.load(this, R.raw.holdknee, 1)
        sndstretch = soundPool.load(this, R.raw.stretch, 1)
        sndbend = soundPool.load(this, R.raw.bend, 1)


        // v1〜v30 を自動でロードしてリストに追加
        sounds.clear()
        for (i in 1..30) {
            val resId = resources.getIdentifier("v$i", "raw", packageName)
            if (resId != 0) {
                sounds.add(soundPool.load(this, resId, 1))
            }
        }
        // keep1s〜keep30s を自動でロードしてリストに追加
        soundskeep.clear()
        for (i in 1..30) {
            val resId = resources.getIdentifier("keep${i}s", "raw", packageName)
            if (resId != 0) {
                soundskeep.add(soundPool.load(this, resId, 1))
            }
        }
    }

    /**
     * 基本の音源（スタート、終了、アップ、足替え）と
     * カウント音（v1, v2...）を一括でロードする
     */
    protected fun loadCommonSounds(countResIds: List<Int>) {
        // 1. 固定音源のロード
        sndstr = soundPool.load(this, R.raw.start, 1)
        sndend = soundPool.load(this, R.raw.goodjob, 1)
        sndup = soundPool.load(this, R.raw.up, 1)
        sndchangleg = soundPool.load(this, R.raw.changeleg, 1)

        // 2. カウント音源（v1〜v15など）をリストにロード
        sounds.clear()
        countResIds.forEach { resId ->
            sounds.add(soundPool.load(this, resId, 1))
        }
    }



    // --- 初期化：Intent・DB・UI・SoundPoolを一括設定 ---
    protected fun initializeStandardSettings(explanation: String,) {


        // Intentデータ取得
        countVolume = intent.getFloatExtra("TEXT_KEY", 1.0f)
        _workoutId = intent.getIntExtra("TEXT_KEY2", 0)

        // メニュー名取得
        val menuArray = resources.getStringArray(R.array.lv_menu)
        workmenu = menuArray.getOrElse(_workoutId) { "" }


        // UI紐付け
        setupActivityUI(workmenu, explanation)

        // 目標回数取得
        loadSettingsTick()

        // SoundPool初期化
        val aa = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()
        soundPool = SoundPool.Builder().setAudioAttributes(aa).setMaxStreams(3).build()
    }


    // YouTube画面へ遷移する共通メソッド
    protected fun openYoutube(youtubeUrl: String) {
        val intentY = Intent(this, Youtube::class.java)
        intentY.putExtra("yID", youtubeUrl)
        startActivity(intentY)
    }

    // 設定変更画面へ遷移する共通メソッド
    protected fun openChangeTimes(stdText: String, maxLimit: Int, maxRepsLimit: Int) {
        val intentC = Intent(this, MainActivity2::class.java)
        intentC.putExtra("TEXT_KEY4", workmenu) // initializeStandardSettingsで取得済みの変数
        intentC.putExtra("TEXT_KEY5", _workoutId)

        // 👇 ここで受け取った文章をIntentに詰める
        intentC.putExtra("STD_TEXT", stdText)
// 👇【追加】上限の数値をIntentに詰める
        intentC.putExtra("MAX_LIMIT", maxLimit)
        intentC.putExtra("MAX_REPS_LIMIT", maxRepsLimit)
        startActivity(intentC)
    }

    private fun setupActivityUI(title: String, explanation: String) {

        tv = findViewById(R.id.tv)
        tv2 = findViewById(R.id.tv2)
        textmenu = findViewById(R.id.textmenu)
        tvexpla = findViewById(R.id.tvexpla)
        btnback = findViewById(R.id.btnback)
        btnstart = findViewById(R.id.btStart)
        btnstop = findViewById(R.id.btStop)
        btnrerstart = findViewById(R.id.btnrestart)
        btnyoutube = findViewById(R.id.youtube)
        btnChangeTimes = findViewById(R.id.button2)
        btnspeed = findViewById(R.id.btspeed)

        textmenu.text = title
        tvexpla.text = explanation
        btnstop.isEnabled = false
        btnrerstart.isEnabled = false

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()
    }

    // 前の音をクリアして再生する便利メソッド
    protected fun playSoundSingle(soundId: Int) {
        if (soundId != 0) {
            soundPool.autoPause() // 再生中の全音を停止
            soundPool.play(soundId, countVolume, countVolume, 1, 0, 1.0f)
        }
    }
    open fun loadSettingsTick() {

        val (times, reps) = getPreferenceSettings(_workoutId)

        maxextimes = times
        maxReps = reps
    }
    //プリファレンスから値を持ってくる
    protected fun getPreferenceSettings(workoutId: Int): Pair<Int, Int> {

        val pref = getSharedPreferences("WorkoutSettings", MODE_PRIVATE)

        val times = pref.getInt("times_$workoutId", 2)
        val reps = pref.getInt("reps_$workoutId", 15)

        return Pair(times, reps)
    }

    // --- ボタン状態制御 ---
    protected fun setUIForStarting(runnable: Runnable,startNum: Int,vararg otherButtons: View)
    {
        btnstart.isEnabled = false
        btnstop.isEnabled = true
        btnrerstart.isEnabled = false
        btnspeed.isEnabled = false

        otherButtons.forEach {
            it.isEnabled = false
        }

        // トレーニング状態初期化
        isSaved = false
        isStart = true
        isSpeed = false
        extimes = 1
        nn = 0
        timeCount = 0
        num = startNum
        speedTime = normalspeedTime

        // 音と表示
        playSoundSingle(sndstr)
        tv2.text = "始めます"

        // Runnable開始
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    protected fun restartTraining(
        runnable: Runnable,
        vararg otherButtons: View
    ) {
        btnstart.isEnabled = false
        btnstop.isEnabled = true
        btnrerstart.isEnabled = false
        btnspeed.isEnabled = false
        otherButtons.forEach {
            it.isEnabled = false
        }

        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }


    protected fun setUIForStopping(vararg otherButtons: View) {
        btnstart.isEnabled = true
        btnstop.isEnabled = false
        btnrerstart.isEnabled = true
        btnspeed.isEnabled = true
        otherButtons.forEach { it.isEnabled = true }
        soundPool.autoPause() // 停止時も音をクリア
    }

    // speedモード開始
    protected fun setUIForSpeedStarting(
        runnable: Runnable,
        startNum: Int,
        vararg otherButtons: View
    ) {
        btnstart.isEnabled = false
        btnstop.isEnabled = true
        btnrerstart.isEnabled = false
        btnspeed.isEnabled = false

        otherButtons.forEach {
            it.isEnabled = false
        }

        // トレーニング状態初期化
        isSaved = false
        isStart = true
        isSpeed = true
        extimes = 1
        nn = 0
        timeCount = 0
        num = startNum

        // 音と表示
        playSoundSingle(sndstr)
        tv2.text = "始めます"
        // speed専用
        speedTime = speedspeedTime
        // Runnable開始
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    protected fun handleTrainingComplete(tvMessage: TextView, vararg otherButtons: View, onSaveComplete: () -> Unit) {

        btnstart.isEnabled = true
        btnstop.isEnabled = false
        btnrerstart.isEnabled = false
        otherButtons.forEach { it.isEnabled = true }

        if (!isSaved) {

            //    RecordManager.saveRecord(this, "${_workoutId}$workmenu")
            RecordManager.saveRecord(this, "%02d%s".format(_workoutId, workmenu))

            // 💡【追加】LetsDoItの窓口へ、アプリキー「strech」として完了したメニュー名を送信
            SharedRecordManager.updateStats(this, "strech", workmenu)

                isSaved = true
                tvMessage.text = getString(R.string.good_job)
                onSaveComplete()
        }
    }


    override fun onDestroy() {
        if (::soundPool.isInitialized) soundPool.release()
        super.onDestroy()
    }
}