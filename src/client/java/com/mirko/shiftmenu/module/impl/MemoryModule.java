package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class MemoryModule extends ValueModule {
	public final ModeSetting display = add(new ModeSetting("Display", "Percent", "Percent", "Megabytes").in("Text"));

	public MemoryModule() {
		super("memory", "Memory", Category.INFO, false, 0.99f, 0.114f, "Mem: {value}");
	}

	@Override
	protected String value(boolean preview) {
		return display.is("Percent") ? Placeholders.memPercent() : ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "/" + (Runtime.getRuntime().maxMemory() >> 20) + " MB";
	}
}
