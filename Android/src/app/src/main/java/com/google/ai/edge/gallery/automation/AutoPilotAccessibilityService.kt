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

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.ai.edge.gallery.model.ScreenDescription
import com.google.ai.edge.gallery.model.ScreenElement

class AutoPilotAccessibilityService : AccessibilityService() {

  override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    // Monitor screen changes if needed.
  }

  override fun onInterrupt() {
    Log.e(TAG, "Accessibility service interrupted.")
  }

  fun getScreenDescription(): ScreenDescription {
    val rootNode = rootInActiveWindow ?: return ScreenDescription("", "", emptyList())
    val elements = mutableListOf<ScreenElement>()
    traverseNodes(rootNode, elements)

    // Using placeholder for activity name as it's not directly available in node.
    return ScreenDescription(rootNode.packageName?.toString() ?: "", "CurrentActivity", elements)
  }

  fun performTap(x: Int, y: Int): Boolean {
    Log.d(TAG, "Tapping at ($x, $y)")
    val rootNode = rootInActiveWindow ?: return false
    val node = findNodeAtPoint(rootNode, x, y) ?: return false
    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
  }

  fun performType(text: String, nodeId: String?): Boolean {
    Log.d(TAG, "Typing '$text' into $nodeId")
    val rootNode = rootInActiveWindow ?: return false
    val nodes = rootNode.findAccessibilityNodeInfosByViewId(nodeId ?: "")
    if (nodes.isEmpty()) return false
    val node = nodes[0]
    return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, android.os.Bundle().apply {
      putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
    })
  }

  private fun findNodeAtPoint(node: AccessibilityNodeInfo, x: Int, y: Int): AccessibilityNodeInfo? {
    val bounds = Rect()
    node.getBoundsInScreen(bounds)
    if (bounds.contains(x, y)) {
      for (i in 0 until node.childCount) {
        val child = node.getChild(i) ?: continue
        val result = findNodeAtPoint(child, x, y)
        if (result != null) return result
      }
      return node
    }
    return null
  }

  private fun traverseNodes(node: AccessibilityNodeInfo, elements: MutableList<ScreenElement>) {
    val bounds = Rect()
    node.getBoundsInScreen(bounds)

    if (node.text != null || node.contentDescription != null || node.isClickable) {
      elements.add(
        ScreenElement(
          id = node.viewIdResourceName,
          text = node.text?.toString(),
          contentDesc = node.contentDescription?.toString(),
          isClickable = node.isClickable,
          bounds = bounds,
        )
      )
    }

    for (i in 0 until node.childCount) {
      val child = node.getChild(i) ?: continue
      traverseNodes(child, elements)
    }
  }

  companion object {
    private const val TAG = "AutoPilotAccService"
    private var instance: AutoPilotAccessibilityService? = null

    fun getInstance(): AutoPilotAccessibilityService? = instance
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    instance = this
    Log.d(TAG, "Service connected.")
  }

  override fun onDestroy() {
    super.onDestroy()
    instance = null
  }
}
