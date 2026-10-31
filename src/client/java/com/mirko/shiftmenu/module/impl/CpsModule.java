package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.util.Keys;

public class CpsModule extends ValueModule {
	public final ModeSetting buttons = add(new ModeSetting("Buttons", "Left", "Left", "Right", "Left | Right").in("Text"));

	public CpsModule() {
		super("cps", "CPS", Category.COMBAT, true, 0.004f, 0.06f, "{value} CPS");
	}

	@Override
	protected String value(boolean preview) {
		return switch (buttons.get()) {
			case "Right" -> String.valueOf(Keys.rightCps());
			case "Left | Right" -> Keys.leftCps() + " | " + Keys.rightCps();
			default -> String.valueOf(Keys.leftCps());
		};
	}
}
