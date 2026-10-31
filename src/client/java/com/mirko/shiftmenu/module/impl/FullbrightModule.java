package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.KeySetting;
import com.mirko.shiftmenu.util.Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * See in the dark. Gives your client a hidden, client-side night vision effect
 * (no particles, no icon). Has its own toggle key.
 */
public class FullbrightModule extends HudModule {
	public final KeySetting toggleKey = add(new KeySetting("Toggle Key", -1).in("Fullbright"));

	private boolean applied;

	public FullbrightModule() {
		super("fullbright", "Fullbright", Category.MECHANIC, false, 0, 0);
	}

	@Override
	public boolean isDraggable() {
		return false;
	}

	public boolean isOurs(MobEffectInstance e) {
		return applied && e.getEffect().equals(MobEffects.NIGHT_VISION) && e.isInfiniteDuration() && !e.showIcon();
	}

	@Override
	public void tick(Minecraft mc) {
		if (toggleKey.pressed(mc.screen == null && mc.player != null && Keys.isKeyDown(mc, toggleKey.get()))) {
			toggle();
		}
		LocalPlayer p = mc.player;
		if (p == null) {
			applied = false;
			return;
		}
		if (isEnabled()) {
			if (!p.hasEffect(MobEffects.NIGHT_VISION)) {
				p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION, 0, false, false, false));
				applied = true;
			}
		} else if (applied) {
			MobEffectInstance e = p.getEffect(MobEffects.NIGHT_VISION);
			if (e != null && e.isInfiniteDuration() && !e.showIcon()) p.removeEffect(MobEffects.NIGHT_VISION);
			applied = false;
		}
	}

	@Override
	public int width(boolean preview) {
		return 24;
	}

	@Override
	public int height(boolean preview) {
		return 24;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		// sun icon as the card preview
		com.mirko.shiftmenu.util.Draw.rounded(g, 7, 7, 17, 17, 5, 0xFFFFDD55);
		int[][] rays = {{11, 1, 13, 5}, {11, 19, 13, 23}, {1, 11, 5, 13}, {19, 11, 23, 13}};
		for (int[] r : rays) g.fill(r[0], r[1], r[2], r[3], 0xFFFFDD55);
	}
}
