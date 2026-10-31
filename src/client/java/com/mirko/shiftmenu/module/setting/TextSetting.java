package com.mirko.shiftmenu.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/** Free text the player types in (custom formats, labels, custom text). Supports &-color codes. */
public class TextSetting extends Setting<String> {
	public final int maxLength;

	public TextSetting(String name, String def) {
		this(name, def, 96);
	}

	public TextSetting(String name, String def, int maxLength) {
		super(name, def);
		this.maxLength = maxLength;
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}

	@Override
	public void fromJson(JsonElement e) {
		String s = e.getAsString();
		value = s.length() > maxLength ? s.substring(0, maxLength) : s;
	}
}
