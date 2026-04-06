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

package com.google.ai.edge.gallery.automation

import com.google.ai.edge.gallery.model.TaskNode
import com.google.ai.edge.gallery.model.TaskStatus
import java.util.UUID

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Orchestrator(private val taskScheduler: TaskScheduler) {

  private val _logs = MutableStateFlow<List<String>>(emptyList())
  val logs: StateFlow<List<String>> = _logs.asStateFlow()

  fun submitGoal(goal: String) {
    val taskId = UUID.randomUUID().toString()
    val rootTask = TaskNode(id = taskId, description = goal, status = TaskStatus.PENDING)
    addLog("Goal submitted: $goal")
    taskScheduler.submitGoal(goal, rootTask)
  }

  fun addLog(message: String) {
    _logs.value = _logs.value + message
  }

  fun getTaskStatus(taskId: String): TaskStatus {
    return taskScheduler.getStatus(taskId)
  }
}
