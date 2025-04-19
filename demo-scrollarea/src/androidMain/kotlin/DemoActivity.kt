package com.composeunstyled.demo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
        )

        setContent {
            ScrollAreaDemo()
        }
        // showViewBasedScrollView()
    }

    private fun showViewBasedScrollView() {
        // kept for quick tests of view-based scroll behavior
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        for (i in 1..100) {
            val textView = TextView(this).apply {
                text = "Item #$i"
                textSize = 20f
                setPadding(16, 16, 16, 16)
            }
            linearLayout.addView(textView)
        }

        scrollView.addView(linearLayout)

        setContentView(scrollView)
    }
}
