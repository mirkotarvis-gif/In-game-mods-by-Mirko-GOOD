package com.mirko.shiftmenu.module;

import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.module.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public abstract class HudModule {
	public enum Category { HUD, COMBAT, MECHANIC, INFO, SYSTEM }

	public final String id;
	public final String name;
	public final Category category;
	private final boolean defaultEnabled;
	private final float defaultX, defaultY;
	private boolean enabled;

	/** Position as a fraction (0..1) of the screen size, so layouts survive window resizes. */
	public float rx, ry;

	public final List<Setting<?>> settings = new ArrayList<>();
	public final NumberSetting scale;

	protected HudModule(String id, String name, Category category, boolean enabled, float x, float y) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.defaultEnabled = enabled;
		this.enabled = enabled;
		this.defaultX = x;
		this.defaultY = y;
		this.rx = x;
		this.ry = y;
		this.scale = add(new NumberSetting("Scale", 1.0, 0.5, 3.0, 0.05).in("General"));
	}

	protected <S extends Setting<?>> S add(S s) {
		settings.add(s);
		return s;
	}

	/** Unscaled size. preview = true when drawn inside the editor / mod menu. */
	public abstract int width(boolean preview);

	public abstract int height(boolean preview);

	/** Draw at (0,0) with unscaled coordinates. */
	public abstract void render(GuiGraphics g, boolean preview);

	public void tick(Minecraft mc) {}

	protected void onDisable() {}

	/** False for things with a fixed spot, like the crosshair. */
	public boolean isDraggable() {
		return true;
	}

	/** False for the general settings page (it has no on/off). */
	public boolean hasToggle() {
		return true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean e) {
		if (this.enabled && !e) onDisable();
		this.enabled = e;
	}

	public void setEnabledSilently(boolean e) {
		this.enabled = e;
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	public void resetPosition() {
		rx = defaultX;
		ry = defaultY;
	}

	public void resetSettings() {
		for (Setting<?> s : settings) s.reset();
		setEnabled(defaultEnabled);
	}

	public float effectiveScale() {
		return scale.getF() * ModuleManager.SETTINGS.globalScale.getF();
	}

	public int scaledWidth(boolean preview) {
		return (int) Math.ceil(width(preview) * effectiveScale());
	}

	public int scaledHeight(boolean preview) {
		return (int) Math.ceil(height(preview) * effectiveScale());
	}

	public int getX(int screenW, boolean preview) {
		int w = scaledWidth(preview);
		return Math.max(0, Math.min(Math.round(rx * screenW), screenW - w));
	}

	public int getY(int screenH, boolean preview) {
		int h = scaledHeight(preview);
		return Math.max(0, Math.min(Math.round(ry * screenH), screenH - h));
	}

	public void setPosition(int x, int y, int screenW, int screenH) {
		rx = screenW <= 0 ? 0 : x / (float) screenW;
		ry = screenH <= 0 ? 0 : y / (float) screenH;
	}

	public void renderAt(GuiGraphics g, boolean preview) {
		int x = getX(g.guiWidth(), preview);
		int y = getY(g.guiHeight(), preview);
		float s = effectiveScale();
		g.pose().pushMatrix();
		g.pose().translate((float) x, (float) y);
		g.pose().scale(s, s);
		render(g, preview);
		g.pose().popMatrix();
	}
}
