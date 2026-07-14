package net.the_okazakis.workoutbrisk

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.net.toUri
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.KeyEvent
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast

// インポートの追加
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// 結果を待ち受けるためのリクエストコード (任意の値)
private const val CONGRATULATIONS_REQUEST_CODE = 100

// CongratulationsActivityから返される結果コード
private const val RESULT_CONTINUE = 1 // 続ける
private const val RESULT_FINISH = 2   // 終了する


class BriskMainActivity : AppCompatActivity() {

    // 💡【修正】センサー用の変数をクラスのプロパティ（内部）へ移動
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var initialStepCount = -1f // アプリ起動時（またはスタート時）の累計歩数を保持
    private var currentSessionSteps = 0 // 今回の計測中に歩いた歩数
    private lateinit var tvLiveSteps: TextView // 歩数表示用のTextView

    // 💡【修正】リスナーをクラス内部に移動し、handler.post で画面更新するように変更
    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = event.values[0]
                if (initialStepCount < 0) {
                    initialStepCount = totalSteps
                }
                currentSessionSteps = (totalSteps - initialStepCount).toInt()

                // メインスレッドのhandlerを使って安全にUI更新
                handler.post {
                    tvLiveSteps.text = getString(R.string.brisk_steps_format, currentSessionSteps)
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private lateinit var tvMode: TextView
    private lateinit var tvCount: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvTempoLabel2 : TextView  // 現在のテンポラベル
    private lateinit var tvTempoLabel : TextView  // 現在のテンポラベル
    private lateinit var tvCurrentTempo: TextView
    private lateinit var btnTempoUp: Button
    private lateinit var btnTempoDown: Button // テンポ入力
    private lateinit var label0: TextView
    private lateinit var btnStart: Button
    private lateinit var btnMood: Button
    private lateinit var btnRestart: Button
    private lateinit var btnStop: Button
    private lateinit var btnDelete: Button
    private lateinit var txtLogA: TextView

    private var fastMood = false
    private var isFastWalk = false
    private var fastWalkCount = 0
    private var tempoBpm = 125
    private var nokoriSec = 0
    private var timerLength: Long = 3 * 60 * 1000    //3分
    //private var timerLength: Long = 15 * 1000    //15秒  ***********テスト用
    private var timerLength0: Long = 0
    private var restartFlag = false
    private var pauseNokori: Long = 0

    private lateinit var handler: Handler
    private lateinit var vibrator: Vibrator
    private var beatTimer: CountDownTimer? = null
    private var modeTimer: CountDownTimer? = null
    private var soundPool: SoundPool? = null
    private var soundIdChoice = 0
    private var soundIdChoice2 = 0
    private var soundIdFast0 = 0
    private var soundIdNorm = 0
    private var soundId5Sec = 0
    private var soundIdStopClick = 0
    private var soundIdKai1 = 0
    private var soundIdKai2 = 0
    private var soundIdKai3 = 0
    private var soundIdKai4 = 0
    private var rightLeg = true

    private val prefName = "WalkPrefs"
    private val keyCount = "fastWalkCount"
    private val keyTempo = "tempoBpm"
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    private val keyMode = "fastMood"

    private var fastWalkCount2 = 0

    // ダブルクリック判定用の変数
    private var clickCount = 0
    private val doubleClickDelayHandler = Handler(Looper.getMainLooper())
    private val doubleClickDelay = 300L // 判定時間（ミリ秒）

    private var shutterEnabled = true
    private lateinit var switchShutter: androidx.appcompat.widget.SwitchCompat

    private var buttonShow = 0
    var countVolume = 1.0f  //ボリューム初期値

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brisk_main)

