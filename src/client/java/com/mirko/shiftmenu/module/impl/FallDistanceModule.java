package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FallDistanceModule extends ValueModule {
	public final BoolSetting hideWhenZero = add(new BoolSetting("Hide When Not Falling", true).in("Text"));

	public FallDistanceModule() {
		super("falldistance", "Fall Distance", Category.INFO, false, 0.8f, 0.48f, "Fall: {value}m");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); double f = p == null ? 0 : (double) p.fallDistance;
		if (f < 0.1 && hideWhenZero.on() && !preview) return null;
		return Game.fmt(f, 1);
	}
}
