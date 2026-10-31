package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class PingModule extends ValueModule {
	

	public PingModule() {
		super("ping", "Ping", Category.INFO, false, 0.99f, 0.006f, "{value} ms");
	}

	@Override
	protected String value(boolean preview) {
		return String.valueOf(Placeholders.ping());
	}
}
