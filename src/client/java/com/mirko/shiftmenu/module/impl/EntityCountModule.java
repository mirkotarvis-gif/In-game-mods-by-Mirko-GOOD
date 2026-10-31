package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class EntityCountModule extends ValueModule {


	public EntityCountModule() {
		super("entities", "Entity Count", Category.INFO, false, 0.2f, 0.78f, "Entities: {value}");
	}

	@Override
	protected String value(boolean preview) {
		var lvl = Game.mc().level;
		if (lvl == null) return "0";
		int n = 0;
		for (var e : lvl.entitiesForRendering()) n++;
		return String.valueOf(n);
	}
}
