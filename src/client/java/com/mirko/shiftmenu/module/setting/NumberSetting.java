package com.mirko.shiftmenu.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberSetting extends Setting<Double> {
	public final double min, max, step;

	public NumberSetting(String name, double def, double min, double max, double step) {
		super(name, def);
		this.min = min;
		this.max = max;
		this.step = step;
	}

	public float getF() {
		return value.floatValue();
	}

	@Override
	public void set(Double v) {
		double c = Math.max(min, Math.min(max, v));
		if (step > 0) c = Math.round(c / step) * step;
		// avoid 0.30000000004 style values
		c = Math.round(c * 1000.0) / 1000.0;
		this.value = Math.max(min, Math.min(max, c));
	}

	/** 0..1 position of the value inside its range. */
	public double progress() {
		return (value - min) / (max - min);
	}

	public void setProgress(double p) {
		set(min + Math.max(0, Math.min(1, p)) * (max - min));
	}

	public String display() {
		if (step >= 1) return String.valueOf(Math.round(value));
		return String.format("%.2f", value);
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}

	@Override
	public void fromJson(JsonElement e) {
		set(e.getAsDouble());
	}
}
