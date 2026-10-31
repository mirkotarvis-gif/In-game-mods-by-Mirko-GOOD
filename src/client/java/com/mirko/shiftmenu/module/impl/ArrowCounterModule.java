package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ArrowCounterModule extends ValueModule {
	public final BoolSetting hideZero = add(new BoolSetting("Hide When Zero", false).in("Text"));

	public ArrowCounterModule() {
		super("arrows", "Arrow Counter", Category.COMBAT, false, 0.4f, 0.28f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		int n = Game.count(s -> s.is(Items.ARROW) || s.is(Items.SPECTRAL_ARROW) || s.is(Items.TIPPED_ARROW));
		return n == 0 && hideZero.on() && !preview ? null : String.valueOf(n);
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return new ItemStack(Items.ARROW);
	}
}
