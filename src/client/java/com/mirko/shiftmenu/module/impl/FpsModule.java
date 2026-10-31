package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Placeholders;

public class FpsModule extends ValueModule {
	

	public FpsModule() {
		super("fps", "FPS", Category.HUD, true, 0.004f, 0.006f, "{value} FPS");
	}

	@Override
	protected String value(boolean preview) {
		return String.valueOf(net.minecraft.client.Minecraft.getInstance().getFps());
	}
}
