package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class ServerAddressModule extends ValueModule {
	

	public ServerAddressModule() {
		super("server", "Server Address", Category.INFO, false, 0.5f, 0.006f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		return Placeholders.server();
	}
}
