package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class VelocityModule extends ValueModule {
	public final NumberSetting decimals = add(new NumberSetting("Decimals", 2, 0, 3, 1).in("Text"));

	public VelocityModule() {
		super("velocity", "Vertical Speed", Category.INFO, false, 0.6f, 0.48f, "Y Speed: {value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); return Game.fmt(p == null ? 0 : p.getDeltaMovement().y * 20.0, decimals.get().intValue());
	}
}
