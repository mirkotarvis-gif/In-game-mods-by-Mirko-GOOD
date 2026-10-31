package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class HealthModule extends ValueModule {
	public final ModeSetting display = add(new ModeSetting("Display", "Value", "Value", "Value / Max", "Percent", "Hearts").in("Text"));
	public final NumberSetting decimals = add(new NumberSetting("Decimals", 1, 0, 2, 1).in("Text"));

	public HealthModule() {
		super("health", "Health", Category.COMBAT, false, 0.2f, 0.08f, "&c❤ &f{value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); float hp = p == null ? 20f : p.getHealth(), max = p == null ? 20f : p.getMaxHealth();
		return switch (display.get()) {
			case "Value / Max" -> Game.fmt(hp, decimals.get().intValue()) + "/" + Game.fmt(max, 0);
			case "Percent" -> Math.round(hp * 100 / Math.max(1, max)) + "%";
			case "Hearts" -> Game.fmt(hp / 2f, 1);
			default -> Game.fmt(hp, decimals.get().intValue());
		};
	}
}
