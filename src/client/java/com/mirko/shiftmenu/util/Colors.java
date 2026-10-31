package com.mirko.shiftmenu.util;

public final class Colors {
	public static final String[] NAMES = {"White", "Light Gray", "Aqua", "Green", "Gold", "Red", "Pink", "Chroma"};

	private Colors() {}

	public static int resolve(String name, int offset) {
		return switch (name) {
			case "Light Gray" -> 0xFFC8C8C8;
			case "Aqua" -> 0xFF55FFFF;
			case "Green" -> 0xFF55FF55;
			case "Gold" -> 0xFFFFAA00;
			case "Red" -> 0xFFFF5555;
			case "Pink" -> 0xFFFF77C8;
			case "Chroma" -> chroma(offset);
			default -> 0xFFFFFFFF;
		};
	}

	public static int chroma(int offset) {
		float hue = ((System.currentTimeMillis() + offset * 40L) % 3000L) / 3000f;
		return hsv(hue, 0.75f, 1f);
	}

	public static int hsv(float h, float s, float v) {
		float r, g, b;
		int i = (int) Math.floor(h * 6f);
		float f = h * 6f - i;
		float p = v * (1 - s);
		float q = v * (1 - f * s);
		float t = v * (1 - (1 - f) * s);
		switch (((i % 6) + 6) % 6) {
			case 0 -> { r = v; g = t; b = p; }
			case 1 -> { r = q; g = v; b = p; }
			case 2 -> { r = p; g = v; b = t; }
			case 3 -> { r = p; g = q; b = v; }
			case 4 -> { r = t; g = p; b = v; }
			default -> { r = v; g = p; b = q; }
		}
		return 0xFF000000 | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
	}

	public static float[] rgbToHsb(int r, int g, int b) {
		float rf = r / 255f, gf = g / 255f, bf = b / 255f;
		float max = Math.max(rf, Math.max(gf, bf));
		float min = Math.min(rf, Math.min(gf, bf));
		float d = max - min;
		float h = 0f;
		if (d > 0f) {
			if (max == rf) h = ((gf - bf) / d) % 6f;
			else if (max == gf) h = (bf - rf) / d + 2f;
			else h = (rf - gf) / d + 4f;
			h /= 6f;
			if (h < 0f) h += 1f;
		}
		float s = max == 0f ? 0f : d / max;
		return new float[]{h, s, max};
	}

	/** Linear blend between two ARGB colors. */
	public static int lerp(int a, int b, float t) {
		t = Math.max(0, Math.min(1, t));
		int aa = (a >>> 24) & 0xFF, ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
		int ba = (b >>> 24) & 0xFF, br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
		int oa = Math.round(aa + (ba - aa) * t);
		int or = Math.round(ar + (br - ar) * t);
		int og = Math.round(ag + (bg - ag) * t);
		int ob = Math.round(ab + (bb - ab) * t);
		return (oa << 24) | (or << 16) | (og << 8) | ob;
	}

	public static int withAlpha(int color, int alpha) {
		return (alpha << 24) | (color & 0xFFFFFF);
	}
}
