package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.TextModule;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.Placeholders;

import java.util.ArrayList;
import java.util.List;

/** A title in the corner. Defaults to rainbow "ShiftMenu" in Montserrat bold. */
public class WatermarkModule extends TextModule {
	public final TextSetting title = add(new TextSetting("Title", "ShiftMenu").in("Content"));
	public final TextSetting subtitle = add(new TextSetting("Subtitle", "&7{fps} FPS • {time}").in("Content"));

	public WatermarkModule() {
		super("watermark", "Watermark", Category.HUD, false, 0.004f, 0.006f, false, false);
		textColor.chroma = true;
		fontName.set("Montserrat");
		bold.set(true);
		scale.set(1.25);
	}

	@Override
	protected String defaultAlign() {
		return "Left";
	}

	@Override
	public void resetSettings() {
		super.resetSettings();
		textColor.chroma = true;
		fontName.set("Montserrat");
		bold.set(true);
		scale.set(1.25);
	}

	@Override
	protected List<String> lines(boolean preview) {
		List<String> out = new ArrayList<>();
		if (!title.get().isEmpty()) out.add(Placeholders.apply(title.get()));
		if (!subtitle.get().isEmpty()) out.add(Placeholders.apply(subtitle.get()));
		return out;
	}
}
