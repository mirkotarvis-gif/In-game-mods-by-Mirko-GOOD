package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

/** Turn individual vanilla HUD parts on/off (hotbar, hearts, scoreboard, boss bar, ...). */
public class VanillaHudModule extends HudModule {
	private final Map<Identifier, BoolSetting> parts = new LinkedHashMap<>();

	public VanillaHudModule() {
		super("vanillahud", "Vanilla HUD", Category.HUD, false, 0, 0);
		part(VanillaHudElements.HOTBAR, "Hotbar");
		part(VanillaHudElements.HEALTH_BAR, "Health Bar");
		part(VanillaHudElements.ARMOR_BAR, "Armor Bar");
		part(VanillaHudElements.FOOD_BAR, "Food Bar");
		part(VanillaHudElements.AIR_BAR, "Air Bubbles");
		part(VanillaHudElements.MOUNT_HEALTH, "Mount Health");
		part(VanillaHudElements.INFO_BAR, "XP / Locator Bar");
		part(VanillaHudElements.EXPERIENCE_LEVEL, "XP Level Number");
		part(VanillaHudElements.HELD_ITEM_TOOLTIP, "Held Item Name");
		part(VanillaHudElements.BOSS_BAR, "Boss Bar");
		part(VanillaHudElements.SCOREBOARD, "Scoreboard");
		part(VanillaHudElements.OVERLAY_MESSAGE, "Action Bar Messages");
		part(VanillaHudElements.TITLE_AND_SUBTITLE, "Titles");
		part(VanillaHudElements.CHAT, "Chat");
		part(VanillaHudElements.PLAYER_LIST, "Tab List");
		part(VanillaHudElements.SUBTITLES, "Subtitles");
		part(VanillaHudElements.MISC_OVERLAYS, "Overlays (pumpkin, vignette...)");
	}

	private void part(Identifier id, String name) {
		parts.put(id, add(new BoolSetting("Show " + name, true).in("Vanilla HUD Parts")));
	}

	/** Called once at startup: wraps every vanilla part so it can be hidden. */
	public void install() {
		for (Map.Entry<Identifier, BoolSetting> e : parts.entrySet()) {
			BoolSetting show = e.getValue();
			HudElementRegistry.replaceElement(e.getKey(), vanilla -> (HudElement) (g, dt) -> {
				if (!isEnabled() || show.on()) vanilla.render(g, dt);
			});
		}
	}

	@Override
	public boolean isDraggable() {
		return false;
	}

	@Override
	public int width(boolean preview) {
		return 60;
	}

	@Override
	public int height(boolean preview) {
		return 24;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		// mini hotbar as the card preview
		for (int i = 0; i < 6; i++) {
			int x = i * 10;
			g.fill(x, 12, x + 9, 21, 0x99000000);
			com.mirko.shiftmenu.util.Draw.outline(g, x, 12, x + 9, 21, 0xFF888888);
		}
		for (int i = 0; i < 5; i++) g.fill(i * 7, 4, i * 7 + 5, 9, 0xFFFF4444);
	}
}
