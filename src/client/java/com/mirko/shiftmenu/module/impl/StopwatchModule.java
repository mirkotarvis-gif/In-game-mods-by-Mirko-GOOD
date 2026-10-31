package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.ValueModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.KeySetting;
import com.mirko.shiftmenu.util.Keys;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/** Start/stop and reset with your own keys. Great for speedrun-style timing. */
public class StopwatchModule extends ValueModule {
	public final KeySetting startStop = add(new KeySetting("Start / Stop Key", GLFW.GLFW_KEY_KP_ADD).in("Controls"));
	public final KeySetting reset = add(new KeySetting("Reset Key", GLFW.GLFW_KEY_KP_SUBTRACT).in("Controls"));
	public final BoolSetting showMillis = add(new BoolSetting("Show Hundredths", true).in("Controls"));

	private long startedAt, accumulated;
	private boolean running;

	public StopwatchModule() {
		super("stopwatch", "Stopwatch", com.mirko.shiftmenu.module.HudModule.Category.INFO, false, 0.5f, 0.32f, "Timer: {value}");
	}

	@Override
	public void tick(Minecraft mc) {
		boolean inGame = mc.screen == null && mc.player != null;
		if (startStop.pressed(inGame && Keys.isKeyDown(mc, startStop.get()))) {
			if (running) {
				accumulated += System.currentTimeMillis() - startedAt;
				running = false;
			} else {
				startedAt = System.currentTimeMillis();
				running = true;
			}
		}
		if (reset.pressed(inGame && Keys.isKeyDown(mc, reset.get()))) {
			accumulated = 0;
			startedAt = System.currentTimeMillis();
		}
	}

	@Override
	protected String value(boolean preview) {
		long ms = accumulated + (running ? System.currentTimeMillis() - startedAt : 0);
		long m = ms / 60000, s = (ms / 1000) % 60, cs = (ms / 10) % 100;
		return showMillis.on() ? String.format("%02d:%02d.%02d", m, s, cs) : String.format("%02d:%02d", m, s);
	}
}
