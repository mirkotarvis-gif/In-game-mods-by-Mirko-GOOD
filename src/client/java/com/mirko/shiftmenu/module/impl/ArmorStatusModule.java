package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Colors;
import com.mirko.shiftmenu.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Armor pieces (+ held items) with durability. */
public class ArmorStatusModule extends HudModule {
	private static final int ITEM = 16;

	public final ModeSetting layout = add(new ModeSetting("Layout", "Vertical", "Vertical", "Horizontal").in("Layout"));
	public final ModeSetting durability = add(new ModeSetting("Durability", "Value", "Value", "Percent", "Value / Max", "Off").in("Layout"));
	public final ModeSetting textSide = add(new ModeSetting("Text Side", "Right", "Right", "Left").in("Layout").showIf(() -> this.layout.is("Vertical") && !this.durability.is("Off")));
	public final BoolSetting showMainHand = add(new BoolSetting("Show Main Hand", true).in("Layout"));
	public final BoolSetting showOffHand = add(new BoolSetting("Show Off Hand", false).in("Layout"));
	public final BoolSetting showEmpty = add(new BoolSetting("Show Empty Slots", false).in("Layout"));
	public final BoolSetting vanillaBar = add(new BoolSetting("Vanilla Durability Bar", true).in("Layout"));
	public final BoolSetting showCount = add(new BoolSetting("Show Item Count", true).in("Layout"));
	public final NumberSetting spacing = add(new NumberSetting("Spacing", 2, 0, 10, 1).in("Layout"));

	public final ModeSetting colorMode = add(new ModeSetting("Text Color Mode", "Dynamic", "Dynamic", "Custom").in("Text").showIf(() -> !this.durability.is("Off")));
	public final ColorSetting textColor = add(new ColorSetting("Text Color", 0xFFFFFFFF).in("Text").showIf(() -> this.colorMode.is("Custom")));
	public final BoolSetting shadow = add(new BoolSetting("Text Shadow", true).in("Text"));

	public final BoolSetting background = add(new BoolSetting("Background", false).in("Background"));
	public final ColorSetting bgColor = add(new ColorSetting("Background Color", 0x6F000000).in("Background").showIf(this.background::on));
	public final NumberSetting radius = add(new NumberSetting("Corner Radius", 0, 0, 9, 1).in("Background").showIf(this.background::on));
	public final NumberSetting padding = add(new NumberSetting("Padding", 3, 0, 12, 1).in("Background").showIf(this.background::on));

	public ArmorStatusModule() {
		super("armor", "Armor Status", Category.COMBAT, false, 0.99f, 0.62f);
	}

	private List<ItemStack> stacks(boolean preview) {
		List<ItemStack> out = new ArrayList<>();
		LocalPlayer p = Minecraft.getInstance().player;
		boolean any = false;
		if (p != null) {
			EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
			for (EquipmentSlot s : slots) {
				ItemStack st = p.getItemBySlot(s);
				if (!st.isEmpty()) any = true;
				if (!st.isEmpty() || showEmpty.on()) out.add(st);
			}
			if (showMainHand.on() && (!p.getMainHandItem().isEmpty() || showEmpty.on())) {
				out.add(p.getMainHandItem());
				any |= !p.getMainHandItem().isEmpty();
			}
			if (showOffHand.on() && (!p.getOffhandItem().isEmpty() || showEmpty.on())) {
				out.add(p.getOffhandItem());
				any |= !p.getOffhandItem().isEmpty();
			}
		}
		if (preview && !any) {
			out.clear();
			out.add(new ItemStack(Items.DIAMOND_HELMET));
			out.add(new ItemStack(Items.DIAMOND_CHESTPLATE));
			out.add(new ItemStack(Items.DIAMOND_LEGGINGS));
			out.add(new ItemStack(Items.DIAMOND_BOOTS));
			if (showMainHand.on()) out.add(new ItemStack(Items.DIAMOND_SWORD));
		}
		return out;
	}

	private String text(ItemStack st) {
		if (durability.is("Off") || st.isEmpty() || !st.isDamageableItem()) return "";
		int max = st.getMaxDamage();
		int left = max - st.getDamageValue();
		return switch (durability.get()) {
			case "Percent" -> (left * 100 / Math.max(1, max)) + "%";
			case "Value / Max" -> left + "/" + max;
			default -> String.valueOf(left);
		};
	}

	private int color(ItemStack st) {
		if (colorMode.is("Custom") || !st.isDamageableItem()) return textColor.color();
		float f = 1f - st.getDamageValue() / (float) Math.max(1, st.getMaxDamage());
		return Colors.hsv(f / 3f, 0.8f, 1f); // red -> yellow -> green
	}

	private int pad() {
		return background.on() ? padding.get().intValue() : 0;
	}

	private int maxTextW(List<ItemStack> list) {
		Font f = Minecraft.getInstance().font;
		int w = 0;
		for (ItemStack s : list) w = Math.max(w, f.width(text(s)));
		return w;
	}

	@Override
	public int width(boolean preview) {
		List<ItemStack> list = stacks(preview);
		if (list.isEmpty()) return 0;
		int tw = maxTextW(list);
		int sp = spacing.get().intValue();
		if (layout.is("Vertical")) return ITEM + (tw > 0 ? 3 + tw : 0) + pad() * 2;
		int col = Math.max(ITEM, tw);
		return list.size() * col + (list.size() - 1) * sp + pad() * 2;
	}

	@Override
	public int height(boolean preview) {
		List<ItemStack> list = stacks(preview);
		if (list.isEmpty()) return 0;
		int sp = spacing.get().intValue();
		if (layout.is("Vertical")) return list.size() * ITEM + (list.size() - 1) * sp + pad() * 2;
		return ITEM + (maxTextW(list) > 0 ? 10 : 0) + pad() * 2;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		List<ItemStack> list = stacks(preview);
		if (list.isEmpty()) return;
		Font font = Minecraft.getInstance().font;
		int w = width(preview), h = height(preview);
		if (background.on()) Draw.rounded(g, 0, 0, w, h, radius.get().intValue(), bgColor.color());
		int p = pad(), sp = spacing.get().intValue();
		int tw = maxTextW(list);
		boolean left = textSide.is("Left");
		for (int i = 0; i < list.size(); i++) {
			ItemStack st = list.get(i);
			String t = text(st);
			int ix, iy, tx, ty;
			if (layout.is("Vertical")) {
				iy = p + i * (ITEM + sp);
				ix = left && tw > 0 ? p + tw + 3 : p;
				tx = left ? p + tw - font.width(t) : p + ITEM + 3;
				ty = iy + 4;
			} else {
				int col = Math.max(ITEM, tw);
				int cx = p + i * (col + sp);
				ix = cx + (col - ITEM) / 2;
				iy = p;
				tx = cx + (col - font.width(t)) / 2;
				ty = p + ITEM + 1;
			}
			if (!st.isEmpty()) {
				g.renderItem(st, ix, iy);
				if (vanillaBar.on() || showCount.on()) {
					if (vanillaBar.on() && showCount.on()) {
						g.renderItemDecorations(font, st, ix, iy);
					} else if (showCount.on()) {
						if (st.getCount() > 1) {
							String c = String.valueOf(st.getCount());
							g.drawString(font, c, ix + 17 - font.width(c), iy + 9, 0xFFFFFFFF, true);
						}
					} else {
						g.renderItemDecorations(font, st, ix, iy, "");
					}
				}
			}
			if (!t.isEmpty()) g.drawString(font, t, tx, ty, color(st), shadow.on());
		}
	}
}
