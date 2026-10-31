package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.TextModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.Placeholders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Active potion effects as text, e.g. "Speed II 1:23". Can hide the vanilla effect icons. */
public class PotionEffectsModule extends TextModule {
	private static final String[] ROMAN = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

	public final TextSetting format = add(new TextSetting("Format", "{name} {level} &7{duration}").in("Effects"));
	public final BoolSetting hideLevelOne = add(new BoolSetting("Hide Level I", true).in("Effects"));
	public final ModeSetting sort = add(new ModeSetting("Sort", "Duration", "Duration", "Name", "None").in("Effects"));
	public final NumberSetting blinkUnder = add(new NumberSetting("Blink Under (s)", 10, 0, 60, 1).in("Effects"));
	public final BoolSetting hideVanilla = add(new BoolSetting("Hide Vanilla Effect Icons", false).in("Effects"));

	public PotionEffectsModule() {
		super("potions", "Potion Effects", Category.HUD, false, 0.99f, 0.4f, true, false);
	}

	@Override
	protected String defaultAlign() {
		return "Left";
	}

	private static String duration(MobEffectInstance e) {
		if (e.isInfiniteDuration()) return "**:**";
		int s = e.getDuration() / 20;
		return String.format("%d:%02d", s / 60, s % 60);
	}

	private String line(String name, int amplifier, String dur) {
		String level = amplifier == 0 && hideLevelOne.on() ? "" : (amplifier < ROMAN.length ? ROMAN[amplifier] : String.valueOf(amplifier + 1));
		String s = format.get().replace("{name}", name).replace("{level}", level).replace("{duration}", dur);
		return Placeholders.apply(s.replace("  ", " ").trim());
	}

	@Override
	protected List<String> lines(boolean preview) {
		LocalPlayer p = Minecraft.getInstance().player;
		List<String> out = new ArrayList<>();
		if (p != null) {
			List<MobEffectInstance> effects = new ArrayList<>(p.getActiveEffects());
			effects.removeIf(e -> com.mirko.shiftmenu.module.ModuleManager.FULLBRIGHT.isOurs(e));
			if (sort.is("Duration")) effects.sort(Comparator.comparingInt(MobEffectInstance::getDuration));
			else if (sort.is("Name")) effects.sort(Comparator.comparing(e -> e.getEffect().value().getDisplayName().getString()));
			boolean blinkOff = (System.currentTimeMillis() / 400) % 2 == 0;
			for (MobEffectInstance e : effects) {
				boolean expiring = !e.isInfiniteDuration() && e.getDuration() / 20 < blinkUnder.get();
				if (expiring && blinkOff) {
					out.add("§8" + line(e.getEffect().value().getDisplayName().getString(), e.getAmplifier(), duration(e)).replaceAll("§.", ""));
				} else {
					out.add(line(e.getEffect().value().getDisplayName().getString(), e.getAmplifier(), duration(e)));
				}
			}
		}
		if (out.isEmpty() && preview) {
			out.add(line("Speed", 1, "1:23"));
			out.add(line("Strength", 0, "0:45"));
		}
		return out;
	}
}
