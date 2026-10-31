package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TotemCounterModule extends ValueModule {
	public final BoolSetting hideZero = add(new BoolSetting("Hide When Zero", false).in("Text"));

	public TotemCounterModule() {
		super("totems", "Totem Counter", Category.COMBAT, false, 0.6f, 0.28f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		int n = Game.count(s -> s.is(Items.TOTEM_OF_UNDYING));
		return n == 0 && hideZero.on() && !preview ? null : String.valueOf(n);
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return new ItemStack(Items.TOTEM_OF_UNDYING);
	}
}
