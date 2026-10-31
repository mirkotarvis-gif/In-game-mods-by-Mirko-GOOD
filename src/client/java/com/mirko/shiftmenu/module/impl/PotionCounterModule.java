package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PotionCounterModule extends ValueModule {
	public final BoolSetting hideZero = add(new BoolSetting("Hide When Zero", false).in("Text"));

	public PotionCounterModule() {
		super("potioncount", "Potion Counter", Category.COMBAT, false, 0.6f, 0.38f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		int n = Game.count(s -> s.is(Items.POTION) || s.is(Items.SPLASH_POTION) || s.is(Items.LINGERING_POTION));
		return n == 0 && hideZero.on() && !preview ? null : String.valueOf(n);
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return new ItemStack(Items.SPLASH_POTION);
	}
}
