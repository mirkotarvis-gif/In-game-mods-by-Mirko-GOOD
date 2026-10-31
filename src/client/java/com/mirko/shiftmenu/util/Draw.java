package com.mirko.shiftmenu.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class Draw {
	private Draw() {}

	/** Filled rectangle with rounded corners (pixel-accurate, no overlapping fills). */
	public static void rounded(GuiGraphics g, int x1, int y1, int x2, int y2, int r, int color) {
		int w = x2 - x1, h = y2 - y1;
		if (w <= 0 || h <= 0) return;
		r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
		if (r == 0) {
			g.fill(x1, y1, x2, y2, color);
			return;
		}
		g.fill(x1 + r, y1, x2 - r, y2, color);       // center column
		g.fill(x1, y1 + r, x1 + r, y2 - r, color);   // left side
		g.fill(x2 - r, y1 + r, x2, y2 - r, color);   // right side
		for (int i = 0; i < r; i++) {
			double dy = r - i - 0.5;
			int inset = (int) Math.round(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
			g.fill(x1 + inset, y1 + i, x1 + r, y1 + i + 1, color);
			g.fill(x2 - r, y1 + i, x2 - inset, y1 + i + 1, color);
			g.fill(x1 + inset, y2 - i - 1, x1 + r, y2 - i, color);
			g.fill(x2 - r, y2 - i - 1, x2 - inset, y2 - i, color);
		}
	}

	/** Rounded ring of thickness t (no overlapping fills, so translucent colors stay even). */
	public static void roundedBorder(GuiGraphics g, int x1, int y1, int x2, int y2, int r, int t, int color) {
		int w = x2 - x1, h = y2 - y1;
		if (w <= 0 || h <= 0 || t <= 0) return;
		t = Math.min(t, Math.min(w, h) / 2);
		r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
		if (r <= t) {
			// square-ish corners
			g.fill(x1, y1, x2, y1 + t, color);
			g.fill(x1, y2 - t, x2, y2, color);
			g.fill(x1, y1 + t, x1 + t, y2 - t, color);
			g.fill(x2 - t, y1 + t, x2, y2 - t, color);
			return;
		}
		g.fill(x1 + r, y1, x2 - r, y1 + t, color);
		g.fill(x1 + r, y2 - t, x2 - r, y2, color);
		g.fill(x1, y1 + r, x1 + t, y2 - r, color);
		g.fill(x2 - t, y1 + r, x2, y2 - r, color);
		int ri = r - t;
		for (int i = 0; i < r; i++) {
			double dy = r - i - 0.5;
			int outer = (int) Math.round(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
			int innerEnd = dy >= ri ? r : (int) Math.round(r - Math.sqrt(Math.max(0, ri * ri - dy * dy)));
			if (innerEnd <= outer) continue;
			g.fill(x1 + outer, y1 + i, x1 + innerEnd, y1 + i + 1, color);
			g.fill(x2 - innerEnd, y1 + i, x2 - outer, y1 + i + 1, color);
			g.fill(x1 + outer, y2 - i - 1, x1 + innerEnd, y2 - i, color);
			g.fill(x2 - innerEnd, y2 - i - 1, x2 - outer, y2 - i, color);
		}
	}

	/** Background + optional border, the shared "box" look every module uses. */
	public static void box(GuiGraphics g, int x1, int y1, int x2, int y2, int radius, boolean bg, int bgColor,
						   boolean border, int borderThickness, int borderColor) {
		int t = border ? borderThickness : 0;
		if (bg) {
			rounded(g, x1 + t, y1 + t, x2 - t, y2 - t, Math.max(0, radius - t), bgColor);
		}
		if (border) {
			roundedBorder(g, x1, y1, x2, y2, radius, t, borderColor);
		}
	}

	public static void outline(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
		g.fill(x1, y1, x2, y1 + 1, color);
		g.fill(x1, y2 - 1, x2, y2, color);
		g.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
		g.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
	}

	public static void dashed(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
		int dash = 3, gap = 2;
		for (int x = x1; x < x2; x += dash + gap) {
			int e = Math.min(x + dash, x2);
			g.fill(x, y1, e, y1 + 1, color);
			g.fill(x, y2 - 1, e, y2, color);
		}
		for (int y = y1 + 1; y < y2 - 1; y += dash + gap) {
			int e = Math.min(y + dash, y2 - 1);
			g.fill(x1, y, x1 + 1, e, color);
			g.fill(x2 - 1, y, x2, e, color);
		}
	}

	private static Fonts.TextStyle menu() {
		return com.mirko.shiftmenu.module.ModuleManager.SETTINGS.menuStyle();
	}

	/** Menu text (uses the menu font from the Settings page). */
	public static void text(GuiGraphics g, String s, int x, int y, int color) {
		Fonts.draw(g, s, x, y, color, false, menu());
	}

	public static void centered(GuiGraphics g, String s, int cx, int y, int color) {
		int w = width(s);
		Fonts.draw(g, s, cx - w / 2, y, color, false, menu());
	}

	public static int width(String s) {
		return Fonts.width(s, menu());
	}

	public static boolean inside(double mx, double my, int x, int y, int w, int h) {
		return mx >= x && my >= y && mx < x + w && my < y + h;
	}

	/** iOS-style switch, 18x10. */
	public static void toggleSwitch(GuiGraphics g, int x, int y, boolean on) {
		rounded(g, x, y, x + 18, y + 10, 5, on ? 0xFF3BB273 : 0xFF4A4A52);
		int kx = on ? x + 9 : x + 1;
		rounded(g, kx, y + 1, kx + 8, y + 9, 4, 0xFFFFFFFF);
	}
}
