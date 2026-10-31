package com.mirko.shiftmenu.module.setting;

import com.google.gson.JsonElement;

import java.util.function.BooleanSupplier;

public abstract class Setting<T> {
	public final String name;
	protected final T defaultValue;
	protected T value;
	private String section = "General";
	private BooleanSupplier visible = () -> true;

	protected Setting(String name, T defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	public T get() {
		return value;
	}

	public void set(T v) {
		this.value = v;
	}

	public void reset() {
		this.value = defaultValue;
	}

	public String key() {
		return (section + "_" + name).toLowerCase().replace(' ', '_');
	}

	/** Which group this setting is shown under on the settings page. */
	@SuppressWarnings("unchecked")
	public <S extends Setting<T>> S in(String section) {
		this.section = section;
		return (S) this;
	}

	/** Only show this setting while the condition is true (e.g. background color only when background is on). */
	@SuppressWarnings("unchecked")
	public <S extends Setting<T>> S showIf(BooleanSupplier condition) {
		this.visible = condition;
		return (S) this;
	}

	public String section() {
		return section;
	}

	public boolean isVisible() {
		return visible.getAsBoolean();
	}

	public abstract JsonElement toJson();

	public abstract void fromJson(JsonElement e);
}
