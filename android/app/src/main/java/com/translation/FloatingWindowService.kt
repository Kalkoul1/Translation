
package com.translation

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private var bubbleView: View? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra("hide", false) == true) {
            removeBubble()
            return START_NOT_STICKY
        }

        val text = intent?.getStringExtra("text") ?: return START_NOT_STICKY
        showBubble(text)

        return START_STICKY
    }

    private fun showBubble(text: String) {
        removeBubble()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        bubbleView = LayoutInflater.from(this).inflate(R.layout.bubble_layout, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 50
        params.y = 200

        val textView = bubbleView!!.findViewById<TextView>(R.id.bubbleText)

        CoroutineScope(Dispatchers.IO).launch {
            val translated = translate(text)
            withContext(Dispatchers.Main) {
                textView.text = translated
            }
        }

        windowManager.addView(bubbleView, params)
    }

    private fun removeBubble() {
        if (bubbleView != null) {
            windowManager.removeView(bubbleView)
            bubbleView = null
        }
    }

    private fun translate(text: String): String {
        return try {
            val response = URL("https://libretranslate.com/translate?q=$text&source=auto&target=ar").readText()
            val json = JSONObject(response)
            json.getString("translatedText")
        } catch (e: Exception) {
            "خطأ في الترجمة"
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
