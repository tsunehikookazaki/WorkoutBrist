package net.the_okazakis.workoutbrisk


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri



class Youtube : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. IntentからURLを受け取る。もし空ならデフォルトのURLを入れる
        val videoUrl = intent.getStringExtra("yID") ?: "https://www.youtube.com/watch?v=デフォルトのID"

// 2. 受け取ったURLで「ブラウザ」を指定して起動
        val intent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())

        // 【重要】ブラウザで開くように強制するフラグ
        // これによりYouTubeアプリが立ち上がらず、小窓（PiP）も発生しなくなります
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.component = null // 特定のアプリ固定を避ける（または下記のようにパッケージ指定）

        try {
            startActivity(intent)
        } catch (_: Exception) {
            // 万が一、ブラウザが見つからない場合のフォールバック
            val backupIntent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
            startActivity(backupIntent)
        }




        /**
        // 2. 受け取ったURLでYouTubeを起動
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
        startActivity(intent)

         **/

        // 3. このアクティビティ自体は終了させる
        finish()
    }
}