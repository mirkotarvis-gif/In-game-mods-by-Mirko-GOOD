package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TargetDistanceModule extends ValueModule {
	public final NumberSetting decimals = add(new NumberSetting("Decimals", 1, 0, 3, 1).in("Text"));
	public final BoolSetting hideNoTarget = add(new BoolSetting("Hide Without Target", true).in("Text"));

	public TargetDistanceModule() {
		super("targetdistance", "Target Distance", Category.COMBAT, false, 0.8f, 0.58f, "Target: {value}m");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); var e = Game.mc().crosshairPickEntity;
		if (p == null || e == null) return preview ? "3.2" : (hideNoTarget.on() ? null : "-");
		return Game.fmt(p.distanceTo(e), decimals.get().intValue());
	}
}
