package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.TextModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Game;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/** Name, health bar and distance of whatever you're looking at (or last hit). */
public class TargetInfoModule extends TextModule {
	public final BoolSetting showHealth = add(new BoolSetting("Show Health", true).in("Target"));
	public final BoolSetting showDistance = add(new BoolSetting("Show Distance", true).in("Target"));
	public final BoolSetting healthBar = add(new BoolSetting("Health Bar", true).in("Target"));
	public final ColorSetting barColor = add(new ColorSetting("Bar Color", 0xFF55FF55).in("Target").showIf(this.healthBar::on));
	public final BoolSetting dynamicBar = add(new BoolSetting("Bar Color By Health", true).in("Target").showIf(this.healthBar::on));
	public final ColorSetting barBg = add(new ColorSetting("Bar Background", 0x80000000).in("Target").showIf(this.healthBar::on));
	public final NumberSetting holdTime = add(new NumberSetting("Keep After Looking Away (s)", 2, 0, 10, 0.5).in("Target"));

	private Entity last;
	private long lastSeen;

	public TargetInfoModule() {
		super("targetinfo", "Target Info", Category.COMBAT, false, 0.5f, 0.6f, true, false);
	}

	@Override
	protected String defaultAlign() {
		return "Left";
	}

	private LivingEntity target() {
		Entity e = Game.mc().crosshairPickEntity;
		long now = System.currentTimeMillis();
		if (e != null) {
			last = e;
			lastSeen = now;
		} else if (last != null && (now - lastSeen > holdTime.get() * 1000 || !last.isAlive())) {
			last = null;
		}
		return last instanceof LivingEntity le ? le : null;
	}

	@Override
	protected List<String> lines(boolean preview) {
		LivingEntity t = target();
		List<String> out = new ArrayList<>();
		if (t == null) {
			if (!preview) return out;
			out.add("Steve");
			if (showHealth.on()) out.add("&c❤ &f15.0 / 20");
			if (showDistance.on()) out.add("&73.2m away");
			return out;
		}
		out.add(t.getName().getString());
		if (showHealth.on()) out.add(String.format("&c❤ &f%.1f / %.0f", t.getHealth(), t.getMaxHealth()).replace('&', '§'));
		if (showDistance.on() && Game.player() != null) out.add(String.format("§7%.1fm away", Game.player().distanceTo(t)));
		return out;
	}

	@Override
	public int height(boolean preview) {
		int h = super.height(preview);
		return h > 0 && healthBar.on() ? h + 4 : h;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		super.render(g, preview);
		if (!healthBar.on()) return;
		int w = width(preview), h = height(preview);
		if (w <= 0) return;
		LivingEntity t = target();
		float frac = t == null ? 0.75f : Math.max(0, Math.min(1, t.getHealth() / Math.max(1, t.getMaxHealth())));
		int pad = background.on() || border.on() ? padX.get().intValue() : 0;
		int y = h - (background.on() || border.on() ? padY.get().intValue() : 0) - 3;
		int x1 = pad, x2 = w - pad;
		g.fill(x1, y, x2, y + 2, barBg.color());
		int c = dynamicBar.on() ? com.mirko.shiftmenu.util.Colors.hsv(frac / 3f, 0.8f, 1f) : barColor.color();
		g.fill(x1, y, x1 + Math.round((x2 - x1) * frac), y + 2, c);
	}
}
