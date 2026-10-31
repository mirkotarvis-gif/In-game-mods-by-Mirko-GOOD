package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.TextModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.Placeholders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesModule extends TextModule {
	public final ModeSetting layout = add(new ModeSetting("Layout", "Vertical", "Vertical", "Horizontal").in("Coordinates"));
	public final NumberSetting decimals = add(new NumberSetting("Decimals", 0, 0, 3, 1).in("Coordinates"));
	public final BoolSetting showDirection = add(new BoolSetting("Show Direction", true).in("Coordinates"));
	public final TextSetting xLabel = add(new TextSetting("X Label", "&cX: &f").in("Coordinates").showIf(() -> this.layout.is("Vertical")));
	public final TextSetting yLabel = add(new TextSetting("Y Label", "&aY: &f").in("Coordinates").showIf(() -> this.layout.is("Vertical")));
	public final TextSetting zLabel = add(new TextSetting("Z Label", "&9Z: &f").in("Coordinates").showIf(() -> this.layout.is("Vertical")));
	public final TextSetting horizontalFormat = add(new TextSetting("Horizontal Format", "XYZ: {x}, {y}, {z}").in("Coordinates").showIf(() -> this.layout.is("Horizontal")));
	public final TextSetting directionFormat = add(new TextSetting("Direction Format", "&7Facing: &f{facing} ({axis})").in("Coordinates").showIf(this.showDirection::on));

	public CoordinatesModule() {
		super("coordinates", "Coordinates", Category.INFO, false, 0.004f, 0.62f, true, false);
	}

	@Override
	protected String defaultAlign() {
		return "Left";
	}

	private String num(double v) {
		int d = decimals.get().intValue();
		if (d == 0) return String.valueOf((int) Math.floor(v));
		return String.format("%." + d + "f", v);
	}

	@Override
	protected List<String> lines(boolean preview) {
		LocalPlayer p = Minecraft.getInstance().player;
		double x = p == null ? 120 : p.getX(), y = p == null ? 64 : p.getY(), z = p == null ? -33 : p.getZ();
		List<String> out = new ArrayList<>();
		if (layout.is("Horizontal")) {
			out.add(Placeholders.apply(horizontalFormat.get().replace("{x}", num(x)).replace("{y}", num(y)).replace("{z}", num(z))));
		} else {
			out.add(Placeholders.apply(xLabel.get() + num(x)));
			out.add(Placeholders.apply(yLabel.get() + num(y)));
			out.add(Placeholders.apply(zLabel.get() + num(z)));
		}
		if (showDirection.on()) out.add(Placeholders.apply(directionFormat.get()));
		return out;
	}
}
