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

import com.google.ai.edge.gallery.model.Action
import com.google.ai.edge.gallery.model.ScreenDescription

interface Perception {
  suspend fun getScreenDescription(): ScreenDescription

  suspend fun executeAction(action: Action): ActionResult

  suspend fun getWebViewDescription(webViewId: String): String
}

data class ActionResult(val success: Boolean, val error: String? = null)

class DefaultPerception(private val accessibilityService: AutoPilotAccessibilityService?) :
  Perception {

  override suspend fun getScreenDescription(): ScreenDescription {
    return accessibilityService?.getScreenDescription() ?: ScreenDescription("", "", emptyList())
  }

  override suspend fun executeAction(action: Action): ActionResult {
    return when (action) {
      is Action.Tap -> {
        val success = accessibilityService?.performTap(action.x ?: 0, action.y ?: 0) ?: false
        ActionResult(success)
      }
      is Action.Type -> {
        val success = accessibilityService?.performType(action.text, action.elementId) ?: false
        ActionResult(success)
      }
      else -> ActionResult(false, "Action not supported yet")
    }
  }

  override suspend fun getWebViewDescription(webViewId: String): String {
    return "WebView Content"
  }
}
