package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ItemCounterModule extends ValueModule {


	public ItemCounterModule() {
		super("itemcounter", "Held Item Counter", Category.INFO, false, 0.2f, 0.28f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		ItemStack st = Game.held();
		if (st.isEmpty()) return preview ? "64" : null;
		return String.valueOf(Game.count(s -> ItemStack.isSameItem(s, st)));
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return Game.held().isEmpty() ? new ItemStack(Items.COBBLESTONE) : Game.held();
	}
}
