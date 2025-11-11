package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.composeunstyled.Button
import com.composeunstyled.Icon
import com.composeunstyled.Text
import com.composeunstyled.TextField
import com.composeunstyled.TextInput


@Composable
fun TextFieldDemo() {
    var email = rememberTextFieldState()
    var password = rememberTextFieldState()
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF9D50BB), Color(0xFF6E48AA))))
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    state = email,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                ) {
                    Text("Email", modifier = Modifier.padding(bottom = 8.dp), fontWeight = FontWeight.SemiBold)
                    TextInput(
                        Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(8.dp))
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        placeholder = { Text("email@example.com", color = Color.Black.copy(0.6f)) },
                    )
                }

                TextField(
                    state = password,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
                ) {
                    Text("Password", modifier = Modifier.padding(bottom = 8.dp), fontWeight = FontWeight.SemiBold)
                    TextInput(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(8.dp))
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(vertical = 4.dp)
                            .padding(start = 16.dp, end = 4.dp),
                        placeholder = { Text("8-12 characters", color = Color.Black.copy(0.6f)) },
                        trailing = {
                            Button(
                                onClick = { showPassword = !showPassword },
                                backgroundColor = Color.Transparent,
                                contentPadding = PaddingValues(4.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (showPassword) Lucide.EyeOff else Lucide.Eye,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Color(0xFF757575)
                                )
                            }
                        }
                    )
                }

                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF8E44AD),
                    contentColor = Color.White,
                    contentPadding = PaddingValues(12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
