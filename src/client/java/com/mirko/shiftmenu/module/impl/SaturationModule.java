package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SaturationModule extends ValueModule {


	public SaturationModule() {
		super("saturation", "Saturation", Category.INFO, false, 0.6f, 0.08f, "&eSaturation: &f{value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); return Game.fmt(p == null ? 5f : p.getFoodData().getSaturationLevel(), 1);
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return new ItemStack(Items.GOLDEN_CARROT);
	}
}
