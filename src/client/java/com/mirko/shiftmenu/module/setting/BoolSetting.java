package com.mirko.shiftmenu.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BoolSetting extends Setting<Boolean> {
	public BoolSetting(String name, boolean def) {
		super(name, def);
	}

	public boolean on() {
		return value;
	}

	public void toggle() {
		value = !value;
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}

	@Override
	public void fromJson(JsonElement e) {
		value = e.getAsBoolean();
	}
}
