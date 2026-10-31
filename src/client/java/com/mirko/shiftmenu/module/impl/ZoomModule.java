package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.KeySetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Hold (or toggle) a key to zoom. Works by temporarily lowering the FOV option (down to the game's
 * minimum of 30) and mouse sensitivity, then restoring them exactly.
 */
public class ZoomModule extends HudModule {
	public final KeySetting key = add(new KeySetting("Zoom Key", GLFW.GLFW_KEY_C).in("Zoom"));
	public final ModeSetting mode = add(new ModeSetting("Mode", "Hold", "Hold", "Toggle").in("Zoom"));
	public final NumberSetting zoomFov = add(new NumberSetting("Zoomed FOV", 30, 30, 70, 1).in("Zoom"));
	public final BoolSetting smooth = add(new BoolSetting("Smooth Zoom", true).in("Zoom"));
	public final NumberSetting smoothSpeed = add(new NumberSetting("Smooth Speed", 40, 5, 100, 5).in("Zoom").showIf(this.smooth::on));
	public final BoolSetting reduceSens = add(new BoolSetting("Reduce Sensitivity", true).in("Zoom"));
	public final NumberSetting sensFactor = add(new NumberSetting("Sensitivity While Zoomed (%)", 50, 10, 100, 5).in("Zoom").showIf(this.reduceSens::on));

	private boolean zooming, toggled;
	private int savedFov = -1;
	private double savedSens = -1;
	private double currentFov;

	public ZoomModule() {
		super("zoom", "Zoom", Category.MECHANIC, true, 0, 0);
	}

	@Override
	public boolean isDraggable() {
		return false;
	}

	@Override
	public void tick(Minecraft mc) {
		boolean inGame = isEnabled() && mc.player != null && mc.screen == null;
		boolean down = inGame && Keys.isKeyDown(mc, key.get());
		if (key.pressed(down) && mode.is("Toggle")) toggled = !toggled;
		if (!inGame && mc.player == null) toggled = false;
		boolean want = isEnabled() && mc.player != null && (mode.is("Toggle") ? toggled : down);

		if (want && !zooming) {
			savedFov = mc.options.fov().get();
			savedSens = mc.options.sensitivity().get();
			currentFov = savedFov;
			zooming = true;
			if (reduceSens.on()) mc.options.sensitivity().set(savedSens * sensFactor.get() / 100.0);
		}
		if (zooming) {
			double target = want ? zoomFov.get() : savedFov;
			if (smooth.on()) currentFov += (target - currentFov) * Math.min(1.0, smoothSpeed.get() / 100.0);
			else currentFov = target;
			if (Math.abs(currentFov - target) < 0.5) currentFov = target;
			int f = (int) Math.round(Math.max(30, Math.min(110, currentFov)));
			if (mc.options.fov().get() != f) mc.options.fov().set(f);
			if (!want && currentFov == savedFov) restore(mc);
		}
	}

	private void restore(Minecraft mc) {
		if (savedFov >= 0) mc.options.fov().set(savedFov);
		if (savedSens >= 0) mc.options.sensitivity().set(savedSens);
		savedFov = -1;
		savedSens = -1;
		zooming = false;
	}

	@Override
	protected void onDisable() {
		toggled = false;
		if (zooming) restore(Minecraft.getInstance());
	}

	@Override
	public int width(boolean preview) {
		return 40;
	}

	@Override
	public int height(boolean preview) {
		return 24;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		// magnifier icon as the card preview
		com.mirko.shiftmenu.util.Draw.roundedBorder(g, 10, 2, 26, 18, 8, 2, 0xFFFFFFFF);
		g.fill(25, 17, 28, 20, 0xFFFFFFFF);
		g.fill(27, 19, 30, 22, 0xFFFFFFFF);
	}
}
