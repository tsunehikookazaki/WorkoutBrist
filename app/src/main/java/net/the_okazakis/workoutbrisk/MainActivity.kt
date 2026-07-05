package net.the_okazakis.workoutbrisk

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//private var _player: MediaPlayer? = null

/**
 * WorkoutStretch550アプリのトップ（メニュー）画面を管理するActivity。
 * 音量調整、各種ストレッチ画面への遷移、およびバックグラウンドでの記録整理・同期を行います。
 */
class MainActivity : AppCompatActivity() {

    //Ver3.2  ボールのヒップアダクション追加
    //Ver3.1　BGMを流すようにした
    //ver2.3　各運動に説明を付けた
    //Ver2.0 鎌田式スクワット追加

    var countVolume = 1.0f  //ボリューム初期値
    var listnum = 0  //メニュー番号初期値


    /**
     * Activityが初めて作成されたときに呼び出されます。
     * UIの初期化（ボタン、音量調整用SeekBar、メニュー用ListViewの設定）を行います。
     *
     * @param savedInstanceState 以前の保存状態がある場合はそのデータ
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 13以降の通知許可リクエスト
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // ボタンをIDで取得
        val buttonLog = findViewById<Button>(R.id.buttonLog)

        // ← この中に書く
        buttonLog.setOnClickListener {
            val intent = Intent(this, LogActivity::class.java)
            startActivity(intent)
        }



        //画面をオンのままにする
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val textView = findViewById<TextView>(R.id.textView)   //ボリューム表示ようのテキストビュー
        val seekBar = findViewById<SeekBar>(R.id.seek_bar)  //シークバー
        seekBar.max = 5  // シークバーの範囲　0~5　6段階

        var volume0 = "100%"  //ボリューム初期値
        var prog0: Int //ボリュームの6段階の値　0~5
        textView.text = "音楽を聞きながらワークアウトする時\nに、ワークアウトのボリュームを調整\n" +
                "volume $volume0"  //ボリュームの値を表示


        // イベントリスナーの追加　　シークバーの値を変える
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            // 値が変更された時に呼ばれる
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                prog0 = progress  //変更されたらボリューム値を変える
                when (prog0) {
                    5 -> {
                        countVolume = 1.0f;volume0 = "100%"
                    }
                    4 -> {
                        countVolume = 0.8f;volume0 = "80%"
                    }
                    3 -> {
                        countVolume = 0.6f;volume0 = "60%"
                    }
                    2 -> {
                        countVolume = 0.40f;volume0 = "40%"
                    }
                    1 -> {
                        countVolume = 0.10f;volume0 = "10"
                    }
                    0 -> {
                        countVolume = 0.05f;volume0 = "5%"
                    }
                }
                textView.text = "音楽を聞きながらワークアウトする時\nに、ワークアウトのボリュームを調整\n" +
                        "volume $volume0"  //ボリュームの値を表示
            }

            // つまみがタッチされた時に呼ばれる
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            // つまみが離された時に呼ばれる
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        val lvMenu = findViewById<ListView>(R.id.lvMenu)   // ListViewオブジェクトを取得。
        lvMenu.onItemClickListener = ListItemClickListener()  // ListViewにリスナを設定。


    }

    /**
     * Activityが表示されるたび（起動時や別画面からの復帰時）に呼び出されます。
     * バックグラウンド（別スレッド）で古い記録の削除や、サーバーへの状態同期を実行します。
     */
    override fun onResume() {
        super.onResume()
        // 起動・復帰時にバックグラウンドで処理を実行
        val executor = java.util.concurrent.Executors.newSingleThreadExecutor()
        executor.execute {
            // 8日以上前の古い記録をクリーンアップ
            RecordManager.cleanUpOldRecords(this)

            // 今日のセット数を計算
            val records = RecordManager.getRecords(this)
            val todayStr = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.JAPAN).format(java.util.Date())
            val todaySets = records.values.sumOf { it.lines().filter { l -> l.startsWith(todayStr) }.size }

        }
    }

    /**
     * 種目一覧（ListView）のアイテムがクリックされた時の処理を定義するリスナークラス。
     */
    private inner class ListItemClickListener : AdapterView.OnItemClickListener {

        /**
         * リスト内の種目がタップされた際に呼び出され、選択された種目に対応する画面へ遷移します。
         *
         * @param parent クリックされたListView
         * @param view クリックされたアイテムのView
         * @param position クリックされたアイテムの位置（種目番号、0〜23）
         * @param id クリックされたアイテムのID
         */
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            listnum = position


            //飛び先の設定
            var intent = Intent(this@MainActivity, Ball::class.java)
            when (position) {
                0 -> {
                    intent = Intent(this@MainActivity, Hihukukinn::class.java)
                }
                1 -> {
                    intent = Intent(this@MainActivity, Tsumasakidachi::class.java)
                }
                2 -> {
                    intent = Intent(this@MainActivity, AshiuranobashiTachi::class.java)
                }
                3 -> {
                    intent = Intent(this@MainActivity, Ball::class.java)
                }
                4 -> {
                    intent = Intent(this@MainActivity, Ashijanken::class.java)
                }
                5 -> {
                    intent = Intent(this@MainActivity, AshikubiKushin::class.java)
                }
                6 -> {
                    intent = Intent(this@MainActivity, AshiuranobashiNete::class.java)
                }
                7 -> {
                    intent = Intent(this@MainActivity, Hizakakae::class.java)
                }
                8 -> {
                    intent = Intent(this@MainActivity, Hukkin::class.java)
                }
                9 -> {
                    intent = Intent(this@MainActivity, Legraise::class.java)
                }
                10 -> {
                    intent = Intent(this@MainActivity, HipLift::class.java)
                }
                11 -> {
                    intent = Intent(this@MainActivity, NaiGaiten::class.java)
                }
                12 -> {
                    intent = Intent(this@MainActivity, BirdDog::class.java)
                }
                13 -> {
                    intent = Intent(this@MainActivity, HipabdactionBelt::class.java)
                }
                14 -> {
                    intent = Intent(this@MainActivity, HipaddactionBall::class.java)
                }
                15 -> {
                    intent = Intent(this@MainActivity, Sukebo::class.java)
                }
                16 -> {
                    intent = Intent(this@MainActivity, Ashiage::class.java)
                }
                17 -> {
                    intent = Intent(this@MainActivity, ChairSquat::class.java)
                }
                18 -> {
                    intent = Intent(this@MainActivity, Forwardlunge::class.java)
                }
                19 -> {
                    intent = Intent(this@MainActivity, KamatasquatTable::class.java)
                }
                20 -> {
                    intent = Intent(this@MainActivity, Kamatasquatwide::class.java)
                }
                21 -> {
                    intent = Intent(this@MainActivity, Kamatakakato::class.java)
                }
                22 -> {
                    intent = Intent(this@MainActivity, Momomae::class.java)
                }
                23 -> {
                    intent = Intent(this@MainActivity, Ashiuranobashi::class.java)
                }
            }
            //それぞれのページに飛ぶ
            intent.putExtra("TEXT_KEY", countVolume) //ボリュームの値を転送
            intent.putExtra("TEXT_KEY2", listnum) //運動のリスト番号を転送
            //次の画面を起動
            startActivity(intent)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_stop_app -> {
                finishAndRemoveTask()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
