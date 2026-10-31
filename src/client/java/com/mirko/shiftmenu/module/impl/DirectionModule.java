package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class DirectionModule extends ValueModule {
	

	public DirectionModule() {
		super("direction", "Direction", Category.INFO, false, 0.99f, 0.222f, "{value} ({axis})");
	}

	@Override
	protected String value(boolean preview) {
		return Placeholders.apply("{facing}");
	}
}
