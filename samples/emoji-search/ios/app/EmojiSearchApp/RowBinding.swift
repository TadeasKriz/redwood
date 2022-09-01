//
//  ColumnBinding.swift
//  EmojiSearchApp
//
//  Created by Kyle Bashour on 8/30/22.
//  Copyright © 2022 Square Inc. All rights reserved.
//

import UIKit
import shared

class RowBinding: WidgetRow {
    private let root: UIStackView = {
        let view = UIStackView()
        view.axis = .horizontal
        view.contentMode = .left
        return view
    }()

    init() {}

    lazy var children: Redwood_widgetWidgetChildren = ChildrenBinding { [unowned self] views in
        self.root.subviews.forEach { $0.removeFromSuperview() }
        views.forEach { self.root.addArrangedSubview($0) }
    }
    var layoutModifiers: Redwood_runtimeLayoutModifier = NoopRuntimeLayoutModifier()
    var value: Any { root }
}
