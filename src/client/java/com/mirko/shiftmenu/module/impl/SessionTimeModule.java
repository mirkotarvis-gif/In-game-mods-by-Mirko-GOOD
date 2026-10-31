package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class SessionTimeModule extends ValueModule {
	

	public SessionTimeModule() {
		super("session", "Session Time", Category.INFO, false, 0.99f, 0.276f, "Session: {value}");
	}

	@Override
	protected String value(boolean preview) {
		return Placeholders.session();
	}
}
