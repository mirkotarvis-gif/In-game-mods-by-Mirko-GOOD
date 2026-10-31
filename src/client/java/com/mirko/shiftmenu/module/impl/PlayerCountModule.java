package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class PlayerCountModule extends ValueModule {
	

	public PlayerCountModule() {
		super("players", "Player Count", Category.INFO, false, 0.99f, 0.33f, "Players: {value}");
	}

	@Override
	protected String value(boolean preview) {
		return String.valueOf(Placeholders.players());
	}
}
