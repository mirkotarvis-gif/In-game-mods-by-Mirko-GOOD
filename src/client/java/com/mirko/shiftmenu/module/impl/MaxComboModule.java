package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MaxComboModule extends ValueModule {


	public MaxComboModule() {
		super("maxcombo", "Best Combo", Category.COMBAT, false, 0.4f, 0.68f, "Best Combo: {value}");
	}

	@Override
	protected String value(boolean preview) {
		return String.valueOf(CombatTracker.maxCombo());
	}
}
