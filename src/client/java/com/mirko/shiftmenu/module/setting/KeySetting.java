package com.mirko.shiftmenu.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/** A keyboard key (GLFW key code). -1 = not bound. Click it in the menu, then press a key. */
public class KeySetting extends Setting<Integer> {
	private boolean wasDown;

	public KeySetting(String name, int defKey) {
		super(name, defKey);
	}

	public boolean isBound() {
		return value >= 0;
	}

	/** Edge detection helper: true once when the key goes down. */
	public boolean pressed(boolean downNow) {
		boolean p = downNow && !wasDown;
		wasDown = downNow;
		return p;
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}

	@Override
	public void fromJson(JsonElement e) {
		value = e.getAsInt();
	}
}
