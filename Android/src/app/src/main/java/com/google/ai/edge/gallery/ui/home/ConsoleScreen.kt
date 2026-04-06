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
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.ai.edge.gallery.automation.Orchestrator

@Composable
fun MainTabScreen(orchestrator: Orchestrator? = null) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs =
    listOf(
      TabItem("Console", Icons.AutoMirrored.Filled.Chat),
      TabItem("Tasks", Icons.AutoMirrored.Filled.List),
      TabItem("Web", Icons.Default.Public),
      TabItem("Recipes", Icons.Default.Map),
      TabItem("Skills", Icons.Default.Psychology),
      TabItem("Settings", Icons.Default.Settings),
    )

  Scaffold(
    bottomBar = {
      NavigationBar {
        tabs.forEachIndexed { index, tab ->
          NavigationBarItem(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            icon = { Icon(tab.icon, contentDescription = tab.title) },
            label = { Text(tab.title) },
          )
        }
      }
    }
  ) { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding)) {
      when (selectedTab) {
        0 -> ConsoleScreen(orchestrator)
        1 -> TasksScreen()
        2 -> WebBrowserScreen()
        3 -> RecipesScreen()
        4 -> SkillsScreen()
        5 -> SettingsScreen()
      }
    }
  }
}

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun ConsoleScreen(orchestrator: Orchestrator? = null) {
  var input by remember { mutableStateOf("") }
  val logs by (orchestrator?.logs ?: MutableStateFlow(emptyList())).collectAsState()

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text(text = "AutoPilot Console", style = MaterialTheme.typography.headlineMedium)

    LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 16.dp)) {
      items(logs) { log ->
        Text(text = log, modifier = Modifier.padding(vertical = 4.dp))
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
