package com.mirko.shiftmenu.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.List;

public class ModeSetting extends Setting<String> {
	public final List<String> modes;

	public ModeSetting(String name, String def, String... modes) {
		super(name, def);
		this.modes = List.of(modes);
	}

	public boolean is(String mode) {
		return value.equals(mode);
	}

	public void next() {
		int i = modes.indexOf(value);
		value = modes.get((i + 1) % modes.size());
	}

	public void previous() {
		int i = modes.indexOf(value);
		value = modes.get((i - 1 + modes.size()) % modes.size());
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}

	@Override
	public void fromJson(JsonElement e) {
		String s = e.getAsString();
		if (modes.contains(s)) value = s;
	}
}
