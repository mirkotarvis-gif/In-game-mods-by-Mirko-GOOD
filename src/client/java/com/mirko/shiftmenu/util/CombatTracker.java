package com.mirko.shiftmenu.util;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Tracks reach of your last hit and your current combo (hits in a row without getting hit). */
public final class CombatTracker {
	private static final long COMBO_TIMEOUT_MS = 3000;

	private static double lastReach;
	private static long lastReachAt;
	private static int combo;
	private static long lastHitAt;
	private static int lastHurtTime;
	private static int totalHits, maxCombo;

	private CombatTracker() {}

	public static void register() {
		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (player instanceof LocalPlayer) {
				Vec3 eye = player.getEyePosition();
				Vec3 hit = null;
				if (hitResult != null) {
					hit = hitResult.getLocation();
				} else {
					HitResult hr = Minecraft.getInstance().hitResult;
					if (hr instanceof EntityHitResult ehr) hit = ehr.getLocation();
				}
				if (hit != null) {
					lastReach = eye.distanceTo(hit);
					lastReachAt = System.currentTimeMillis();
				}
				combo++;
				totalHits++;
				maxCombo = Math.max(maxCombo, combo);
				lastHitAt = System.currentTimeMillis();
			}
			return InteractionResult.PASS;
		});
	}

	public static void tick(Minecraft mc) {
		LocalPlayer p = mc.player;
		if (p == null) {
			combo = 0;
			lastHurtTime = 0;
			totalHits = 0;
			maxCombo = 0;
			Keys.resetSwings();
			return;
		}
		// got hit -> combo broken
		if (p.hurtTime > lastHurtTime) combo = 0;
		lastHurtTime = p.hurtTime;
		if (combo > 0 && System.currentTimeMillis() - lastHitAt > COMBO_TIMEOUT_MS) combo = 0;
	}

	public static int combo() {
		return combo;
	}

	public static int totalHits() {
		return totalHits;
	}

	public static int maxCombo() {
		return maxCombo;
	}

	/** Hits / left clicks this session, in percent. */
	public static int accuracy() {
		return (int) Math.round(totalHits * 100.0 / Math.max(1, Keys.swings()));
	}

	public static double lastReach() {
		return lastReach;
	}

	public static long msSinceReach() {
		return lastReachAt == 0 ? Long.MAX_VALUE : System.currentTimeMillis() - lastReachAt;
	}
}
