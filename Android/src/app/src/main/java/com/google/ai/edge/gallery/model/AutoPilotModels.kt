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

package com.google.ai.edge.gallery.model

import android.graphics.Rect

// Atomic action – what the agent can do
sealed class Action {
  data class Tap(
    val elementId: String? = null,
    val text: String? = null,
    val x: Int? = null,
    val y: Int? = null,
  ) : Action()

  data class Type(val text: String, val elementId: String? = null) : Action()

  data class Swipe(val startX: Int, val startY: Int, val endX: Int, val endY: Int) : Action()

  data class Wait(val ms: Long) : Action()

  data class WaitForText(val text: String, val timeoutMs: Long = 5000) : Action()

  data class LaunchApp(val packageName: String) : Action()

  object PressBack : Action()

  object PressHome : Action()

  data class ReadFile(val path: String) : Action() // returns content

  data class WriteFile(val path: String, val content: String) : Action()

  data class UploadFileInWebView(val webViewId: String, val filePath: String) : Action()

  data class LoadUrl(val url: String, val webViewId: String) : Action()

  data class WebViewExecuteJs(val webViewId: String, val js: String) : Action()
}

// Task graph node
data class TaskNode(
  val id: String,
  val description: String,
  val status: TaskStatus,
  val subtasks: List<TaskNode>? = null,
  val actionSequence: List<Action>? = null,
  val onCompletion: TaskNode? = null,
  val onError: TaskNode? = null,
)

enum class TaskStatus {
  PENDING,
  RUNNING,
  PAUSED,
  COMPLETED,
  FAILED,
}

// UI Recipe – created from user screenshots
data class UiRecipe(
  val id: String,
  val appName: String, // e.g., "bolt.new"
  val screenName: String, // e.g., "new_project"
  val urlPattern: String? = null, // regex to match URL in WebView
  val elements: List<UiElement>,
)

data class UiElement(
  val name: String, // "upload_button"
  val locatorType: LocatorType, // TEXT, CONTENT_DESC, ID, XY
  val locatorValue: String, // e.g., "Upload Files", or "x,y" for relative coords
  val isClickable: Boolean = true,
  val isInput: Boolean = false,
)

enum class LocatorType {
  TEXT,
  CONTENT_DESC,
  ID,
  XY,
}

data class ScreenDescription(
  val packageName: String,
  val activityName: String,
  val elements: List<ScreenElement>,
)

data class ScreenElement(
  val id: String?,
  val text: String?,
  val contentDesc: String?,
  val isClickable: Boolean,
  val bounds: Rect?, // optional, for XY tap
)

data class Plan(
  val reasoning: String, // for logging
  val actions: List<Action>,
  val waitForEvent: WaitEvent? = null, // if waiting for something
  val subtaskDescription: String? = null,
)

data class WaitEvent(
  val type: EventType, // TEXT_APPEARS, ELEMENT_CLICKABLE, TIME_PASSED
  val value: String,
  val timeoutMs: Long,
)

enum class EventType {
  TEXT_APPEARS,
  ELEMENT_CLICKABLE,
  TIME_PASSED,
}
