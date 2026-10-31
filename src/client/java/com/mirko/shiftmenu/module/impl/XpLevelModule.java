package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class XpLevelModule extends ValueModule {


	public XpLevelModule() {
		super("xp", "XP Level", Category.INFO, false, 0.2f, 0.18f, "&aLevel {value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); return String.valueOf(p == null ? 30 : p.experienceLevel);
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return new ItemStack(Items.EXPERIENCE_BOTTLE);
	}
}
