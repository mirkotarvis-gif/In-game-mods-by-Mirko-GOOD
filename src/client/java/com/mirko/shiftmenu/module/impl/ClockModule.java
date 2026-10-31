package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class ClockModule extends ValueModule {
	public final ModeSetting format24 = add(new ModeSetting("Clock Type", "24h", "24h", "12h").in("Text"));

	public ClockModule() {
		super("clock", "Clock", Category.INFO, false, 0.99f, 0.06f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		return Placeholders.apply(format24.is("12h") ? "{time12}" : "{time}");
	}
}
