package net.the_okazakis.workoutbrisk

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

// MainActivityに戻ってサイクルを再開するための結果コード
private const val RESULT_CONTINUE = 1

class CongratulationsActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var soundIdCongratulations = 0
    private var hasCheerPlayed = false // 効果音の重複再生防止フラグ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratulations)

        // 画面を常にオンに保つ
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // MainActivityから回数を取得
        val count = intent.getIntExtra("COUNT", 5)

        // UI要素の取得
        val tvCongratulations = findViewById<TextView>(R.id.tvCongratulations)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        // val ivConfetti = findViewById<ImageView>(R.id.ivConfetti)

        // メッセージを設定
        tvCongratulations.text = "早歩き${count}回達成おめでとうございます！\n素晴らしい！"

        // --- 🎶 音声設定 ---

        // BGM (fan.mp3) の準備と再生
        mediaPlayer = MediaPlayer.create(this, R.raw.fan).apply {
            isLooping = true
            setVolume(0.8f, 0.8f)
            start()
        }

        // 効果音 (goodjob.mp3) の準備と1回再生
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attrs).build()
        soundIdCongratulations = soundPool!!.load(this, R.raw.goodjob, 1)

        soundPool?.setOnLoadCompleteListener { soundPool, sampleId, status ->
            if (status == 0 && sampleId == soundIdCongratulations) {
                if (!hasCheerPlayed) {
                    soundPool.play(soundIdCongratulations, 1.0f, 1.0f, 0, 0, 1f)
                    hasCheerPlayed = true
                }
            }
        }

        // --- 🏃 ボタンリスナー ---

        btnContinue.setOnClickListener {
            // 結果を RESULT_CONTINUE (再開) に設定してActivityを終了
            setResult(RESULT_CONTINUE)
            finish()
        }

        /*
        // --- ✨ 紙吹雪アニメーション ---
        ...
        */

        // --- 🔙 バック操作制御 ---

        // OnBackPressedCallback を使ってバックジェスチャー/キーをインターセプト
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(this@CongratulationsActivity, "ボタンを押してメイン画面に戻ってください", Toast.LENGTH_SHORT).show()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    // --- 🧹 ライフサイクルとリソース解放 ---

    override fun onDestroy() {
        super.onDestroy()
        // 音声リソースの解放
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool?.release()
        soundPool = null
        // 画面オン維持のフラグを解除
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}