package net.the_okazakis.workoutstrech

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val tableLayout = findViewById<TableLayout>(R.id.tableLog)
        val btnClear = findViewById<Button>(R.id.buttonClear)
        val btnBack = findViewById<Button>(R.id.buttonBack)

        // 戻るボタン
        btnBack.setOnClickListener { finish() }

        // 表示
        showWeeklyTable(tableLayout)

        // すべて削除ボタン → 確認ダイアログ
        btnClear.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage("すべての記録を削除してもよろしいですか？")
                .setPositiveButton("はい") { _, _ ->
                    // アプリ内のローカル記録を削除
                    RecordManager.clearRecords(this)

                    // 💡【追加】LetsDoItへ「未実施」を通知し、stats.jsonから削除
                    SharedRecordManager.clearAppStats(this, "strech")

                    // テーブル表示の更新
                    tableLayout.removeAllViews()
                    showWeeklyTable(tableLayout)
                }
                .setNegativeButton("キャンセル", null)
                .show()
        }
        // 【ここに追加】古い記録の整理と削除を実行
        RecordManager.cleanUpOldRecords(this)
    }

    override fun onResume() {
        super.onResume()
        val tableLayout = findViewById<TableLayout>(R.id.tableLog)
        showWeeklyTable(tableLayout)
    }

    private fun showWeeklyTable(tableLayout: TableLayout) {
        tableLayout.removeAllViews()

        // 🔹日付リストの作成（今日から7日前まで）
        val calendar = Calendar.getInstance()
        val dateList = mutableListOf<String>()
        val dateDisplayList = mutableListOf<String>()
        val sdfFull = java.text.SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val sdfShort = java.text.SimpleDateFormat("M/d", Locale.getDefault())

        for (i in 0 until 7) {
            val d = calendar.time
            dateList.add(sdfFull.format(d))
            dateDisplayList.add(sdfShort.format(d))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        // 新しい順（左が最新、右が過去）にするため、reverse()を削除
        // dateList.reverse()
        // dateDisplayList.reverse()

        // 🔹ヘッダー行の作成
        val headerRow = TableRow(this)


        // 「種目」列（可変幅にするため0を指定）
        headerRow.addView(createTextView("種目", true, 0))
        // 各日付の列
        dateDisplayList.forEach { headerRow.addView(createTextView(it, true, 45)) }
        tableLayout.addView(headerRow)

        // 🔹データ行の作成
        val records = RecordManager.getRecordsSortedByActivity(this)
        for (record in records) {
            val row = TableRow(this)

            // 種目名
            row.addView(createTextView(record.activityName, false, 0))

            // 各日付の回数を集計
            val dateCounts = mutableMapOf<String, Int>()
            record.dates.forEach { line ->
                val d = if (line.length >= 10) line.take(10) else ""
                dateCounts[d] = dateCounts.getOrDefault(d, 0) + 1
            }
// 修正対象: dateList.forEach { date -> のループ内
            dateList.forEach { date ->
                val count = dateCounts[date] ?: 0
                val text = if (count > 0) "${count}" else "-"
                row.addView(createTextView(text, false, 45))
            }

            tableLayout.addView(row)
        }
    }

    private fun createTextView(text: String, isHeader: Boolean, widthDp: Int): TextView {
        return TextView(this).apply {
            this.text = text
            this.gravity = if (isHeader || (widthDp > 0 && widthDp < 100)) Gravity.CENTER else Gravity.CENTER_VERTICAL
            this.setPadding(12, 8, 12, 8)
            this.textSize = 10f
            if (widthDp > 0) {
                this.minWidth = (widthDp * resources.displayMetrics.density).toInt()
            }
            // 🔹 各セルに枠線を適用（縦横の線）
            this.setBackgroundResource(R.drawable.table_cell_border)
            if (isHeader) {
                this.setTypeface(null, android.graphics.Typeface.BOLD)
                this.setTextColor(android.graphics.Color.BLACK)
            }
        }
    }
}
