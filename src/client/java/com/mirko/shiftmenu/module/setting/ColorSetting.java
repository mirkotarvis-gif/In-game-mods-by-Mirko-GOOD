package com.mirko.shiftmenu.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mirko.shiftmenu.util.Colors;

/**
 * An ARGB color stored as hue / saturation / brightness / alpha (so the hue isn't lost at
 * zero saturation), plus an optional "chroma" rainbow mode.
 */
public class ColorSetting extends Setting<Integer> {
	private final boolean defaultChroma;
	public float hue, sat, bri, alpha;
	public boolean chroma;
	/** Chroma cycle speed: lower = faster (ms per full cycle). */
	public static final long CHROMA_PERIOD = 3000L;

	public ColorSetting(String name, int argb) {
		this(name, argb, false);
	}

	public ColorSetting(String name, int argb, boolean chroma) {
		super(name, argb);
		this.defaultChroma = chroma;
		setArgb(argb);
		this.chroma = chroma;
	}

	public void setArgb(int argb) {
		float[] hsb = Colors.rgbToHsb((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
		// keep the current hue when the new color has no hue information
		if (hsb[1] > 0.001f && hsb[2] > 0.001f) hue = hsb[0];
		sat = hsb[1];
		bri = hsb[2];
		alpha = ((argb >>> 24) & 0xFF) / 255f;
		value = argb;
	}

	public void setHsba(float h, float s, float b, float a) {
		hue = clamp(h);
		sat = clamp(s);
		bri = clamp(b);
		alpha = clamp(a);
		value = rawArgb();
	}

	private static float clamp(float f) {
		return Math.max(0f, Math.min(1f, f));
	}

	private int rawArgb() {
		int rgb = Colors.hsv(hue, sat, bri) & 0xFFFFFF;
		return (Math.round(alpha * 255f) << 24) | rgb;
	}

	/** The color to draw with right now. offset shifts the chroma phase (per line / per key). */
	public int color(int offset) {
		if (chroma) {
			float h = ((System.currentTimeMillis() + offset * 40L) % CHROMA_PERIOD) / (float) CHROMA_PERIOD;
			int rgb = Colors.hsv(h, Math.max(0.5f, sat), Math.max(0.6f, bri)) & 0xFFFFFF;
			return (Math.round(alpha * 255f) << 24) | rgb;
		}
		return value;
	}

	public int color() {
		return color(0);
	}

	@Override
	public void set(Integer v) {
		setArgb(v);
	}

	@Override
	public void reset() {
		setArgb(defaultValue);
		chroma = defaultChroma;
	}

	public String hex() {
		return String.format("#%08X", value);
	}

	/** Accepts #RGB, #RRGGBB or #AARRGGBB. Returns false if it couldn't parse. */
	public boolean setHex(String s) {
		if (s == null) return false;
		s = s.trim();
		if (s.startsWith("#")) s = s.substring(1);
		try {
			if (s.length() == 3) {
				s = "" + s.charAt(0) + s.charAt(0) + s.charAt(1) + s.charAt(1) + s.charAt(2) + s.charAt(2);
			}
			if (s.length() == 6) {
				int rgb = Integer.parseInt(s, 16);
				setArgb((Math.round(alpha * 255f) << 24) | rgb);
				return true;
			}
			if (s.length() == 8) {
				setArgb((int) Long.parseLong(s, 16));
				return true;
			}
		} catch (NumberFormatException ignored) {
		}
		return false;
	}

	@Override
	public JsonElement toJson() {
		JsonObject o = new JsonObject();
		o.addProperty("color", hex());
		o.addProperty("chroma", chroma);
		return o;
	}

	@Override
	public void fromJson(JsonElement e) {
		JsonObject o = e.getAsJsonObject();
		if (o.has("color")) setHex(o.get("color").getAsString());
		if (o.has("chroma")) chroma = o.get("chroma").getAsBoolean();
	}
}
