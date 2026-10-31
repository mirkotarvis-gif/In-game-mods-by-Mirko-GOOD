package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ChunkModule extends ValueModule {
	public final BoolSetting inChunk = add(new BoolSetting("Show Position In Chunk", false).in("Text"));

	public ChunkModule() {
		super("chunk", "Chunk Position", Category.INFO, false, 0.4f, 0.48f, "Chunk: {value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); int x = p == null ? 0 : (int) Math.floor(p.getX()), z = p == null ? 0 : (int) Math.floor(p.getZ());
		String s = (x >> 4) + ", " + (z >> 4);
		return inChunk.on() ? s + " &7(" + (x & 15) + ", " + (z & 15) + ")" : s;
	}
}
