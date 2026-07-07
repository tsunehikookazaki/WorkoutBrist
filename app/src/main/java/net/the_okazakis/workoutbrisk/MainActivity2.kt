package net.the_okazakis.workoutbrisk

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.edit

/**
 * 選択された各種目の目標セット数や反復回数（reps）を設定・保存するための設定画面Activity。
 * データベース（workouttimes）から現在の設定を読み込み、ユーザーの入力に応じて更新します。
 */
class MainActivity2 : AppCompatActivity() {

    /**
     * 選択されたトレーニングの主キーIDを表すプロパティ。
     */
    private var workoutId = -1


    /**
     * 画面生成時に呼ばれ、データベースから保存されている回数を読み込んで表示します。
     * また、一部の種目に関しては「1セットあたりの回数（reps）」入力を非表示にするなど、画面UIの動的調整も行います。
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // --- ① すべてのViewを取得して一括宣言（ここで1回だけ宣言！） ---
        val tvWorkoutName: TextView = findViewById(R.id.tvWorkoutName)
        val etTimes = findViewById<EditText>(R.id.etTimes)
        val etReps = findViewById<EditText>(R.id.etReps)
        val tvRepsLabel = findViewById<TextView>(R.id.textViewRepsLabel)
        val btnSave: Button = findViewById(R.id.btnSave)

        //プリファレンス
        val pref = getSharedPreferences("WorkoutSettings", MODE_PRIVATE)

        // プラスマイナスボタンの取得
        val btnTimesMinus = findViewById<Button>(R.id.btnTimesMinus)
        val btnTimesPlus = findViewById<Button>(R.id.btnTimesPlus)
        val btnRepsMinus = findViewById<Button>(R.id.btnRepsMinus)
        val btnRepsPlus = findViewById<Button>(R.id.btnRepsPlus)

        // --- ② インテントデータの処理とトレーニング名表示 ---
        val workmenu: String? = intent.getStringExtra("TEXT_KEY4")
        tvWorkoutName.text = workmenu

        val dbid: Int = intent.getIntExtra("TEXT_KEY5", 0)
        workoutId = dbid

        // 👇【ここから追記】送られてきた「標準の文章」を取得して表示する
        val stdText = intent.getStringExtra("STD_TEXT") ?: getString(R.string.msg_not_set)
        val tvStandardLabel = findViewById<TextView>(R.id.tvStandardLabel)

        // 画面に「【標準】 左右10秒ずつ 3回（1セット）」のように表示
        tvStandardLabel.text = stdText

        // 👇【追加】送られてきた制限値（上限）とデフォルト値を取得する
        val maxLimit = intent.getIntExtra("MAX_LIMIT", 99)
        val maxRepsLimit = intent.getIntExtra("MAX_REPS_LIMIT", 99)
        val defaultTimesFromActivity = intent.getIntExtra("DEFAULT_TIMES", 2)
        val defaultRepsFromActivity = intent.getIntExtra("DEFAULT_REPS", 15)

        // --- ③ データベースから初期値を読み込んでEditTextにセット ---
        // 修正：固定値「2」や「15」ではなく、Activityから渡された初期値を使う
        var times0 = pref.getInt("times_$workoutId", defaultTimesFromActivity).toString()
        var reps0 = pref.getInt("reps_$workoutId", defaultRepsFromActivity).toString()

        val timesInt = times0.toIntOrNull() ?: 0
        if (timesInt > maxLimit) {
            times0 = maxLimit.toString()
            showLimitAlert(maxLimit)
        }

        val repsInt = reps0.toIntOrNull() ?: 0
        if (repsInt > maxRepsLimit) {
            reps0 = maxRepsLimit.toString()
            showLimitAlert(maxRepsLimit)
        }

        etTimes.setText(times0)
        etReps.setText(reps0)

        // --- ④ 特定の種目（ID 12以外）でRepsエリアを非表示にする動的制御 ---
        val dynamicIds = listOf(12,18)

        if (!dynamicIds.contains(workoutId)) {
            // Repsのテキストと入力欄を非表示
            etReps.visibility = View.GONE
            tvRepsLabel.visibility = View.GONE

            // Reps用のプラスマイナスボタンも一緒に非表示にする
            btnRepsMinus.visibility = View.GONE
            btnRepsPlus.visibility = View.GONE

            // 保存ボタンの位置を調整
            val params = btnSave.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            params.topToBottom = R.id.layoutTimes
            btnSave.layoutParams = params
        }

        // --- ⑤ 保存ボタン（btnSave）がタップされた時の処理 ---
        btnSave.setOnClickListener {
            // 【修正】ここで再宣言されていた val etTimes と val etReps の2行を削除しました

            // 入力された回数を取得。
            var times = etTimes.text.toString()
            if (times == "") { times = "2" }

            var reps = etReps.text.toString()
            if (reps == "") { reps = "15" }

            pref.edit {
                putInt("times_$workoutId", times.toInt())
                putInt("reps_$workoutId", reps.toInt())
            }

            btnSave.isEnabled = false
            finish()
        }

        // --- ⑥ プラスマイナスボタンのクリックリスナーを設定 ---
        // 【修正】ここで再宣言されていた val btnTimesMinus や val etTimes などの宣言部をすべて削除しました

        // 回数（Times）用のプラスマイナス制御
        btnTimesMinus.setOnClickListener {
            var current = etTimes.text.toString().toIntOrNull() ?: 0
            if (current > 1) {
                current--
                etTimes.setText(current.toString())
            }
        }

        btnTimesPlus.setOnClickListener {
            var current = etTimes.text.toString().toIntOrNull() ?: 0
            // 👇【修正】固定の 99 ではなく、送られてきた maxLimit を上限にする
            if (current < maxLimit) {
                current++
                etTimes.setText(current.toString())
            } else {
                // 👇【追加】すでに上限値なのにさらに「＋」を押した場合にアラートを出す
                showLimitAlert(maxLimit)

            }
        }

        // セット数/秒数（Reps）用のプラスマイナス制御
        btnRepsMinus.setOnClickListener {
            var current = etReps.text.toString().toIntOrNull() ?: 0
            if (current > 1) {
                current--
                etReps.setText(current.toString())
            }
        }

        // セット数/秒数（Reps）用のプラス制御（👇ここを修正）
        btnRepsPlus.setOnClickListener {
            var current = etReps.text.toString().toIntOrNull() ?: 0

            // 👇 送られてきた maxRepsLimit を上限として判定する
            if (current < maxRepsLimit) {
                current++
                etReps.setText(current.toString())
            } else {
                // 👇 上限に達しているのに「＋」が押されたらアラートを出す
                showLimitAlert(maxRepsLimit)
            }
        }
    }

    /**
     * 上限値を超えた際のアラートダイアログを表示する関数
     */
    private fun showLimitAlert(limit: Int) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("設定値のエラー")
            .setMessage("${limit}以下にしてください")
            .setPositiveButton("OK", null) // 閉じるだけのボタン
            .show()
    }

}