package com.mirko.shiftmenu.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

/** Not a real setting: a block of help text shown on the settings page. Never saved. */
public class InfoSetting extends Setting<String> {
	public InfoSetting(String name, String text) {
		super(name, text);
	}

	@Override
	public JsonElement toJson() {
		return JsonNull.INSTANCE;
	}

	@Override
	public void fromJson(JsonElement e) {
	}
}
