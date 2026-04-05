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

import com.google.ai.edge.gallery.model.Plan
import com.google.ai.edge.gallery.model.ScreenDescription
import com.google.ai.edge.gallery.runtime.LlmModelHelper

interface AgentBrain {
  suspend fun plan(
    goal: String,
    screenDesc: ScreenDescription,
    previousErrors: List<String> = emptyList(),
  ): Plan
}

class LiteRTAgentBrain(private val modelHelper: LlmModelHelper) : AgentBrain {
  override suspend fun plan(
    goal: String,
    screenDesc: ScreenDescription,
    previousErrors: List<String>,
  ): Plan {
    // In a real implementation, we would call modelHelper.runInference
    // For now, returning a simulated plan to show the concept.
    return Plan(reasoning = "I will help you with: $goal", actions = emptyList())
  }
}
