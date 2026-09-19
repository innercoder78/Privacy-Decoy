package com.privacydecoy.app

import android.app.Activity
import android.os.Bundle
import android.view.WindowInsets

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val content = findViewById<android.view.View>(R.id.content)
        content.setOnApplyWindowInsetsListener { view, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout(),
            )
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            windowInsets
        }
        content.requestApplyInsets()
    }
}
