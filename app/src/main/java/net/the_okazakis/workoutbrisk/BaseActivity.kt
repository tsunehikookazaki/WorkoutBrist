package net.the_okazakis.workoutbrisk

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    protected lateinit var tv: TextView
    protected lateinit var tv2: TextView
    protected lateinit var textmenu: TextView
    protected lateinit var tvexpla: TextView
    protected lateinit var btnBack: Button
    protected lateinit var btnStart: Button
    protected lateinit var btnStop: Button
    protected lateinit var btnRestart: Button
    protected lateinit var btnChangeTimes: Button
    protected lateinit var btnSpeed: Button
    protected lateinit var btnYoutube: ImageButton

    // --- サービス関連 ---
    protected var workoutService: WorkoutService? = null
    protected var isBound = false
    
    // サービスから取得したSoundPoolを保持する
    protected var soundPool: SoundPool? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as WorkoutService.LocalBinder
            workoutService = binder.getService()
            soundPool = workoutService?.getSoundPool()
            isBound = true
            
            // 接続されたら音源をロードする
            loadAllStandardSounds()
            
            // サブクラスで追加の処理が必要な場合（オーバーライド用）
            onServiceConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            workoutService = null
            soundPool = null
        }
    }

    /**
     * サービス接続時に追加の処理を行いたい場合にサブクラスでオーバーライドする
     */
    open fun onServiceConnected() {}

    // --- 共通の変数 ---
    protected val handler = Handler(Looper.getMainLooper())
    protected var workoutId = 0
    protected var timeCount = 0
    protected var workmenu: String = ""
    protected var countVolume: Float = 1.0f
    protected var maxExTimes = 2
    protected var maxReps = 5
    protected var exTimes = 0
    protected var num = 0
    protected var nn = 0
    protected var speedTime = 1000L
    protected var normalspeedTime = 1000L
    protected var speedspeedTime = 700L
    protected var isSpeed = false
    protected var count = false
    protected var isSaved = false
    protected var isUp = true
    protected var choki = true
    protected var isStart = true

    // --- 共通のサウンドID ---
    protected var sndstr = 0
    protected var sndend = 0
    protected var sndup = 0
    protected var snddown = 0
    protected var sndChangeLeg = 0
    protected var sndstand = 0
    protected var sndsit = 0
    protected var sndgo = 0
    protected var sndchoki = 0
    protected var sndurachoki = 0
    protected var sndpa = 0
    protected var sndstre = 0
    protected var sndbent = 0
    protected var sndnon = 0
    protected var sndtaoshite = 0
    protected var sndkeep30s = 0
    protected var sndkeep = 0
    protected var sndloosen = 0
    protected var sndtsubu = 0
    protected var sndmodo = 0
    protected var sndkeep3s = 0
    protected var sndkaeteagete = 0
    protected var sndbreak = 0
    protected var sndright = 0
    protected var sndleft = 0
    protected var sndback = 0
    protected var sndopen = 0
    protected var sndSlowClose = 0
    protected var sndkeep1s = 0
    protected var sndtsubushite = 0
    protected var snd10re = 0
    protected var sndkeep20s = 0
    protected var sndpi = 0
    protected var sndToesUp = 0
    protected var sndStandToes = 0
    protected var sndDropDown = 0
    protected var sndSlowUp = 0
    protected var sndSlowDown = 0
    protected var sndkeep5 = 0
    protected var sndsizunde = 0
    protected var sndtaete = 0
    protected var sndhikitsuke = 0
    protected var sndSlowOpen = 0
    protected var sndkeepmama = 0
    protected var sndHoldKnee = 0
    protected var sndStretch = 0
    protected var sndBend = 0
    
    protected val sounds: MutableList<Int> = mutableListOf()
    protected val soundskeep: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // サービスにバインド
        val intent = Intent(this, WorkoutService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    protected fun loadAllStandardSounds() {
        val sp = soundPool ?: return
        
        sndstr = sp.load(this, R.raw.start, 1)
        sndend = sp.load(this, R.raw.goodjob, 1)
        sndup = sp.load(this, R.raw.up, 1)
        sndChangeLeg = sp.load(this, R.raw.changeleg, 1)
        sndstand = sp.load(this, R.raw.slowstand, 1)
        sndsit = sp.load(this, R.raw.sitslow, 1)
        sndgo = sp.load(this, R.raw.gu, 1)
        sndchoki = sp.load(this, R.raw.choki, 1)
        sndurachoki = sp.load(this, R.raw.urachoki, 1)
        sndpa = sp.load(this, R.raw.pa, 1)
        sndstre = sp.load(this, R.raw.stretch, 1)
        sndbent = sp.load(this, R.raw.bend, 1)
        sndnon = sp.load(this, R.raw.nosound, 1)
        sndtaoshite = sp.load(this, R.raw.taoshite, 1)
        sndkeep30s = sp.load(this, R.raw.keep30s, 1)
        sndkeep = sp.load(this, R.raw.v10, 1)
        sndloosen = sp.load(this, R.raw.loosen, 1)
        sndtsubu = sp.load(this, R.raw.tsubushite, 1)
        sndmodo = sp.load(this, R.raw.modo, 1)
        sndkeep3s = sp.load(this, R.raw.keep3s, 1)
        sndkaeteagete = sp.load(this, R.raw.kaeteagete, 1)
        sndbreak = sp.load(this, R.raw.takeabreak, 1)
        sndright = sp.load(this, R.raw.leftlegforward, 1)
        sndleft = sp.load(this, R.raw.rightlegforward, 1)
        sndback = sp.load(this, R.raw.goback, 1)
        sndopen = sp.load(this, R.raw.open, 1)
        sndSlowClose = sp.load(this, R.raw.slowclose, 1)
        sndkeep1s = sp.load(this, R.raw.keep1sec, 1)
        sndtsubushite = sp.load(this, R.raw.tsubushite, 1)
        snd10re = sp.load(this, R.raw.relax10sec, 1)
        snddown = sp.load(this, R.raw.down, 1)
        sndkeep20s = sp.load(this, R.raw.keep20s, 1)
        sndpi = sp.load(this, R.raw.pi, 1)
        sndToesUp = sp.load(this, R.raw.toesup, 1)
        sndStandToes = sp.load(this, R.raw.standtoes, 1)
        sndDropDown = sp.load(this, R.raw.dropdown, 1)
        sndSlowUp = sp.load(this, R.raw.slowup, 1)
        sndSlowDown = sp.load(this, R.raw.slowdown, 1)
        sndkeep5 = sp.load(this, R.raw.keep5sec, 1)
        sndsizunde = sp.load(this, R.raw.sizunde, 1)
        sndtaete = sp.load(this, R.raw.taete, 1)
        sndhikitsuke = sp.load(this, R.raw.hikitsuke, 1)
        sndSlowOpen = sp.load(this, R.raw.slowopen, 1)
        sndkeepmama = sp.load(this, R.raw.keepmama, 1)
        sndHoldKnee = sp.load(this, R.raw.holdknee, 1)
        sndStretch = sp.load(this, R.raw.stretch, 1)
        sndBend = sp.load(this, R.raw.bend, 1)

        sounds.clear()
        val vResIds = intArrayOf(
            R.raw.v1, R.raw.v2, R.raw.v3, R.raw.v4, R.raw.v5, R.raw.v6, R.raw.v7, R.raw.v8, R.raw.v9, R.raw.v10,
            R.raw.v11, R.raw.v12, R.raw.v13, R.raw.v14, R.raw.v15, R.raw.v16, R.raw.v17, R.raw.v18, R.raw.v19, R.raw.v20,
            R.raw.v21, R.raw.v22, R.raw.v23, R.raw.v24, R.raw.v25, R.raw.v26, R.raw.v27, R.raw.v28, R.raw.v29, R.raw.v30
        )
        for (resId in vResIds) {
            sounds.add(sp.load(this, resId, 1))
        }

        soundskeep.clear()
        val keepResIds = intArrayOf(
            R.raw.keep1s, R.raw.keep2s, R.raw.keep3s, R.raw.keep4s, R.raw.keep5s,
            R.raw.keep6s, R.raw.keep7s, R.raw.keep8s, R.raw.keep9s, R.raw.keep10s,
        )
        for (resId in keepResIds) {
            soundskeep.add(sp.load(this, resId, 1))
        }
    }


    protected fun initializeStandardSettings(explanation: String, dTimes: Int = 2, dReps: Int = 15) {
        countVolume = intent.getFloatExtra("TEXT_KEY", 1.0f)
        workoutId = intent.getIntExtra("TEXT_KEY2", 0)
        val menuArray = resources.getStringArray(R.array.lv_menu)
        workmenu = menuArray.getOrElse(workoutId) { "" }

        setupActivityUI(workmenu, explanation)
        loadSettingsTick(dTimes, dReps)
        updateHeaderUI()
    }

    protected fun openYoutube(youtubeUrl: String) {
        val intentY = Intent(this, Youtube::class.java)
        intentY.putExtra("yID", youtubeUrl)
        startActivity(intentY)
    }

    protected fun openChangeTimes(stdText: String, maxLimit: Int, maxRepsLimit: Int) {
        val intentC = Intent(this, MainActivity2::class.java)
        intentC.putExtra("TEXT_KEY4", workmenu)
        intentC.putExtra("TEXT_KEY5", workoutId)
        intentC.putExtra("STD_TEXT", stdText)
        intentC.putExtra("MAX_LIMIT", maxLimit)
        intentC.putExtra("MAX_REPS_LIMIT", maxRepsLimit)
        // 👇 追加：現在のコード上の初期値を送る
        intentC.putExtra("DEFAULT_TIMES", maxExTimes)
        intentC.putExtra("DEFAULT_REPS", maxReps)
        startActivity(intentC)
    }

    private fun setupActivityUI(title: String, explanation: String) {
        tv = findViewById(R.id.tv)
        tv2 = findViewById(R.id.tv2)
        textmenu = findViewById(R.id.textmenu)
        tvexpla = findViewById(R.id.tvexpla)
        btnBack = findViewById(R.id.btnback)
        btnStart = findViewById(R.id.btStart)
        btnStop = findViewById(R.id.btStop)
        btnRestart = findViewById(R.id.btnrestart)
        btnYoutube = findViewById(R.id.youtube)
        btnChangeTimes = findViewById(R.id.button2)
        btnSpeed = findViewById(R.id.btspeed)

        textmenu.text = title
        tvexpla.text = explanation
        btnStop.isEnabled = false
        btnRestart.isEnabled = false

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()
    }

    protected fun updateHeaderUI() {
        if (::tv.isInitialized) {
            val unit = if (workmenu.contains("伸ばし") || workmenu.contains("キープ") || workmenu.contains("腓腹筋") || workmenu.contains("セット") || workmenu.contains("潰し")) {
                getString(R.string.unit_sets)
            } else {
                getString(R.string.unit_times)
            }
            tv.text = getString(R.string.header_progress_format, maxExTimes, unit)
        }
    }

    protected fun playSoundSingle(soundId: Int) {
        if (soundId != 0) {
            workoutService?.playSound(soundId, countVolume)
        }
    }

    open fun loadSettingsTick(dTimes: Int = 2, dReps: Int = 15) {
        val pref = getSharedPreferences("WorkoutSettings", MODE_PRIVATE)
        // SharedPreferencesに値がない場合のみ、コード上の初期値(dTimes)を使う
        maxExTimes = pref.getInt("times_$workoutId", dTimes)
        maxReps = pref.getInt("reps_$workoutId", dReps)
    }

    protected fun setUIForStarting(runnable: Runnable, startNum: Int, vararg otherButtons: View) {
        btnStart.isEnabled = false
        btnStop.isEnabled = true
        btnRestart.isEnabled = false
        btnSpeed.isEnabled = false
        otherButtons.forEach { it.isEnabled = false }

        isSaved = false
        isStart = true
        isSpeed = false
        exTimes = 1
        nn = 0
        timeCount = 0
        num = startNum
        speedTime = normalspeedTime

        workoutService?.startForegroundService(workmenu)

        tv2.text = getString(R.string.msg_starting)
        handler.removeCallbacks(runnable)
        
        // 💡 最初の「始めます」が途切れないよう、わずかに（500ms）遅らせて再生・開始する
        handler.postDelayed({
            playSoundSingle(sndstr)
            handler.post(runnable)
        }, 500)
    }

    protected fun restartTraining(runnable: Runnable, vararg otherButtons: View) {
        btnStart.isEnabled = false
        btnStop.isEnabled = true
        btnRestart.isEnabled = false
        btnSpeed.isEnabled = false
        otherButtons.forEach { it.isEnabled = false }

        workoutService?.startForegroundService(workmenu)
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    protected fun setUIForStopping(vararg otherButtons: View) {
        btnStart.isEnabled = true
        btnStop.isEnabled = false
        btnRestart.isEnabled = true
        btnSpeed.isEnabled = true
        otherButtons.forEach { it.isEnabled = true }

        handler.removeCallbacksAndMessages(null)
        workoutService?.stopForegroundService()
    }

    protected fun setUIForSpeedStarting(runnable: Runnable, startNum: Int, vararg otherButtons: View) {
        btnStart.isEnabled = false
        btnStop.isEnabled = true
        btnRestart.isEnabled = false
        btnSpeed.isEnabled = false
        otherButtons.forEach { it.isEnabled = false }

        isSaved = false
        isStart = true
        isSpeed = true
        exTimes = 1
        nn = 0
        timeCount = 0
        num = startNum

        workoutService?.startForegroundService(workmenu)

        tv2.text = getString(R.string.msg_starting)
        speedTime = speedspeedTime
        handler.removeCallbacks(runnable)
        
        // 💡 最初の「始めます」が途切れないよう、わずかに（500ms）遅らせて再生・開始する
        handler.postDelayed({
            playSoundSingle(sndstr)
            handler.post(runnable)
        }, 500)
    }

    protected fun handleTrainingComplete(tvMessage: TextView, vararg otherButtons: View, onSaveComplete: () -> Unit) {
        btnStart.isEnabled = true
        btnStop.isEnabled = false
        btnRestart.isEnabled = false
        otherButtons.forEach { it.isEnabled = true }

        workoutService?.stopForegroundService()

        if (!isSaved) {
            RecordManager.saveRecord(this, "%02d%s".format(workoutId, workmenu))
            SharedRecordManager.updateStats(this, "strech", workmenu)
            isSaved = true
            tvMessage.text = getString(R.string.good_job)
            onSaveComplete()
        }
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
    }
}
