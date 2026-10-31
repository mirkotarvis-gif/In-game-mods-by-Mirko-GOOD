package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Draw;
import com.mirko.shiftmenu.util.Fonts;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.client.gui.GuiGraphics;

/** A horizontal compass strip (like in battle-royale games). */
public class CompassModule extends HudModule {
	private static final String[] LABELS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

	public final NumberSetting stripW = add(new NumberSetting("Width", 160, 60, 360, 4).in("Compass"));
	public final NumberSetting range = add(new NumberSetting("Visible Degrees", 120, 40, 240, 5).in("Compass"));
	public final BoolSetting showDegrees = add(new BoolSetting("Show Degrees", true).in("Compass"));
	public final BoolSetting intercardinal = add(new BoolSetting("Show NE/SE/SW/NW", true).in("Compass"));
	public final ColorSetting textColor = add(new ColorSetting("Text Color", 0xFFFFFFFF).in("Colors"));
	public final ColorSetting northColor = add(new ColorSetting("North Color", 0xFFFF5555).in("Colors"));
	public final ColorSetting tickColor = add(new ColorSetting("Tick Color", 0xAAFFFFFF).in("Colors"));
	public final ColorSetting markerColor = add(new ColorSetting("Center Marker", 0xFFFFFF55).in("Colors"));
	public final ColorSetting bg = add(new ColorSetting("Background", 0x6F000000).in("Colors"));
	public final NumberSetting radius = add(new NumberSetting("Corner Radius", 0, 0, 8, 1).in("Colors"));

	public CompassModule() {
		super("compass", "Compass", Category.INFO, false, 0.5f, 0.02f);
	}

	@Override
	public int width(boolean preview) {
		return stripW.get().intValue();
	}

	@Override
	public int height(boolean preview) {
		return showDegrees.on() ? 30 : 20;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		int w = width(preview);
		Draw.rounded(g, 0, 0, w, 20, radius.get().intValue(), bg.color());
		float yaw = Game.player() == null ? 0 : Game.player().getYRot();
		float heading = ((yaw + 180f) % 360f + 360f) % 360f; // 0 = north
		float half = range.getF() / 2f;
		float pxPerDeg = w / range.getF();
		Fonts.TextStyle ts = Fonts.TextStyle.PLAIN;
		for (int deg = 0; deg < 360; deg += 15) {
			float diff = deg - heading;
			while (diff > 180) diff -= 360;
			while (diff < -180) diff += 360;
			if (Math.abs(diff) > half) continue;
			int x = Math.round(w / 2f + diff * pxPerDeg);
			if (deg % 45 == 0) {
				String l = LABELS[deg / 45];
				if (l.length() == 2 && !intercardinal.on()) {
					g.fill(x, 13, x + 1, 19, tickColor.color());
					continue;
				}
				int c = deg == 0 ? northColor.color() : textColor.color();
				Fonts.draw(g, l, x - Fonts.width(l, ts) / 2, 3, c, true, ts);
				g.fill(x, 14, x + 1, 19, tickColor.color());
			} else {
				g.fill(x, 16, x + 1, 19, tickColor.color());
			}
		}
		g.fill(w / 2, 0, w / 2 + 1, 20, markerColor.color());
		if (showDegrees.on()) {
			String d = Math.round(heading) % 360 + "°";
			Fonts.draw(g, d, (w - Fonts.width(d, ts)) / 2, 21, textColor.color(), true, ts);
		}
	}
}
