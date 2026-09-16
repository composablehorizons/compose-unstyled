/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.composeunstyled.demo

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composeunstyled.LocalContentColor
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.theme.Theme

@Composable
fun Demo(startDestination: String = "home") {
  DemoTheme {
    Box(Modifier.fillMaxSize().background(Theme[demoColors][demoBackground])) {
      DemoSelection(startDestination)
    }
  }
}

@Composable
private fun DemoSelection(startDestination: String) {
  val navController = rememberNavController()
  val initialDestination = generatedDemos
    .firstOrNull { it.id == startDestination }
    ?.id
    ?: "home"

  NavHost(
    navController = navController,
    startDestination = initialDestination,
    enterTransition = {
      EnterTransition.None
    },
    exitTransition = {
      ExitTransition.None
    },
    popEnterTransition = {
      EnterTransition.None
    },
    popExitTransition = {
      ExitTransition.None
    },
  ) {
    composable("home") {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
      ) {
        Column(
          Modifier
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(8.dp)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          generatedDemos.forEach { demo ->
            DemoListButton(
              onClick = { navController.navigate(demo.id) },
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(demo.name)
            }
          }
        }
      }
    }

    generatedDemos.forEach { component ->
      composable(component.id) {
        val launchedFromDemoList = initialDestination == "home"
        Column(Modifier.fillMaxSize()) {
          if (launchedFromDemoList) {
            AppBar(
              onUpClick = { navController.navigateUp() },
              title = component.name,
            )
          }
          Box(Modifier.weight(1f)) {
            component.demo()
          }
        }
      }
    }
  }
}

@Composable
private fun AppBar(onUpClick: () -> Unit, title: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Theme[demoColors][demoBackground])
      .padding(WindowInsets.statusBars.asPaddingValues())
      .padding(4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    UnstyledButton(
      onClick = onUpClick,
      modifier = Modifier
        .clip(CircleShape),
      indication = LocalIndication.current,
    ) {
      Box(Modifier.padding(12.dp)) {
        UnstyledIcon(
          imageVector = Lucide.ArrowLeft,
          contentDescription = "Go back",
          tint = LocalContentColor.current,
        )
      }
    }
    Spacer(Modifier.width(8.dp))
    Text(title)
  }
}

@Composable
private fun DemoListButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  UnstyledButton(
    onClick = onClick,
    modifier = modifier
      .sizeIn(minWidth = 40.dp, minHeight = 48.dp)
      .clip(RoundedCornerShape(8.dp)),
    indication = LocalIndication.current,
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      contentAlignment = Alignment.CenterStart,
    ) {
      content()
    }
  }
}
