package com.mirko.shiftmenu.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.HashMap;
import java.util.Map;

/**
 * Font + text-style handling. Styles are built from text-component JSON
 * ({"font": "...", "bold": true, ...}), which has been stable across Minecraft versions.
 */
public final class Fonts {
	/** Display names, in menu order. "Global" means: use the HUD font from the Settings page. */
	public static final String[] NAMES = {
			"Minecraft", "Inter", "Roboto", "Montserrat", "Poppins", "Ubuntu", "Nunito", "Rubik",
			"Lexend", "Comfortaa", "JetBrains Mono", "Unicode", "Enchanting", "Illager"
	};
	public static final String[] NAMES_WITH_GLOBAL;

	static {
		NAMES_WITH_GLOBAL = new String[NAMES.length + 1];
		NAMES_WITH_GLOBAL[0] = "Global";
		System.arraycopy(NAMES, 0, NAMES_WITH_GLOBAL, 1, NAMES.length);
	}

	/** Text style for one piece of text. */
	public record TextStyle(String font, boolean bold, boolean italic, boolean underline, boolean strike) {
		public static final TextStyle PLAIN = new TextStyle("Minecraft", false, false, false, false);

		public boolean isPlain() {
			return font.equals("Minecraft") && !bold && !italic && !underline && !strike;
		}
	}

	private static final Map<TextStyle, Style> CACHE = new HashMap<>();

	private Fonts() {}

	public static String fontId(String name) {
		return switch (name) {
			case "Inter" -> "shiftmenu:inter";
			case "Roboto" -> "shiftmenu:roboto";
			case "Montserrat" -> "shiftmenu:montserrat";
			case "Poppins" -> "shiftmenu:poppins";
			case "Ubuntu" -> "shiftmenu:ubuntu";
			case "Nunito" -> "shiftmenu:nunito";
			case "Rubik" -> "shiftmenu:rubik";
			case "Lexend" -> "shiftmenu:lexend";
			case "Comfortaa" -> "shiftmenu:comfortaa";
			case "JetBrains Mono" -> "shiftmenu:jetbrains_mono";
			case "Unicode" -> "minecraft:uniform";
			case "Enchanting" -> "minecraft:alt";
			case "Illager" -> "minecraft:illageralt";
			default -> "minecraft:default";
		};
	}

	public static Style style(TextStyle ts) {
		return CACHE.computeIfAbsent(ts, Fonts::build);
	}

	private static Style build(TextStyle ts) {
		try {
			JsonObject o = new JsonObject();
			o.addProperty("text", "");
			o.addProperty("font", fontId(ts.font()));
			if (ts.bold()) o.addProperty("bold", true);
			if (ts.italic()) o.addProperty("italic", true);
			if (ts.underline()) o.addProperty("underlined", true);
			if (ts.strike()) o.addProperty("strikethrough", true);
			Component c = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, o).getOrThrow();
			return c.getStyle();
		} catch (Throwable t) {
			System.err.println("[ShiftMenu] Couldn't build font style " + ts + ": " + t);
			return Style.EMPTY;
		}
	}

	public static Component text(String s, TextStyle ts) {
		MutableComponent c = Component.literal(s);
		return ts.isPlain() ? c : c.withStyle(style(ts));
	}

	public static int width(String s, TextStyle ts) {
		Font f = Minecraft.getInstance().font;
		return ts.isPlain() ? f.width(s) : f.width(text(s, ts));
	}

	public static void draw(GuiGraphics g, String s, int x, int y, int color, boolean shadow, TextStyle ts) {
		Font f = Minecraft.getInstance().font;
		if (ts.isPlain()) g.drawString(f, s, x, y, color, shadow);
		else g.drawString(f, text(s, ts), x, y, color, shadow);
	}
}
