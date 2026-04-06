/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.edge.gallery.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.ai.edge.gallery.R

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
  var step by remember { mutableIntStateOf(1) }
  val context = LocalContext.current

  val permissionsLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
      onComplete()
    }

  Column(
    modifier = Modifier.fillMaxSize().padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    when (step) {
      1 -> {
        Text(
          text = stringResource(R.string.tos_dialog_title_app),
          style = MaterialTheme.typography.headlineMedium,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Your personal AI agent that performs tasks for you, entirely on your device.",
          textAlign = TextAlign.Center,
        )
      }
      2 -> {
        Text(
          text = "Private and Secure",
          style = MaterialTheme.typography.headlineMedium,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "AutoPilot works offline. Your data stays private and never leaves your phone.",
          textAlign = TextAlign.Center,
        )
      }
      3 -> {
        Text(
          text = "Ready to start?",
          style = MaterialTheme.typography.headlineMedium,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "To work effectively, AutoPilot needs Accessibility and some basic permissions to interact with your device.",
          textAlign = TextAlign.Center,
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
      onClick = {
        if (step < 3) {
          step++
        } else {
          // 3rd click on Step 3 triggers permissions.
          val permissions = mutableListOf<String>()
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
          } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
          }
          permissionsLauncher.launch(permissions.toTypedArray())

          // Request Manage External Storage on Android 11+
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
              val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
              intent.data = Uri.parse("package:${context.packageName}")
              context.startActivity(intent)
            }
          }

          // Also guide to accessibility settings as it's crucial for AutoPilot
          val accessibilityIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
          context.startActivity(accessibilityIntent)
        }
      }
    ) {
      Text("Continue")
    }
  }
}
