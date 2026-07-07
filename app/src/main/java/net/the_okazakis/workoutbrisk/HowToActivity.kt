package net.the_okazakis.workoutbrisk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.TextView

class HowToActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_howto)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_howto)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.label_howto)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val textView = findViewById<TextView>(R.id.tvHowTo)
        textView.text = getString(R.string.howto)
        .trimIndent()
    }

    // 戻る矢印タップ時の動作
    override fun onSupportNavigateUp(): Boolean {
        finish() // Activity を閉じて前の画面に戻る
        return true
    }
}