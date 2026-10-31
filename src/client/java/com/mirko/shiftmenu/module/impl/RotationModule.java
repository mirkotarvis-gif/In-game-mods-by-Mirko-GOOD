package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.*;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class RotationModule extends ValueModule {
	public final NumberSetting decimals = add(new NumberSetting("Decimals", 1, 0, 3, 1).in("Text"));
	public final BoolSetting wrapYaw = add(new BoolSetting("Wrap Yaw (-180..180)", true).in("Text"));

	public RotationModule() {
		super("rotation", "Rotation", Category.INFO, false, 0.8f, 0.38f, "Yaw/Pitch: {value}");
	}

	@Override
	protected String value(boolean preview) {
		var p = Game.player(); float yaw = p == null ? 0 : p.getYRot(), pitch = p == null ? 0 : p.getXRot();
		if (wrapYaw.on()) { yaw = yaw % 360f; if (yaw > 180f) yaw -= 360f; if (yaw < -180f) yaw += 360f; }
		int d = decimals.get().intValue();
		return Game.fmt(yaw, d) + " / " + Game.fmt(pitch, d);
	}
}
