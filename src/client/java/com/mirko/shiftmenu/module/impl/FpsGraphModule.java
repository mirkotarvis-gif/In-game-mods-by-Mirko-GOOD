package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Colors;
import com.mirko.shiftmenu.util.Draw;
import com.mirko.shiftmenu.util.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** A small live graph of FPS (or CPS / ping) over time. */
public class FpsGraphModule extends HudModule {
	private static final int MAX = 200;

	public final ModeSetting source = add(new ModeSetting("Graph Of", "FPS", "FPS", "CPS", "Ping").in("Graph"));
	public final NumberSetting graphW = add(new NumberSetting("Width", 90, 40, 200, 2).in("Graph"));
	public final NumberSetting graphH = add(new NumberSetting("Height", 30, 12, 80, 1).in("Graph"));
	public final NumberSetting interval = add(new NumberSetting("Sample Every (ms)", 100, 20, 1000, 10).in("Graph"));
	public final ModeSetting style = add(new ModeSetting("Style", "Bars", "Bars", "Line").in("Graph"));
	public final BoolSetting showLabel = add(new BoolSetting("Show Current Value", true).in("Graph"));
	public final ColorSetting barColor = add(new ColorSetting("Graph Color", 0xFF55FF55).in("Colors"));
	public final BoolSetting colorByValue = add(new BoolSetting("Color By Value", true).in("Colors"));
	public final ColorSetting bg = add(new ColorSetting("Background", 0x6F000000).in("Colors"));
	public final ColorSetting labelColor = add(new ColorSetting("Label Color", 0xFFFFFFFF).in("Colors"));
	public final NumberSetting radius = add(new NumberSetting("Corner Radius", 0, 0, 8, 1).in("Colors"));

	private final int[] samples = new int[MAX];
	private int head, count;
	private long lastSample;

	public FpsGraphModule() {
		super("fpsgraph", "Performance Graph", Category.INFO, false, 0.8f, 0.85f);
	}

	private int current() {
		return switch (source.get()) {
			case "CPS" -> com.mirko.shiftmenu.util.Keys.leftCps();
			case "Ping" -> com.mirko.shiftmenu.util.Placeholders.ping();
			default -> Minecraft.getInstance().getFps();
		};
	}

	private void sample() {
		long now = System.currentTimeMillis();
		if (now - lastSample < interval.get()) return;
		lastSample = now;
		samples[head] = current();
		head = (head + 1) % MAX;
		count = Math.min(MAX, count + 1);
	}

	@Override
	public int width(boolean preview) {
		return graphW.get().intValue();
	}

	@Override
	public int height(boolean preview) {
		return graphH.get().intValue() + (showLabel.on() ? 10 : 0);
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		sample();
		int w = width(preview), gh = graphH.get().intValue(), top = showLabel.on() ? 10 : 0;
		Draw.rounded(g, 0, 0, w, height(preview), radius.get().intValue(), bg.color());
		int n = Math.min(count, w / 2);
		int max = 1;
		for (int i = 0; i < n; i++) max = Math.max(max, samples[(head - 1 - i + MAX) % MAX]);
		int prevY = -1;
		for (int i = 0; i < n; i++) {
			int v = samples[(head - 1 - i + MAX) % MAX];
			int bh = Math.max(1, Math.round((gh - 2) * (v / (float) max)));
			int x = w - 2 - i * 2;
			int c = colorByValue.on() ? Colors.hsv(Math.min(1f, v / (float) Math.max(1, max)) / 3f, 0.8f, 1f) : barColor.color();
			if (source.is("Ping") && colorByValue.on()) c = Colors.hsv((1f - Math.min(1f, v / 300f)) / 3f, 0.8f, 1f);
			int y = top + gh - 1 - bh;
			if (style.is("Bars")) {
				g.fill(x - 1, y, x + 1, top + gh - 1, c);
			} else {
				g.fill(x - 1, y, x + 1, y + 1, c);
				if (prevY >= 0) g.fill(x + 1, Math.min(prevY, y), x + 2, Math.max(prevY, y) + 1, c);
				prevY = y;
			}
		}
		if (showLabel.on()) {
			String label = current() + " " + source.get();
			Fonts.draw(g, label, 3, 2, labelColor.color(), true, Fonts.TextStyle.PLAIN);
		}
	}
}
