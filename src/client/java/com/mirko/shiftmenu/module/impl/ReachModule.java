package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.CombatTracker;

/** Distance of your last hit on an entity. */
public class ReachModule extends ValueModule {
	public final NumberSetting decimals = add(new NumberSetting("Decimals", 2, 0, 3, 1).in("Text"));
	public final NumberSetting resetAfter = add(new NumberSetting("Reset After (s)", 3, 1, 30, 1).in("Text"));
	public final BoolSetting hideWhenIdle = add(new BoolSetting("Hide When Idle", false).in("Text"));
	public final TextSetting idleText = add(new TextSetting("Idle Text", "Hasn't attacked").in("Text").showIf(() -> !this.hideWhenIdle.on()));

	public ReachModule() {
		super("reach", "Reach Display", Category.COMBAT, false, 0.004f, 0.52f, "{value} blocks");
	}

	@Override
	protected String value(boolean preview) {
		boolean idle = CombatTracker.msSinceReach() > resetAfter.get() * 1000;
		if (idle && !preview) {
			if (hideWhenIdle.on()) return null;
			return idleText.get().isEmpty() ? null : "\u0000" + idleText.get();
		}
		double r = idle ? 3.0 : CombatTracker.lastReach();
		return String.format("%." + decimals.get().intValue() + "f", r);
	}

	@Override
	protected java.util.List<String> lines(boolean preview) {
		String v = value(preview);
		if (v == null) return java.util.List.of();
		// idle text replaces the whole format
		if (v.startsWith("\u0000")) return java.util.List.of(com.mirko.shiftmenu.util.Placeholders.apply(v.substring(1)));
		return super.lines(preview);
	}
}
