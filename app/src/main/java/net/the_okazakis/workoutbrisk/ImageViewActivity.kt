package net.the_okazakis.workoutbrisk

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.graphics.createBitmap

class ImageViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_imageview)
        } catch (e: Exception) {
            Log.e("ImageViewActivity", "XMLのインフレートに失敗しました。レイアウト内の記述を確認してください。", e)
            Toast.makeText(this, "画面の表示に失敗しました", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_image)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // 戻る矢印

        // 落ちるのを防ぐため安全にタイトルを取得
        supportActionBar?.title = try {
            getString(R.string.title_tempo_guide)
        } catch (_: Exception) {
            "テンポ（歩数）の目安"
        }

        // XML の ZoomableImageView を取得
        val zoomableImageView = findViewById<ZoomableImageView>(R.id.zoomable_image_view)
        if (zoomableImageView == null) {
            Log.e("ImageViewActivity", "XMLからZoomableImageViewを取得できませんでした。")
            finish()
            return
        }

        // 画像リソースIDを取得
        val imageResId = intent.getIntExtra("image_res_id", R.raw.kyoudo)

        // PDFか通常の画像かを判断してセット
        if (isPdfResource(imageResId)) {
            displayPdf(imageResId, zoomableImageView)
        } else {
            try {
                zoomableImageView.setImageResource(imageResId)
            } catch (e: Exception) {
                Log.e("ImageViewActivity", "画像のセットに失敗しました", e)
                zoomableImageView.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }

    private fun isPdfResource(resId: Int): Boolean {
        return try {
            val resName = resources.getResourceEntryName(resId)
            val resType = resources.getResourceTypeName(resId)
            resName.contains("kyoudo") || resType == "raw"
        } catch (_: Exception) {
            false
        }
    }

    private fun displayPdf(resourceId: Int, zoomableImageView: ZoomableImageView) {
        var fd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            val tempFile = File(cacheDir, "temp_guide.pdf")

            // rawリソースの存在チェックを兼ねてコピー
            resources.openRawResource(resourceId).use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            fd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(fd)

            if (renderer.pageCount > 0) {
                val page = renderer.openPage(0)

                // メモリ不足（OutOfMemory）を回避するため、解像度倍率を安全な1.5倍に調整
                val density = resources.displayMetrics.density
                val width = (page.width * density * 1.5f).toInt()
                val height = (page.height * density * 1.5f).toInt()

                // 確実にBitmapを生成（Configを指定して安全性を高める）
                val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                zoomableImageView.setImageBitmap(bitmap)
                page.close()
            } else {
                throw IOException("PDFのページ数が0です")
            }
        } catch (e: Throwable) { // Exceptionだけでなく致命的なErrorもキャッチ
            Log.e("ImageViewActivity", "PDFレンダリングに失敗しました。フォールバックします。", e)
            // 失敗した場合は絶対に落ちないよう、標準の背景をセット
            zoomableImageView.setImageResource(R.drawable.ic_launcher_background)
        } finally {
            try {
                renderer?.close()
                fd?.close()
            } catch (_: Exception) {}
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}