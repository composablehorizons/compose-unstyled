package com.composeunstyled.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.composeunstyled.Button
import com.composeunstyled.ProvideContentColor
import com.composeunstyled.Text
import com.composeunstyled.theme.Theme

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
        )

        setContent {
            AndroidTheme {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(Theme[colors][background])
                        .safeContentPadding()
                ) {
                    ProvideContentColor(Theme[colors][onBackgroundColor]) {
                        Text("Header", style = Theme[textStyles][header])
                        Column(Modifier.padding(vertical = 40.dp)) {
                            Text("Dp: ${Theme[dimensions][demoDimension]}")
                            Text("Sp: ${Theme[textSizes][demoTextSize]}")

                            Spacer(Modifier.height(16.dp))

                            Text("String: ${Theme[values][demoString]}")
                            Text("Int: ${Theme[numbers][demoInt]}")
                            Text("Float: ${Theme[floats][demoFloat]}")
                            Text("Is Goat: ${Theme[flags][isGoat]}")
                            Text("Boolean: ${Theme[flags][demoBoolean]}")

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = { /* */ },
                                backgroundColor = Theme[colors][primary],
                                contentColor = Theme[colors][onPrimary],
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Button")
                            }
                        }
                    }
                }
            }
        }
    }
}