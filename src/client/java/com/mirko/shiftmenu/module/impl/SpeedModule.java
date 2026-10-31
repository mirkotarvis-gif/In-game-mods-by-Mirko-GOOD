package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class SpeedModule extends ValueModule {
	public final NumberSetting decimals = add(new NumberSetting("Decimals", 2, 0, 3, 1).in("Text"));

	public SpeedModule() {
		super("speed", "Speed", Category.INFO, false, 0.99f, 0.168f, "{value} m/s");
	}

	@Override
	protected String value(boolean preview) {
		return String.format("%." + decimals.get().intValue() + "f", Placeholders.speed());
	}
}
