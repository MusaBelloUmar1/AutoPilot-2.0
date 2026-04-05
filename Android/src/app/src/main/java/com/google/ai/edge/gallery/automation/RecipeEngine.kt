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

import com.google.ai.edge.gallery.model.ScreenDescription
import com.google.ai.edge.gallery.model.UiElement
import com.google.ai.edge.gallery.model.UiRecipe

interface RecipeEngine {
  fun addRecipe(recipe: UiRecipe)

  fun getRecipeForCurrentScreen(screenDesc: ScreenDescription, webViewUrl: String?): UiRecipe?

  fun findElementByName(recipe: UiRecipe, elementName: String): UiElement?

  fun screenMatchesRecipe(screenDesc: ScreenDescription, recipe: UiRecipe): Boolean
}

class DefaultRecipeEngine : RecipeEngine {
  private val recipes = mutableListOf<UiRecipe>()

  override fun addRecipe(recipe: UiRecipe) {
    recipes.add(recipe)
  }

  override fun getRecipeForCurrentScreen(
    screenDesc: ScreenDescription,
    webViewUrl: String?,
  ): UiRecipe? {
    return recipes.find { screenMatchesRecipe(screenDesc, it) }
  }

  override fun findElementByName(recipe: UiRecipe, elementName: String): UiElement? {
    return recipe.elements.find { it.name == elementName }
  }

  override fun screenMatchesRecipe(screenDesc: ScreenDescription, recipe: UiRecipe): Boolean {
    // Basic package name matching logic.
    return screenDesc.packageName == recipe.appName
  }
}
