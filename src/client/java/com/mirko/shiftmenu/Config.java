package com.mirko.shiftmenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.ModuleManager;
import com.mirko.shiftmenu.module.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
	/** GLFW key that opens the menu. Default: Left Shift. */
	public static int menuKey = GLFW.GLFW_KEY_LEFT_SHIFT;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private Config() {}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("shiftmenu.json");
	}

	public static void load() {
		Path p = path();
		if (!Files.exists(p)) {
			save();
			return;
		}
		try (Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
			JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
			if (root.has("menuKey")) menuKey = root.get("menuKey").getAsInt();
			if (root.has("modules")) {
				JsonObject mods = root.getAsJsonObject("modules");
				for (HudModule m : ModuleManager.ALL) {
					if (!mods.has(m.id)) continue;
					JsonObject o = mods.getAsJsonObject(m.id);
					if (o.has("enabled")) m.setEnabledSilently(o.get("enabled").getAsBoolean());
					if (o.has("x")) m.rx = o.get("x").getAsFloat();
					if (o.has("y")) m.ry = o.get("y").getAsFloat();
					if (o.has("settings")) {
						JsonObject s = o.getAsJsonObject("settings");
						for (Setting<?> setting : m.settings) {
							JsonElement e = s.get(setting.key());
							if (e != null) {
								try {
									setting.fromJson(e);
								} catch (Exception ignored) {
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[ShiftMenu] Failed to read config, using defaults: " + e);
		}
	}

	public static void save() {
		JsonObject root = new JsonObject();
		root.addProperty("menuKey", menuKey);
		JsonObject mods = new JsonObject();
		for (HudModule m : ModuleManager.ALL) {
			JsonObject o = new JsonObject();
			o.addProperty("enabled", m.isEnabled());
			o.addProperty("x", m.rx);
			o.addProperty("y", m.ry);
			JsonObject s = new JsonObject();
			for (Setting<?> setting : m.settings) {
				if (setting instanceof com.mirko.shiftmenu.module.setting.InfoSetting) continue;
				s.add(setting.key(), setting.toJson());
			}
			o.add("settings", s);
			mods.add(m.id, o);
		}
		root.add("modules", mods);
		try {
			Files.createDirectories(path().getParent());
			try (Writer w = Files.newBufferedWriter(path(), StandardCharsets.UTF_8)) {
				GSON.toJson(root, w);
			}
		} catch (Exception e) {
			System.err.println("[ShiftMenu] Failed to save config: " + e);
		}
	}
}
