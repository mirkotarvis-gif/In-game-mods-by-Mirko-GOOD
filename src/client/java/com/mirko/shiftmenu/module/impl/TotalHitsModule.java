package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TotalHitsModule extends ValueModule {


	public TotalHitsModule() {
		super("hits", "Total Hits", Category.COMBAT, false, 0.2f, 0.68f, "Hits: {value}");
	}

	@Override
	protected String value(boolean preview) {
		return String.valueOf(CombatTracker.totalHits());
	}
}
