package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class HeldDurabilityModule extends ValueModule {
	public final BoolSetting percent = add(new BoolSetting("Show Percent", false).in("Text"));

	public HeldDurabilityModule() {
		super("helddurability", "Held Durability", Category.COMBAT, false, 0.8f, 0.18f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		ItemStack st = Game.held();
		if (st.isEmpty() || !st.isDamageableItem()) return preview ? "1561" : null;
		int left = st.getMaxDamage() - st.getDamageValue();
		return percent.on() ? (left * 100 / Math.max(1, st.getMaxDamage())) + "%" : String.valueOf(left);
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return Game.held().isEmpty() ? new ItemStack(Items.DIAMOND_PICKAXE) : Game.held();
	}
}
