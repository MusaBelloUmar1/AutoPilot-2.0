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

import java.io.File

interface FileSystem {
  fun listFiles(path: String): List<String>

  fun readTextFile(path: String): String?

  fun writeTextFile(path: String, content: String): Boolean
}

class DefaultFileSystem : FileSystem {
  override fun listFiles(path: String): List<String> {
    return File(path).list()?.toList() ?: emptyList()
  }

  override fun readTextFile(path: String): String? {
    return File(path).readText()
  }

  override fun writeTextFile(path: String, content: String): Boolean {
    return try {
      File(path).writeText(content)
      true
    } catch (e: Exception) {
      false
    }
  }
}
