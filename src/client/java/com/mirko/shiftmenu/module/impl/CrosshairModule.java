package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Custom crosshair. When enabled it replaces the vanilla crosshair (see ShiftMenuClient).
 * Always drawn in the screen center, so it isn't draggable.
 */
public class CrosshairModule extends HudModule {
	public final ModeSetting style = add(new ModeSetting("Style", "Cross", "Cross", "Cross + Dot", "Dot", "Circle", "Circle + Dot", "T-Shape", "Square", "X").in("Shape"));
	public final NumberSetting length = add(new NumberSetting("Length", 5, 1, 20, 1).in("Shape"));
	public final NumberSetting gap = add(new NumberSetting("Gap", 2, 0, 12, 1).in("Shape"));
	public final NumberSetting thickness = add(new NumberSetting("Thickness", 1, 1, 6, 1).in("Shape"));
	public final NumberSetting dotSize = add(new NumberSetting("Dot Size", 2, 1, 8, 1).in("Shape"));
	public final BoolSetting dynamicGap = add(new BoolSetting("Spread When Moving", false).in("Shape"));

	public final ColorSetting color = add(new ColorSetting("Color", 0xFFFFFFFF).in("Colors"));
	public final BoolSetting outline = add(new BoolSetting("Outline", true).in("Colors"));
	public final ColorSetting outlineColor = add(new ColorSetting("Outline Color", 0xC0000000).in("Colors").showIf(this.outline::on));
	public final BoolSetting targetColor = add(new BoolSetting("Color On Target", false).in("Colors"));
	public final ColorSetting targetColorValue = add(new ColorSetting("Target Color", 0xFFFF5555).in("Colors").showIf(this.targetColor::on));

	public final BoolSetting thirdPerson = add(new BoolSetting("Show In Third Person", false).in("Visibility"));
	public final BoolSetting hideInScreens = add(new BoolSetting("Hide In Menus", true).in("Visibility"));

	private float spread;

	public CrosshairModule() {
		super("crosshair", "Crosshair", Category.HUD, false, 0.5f, 0.5f);
	}

	@Override
	public boolean isDraggable() {
		return false;
	}

	private int reach() {
		return gap.get().intValue() + length.get().intValue() + (int) Math.ceil(spread) + 2;
	}

	@Override
	public int width(boolean preview) {
		return reach() * 2 + thickness.get().intValue();
	}

	@Override
	public int height(boolean preview) {
		return width(preview);
	}

	/** Should the custom crosshair be drawn this frame? */
	public boolean shouldDraw(Minecraft mc) {
		if (mc.options.hideGui || mc.player == null) return false;
		if (hideInScreens.on() && mc.screen != null) return false;
		return thirdPerson.on() || mc.options.getCameraType().isFirstPerson();
	}

	/** Draws centered on the screen. */
	public void drawCentered(GuiGraphics g) {
		Minecraft mc = Minecraft.getInstance();
		if (dynamicGap.on() && mc.player != null) {
			double v = mc.player.getDeltaMovement().horizontalDistance();
			float target = (float) Math.min(6.0, v * 20.0);
			spread += (target - spread) * 0.2f;
		} else {
			spread = 0;
		}
		int w = width(false);
		float s = effectiveScale();
		g.pose().pushMatrix();
		g.pose().translate(g.guiWidth() / 2f - w * s / 2f, g.guiHeight() / 2f - w * s / 2f);
		g.pose().scale(s, s);
		render(g, false);
		g.pose().popMatrix();
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		Minecraft mc = Minecraft.getInstance();
		int c = color.color();
		if (targetColor.on() && !preview && mc.crosshairPickEntity != null) c = targetColorValue.color();
		int oc = outlineColor.color();
		boolean ol = outline.on();

		int t = thickness.get().intValue();
		int len = length.get().intValue();
		int gp = gap.get().intValue() + (int) spread;
		int ds = dotSize.get().intValue();
		int full = width(preview);
		int cx = full / 2 - t / 2; // left/top of the center line
		int cy = cx;

		String st = style.get();
		boolean lines = st.startsWith("Cross") || st.equals("T-Shape");
		boolean dot = st.equals("Dot") || st.endsWith("+ Dot");

		if (lines) {
			// left, right, bottom, (top unless T)
			bar(g, cx - gp - len, cy, len, t, c, oc, ol);
			bar(g, cx + t + gp, cy, len, t, c, oc, ol);
			bar(g, cx, cy + t + gp, t, len, c, oc, ol);
			if (!st.equals("T-Shape")) bar(g, cx, cy - gp - len, t, len, c, oc, ol);
		}
		if (st.startsWith("Circle")) {
			int r = gp + len / 2 + 1;
			ring(g, full / 2, full / 2, r, t, c, oc, ol);
		}
		if (st.equals("Square")) {
			int r = gp + 1;
			int x1 = full / 2 - r - t, y1 = full / 2 - r - t, x2 = full / 2 + r + t, y2 = full / 2 + r + t;
			if (ol) Draw.outline(g, x1 - 1, y1 - 1, x2 + 1, y2 + 1, oc);
			Draw.roundedBorder(g, x1, y1, x2, y2, 0, t, c);
		}
		if (st.equals("X")) {
			int r = gp + len;
			for (int i = gp; i < r; i++) {
				int m = full / 2;
				for (int[] d : new int[][]{{1, 1}, {-1, 1}, {1, -1}, {-1, -1}}) {
					int px = m + d[0] * i - (d[0] < 0 ? t : 0), py = m + d[1] * i - (d[1] < 0 ? t : 0);
					if (ol) g.fill(px - 1, py - 1, px + t + 1, py + t + 1, oc);
					g.fill(px, py, px + t, py + t, c);
				}
			}
		}
		if (dot) {
			int dx = full / 2 - ds / 2, dy = full / 2 - ds / 2;
			bar(g, dx, dy, ds, ds, c, oc, ol);
		}
	}

	private static void bar(GuiGraphics g, int x, int y, int w, int h, int c, int oc, boolean ol) {
		if (ol) g.fill(x - 1, y - 1, x + w + 1, y + h + 1, oc);
		g.fill(x, y, x + w, y + h, c);
	}

	private static void ring(GuiGraphics g, int cx, int cy, int r, int t, int c, int oc, boolean ol) {
		if (ol) Draw.roundedBorder(g, cx - r - t - 1, cy - r - t - 1, cx + r + t + 1, cy + r + t + 1, r + t + 1, t + 2, oc);
		Draw.roundedBorder(g, cx - r - t, cy - r - t, cx + r + t, cy + r + t, r + t, t, c);
	}
}
