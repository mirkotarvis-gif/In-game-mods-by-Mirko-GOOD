package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FoodModule extends ValueModule {


	public FoodModule() {
		super("food", "Hunger", Category.INFO, false, 0.4f, 0.08f, "&6Food: &f{value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); return String.valueOf(p == null ? 20 : p.getFoodData().getFoodLevel());
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return new ItemStack(Items.COOKED_BEEF);
	}
}
