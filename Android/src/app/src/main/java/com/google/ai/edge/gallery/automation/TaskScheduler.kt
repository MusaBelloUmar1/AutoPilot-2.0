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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

interface TaskScheduler {
  fun submitGoal(goal: String, rootTask: TaskNode)

  suspend fun runTask(taskId: String)

  fun pauseTask(taskId: String)

  fun resumeTask(taskId: String)

  fun cancelTask(taskId: String)

  fun getStatus(taskId: String): TaskStatus
}

class DefaultTaskScheduler(
  private val brain: AgentBrain,
  private val perception: Perception,
  private val onLog: (String) -> Unit = {},
) : TaskScheduler {

  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  private val tasks = mutableMapOf<String, TaskNode>()

  override fun submitGoal(goal: String, rootTask: TaskNode) {
    tasks[rootTask.id] = rootTask
    scope.launch { runTask(rootTask.id) }
  }

  override suspend fun runTask(taskId: String) {
    val task = tasks[taskId] ?: return
    tasks[taskId] = task.copy(status = TaskStatus.RUNNING)
    onLog("Agent is thinking...")

    val screenDesc = perception.getScreenDescription()
    val plan = brain.plan(task.description, screenDesc)
    onLog("Plan: ${plan.reasoning}")

    for (action in plan.actions) {
      onLog("Executing: $action")
      perception.executeAction(action)
    }

    tasks[taskId] = task.copy(status = TaskStatus.COMPLETED)
    onLog("Goal achieved!")
  }

  override fun pauseTask(taskId: String) {
    tasks[taskId]?.let { tasks[taskId] = it.copy(status = TaskStatus.PAUSED) }
  }

  override fun resumeTask(taskId: String) {
    scope.launch { runTask(taskId) }
  }

  override fun cancelTask(taskId: String) {
    tasks.remove(taskId)
  }

  override fun getStatus(taskId: String): TaskStatus {
    return tasks[taskId]?.status ?: TaskStatus.FAILED
  }
}
