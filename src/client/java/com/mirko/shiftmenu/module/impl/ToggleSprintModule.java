package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.TextModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.Keys;
import com.mirko.shiftmenu.util.Placeholders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.List;

/**
 * Toggle Sprint / Toggle Sneak with the classic "[Sprinting (Toggled)]" status text.
 * Every status text is editable.
 */
public class ToggleSprintModule extends TextModule {
	public final BoolSetting toggleSprint;
	public final BoolSetting toggleSneak;
	public final BoolSetting showText;
	public final TextSetting tSprintToggled, tSprintHeld, tSprintVanilla, tSneakToggled, tSneakHeld, tFlying, tRiding;

	private boolean sprintToggled, sneakToggled;
	private boolean lastSprintPhys, lastSneakPhys;
	private boolean forcingSprint, forcingSneak;

	public ToggleSprintModule() {
		super("togglesprint", "Toggle Sprint", Category.MECHANIC, true, 0.004f, 0.97f, false, true);
		toggleSprint = add(new BoolSetting("Toggle Sprint", true).in("Behaviour"));
		toggleSneak = add(new BoolSetting("Toggle Sneak", false).in("Behaviour"));
		showText = add(new BoolSetting("Show HUD Text", true).in("Behaviour"));
		tSprintToggled = add(new TextSetting("Sprint Toggled", "Sprinting (Toggled)").in("Status Texts"));
		tSprintHeld = add(new TextSetting("Sprint Key Held", "Sprinting (Key Held)").in("Status Texts"));
		tSprintVanilla = add(new TextSetting("Sprint Vanilla", "Sprinting (Vanilla)").in("Status Texts"));
		tSneakToggled = add(new TextSetting("Sneak Toggled", "Sneaking (Toggled)").in("Status Texts"));
		tSneakHeld = add(new TextSetting("Sneak Key Held", "Sneaking (Key Held)").in("Status Texts"));
		tFlying = add(new TextSetting("Flying", "Flying").in("Status Texts"));
		tRiding = add(new TextSetting("Riding", "").in("Status Texts"));
	}

	@Override
	protected String defaultAlign() {
		return "Left";
	}

	/** Detect presses of the sprint / sneak keys (called every frame + tick). */
	public void pollEdges(Minecraft mc, boolean inGame) {
		boolean sp = inGame && Keys.isPhysicallyDown(mc, mc.options.keySprint);
		boolean sn = inGame && Keys.isPhysicallyDown(mc, mc.options.keyShift);
		if (isEnabled() && inGame) {
			if (sp && !lastSprintPhys && toggleSprint.on()) sprintToggled = !sprintToggled;
			if (sn && !lastSneakPhys && toggleSneak.on()) sneakToggled = !sneakToggled;
		}
		lastSprintPhys = sp;
		lastSneakPhys = sn;
	}

	/** Hold the vanilla key mappings down while toggled. */
	public void applyKeys(Minecraft mc) {
		boolean active = isEnabled() && mc.player != null && mc.screen == null;

		boolean wantSprint = active && toggleSprint.on() && sprintToggled;
		if (wantSprint) {
			mc.options.keySprint.setDown(true);
			forcingSprint = true;
		} else if (forcingSprint) {
			forcingSprint = false;
			mc.options.keySprint.setDown(Keys.isPhysicallyDown(mc, mc.options.keySprint));
		}

		boolean wantSneak = active && toggleSneak.on() && sneakToggled;
		if (wantSneak) {
			mc.options.keyShift.setDown(true);
			forcingSneak = true;
		} else if (forcingSneak) {
			forcingSneak = false;
			mc.options.keyShift.setDown(Keys.isPhysicallyDown(mc, mc.options.keyShift));
		}
	}

	@Override
	protected void onDisable() {
		sprintToggled = false;
		sneakToggled = false;
		applyKeys(Minecraft.getInstance());
	}

	@Override
	protected List<String> lines(boolean preview) {
		if (!showText.on() && !preview) return List.of();
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer p = mc.player;
		String s = "";
		if (p != null) {
			boolean sneakHeld = Keys.isPhysicallyDown(mc, mc.options.keyShift);
			boolean sprintHeld = Keys.isPhysicallyDown(mc, mc.options.keySprint);
			if (p.getAbilities().flying) s = tFlying.get();
			else if (p.isPassenger()) s = tRiding.get();
			else if (toggleSneak.on() && sneakToggled) s = tSneakToggled.get();
			else if (sneakHeld || p.isShiftKeyDown()) s = tSneakHeld.get();
			else if (toggleSprint.on() && sprintToggled) s = tSprintToggled.get();
			else if (sprintHeld) s = tSprintHeld.get();
			else if (p.isSprinting()) s = tSprintVanilla.get();
		}
		if (s.isEmpty() && preview) s = tSprintToggled.get();
		return s.isEmpty() ? List.of() : List.of(Placeholders.apply(s));
	}
}
