package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class NearbyPlayersModule extends ValueModule {
	public final NumberSetting range = add(new NumberSetting("Range", 32, 4, 128, 4).in("Text"));

	public NearbyPlayersModule() {
		super("nearbyplayers", "Nearby Players", Category.COMBAT, false, 0.4f, 0.78f, "Nearby: {value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); var lvl = Game.mc().level;
		if (p == null || lvl == null) return "2";
		int n = 0;
		double r = range.get();
		for (var o : lvl.players()) if (o != p && o.distanceTo(p) <= r) n++;
		return String.valueOf(n);
	}
}
