package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class GameModeModule extends ValueModule {


	public GameModeModule() {
		super("gamemode", "Game Mode", Category.INFO, false, 0.2f, 0.58f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		var gm = Game.mc().gameMode;
		return gm == null ? "Survival" : Game.capitalize(gm.getPlayerMode().name());
	}
}
