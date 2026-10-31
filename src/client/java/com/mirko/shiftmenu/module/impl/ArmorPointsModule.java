package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ArmorPointsModule extends ValueModule {


	public ArmorPointsModule() {
		super("armorpoints", "Armor Points", Category.COMBAT, false, 0.8f, 0.08f, "&7Armor: &f{value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); return String.valueOf(p == null ? 20 : p.getArmorValue());
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return new ItemStack(Items.IRON_CHESTPLATE);
	}
}
