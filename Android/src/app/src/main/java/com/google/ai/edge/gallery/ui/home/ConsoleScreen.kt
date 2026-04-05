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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.ai.edge.gallery.automation.Orchestrator

@Composable
fun ConsoleScreen(orchestrator: Orchestrator? = null) {
  var input by remember { mutableStateOf("") }
  val messages = remember { mutableStateListOf<String>() }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text(text = "AutoPilot Console", style = MaterialTheme.typography.headlineMedium)

    LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 16.dp)) {
      items(messages) { message ->
        Text(text = message, modifier = Modifier.padding(vertical = 4.dp))
      }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
      TextField(
        value = input,
        onValueChange = { input = it },
        modifier = Modifier.weight(1f),
        placeholder = { Text("Enter goal...") },
      )
      Button(
        onClick = {
          if (input.isNotBlank()) {
            messages.add("User: $input")
            messages.add("Agent: Planning to achieve: $input")
            orchestrator?.submitGoal(input)
            input = ""
          }
        },
        modifier = Modifier.padding(start = 8.dp),
      ) {
        Text("Send")
      }
    }
  }
}
