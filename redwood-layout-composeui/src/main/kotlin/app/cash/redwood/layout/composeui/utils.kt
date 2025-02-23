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
package app.cash.redwood.layout.composeui

import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import app.cash.redwood.LayoutModifier
import app.cash.redwood.flexcontainer.AlignItems
import app.cash.redwood.flexcontainer.AlignSelf
import app.cash.redwood.flexcontainer.FlexDirection
import app.cash.redwood.flexcontainer.FlexItem
import app.cash.redwood.flexcontainer.FlexItem.Companion.DefaultFlexGrow
import app.cash.redwood.flexcontainer.FlexItem.Companion.DefaultFlexShrink
import app.cash.redwood.flexcontainer.JustifyContent
import app.cash.redwood.flexcontainer.Measurable as RedwoodMeasurable
import app.cash.redwood.flexcontainer.MeasureSpec
import app.cash.redwood.flexcontainer.MeasureSpecMode
import app.cash.redwood.flexcontainer.Size
import app.cash.redwood.flexcontainer.Spacing
import app.cash.redwood.flexcontainer.isHorizontal
import app.cash.redwood.flexcontainer.isVertical
import app.cash.redwood.layout.Grow
import app.cash.redwood.layout.HorizontalAlignment
import app.cash.redwood.layout.Padding as PaddingModifier
import app.cash.redwood.layout.Shrink
import app.cash.redwood.layout.VerticalAlignment
import app.cash.redwood.layout.api.CrossAxisAlignment
import app.cash.redwood.layout.api.MainAxisAlignment
import app.cash.redwood.layout.api.Padding

internal fun MainAxisAlignment.toJustifyContent() = when (this) {
  MainAxisAlignment.Start -> JustifyContent.FlexStart
  MainAxisAlignment.Center -> JustifyContent.Center
  MainAxisAlignment.End -> JustifyContent.FlexEnd
  MainAxisAlignment.SpaceBetween -> JustifyContent.SpaceBetween
  MainAxisAlignment.SpaceAround -> JustifyContent.SpaceAround
  MainAxisAlignment.SpaceEvenly -> JustifyContent.SpaceEvenly
  else -> throw AssertionError()
}

internal fun CrossAxisAlignment.toAlignItems() = when (this) {
  CrossAxisAlignment.Start -> AlignItems.FlexStart
  CrossAxisAlignment.Center -> AlignItems.Center
  CrossAxisAlignment.End -> AlignItems.FlexEnd
  CrossAxisAlignment.Stretch -> AlignItems.Stretch
  else -> throw AssertionError()
}

internal fun CrossAxisAlignment.toAlignSelf() = when (this) {
  CrossAxisAlignment.Start -> AlignSelf.FlexStart
  CrossAxisAlignment.Center -> AlignSelf.Center
  CrossAxisAlignment.End -> AlignSelf.FlexEnd
  CrossAxisAlignment.Stretch -> AlignSelf.Stretch
  else -> throw AssertionError()
}

internal fun Padding.toSpacing() = Spacing(start, end, top, bottom)

internal fun Constraints.toMeasureSpecs(): Pair<MeasureSpec, MeasureSpec> {
  val widthSpec = when {
    hasFixedWidth -> MeasureSpec.from(maxWidth, MeasureSpecMode.Exactly)
    hasBoundedWidth -> MeasureSpec.from(maxWidth, MeasureSpecMode.AtMost)
    else -> MeasureSpec.from(minWidth, MeasureSpecMode.Unspecified)
  }
  val heightSpec = when {
    hasFixedHeight -> MeasureSpec.from(maxHeight, MeasureSpecMode.Exactly)
    hasBoundedHeight -> MeasureSpec.from(maxHeight, MeasureSpecMode.AtMost)
    else -> MeasureSpec.from(minHeight, MeasureSpecMode.Unspecified)
  }
  return widthSpec to heightSpec
}

internal fun measureSpecsToConstraints(widthSpec: MeasureSpec, heightSpec: MeasureSpec): Constraints {
  val minWidth: Int
  val maxWidth: Int
  when (widthSpec.mode) {
    MeasureSpecMode.Exactly -> {
      minWidth = widthSpec.size
      maxWidth = widthSpec.size
    }
    MeasureSpecMode.AtMost -> {
      minWidth = 0
      maxWidth = widthSpec.size
    }
    MeasureSpecMode.Unspecified -> {
      minWidth = 0
      maxWidth = Constraints.Infinity
    }
    else -> throw AssertionError()
  }
  val minHeight: Int
  val maxHeight: Int
  when (heightSpec.mode) {
    MeasureSpecMode.Exactly -> {
      minHeight = heightSpec.size
      maxHeight = heightSpec.size
    }
    MeasureSpecMode.AtMost -> {
      minHeight = 0
      maxHeight = heightSpec.size
    }
    MeasureSpecMode.Unspecified -> {
      minHeight = 0
      maxHeight = Constraints.Infinity
    }
    else -> throw AssertionError()
  }
  return Constraints(minWidth, maxWidth, minHeight, maxHeight)
}

internal fun Measurable.asItem(layoutModifiers: LayoutModifier, direction: FlexDirection): FlexItem {
  var flexGrow = DefaultFlexGrow
  var flexShrink = DefaultFlexShrink
  var padding = Padding.Zero
  var crossAxisAlignment = CrossAxisAlignment.Start
  var isCrossAxisAlignmentSet = false
  layoutModifiers.forEach { modifier ->
    when (modifier) {
      is Grow -> flexGrow = modifier.value
      is Shrink -> flexShrink = modifier.value
      is PaddingModifier -> padding = modifier.padding
      is HorizontalAlignment -> if (direction.isVertical) {
        crossAxisAlignment = modifier.alignment
        isCrossAxisAlignmentSet = true
      }
      is VerticalAlignment -> if (direction.isHorizontal) {
        crossAxisAlignment = modifier.alignment
        isCrossAxisAlignmentSet = true
      }
    }
  }
  return FlexItem(
    flexGrow = flexGrow,
    flexShrink = flexShrink,
    margin = padding.toSpacing(),
    alignSelf = if (isCrossAxisAlignmentSet) {
      crossAxisAlignment.toAlignSelf()
    } else {
      AlignSelf.Auto
    },
    measurable = ComposeMeasurable(this),
  )
}

internal class ComposeMeasurable(private val measurable: Measurable) : RedwoodMeasurable() {

  lateinit var placeable: Placeable
    private set

  override fun width(height: Int): Int {
    return measurable.minIntrinsicWidth(height)
  }

  override fun height(width: Int): Int {
    return measurable.minIntrinsicHeight(width)
  }

  override fun measure(widthSpec: MeasureSpec, heightSpec: MeasureSpec): Size {
    this.placeable = measurable.measure(measureSpecsToConstraints(widthSpec, heightSpec))
    return Size(placeable.width, placeable.height)
  }
}
