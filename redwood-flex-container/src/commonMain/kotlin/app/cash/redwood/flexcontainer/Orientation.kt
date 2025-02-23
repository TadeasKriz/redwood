/*
 * Copyright (C) 2022 Square, Inc.
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
package app.cash.redwood.flexcontainer

internal fun FlexDirection.toOrientation(): Orientation {
  return if (isHorizontal) Orientation.Horizontal else Orientation.Vertical
}

internal fun Orientation.mainMeasuredSizeWithMargin(node: FlexItem): Int =
  mainMeasuredSize(node) + mainMargin(node)

internal fun Orientation.crossMeasuredSizeWithMargin(node: FlexItem): Int =
  crossMeasuredSize(node) + crossMargin(node)

/**
 * An interface to perform operations along the main/cross axis without knowledge
 * of the underlying [FlexDirection].
 */
internal sealed interface Orientation {
  fun mainPadding(padding: Spacing): Int
  fun crossPadding(padding: Spacing): Int

  fun mainMargin(item: FlexItem): Int
  fun crossMargin(item: FlexItem): Int

  fun mainSize(item: FlexItem): Int
  fun crossSize(item: FlexItem): Int
  fun mainMeasuredSize(item: FlexItem): Int
  fun crossMeasuredSize(item: FlexItem): Int

  object Horizontal : Orientation {
    override fun mainPadding(padding: Spacing) = padding.start + padding.end
    override fun crossPadding(padding: Spacing) = padding.top + padding.bottom
    override fun mainMargin(item: FlexItem) = item.margin.start + item.margin.end
    override fun crossMargin(item: FlexItem) = item.margin.top + item.margin.bottom
    override fun mainSize(item: FlexItem) = item.measurable.requestedWidth
    override fun crossSize(item: FlexItem) = item.measurable.requestedHeight
    override fun mainMeasuredSize(item: FlexItem) = item.measuredWidth
    override fun crossMeasuredSize(item: FlexItem) = item.measuredHeight
  }

  object Vertical : Orientation {
    override fun mainPadding(padding: Spacing) = padding.top + padding.bottom
    override fun crossPadding(padding: Spacing) = padding.start + padding.end
    override fun mainMargin(item: FlexItem) = item.margin.top + item.margin.bottom
    override fun crossMargin(item: FlexItem) = item.margin.start + item.margin.end
    override fun mainSize(item: FlexItem) = item.measurable.requestedHeight
    override fun crossSize(item: FlexItem) = item.measurable.requestedWidth
    override fun mainMeasuredSize(item: FlexItem) = item.measuredHeight
    override fun crossMeasuredSize(item: FlexItem) = item.measuredWidth
  }
}
