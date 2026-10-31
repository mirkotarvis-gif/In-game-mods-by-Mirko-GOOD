package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class HeldItemModule extends ValueModule {
	public final BoolSetting showCount = add(new BoolSetting("Show Count", true).in("Text"));
	public final BoolSetting hideEmpty = add(new BoolSetting("Hide When Empty", true).in("Text"));

	public HeldItemModule() {
		super("helditem", "Held Item", Category.INFO, false, 0.6f, 0.18f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		ItemStack st = Game.held();
		if (st.isEmpty()) return preview ? "Diamond Sword" : (hideEmpty.on() ? null : "Empty Hand");
		String n = st.getHoverName().getString();
		return showCount.on() && st.getCount() > 1 ? n + " x" + st.getCount() : n;
	}

	@Override
	protected boolean hasIcon() {
		return true;
	}

	@Override
	protected ItemStack icon(boolean preview) {
		return Game.held().isEmpty() ? new ItemStack(Items.DIAMOND_SWORD) : Game.held();
	}
}
