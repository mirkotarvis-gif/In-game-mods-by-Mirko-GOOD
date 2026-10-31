package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SystemInfoModule extends ValueModule {


	public SystemInfoModule() {
		super("system", "System Info", Category.INFO, false, 0.8f, 0.68f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		Runtime rt = Runtime.getRuntime();
		return "Java " + Runtime.version().feature() + " \u2022 " + rt.availableProcessors() + " cores \u2022 " + (rt.maxMemory() >> 20) + " MB";
	}
}
