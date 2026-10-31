package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import net.minecraft.client.gui.GuiGraphics;

/** Not a HUD module: the global "Settings" page (menu theme, editor, HUD, controls). */
public class GeneralSettings extends HudModule {
	// Menu look
	public final ColorSetting accent;
	public final ColorSetting panelColor;
	public final ColorSetting cardColor;
	public final ColorSetting enabledColor;
	public final ColorSetting disabledColor;
	public final NumberSetting panelRadius;
	public final BoolSetting blur;
	public final BoolSetting animations;
	public final BoolSetting sounds;
	public final ModeSetting columns;
	// Editor
	public final BoolSetting snapCenter;
	public final BoolSetting snapEdges;
	public final BoolSetting snapModules;
	public final NumberSetting gridSnap;
	public final BoolSetting showGrid;
	public final NumberSetting editorDim;
	public final BoolSetting outlines;
	public final ColorSetting menuText;
	public final ColorSetting menuTextDim;
	public final NumberSetting menuWidth;
	public final NumberSetting menuHeight;
	public final NumberSetting cardHeight;
	public final ModeSetting menuFont;
	public final BoolSetting menuBold;
	// HUD
	public final ModeSetting hudFont;
	public final BoolSetting hudBold;
	public final BoolSetting hudItalic;
	public final NumberSetting globalScale;
	public final ModeSetting hudVisibility;
	// Controls
	public final ModeSetting openMode;
	public final NumberSetting tapTime;

	public GeneralSettings() {
		super("general", "Settings", Category.SYSTEM, true, 0, 0);
		settings.remove(scale); // no per-page scale here

		accent = add(new ColorSetting("Accent Color", 0xFF3B82F6).in("Menu Look"));
		panelColor = add(new ColorSetting("Panel Color", 0xEE0F0F12).in("Menu Look"));
		cardColor = add(new ColorSetting("Card Color", 0xFF1C1C21).in("Menu Look"));
		enabledColor = add(new ColorSetting("Enabled Color", 0xFF2FA866).in("Menu Look"));
		disabledColor = add(new ColorSetting("Disabled Color", 0xFFD9434B).in("Menu Look"));
		panelRadius = add(new NumberSetting("Corner Radius", 6, 0, 10, 1).in("Menu Look"));
		columns = add(new ModeSetting("Card Columns", "Auto", "Auto", "2", "3", "4", "5", "6").in("Menu Look"));
		menuText = add(new ColorSetting("Text Color", 0xFFFFFFFF).in("Menu Look"));
		menuTextDim = add(new ColorSetting("Secondary Text Color", 0xFF9A9AA5).in("Menu Look"));
		menuWidth = add(new NumberSetting("Menu Width", 460, 300, 800, 10).in("Menu Look"));
		menuHeight = add(new NumberSetting("Menu Height", 290, 200, 500, 10).in("Menu Look"));
		cardHeight = add(new NumberSetting("Card Height", 92, 70, 130, 2).in("Menu Look"));
		menuFont = add(new ModeSetting("Menu Font", "Minecraft", com.mirko.shiftmenu.util.Fonts.NAMES).in("Menu Look"));
		menuBold = add(new BoolSetting("Menu Bold Text", false).in("Menu Look"));
		blur = add(new BoolSetting("Blur Background", true).in("Menu Look"));
		animations = add(new BoolSetting("Animations", true).in("Menu Look"));
		sounds = add(new BoolSetting("Click Sounds", true).in("Menu Look"));

		snapCenter = add(new BoolSetting("Snap To Center", true).in("HUD Editor"));
		snapEdges = add(new BoolSetting("Snap To Edges", true).in("HUD Editor"));
		snapModules = add(new BoolSetting("Snap To Other Modules", true).in("HUD Editor"));
		gridSnap = add(new NumberSetting("Grid Snap", 0, 0, 20, 1).in("HUD Editor"));
		showGrid = add(new BoolSetting("Show Grid", false).in("HUD Editor").showIf(() -> this.gridSnap.get() > 0));
		editorDim = add(new NumberSetting("Background Dim", 33, 0, 90, 1).in("HUD Editor"));
		outlines = add(new BoolSetting("Module Outlines", true).in("HUD Editor"));

		hudFont = add(new ModeSetting("Default HUD Font", "Minecraft", com.mirko.shiftmenu.util.Fonts.NAMES).in("HUD"));
		hudBold = add(new BoolSetting("Default Bold", false).in("HUD"));
		hudItalic = add(new BoolSetting("Default Italic", false).in("HUD"));
		globalScale = add(new NumberSetting("Global HUD Scale", 1.0, 0.5, 2.0, 0.05).in("HUD"));
		hudVisibility = add(new ModeSetting("Show HUD", "Always", "Always", "Hide In Menus", "Hide In Menus Except Chat").in("HUD"));

		openMode = add(new ModeSetting("Open Menu On", "Tap", "Tap", "Press").in("Controls"));
		tapTime = add(new NumberSetting("Max Tap Time (ms)", 250, 100, 600, 10).in("Controls").showIf(() -> this.openMode.is("Tap")));
	}

	public com.mirko.shiftmenu.util.Fonts.TextStyle menuStyle() {
		return new com.mirko.shiftmenu.util.Fonts.TextStyle(menuFont.get(), menuBold.on(), false, false, false);
	}

	@Override
	public float effectiveScale() {
		return 1f;
	}

	@Override
	public boolean isDraggable() {
		return false;
	}

	@Override
	public boolean hasToggle() {
		return false;
	}

	@Override
	public int width(boolean preview) {
		return 0;
	}

	@Override
	public int height(boolean preview) {
		return 0;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
	}
}
