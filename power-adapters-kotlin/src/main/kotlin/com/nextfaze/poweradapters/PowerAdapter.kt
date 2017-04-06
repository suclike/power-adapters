package com.nextfaze.poweradapters

import android.support.annotation.LayoutRes

operator fun PowerAdapter.plus(adapter: PowerAdapter) = append(adapter)

operator fun PowerAdapter.plus(adapters: Iterable<PowerAdapter>) = adapter { adapter(this@plus); adapters(adapters) }

operator fun PowerAdapter.plus(view: ViewFactory) = append(view)

@JvmName("plusViews") operator fun PowerAdapter.plus(views: Iterable<ViewFactory>) =
        adapter { adapter(this@plus); views(views) }

operator fun PowerAdapter.plus(@LayoutRes layoutResource: Int) = append(layoutResource)

@JvmName("plusLayoutResources") operator fun PowerAdapter.plus(layoutResources: Iterable<Int>) =
        adapter { adapter(this@plus); layoutResources(layoutResources) }