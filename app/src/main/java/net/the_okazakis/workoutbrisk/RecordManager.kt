package net.the_okazakis.workoutbrisk

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object RecordManager {

    private const val PREF_NAME = "record_prefs"
    private const val KEY_PREFIX = "record_"

    // 公開して外部で参照可能
    val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.JAPAN)

    data class Record(
        val activityNumber: Int,
        val activityName: String,
        val dates: List<String>
    )

    // 記録保存
    fun saveRecord(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = KEY_PREFIX + name
        val oldData = prefs.getString(key, "") ?: ""
        val now = dateFormat.format(Date())
        val newData = if (oldData.isEmpty()) now else "$oldData\n$now"
        prefs.edit().putString(key, newData).apply()

        // 共有ファイルも更新
        val records = getRecords(context)
        val todayStr = now.take(10) // "yyyy.MM.dd"
        val todaySets = records.values.sumOf { it.lines().filter { l -> l.startsWith(todayStr) }.size }


    }

    // 全記録取得
    fun getRecords(context: Context): Map<String, String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.all
            .filterKeys { it.startsWith(KEY_PREFIX) }
            .mapKeys { it.key.removePrefix(KEY_PREFIX) }
            .mapValues { it.value.toString() }
    }

    // 全削除
    fun clearRecords(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Web同期は停止しました
    }

    // 種目番号順に Record リスト取得
    fun getRecordsSortedByActivity(context: Context): List<Record> {
        return getRecords(context).entries
            .filter { (name, _) ->
                // 01〜24のような種目番号で始まるもの以外（strechなど）を除外
                name.length >= 2 && name.take(2).all { it.isDigit() }
            }
            .map { (name, text) ->
                val number = name.take(2).toInt()
                val activityName = name.drop(2)
                val dates = text.lines().filter { it.isNotBlank() }
                Record(
                    activityNumber = number,
                    activityName = activityName,
                    dates = dates
                )
            }.sortedBy { it.activityNumber }
    }
    // 📅 【新規追加】8日以上前の記録を削除する
    fun cleanUpOldRecords(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // 基準日時の設定: 7日前（丸1週間分残す）
        val sevenDaysAgoBoundary = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // 現在のすべての記録を取得
        val allRecords = getRecords(context)

        var hasChanges = false

        allRecords.forEach { (activityKey, datesString) ->
            val fullKey = KEY_PREFIX + activityKey

            // 日付文字列を解析し、7日以内のものだけをフィルタリング
            val recentDates = datesString.lines()
                .filter { it.isNotBlank() }
                .filter { dateString ->
                    runCatching { dateFormat.parse(dateString) }.getOrNull()?.let { date ->
                        // 7日前の境界日時より後(新しい)であるかチェック
                        date.after(sevenDaysAgoBoundary)
                    } ?: false // パース失敗時は削除対象
                }

            if (recentDates.size < datesString.lines().size) {
                // 記録が削除された場合 (変更あり)
                hasChanges = true
                if (recentDates.isEmpty()) {
                    // 7日以内の記録が一つも残らなければ、キー自体を削除
                    editor.remove(fullKey)
                } else {
                    // 7日以内の記録が残っていれば、そのリストで更新
                    editor.putString(fullKey, recentDates.joinToString("\n"))
                }
            }
        }

        // 変更があった場合のみ保存を適用
        if (hasChanges) {
            editor.apply()
        }

        // 【ついでに修正】誤って作成された可能性のあるキーを削除
        if (prefs.contains("record_strech")) {
            prefs.edit().remove("record_strech").apply()
        }
    }

}
