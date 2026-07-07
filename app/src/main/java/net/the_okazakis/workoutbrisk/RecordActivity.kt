package net.the_okazakis.workoutbrisk

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class RecordActivity : AppCompatActivity() {

    private lateinit var tvRecord: TextView
    private val prefFile = "fast_log"
    private val dateFormatString = "yyyy/MM/dd HH:mm"
    private val sdf = SimpleDateFormat(dateFormatString, Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_record)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // 戻る矢印
        supportActionBar?.title = getString(R.string.title_brisk_detail)


        // View取得
        tvRecord = findViewById(R.id.tvRecord)

        // 古いログ削除
        cleanOldLogs()

        // 表示更新
        showLog()
    }

    // 戻る矢印処理
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /** 表示更新 */
    private fun showLog() {
        val prefs = getSharedPreferences(prefFile, MODE_PRIVATE)
        val jsonArray = JSONArray(prefs.getString("logs", "[]"))

        if (jsonArray.length() == 0) {
            tvRecord.text = getString(R.string.msg_no_records)
            return
        }

        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val log = jsonArray.getString(i)
            val parts = log.split(",")
            if (parts.size >= 2) {
                list.add(parts[1])  // 日付文字列
            }
        }

        // 日付順で表示
        tvRecord.text = list.joinToString("\n")
    }

    /** 古いログ削除（8日以上前） */
    private fun cleanOldLogs() {
        val prefs = getSharedPreferences(prefFile, MODE_PRIVATE)
        val jsonArray = JSONArray(prefs.getString("logs", "[]"))
        val now = System.currentTimeMillis()
        val sevenDaysMillis = 7L * 24 * 60 * 60 * 1000

        val newArray = JSONArray()
        for (i in 0 until jsonArray.length()) {
            val log = jsonArray.getString(i)
            val parts = log.split(",")
            if (parts.size >= 2) {
                val timestamp = try {
                    sdf.parse(parts[1])?.time ?: now
                } catch (_: Exception) { now }
                if ((now - timestamp) <= sevenDaysMillis) {
                    newArray.put(log)
                }
            }
        }

        prefs.edit {
            putString("logs", newArray.toString())
        }
    }
}
