package net.the_okazakis.workoutbrisk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val btnGoBrisk = findViewById<Button>(R.id.btnGoBrisk)
        val btnGoWorkout = findViewById<Button>(R.id.btnGoWorkout)
        val btnGoLog = findViewById<Button>(R.id.btnGoLog)

        btnGoBrisk.setOnClickListener {
            try {
                val intent = Intent(this@LauncherActivity, BriskMainActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                val errorMsg = getString(R.string.error_launch_brisk, e.message)
                android.util.Log.e("LauncherActivity", errorMsg, e)
                android.widget.Toast.makeText(this, errorMsg, android.widget.Toast.LENGTH_LONG).show()
            }
        }
        
        btnGoWorkout.setOnClickListener {
            try {
                val intent = Intent(this@LauncherActivity, MainActivity::class.java)
                startActivity(intent)
            } catch (_: Exception) {
                android.widget.Toast.makeText(this, getString(R.string.error_launch_workout), android.widget.Toast.LENGTH_LONG).show()
            }
        }

        btnGoLog.setOnClickListener {
            val intent = Intent(this, LogActivity::class.java)
            startActivity(intent)
        }
    }
}
