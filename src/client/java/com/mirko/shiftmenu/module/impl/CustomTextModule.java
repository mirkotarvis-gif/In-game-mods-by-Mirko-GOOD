package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.TextModule;
import com.mirko.shiftmenu.module.setting.InfoSetting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.Placeholders;

import java.util.ArrayList;
import java.util.List;

/** Write anything, with placeholders and color codes. Up to 4 lines. */
public class CustomTextModule extends TextModule {
	public final TextSetting line1, line2, line3, line4;

	public CustomTextModule(int n, float x, float y, String def) {
		super("customtext" + n, "Custom Text " + n, Category.HUD, false, x, y, true, false);
		line1 = add(new TextSetting("Line 1", def).in("Content"));
		line2 = add(new TextSetting("Line 2", "").in("Content"));
		line3 = add(new TextSetting("Line 3", "").in("Content"));
		line4 = add(new TextSetting("Line 4", "").in("Content"));
		add(new InfoSetting("Placeholders", Placeholders.HELP).in("Content"));
		// content first
		List<com.mirko.shiftmenu.module.setting.Setting<?>> content = new ArrayList<>(settings.subList(settings.size() - 5, settings.size()));
		settings.removeAll(content);
		settings.addAll(1, content);
	}

	@Override
	protected List<String> lines(boolean preview) {
		List<String> out = new ArrayList<>();
		for (TextSetting t : new TextSetting[]{line1, line2, line3, line4}) {
			if (!t.get().isEmpty()) out.add(Placeholders.apply(t.get()));
		}
		if (out.isEmpty() && preview) out.add("Custom Text");
		return out;
	}
}
