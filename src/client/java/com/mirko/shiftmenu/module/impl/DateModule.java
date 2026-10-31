package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class DateModule extends ValueModule {
	public final ModeSetting pattern = add(new ModeSetting("Date Format", "dd.MM.yyyy", "dd.MM.yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "EEE, d MMM", "EEEE", "d MMMM yyyy").in("Text"));

	public DateModule() {
		super("date", "Date", Category.INFO, false, 0.6f, 0.58f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		try { return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern(pattern.get(), java.util.Locale.ENGLISH)); } catch (Exception e) { return "?"; }
	}
}
