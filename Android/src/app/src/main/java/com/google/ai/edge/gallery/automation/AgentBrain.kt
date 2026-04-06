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

import android.util.Log
import com.google.ai.edge.gallery.data.Model
import com.google.ai.edge.gallery.model.Action
import com.google.ai.edge.gallery.model.Plan
import com.google.ai.edge.gallery.model.ScreenDescription
import com.google.ai.edge.gallery.runtime.LlmModelHelper
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONObject

interface AgentBrain {
  suspend fun plan(
    goal: String,
    screenDesc: ScreenDescription,
    previousErrors: List<String> = emptyList(),
  ): Plan
}

class LiteRTAgentBrain(
  private val modelHelper: LlmModelHelper,
  private val modelProvider: () -> Model? = { null }
) : AgentBrain {
  override suspend fun plan(
    goal: String,
    screenDesc: ScreenDescription,
    previousErrors: List<String>,
  ): Plan {
    val model = modelProvider()
    if (model == null || model.instance == null) {
      Log.w("LiteRTAgentBrain", "No model instance available for planning.")
      return Plan(reasoning = "Thinking (simulated): I'll help you with '$goal'", actions = emptyList())
    }

    val prompt = constructPrompt(goal, screenDesc)
    val deferred = CompletableDeferred<Plan>()
    var fullResult = ""

    modelHelper.runInference(
      model = model,
      input = prompt,
      resultListener = { partialResult, done, partialThinking ->
        fullResult += partialResult
        if (done) {
          try {
            val jsonStr = if (fullResult.contains("```json")) {
                fullResult.substringAfter("```json").substringBefore("```")
            } else {
                fullResult
            }
            val json = JSONObject(jsonStr)
            val reasoning = json.optString("reasoning", "Thinking...")
            val actionsJson = json.optJSONArray("actions")
            val actions = mutableListOf<Action>()
            if (actionsJson != null) {
              for (i in 0 until actionsJson.length()) {
                val actionObj = actionsJson.getJSONObject(i)
                val type = actionObj.getString("type")
                when (type) {
                  "tap" -> actions.add(Action.Tap(x = actionObj.optInt("x"), y = actionObj.optInt("y")))
                  "type" -> actions.add(Action.Type(text = actionObj.getString("text")))
                }
              }
            }
            deferred.complete(Plan(reasoning = reasoning, actions = actions))
          } catch (e: Exception) {
            deferred.complete(Plan(reasoning = "Parsing Error: $fullResult", actions = emptyList()))
          }
        }
      },
      cleanUpListener = {},
      onError = { error ->
        deferred.complete(Plan(reasoning = "Model Error: $error", actions = emptyList()))
      }
    )

    return deferred.await()
  }

  private fun constructPrompt(goal: String, screenDesc: ScreenDescription): String {
    return """
      User Goal: $goal
      Screen: ${screenDesc.packageName}
      Visible Elements:
      ${screenDesc.elements.take(10).joinToString("\n") { "- ${it.text ?: it.contentDesc ?: it.id} (at ${it.bounds?.centerX()}, ${it.bounds?.centerY()})" }}

      Respond ONLY with a JSON block:
      ```json
      {
        "reasoning": "A short sentence describing your plan",
        "actions": [
          {"type": "tap", "x": 100, "y": 200},
          {"type": "type", "text": "Hello"}
        ]
      }
      ```
    """.trimIndent()
  }
}
