package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.TextModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.CombatTracker;
import com.mirko.shiftmenu.util.Placeholders;

import java.util.List;

/** Hits in a row without getting hit back. */
public class ComboModule extends TextModule {
	public final TextSetting format = add(new TextSetting("Format", "{value} Combo").in("Text"));
	public final TextSetting zeroText = add(new TextSetting("No Combo Text", "No Combo").in("Text"));
	public final BoolSetting hideAtZero = add(new BoolSetting("Hide At Zero", false).in("Text"));

	public ComboModule() {
		super("combo", "Combo Counter", Category.COMBAT, false, 0.004f, 0.57f, true, false);
	}

	@Override
	protected List<String> lines(boolean preview) {
		int c = preview && CombatTracker.combo() == 0 ? 3 : CombatTracker.combo();
		if (c == 0) {
			if (hideAtZero.on() || zeroText.get().isEmpty()) return List.of();
			return List.of(Placeholders.apply(zeroText.get()));
		}
		return List.of(Placeholders.apply(format.get().replace("{value}", String.valueOf(c))));
	}
}
