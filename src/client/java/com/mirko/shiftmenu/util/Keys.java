package com.mirko.shiftmenu.util;

import com.mirko.shiftmenu.Config;
import com.mirko.shiftmenu.module.ModuleManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;

/**
 * Raw keyboard / mouse polling. Polled every frame and every tick so short taps aren't missed.
 */
public final class Keys {
	private static final ArrayDeque<Long> leftClicks = new ArrayDeque<>();
	private static final ArrayDeque<Long> rightClicks = new ArrayDeque<>();
	private static boolean lastLeft, lastRight;
	private static int swings;

	private static boolean menuWasDown, menuArmed, suppressUntilRelease, openRequested;
	private static long menuDownAt;

	private Keys() {}

	public static long window(Minecraft mc) {
		return mc.getWindow().handle();
	}

	public static boolean isKeyDown(Minecraft mc, int glfwKey) {
		if (glfwKey < 0) return false;
		return GLFW.glfwGetKey(window(mc), glfwKey) == GLFW.GLFW_PRESS;
	}

	/** Is the physical key/button bound to this mapping currently held? (ignores our own forced state) */
	public static boolean isPhysicallyDown(Minecraft mc, KeyMapping mapping) {
		InputConstants.Key key = KeyBindingHelper.getBoundKeyOf(mapping);
		int v = key.getValue();
		if (v < 0) return false;
		if (key.getType() == InputConstants.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(window(mc), v) == GLFW.GLFW_PRESS;
		}
		if (key.getType() == InputConstants.Type.KEYSYM) {
			return GLFW.glfwGetKey(window(mc), v) == GLFW.GLFW_PRESS;
		}
		return mapping.isDown();
	}

	public static void poll(Minecraft mc) {
		long now = System.currentTimeMillis();
		boolean inGame = mc.player != null && mc.screen == null;

		// --- CPS ---
		boolean l = inGame && isPhysicallyDown(mc, mc.options.keyAttack);
		boolean r = inGame && isPhysicallyDown(mc, mc.options.keyUse);
		if (l && !lastLeft) {
			leftClicks.addLast(now);
			swings++;
		}
		if (r && !lastRight) rightClicks.addLast(now);
		lastLeft = l;
		lastRight = r;
		while (!leftClicks.isEmpty() && now - leftClicks.peekFirst() > 1000) leftClicks.pollFirst();
		while (!rightClicks.isEmpty() && now - rightClicks.peekFirst() > 1000) rightClicks.pollFirst();

		// --- Toggle sprint / sneak edges ---
		ModuleManager.TOGGLE_SPRINT.pollEdges(mc, inGame);

		// --- Menu key ---
		boolean m = isKeyDown(mc, Config.menuKey);
		if (suppressUntilRelease) {
			if (!m) suppressUntilRelease = false;
			menuWasDown = m;
			return;
		}
		if (m && !menuWasDown) {
			menuDownAt = now;
			menuArmed = inGame;
			if (menuArmed && ModuleManager.SETTINGS.openMode.is("Press")) {
				openRequested = true;
				menuArmed = false;
			}
		}
		if (!m && menuWasDown && menuArmed) {
			menuArmed = false;
			if (inGame && now - menuDownAt <= ModuleManager.SETTINGS.tapTime.get()) openRequested = true;
		}
		menuWasDown = m;
	}

	/** Call when a screen is closed with the menu key so the same press doesn't re-open it. */
	public static void suppressMenuKey() {
		suppressUntilRelease = true;
		menuArmed = false;
		openRequested = false;
	}

	public static boolean consumeOpenRequest() {
		boolean r = openRequested;
		openRequested = false;
		return r;
	}

	public static int swings() {
		return swings;
	}

	public static void resetSwings() {
		swings = 0;
	}

	public static int leftCps() {
		return leftClicks.size();
	}

	public static int rightCps() {
		return rightClicks.size();
	}

	public static String keyName(int glfwKey) {
		try {
			return InputConstants.Type.KEYSYM.getOrCreate(glfwKey).getDisplayName().getString();
		} catch (Exception e) {
			return "KEY " + glfwKey;
		}
	}
}
