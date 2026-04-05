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

import android.webkit.WebView

interface WebViewManager {
  fun createWebView(id: String): WebView

  fun loadUrl(id: String, url: String)

  fun getCurrentUrl(id: String): String

  fun executeJavaScript(id: String, script: String, callback: (String) -> Unit)

  fun uploadFile(id: String, filePath: String) // simulates <input type="file">

  fun getPageText(id: String): String // simplified DOM text
}

class DefaultWebViewManager(private val webViewProvider: (String) -> WebView?) : WebViewManager {
  override fun createWebView(id: String): WebView {
    throw UnsupportedOperationException("Creation handled by UI")
  }

  override fun loadUrl(id: String, url: String) {
    webViewProvider(id)?.loadUrl(url)
  }

  override fun getCurrentUrl(id: String): String {
    return webViewProvider(id)?.url ?: ""
  }

  override fun executeJavaScript(id: String, script: String, callback: (String) -> Unit) {
    webViewProvider(id)?.evaluateJavascript(script, callback)
  }

  override fun uploadFile(id: String, filePath: String) {
    // Advanced implementation needed to mock file input
  }

  override fun getPageText(id: String): String {
    return "Page Content Placeholder"
  }
}
