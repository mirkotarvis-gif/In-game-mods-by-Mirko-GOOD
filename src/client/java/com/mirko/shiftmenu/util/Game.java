package com.mirko.shiftmenu.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/** Small helpers for reading game state safely (null-safe when not in a world). */
public final class Game {
	private Game() {}

	public static Minecraft mc() {
		return Minecraft.getInstance();
	}

	public static LocalPlayer player() {
		return Minecraft.getInstance().player;
	}

	public static boolean inWorld() {
		return player() != null && mc().level != null;
	}

	/** Total count of matching items in the player's whole inventory (incl. armor/offhand). */
	public static int count(Predicate<ItemStack> filter) {
		LocalPlayer p = player();
		if (p == null) return 0;
		Inventory inv = p.getInventory();
		int n = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack st = inv.getItem(i);
			if (!st.isEmpty() && filter.test(st)) n += st.getCount();
		}
		return n;
	}

	public static ItemStack held() {
		LocalPlayer p = player();
		return p == null ? ItemStack.EMPTY : p.getMainHandItem();
	}

	public static String capitalize(String s) {
		if (s == null || s.isEmpty()) return "";
		s = s.toLowerCase().replace('_', ' ');
		StringBuilder b = new StringBuilder();
		boolean up = true;
		for (char c : s.toCharArray()) {
			b.append(up ? Character.toUpperCase(c) : c);
			up = c == ' ';
		}
		return b.toString();
	}

	public static String fmt(double v, int decimals) {
		return String.format("%." + Math.max(0, decimals) + "f", v);
	}
}
