package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class NetherCoordsModule extends ValueModule {


	public NetherCoordsModule() {
		super("nethercoords", "Nether Coords", Category.INFO, false, 0.2f, 0.48f, "{value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); if (p == null || Game.mc().level == null) return "Nether: 15, -4";
		boolean nether = Game.mc().level.dimension() == Level.NETHER;
		int x = (int) Math.floor(p.getX()), z = (int) Math.floor(p.getZ());
		return nether ? "Overworld: " + (x * 8) + ", " + (z * 8) : "Nether: " + Math.floorDiv(x, 8) + ", " + Math.floorDiv(z, 8);
	}
}
