package com.translation

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            val selectedText = event.text?.joinToString("") ?: ""

            if (selectedText.isNotEmpty()) {
                val intent = Intent(this, FloatingWindowService::class.java)
                intent.putExtra("text", selectedText)
                startService(intent)
            } else {
                val intent = Intent(this, FloatingWindowService::class.java)
                intent.putExtra("hide", true)
                startService(intent)
            }
        }
    }

    override fun onInterrupt() {}
}