        // 💡 画面を常にオンに保つ設定
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.title = getString(R.string.app_name)
        }

        // ハンドラーを一番最初に初期化（ステップリスナーで使うため）
        handler = Handler(Looper.getMainLooper())

        checkPermissions()

        val volTextView = findViewById<TextView>(R.id.voltext)
        val seekBar = findViewById<SeekBar>(R.id.seek_bar)
        seekBar.max = 5

        var volumeStr = "100%"
        var progress: Int
        volTextView.text = getString(R.string.brisk_volume_format, volumeStr)

        // 💡 センサーマネージャー等の初期化
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = try {
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        } catch (_: Exception) {
            null
        }
        tvLiveSteps = findViewById(R.id.tvLiveSteps)

        // イベントリスナーの追加
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progressValue: Int, fromUser: Boolean) {
                progress = progressValue
                when (progress) {
                    5 -> { countVolume = 1.0f; volumeStr = "100%" }
                    4 -> { countVolume = 0.8f; volumeStr = "80%" }
                    3 -> { countVolume = 0.4f; volumeStr = "40%" }
                    2 -> { countVolume = 0.2f; volumeStr = "20%" }
                    1 -> { countVolume = 0.1f; volumeStr = "10%" }
                    0 -> { countVolume = 0.05f; volumeStr = "5%" }
                }
                volTextView.text = getString(R.string.brisk_volume_format, volumeStr)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 保存されているカウントを読み込み
        val pref = getSharedPreferences(prefName, MODE_PRIVATE)
        fastWalkCount = pref.getInt(keyCount, 0)
        tempoBpm = pref.getInt(keyTempo, 125)
        fastMood = pref.getBoolean(keyMode, true)
        isFastWalk = fastMood

        // View取得
        tvMode = findViewById(R.id.tvMode)
        label0 = findViewById(R.id.label0)
        tvCount = findViewById(R.id.tvCount)
        tvTimer = findViewById(R.id.tvTimer)
        tvTempoLabel2 = findViewById(R.id.tvTempoLabel2)
        tvTempoLabel = findViewById(R.id.tvTempoLabel)
        tvCurrentTempo = findViewById(R.id.tvCurrentTempo)
        btnTempoUp = findViewById(R.id.btnTempoUp)
        btnTempoDown = findViewById(R.id.btnTempoDown)
        btnStart = findViewById(R.id.btnStart)
        btnMood = findViewById(R.id.btnMood)
        btnRestart = findViewById(R.id.btnRestart)
        btnStop = findViewById(R.id.btnStop)
        btnDelete = findViewById(R.id.btnDelete)
        txtLogA = findViewById(R.id.txtLogA)
        tvTempoLabel2.text = getString(R.string.brisk_label_current_tempo)

        tvCount.text = getString(R.string.brisk_count_format, fastWalkCount2)
        updateNextModeLabel()

        // UIに反映
        tvCurrentTempo.text = tempoBpm.toString()
        //tvCurrentTempo.setText(tempoBpm.toString())
        tvTempoLabel2.text = getString(R.string.brisk_tempo_current_format, tempoBpm)
        tvCount.text = getString(R.string.brisk_count_format, fastWalkCount2)

        btnStart.isEnabled = true
        btnMood.isEnabled = true
        btnStop.isEnabled = false
        btnRestart.isEnabled = false
        btnDelete.isEnabled = true
        tvTempoLabel2.visibility = View.GONE
        tvCurrentTempo.visibility = View.VISIBLE
        tvTempoLabel.visibility = View.VISIBLE

        updateNextModeLabel()
        cleanOldLogs()
        showLog()

        // 音初期化
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        try {
            soundPool = SoundPool.Builder().setMaxStreams(3).setAudioAttributes(attrs).build()
            soundIdChoice = soundPool!!.load(this, R.raw.choice, 1)
            soundIdChoice2 = soundPool!!.load(this, R.raw.choice2, 1)
            soundIdFast0 = soundPool!!.load(this, R.raw.fast0, 1)
            soundIdNorm = soundPool!!.load(this, R.raw.norm, 1)
            soundId5Sec = soundPool!!.load(this, R.raw.start5sec, 1)
            soundIdStopClick = soundPool!!.load(this, R.raw.stopclick, 1)
            soundIdKai1 = soundPool!!.load(this, R.raw.kai1, 1)
            soundIdKai2 = soundPool!!.load(this, R.raw.kai2, 1)
            soundIdKai3 = soundPool!!.load(this, R.raw.kai3, 1)
            soundIdKai4 = soundPool!!.load(this, R.raw.kai4, 1)
        } catch (e: Exception) {
            android.util.Log.e("BriskMainActivity", "SoundPool initialization failed", e)
        }

        vibrator = getSystemService(Vibrator::class.java)!!

        btnMood.setOnClickListener {
            fastMood = !fastMood
            updateNextModeLabel()
        }
        btnTempoUp.setOnClickListener {
            if (tempoBpm < 160) {
                tempoBpm++
                tvCurrentTempo.text = tempoBpm.toString()
            }
        }

        btnTempoDown.setOnClickListener {
            if (tempoBpm > 60) {
                tempoBpm--
                tvCurrentTempo.text = tempoBpm.toString()
            }
        }

        btnStart.setOnClickListener{
            startClick()
        }

        btnStop.setOnClickListener {
            stopClick()
        }

        btnRestart.setOnClickListener {
            restartClick()
        }

        btnDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("記録の削除")
                .setMessage("全ての記録を削除しますか？\nこの操作は元に戻せません。")
                .setPositiveButton("削除") { _, _ ->
                    restartFlag = false
                    cleanAll()
                    showLog()

                    btnStart.isEnabled = true
                    btnMood.isEnabled = true
                    btnStop.isEnabled = false
                    btnRestart.isEnabled = false
                    btnDelete.isEnabled = true
                    tvTempoLabel2.visibility = View.GONE
                    tvCurrentTempo.visibility = View.VISIBLE
                    tvTempoLabel.visibility = View.VISIBLE
                    buttonShow = 1
                }
                .setNegativeButton("キャンセル", null)
                .show()
        }

        val pref2 = getSharedPreferences(prefName, MODE_PRIVATE)
        shutterEnabled = pref2.getBoolean("shutterEnabled", true)
        switchShutter = findViewById(R.id.switchShutter)
        switchShutter.setOnCheckedChangeListener(null)
        switchShutter.isChecked = shutterEnabled
        switchShutter.text = if (shutterEnabled) getString(R.string.label_shutter_on) else getString(R.string.label_shutter_off)

        switchShutter.setOnCheckedChangeListener { _, isChecked ->
            shutterEnabled = isChecked
            switchShutter.text = if (isChecked) getString(R.string.label_shutter_on) else getString(R.string.label_shutter_off)
            pref2.edit {
                putBoolean("shutterEnabled", isChecked)
            }
            val toastMsg = if (isChecked) getString(R.string.toast_shutter_on) else getString(R.string.toast_shutter_off)
            Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            permissions.add(android.Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        val needed = permissions.filter {
            androidx.core.content.ContextCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            androidx.core.app.ActivityCompat.requestPermissions(this, needed.toTypedArray(), 100)
        }
    }

    override fun onResume() {
        super.onResume()
        cleanOldLogs()
        updateTodayCount()
        showLog()

        // 💡 歩数センサーの監視を開始
        stepCounterSensor?.let {
            sensorManager.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // 💡 バッテリー消費を防ぐためにセンサーを解除
        sensorManager.unregisterListener(stepListener)
    }

    private fun updateTodayCount() {
        val prefs = getSharedPreferences(prefName, MODE_PRIVATE)
        val logs = JSONArray(prefs.getString("logs", "[]"))
        val todayStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        var countValue = 0
        for (i in 0 until logs.length()) {
            val entry = logs.getString(i)
            if (entry.contains(todayStr)) {
                countValue++
            }
        }
        fastWalkCount2 = countValue
        tvCount.text = getString(R.string.brisk_count_format, fastWalkCount2)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!shutterEnabled) {
            return super.onKeyDown(keyCode, event)
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (event?.repeatCount == 0) {
                processClick()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun processClick() {
        clickCount++
        if (clickCount == 1) {
            doubleClickDelayHandler.postDelayed(singleClickRunnable, doubleClickDelay)
        } else if (clickCount == 2) {
            doubleClickDelayHandler.removeCallbacks(singleClickRunnable)
            if (buttonShow == 0) {
                startClick()
                buttonShow = 1
                Toast.makeText(this, getString(R.string.toast_start_click), Toast.LENGTH_SHORT).show()
                vibrateClick()
            }
            clickCount = 0
        }
    }

    private val singleClickRunnable = Runnable {
        if (buttonShow == 0) {
            restartClick()
            Toast.makeText(this, getString(R.string.toast_restart_click), Toast.LENGTH_SHORT).show()
            vibrateClick()
        } else {
            stopClick()
            Toast.makeText(this, getString(R.string.toast_stop_click), Toast.LENGTH_SHORT).show()
            soundPool?.play(soundIdStopClick, countVolume, countVolume, 1, 0, 1.0f)
            vibrateClick()
        }
        clickCount = 0
    }

    private fun vibrateClick() {
        if (shutterEnabled) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        }
    }

    private fun vibrateLong() {
        val pattern = longArrayOf(0, 200, 50, 200, 50, 200, 50, 200)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_howto -> {
                startActivity(Intent(this, HowToActivity::class.java))
                true
            }
            R.id.menu_record -> {
                startActivity(Intent(this, RecordActivity::class.java))
                true
            }
            R.id.menu_open_image -> {
                startActivity(Intent(this, ImageViewActivity::class.java))
                true
            }
            R.id.menu_stop_app -> {
                finishAndRemoveTask()
                true
            }
            R.id.menu_jtrc -> {
                val intent = Intent(Intent.ACTION_VIEW, "https://www.jtrc.or.jp/".toUri())
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateNextModeLabel() {
        val modeStr = if (fastMood) getString(R.string.brisk_mode_fast) else getString(R.string.brisk_mode_normal)
        btnMood.text = getString(R.string.brisk_start_with_mode_format, modeStr)
        tvMode.text = getString(R.string.label_starting_on_click)
    }

    private fun startCycle() {
        pauseNokori = 0
        timerLength0 = timerLength
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
        startModeTimer(timerLength0)
    }

    private fun startModeTimer(duration: Long) {
        modeTimer?.cancel()
        if (isFastWalk) {
            fastWalkSound()
        } else {
            normWalkSound()
        }

        modeTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                nokoriSec = (millisUntilFinished / 1000).toInt()
                val elapsedSec = ((timerLength - millisUntilFinished) / 1000).toInt()
                tvTimer.text = getString(R.string.brisk_timer_format, elapsedSec)
                pauseNokori = millisUntilFinished

                if (nokoriSec == 5 && !isFastWalk) {
                    soundPool?.play(soundId5Sec, countVolume, countVolume, 1, 0, 1.0f)
                } else if (nokoriSec == 2 && !isFastWalk) {
                    vibrateLong()
                }
            }

            override fun onFinish() {
                if (isFastWalk) {
                    fastWalkCount2++
                    tvCount.text = getString(R.string.brisk_count_format, fastWalkCount2)
                    saveCount()
                    saveLog()
                    showLog()
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(1000)
                    }

                    if (fastWalkCount2 > 0 && fastWalkCount2 % 5 == 0) {
                        val intent = Intent(this@BriskMainActivity, CongratulationsActivity::class.java).apply {
                            putExtra("COUNT", fastWalkCount2)
                        }
                        @Suppress("DEPRECATION")
                        startActivityForResult(intent, CONGRATULATIONS_REQUEST_CODE)

                        isFastWalk = false
                        fastMood = false

                        tvMode.text = getString(R.string.brisk_current_mode_format, getString(R.string.brisk_mode_normal))
                        btnMood.text = getString(R.string.brisk_start_with_mode_format, getString(R.string.brisk_mode_normal))

                        stopClick()
                        return
                    }
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(500)
                    }
                }

                isFastWalk = !isFastWalk
                val modeStr = if (isFastWalk) getString(R.string.brisk_mode_fast) else getString(R.string.brisk_mode_normal)
                tvMode.text = getString(R.string.brisk_current_mode_format, modeStr)
                btnMood.text = getString(R.string.brisk_start_with_mode_format, modeStr)
                startModeTimer(timerLength)
            }
        }.start()

        startBeatTimer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONGRATULATIONS_REQUEST_CODE) {
            when (resultCode) {
                RESULT_CONTINUE -> {
                    startClick()
                }
                RESULT_FINISH -> {
                    stopCycle()
                }
            }
        }
    }

    private fun startBeatTimer() {
        beatTimer?.cancel()
        if (!isFastWalk) return

        val interval = (60000 / tempoBpm).toLong()
        handler.postDelayed({
            beatTimer = object : CountDownTimer(timerLength + 1000, interval) {
                override fun onTick(millisUntilFinished: Long) {
                    soundPool?.play(if (rightLeg) soundIdChoice else soundIdChoice2, countVolume, countVolume, 1, 0, 1.0f)
                    rightLeg = !rightLeg
                }
                override fun onFinish() {}
            }.start()
        }, 800)
    }

    private fun fastWalkSound() {
        soundPool?.play(soundIdFast0, countVolume, countVolume, 0, 0, 1.0f)
    }

    private fun normWalkSound() {
        when (fastWalkCount2) {
            1 -> soundPool?.play(soundIdKai1, countVolume, countVolume, 0, 0, 1.0f)
            2 -> soundPool?.play(soundIdKai2, countVolume, countVolume, 0, 0, 1.0f)
            3 -> soundPool?.play(soundIdKai3, countVolume, countVolume, 0, 0, 1.0f)
            4 -> soundPool?.play(soundIdKai4, countVolume, countVolume, 0, 0, 1.0f)
        }
        handler.postDelayed({
            soundPool?.play(soundIdNorm, countVolume, countVolume, 0, 0, 1.0f)
        }, 800)
    }

    private fun stopCycle() {
        modeTimer?.cancel()
        beatTimer?.cancel()
        handler.removeCallbacksAndMessages(null)
        tvTimer.text = getString(R.string.brisk_status_idle)
        tvMode.text = getString(R.string.label_starting_on_click)
        soundPool?.play(soundIdStopClick, countVolume, countVolume, 1, 0, 1.0f)
    }

    private fun restart() {
        if (pauseNokori > 0) {
            startModeTimer(pauseNokori)
        } else {
            startCycle()
        }
    }

    private fun saveCount() {
        getSharedPreferences(prefName, MODE_PRIVATE).edit {
            putInt(keyCount, fastWalkCount2)
            putInt(keyTempo, tempoBpm)
            putBoolean(keyMode, fastMood)
        }
    }

    private fun saveLog() {
        val prefs = getSharedPreferences(prefName, MODE_PRIVATE)
        val logs = JSONArray(prefs.getString("logs", "[]"))
        val logEntry = "早歩き,${dateFormat.format(Date())}"
        logs.put(logEntry)
        prefs.edit {
            putString("logs", logs.toString())
        }
        SharedRecordManager.updateStats(this, "brisk", logEntry)
    }

    private fun showLog() {
        val prefs = getSharedPreferences(prefName, MODE_PRIVATE)
        val logs = JSONArray(prefs.getString("logs", "[]"))
        val countsByDate = mutableMapOf<String, Int>()

        for (i in 0 until logs.length()) {
            val entry = logs.getString(i)
            try {
                val datePart = entry.split(",")[1].substring(0, 10)
                countsByDate[datePart] = countsByDate.getOrDefault(datePart, 0) + 1
            } catch (_: Exception) {}
        }

        val sb = StringBuilder()
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val cal = java.util.Calendar.getInstance()

        repeat(7) {
            val dateStr = sdf.format(cal.time)
            val countValue = countsByDate.getOrDefault(dateStr, 0)
            if (countValue > 0) {
                sb.append(getString(R.string.brisk_today_achieved_format, dateStr, countValue))
            }
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        txtLogA.text = sb.toString()
    }

    private fun cleanOldLogs() {
        val prefs = getSharedPreferences(prefName, MODE_PRIVATE)
        val logs = JSONArray(prefs.getString("logs", "[]"))
        val newLogs = JSONArray()

        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        cal.add(java.util.Calendar.DAY_OF_YEAR, -6)
        val limitTime = cal.timeInMillis

        for (i in 0 until logs.length()) {
            val entry = logs.getString(i)
            try {
                val dateStr = entry.split(",")[1]
                val date = dateFormat.parse(dateStr)
                if (date != null && date.time >= limitTime) {
                    newLogs.put(entry)
                }
            } catch (_: Exception) {}
        }
        prefs.edit {
            putString("logs", newLogs.toString())
        }
    }

    private fun cleanAll() {
        getSharedPreferences(prefName, MODE_PRIVATE).edit { clear() }
        getSharedPreferences("fast_log", MODE_PRIVATE).edit { clear() }
        fastWalkCount2 = 0
        tvCount.text = getString(R.string.brisk_count_format, 0)
        txtLogA.text = ""
        SharedRecordManager.clearAppStats(this, "brisk")
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool?.release()
        soundPool = null
    }

    private fun startClick(){
        // 💡【修正】新しくウォーキングを開始した時に歩数を「0」にリセットする
        initialStepCount = -1f
        tvLiveSteps.text = getString(R.string.brisk_steps_format, 0)

        tempoBpm = tvCurrentTempo.text.toString().toIntOrNull() ?: tempoBpm
        tvTempoLabel2.text = getString(R.string.brisk_tempo_current_format, tempoBpm)

        isFastWalk = fastMood
        val modeStr = if (isFastWalk) getString(R.string.brisk_mode_fast) else getString(R.string.brisk_mode_normal)
        tvMode.text = getString(R.string.brisk_current_mode_format, modeStr)

        restartFlag = false
        startCycle()

        btnStart.isEnabled = false
        btnMood.isEnabled = false
        btnStop.isEnabled = true
        btnRestart.isEnabled = false
        btnDelete.isEnabled = false
        tvTempoLabel2.visibility = View.VISIBLE
        tvCurrentTempo.visibility = View.INVISIBLE
        tvTempoLabel.visibility = View.GONE
        buttonShow = 1
    }

    private fun stopClick(){
        restartFlag = false
        stopCycle()

        btnStart.isEnabled = true
        btnMood.isEnabled = true
        btnStop.isEnabled = false
        btnRestart.isEnabled = true
        btnDelete.isEnabled = true
        tvTempoLabel2.visibility = View.GONE
        tvCurrentTempo.visibility = View.VISIBLE
        tvTempoLabel.visibility = View.VISIBLE
        buttonShow = 0
    }

    private fun restartClick(){
        restartFlag = true
        val modeStr = if (isFastWalk) getString(R.string.brisk_mode_fast) else getString(R.string.brisk_mode_normal)
        tvMode.text = getString(R.string.brisk_current_mode_format, modeStr)
        restart()

        btnStart.isEnabled = false
        btnMood.isEnabled = false
        btnStop.isEnabled = true
        btnRestart.isEnabled = false
        btnDelete.isEnabled = false
        tvTempoLabel2.visibility = View.VISIBLE
        tvCurrentTempo.visibility = View.INVISIBLE
        tvTempoLabel.visibility = View.GONE
        buttonShow = 1
    }
}
