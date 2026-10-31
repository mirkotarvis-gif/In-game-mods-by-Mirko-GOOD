package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class WeatherModule extends ValueModule {
	public final TextSetting clear = add(new TextSetting("Clear Text", "&eClear").in("Text"));
	public final TextSetting rain = add(new TextSetting("Rain Text", "&9Raining").in("Text"));
	public final TextSetting thunder = add(new TextSetting("Thunder Text", "&5Thunderstorm").in("Text"));

	public WeatherModule() {
		super("weather", "Weather", Category.INFO, false, 0.4f, 0.58f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		var lvl = Game.mc().level;
		if (lvl == null) return "Clear";
		return lvl.isThundering() ? thunder.get() : lvl.isRaining() ? rain.get() : clear.get();
	}
}
