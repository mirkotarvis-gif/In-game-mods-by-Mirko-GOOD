package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AirModule extends ValueModule {
	public final BoolSetting hideWhenFull = add(new BoolSetting("Hide When Full", true).in("Text"));
	public final BoolSetting percent = add(new BoolSetting("Show Percent", true).in("Text"));

	public AirModule() {
		super("air", "Oxygen", Category.INFO, false, 0.4f, 0.18f, "&bAir: &f{value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); if (p == null) return "300";
		if (hideWhenFull.on() && !preview && p.getAirSupply() >= p.getMaxAirSupply()) return null;
		return percent.on() ? Math.max(0, p.getAirSupply() * 100 / Math.max(1, p.getMaxAirSupply())) + "%" : String.valueOf(Math.max(0, p.getAirSupply() / 20));
	}
}
