/*
 * Copyright (C) 2021 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.android.ui

import android.view.View
import android.widget.LinearLayout
import app.cash.treehouse.widget.ViewGroupChildren
import example.ui.widget.Box

class AndroidBox(
  override val value: LinearLayout,
) : Box<View> {
  override val children = ViewGroupChildren(value)
}
