package com.mirko.shiftmenu.module.impl;

import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.Colors;
import com.mirko.shiftmenu.util.Draw;
import com.mirko.shiftmenu.util.Fonts;
import com.mirko.shiftmenu.util.Keys;
import com.mirko.shiftmenu.util.Placeholders;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * W A S D + LMB/RMB (with CPS) + spacebar, with a smooth fade when pressed.
 * Size, gap, colors, shape, border, labels and fade speed are all settings.
 */
public class KeystrokesModule extends HudModule {
	// Layout
	public final NumberSetting keySize = add(new NumberSetting("Key Size", 22, 14, 36, 1).in("Layout"));
	public final NumberSetting gap = add(new NumberSetting("Gap", 2, 0, 8, 1).in("Layout"));
	public final BoolSetting showMouse = add(new BoolSetting("Show Mouse Buttons", true).in("Layout"));
	public final BoolSetting showCps = add(new BoolSetting("Show CPS", true).in("Layout").showIf(this.showMouse::on));
	public final BoolSetting showSpace = add(new BoolSetting("Show Spacebar", true).in("Layout"));
	public final NumberSetting spaceHeight = add(new NumberSetting("Spacebar Height", 12, 6, 24, 1).in("Layout").showIf(this.showSpace::on));
	public final ModeSetting spaceStyle = add(new ModeSetting("Spacebar Style", "Line", "Line", "Text", "Empty").in("Layout").showIf(this.showSpace::on));
	// Labels
	public final ModeSetting labels = add(new ModeSetting("Key Labels", "Bound Keys", "Bound Keys", "WASD", "Arrows").in("Labels"));
	public final TextSetting lmbLabel = add(new TextSetting("LMB Label", "LMB", 8).in("Labels").showIf(this.showMouse::on));
	public final TextSetting rmbLabel = add(new TextSetting("RMB Label", "RMB", 8).in("Labels").showIf(this.showMouse::on));
	public final TextSetting cpsFormat = add(new TextSetting("CPS Format", "{value} CPS", 16).in("Labels").showIf(() -> this.showMouse.on() && this.showCps.on()));
	public final TextSetting spaceLabel = add(new TextSetting("Spacebar Text", "SPACE", 12).in("Labels").showIf(() -> this.showSpace.on() && this.spaceStyle.is("Text")));
	public final BoolSetting shadow = add(new BoolSetting("Text Shadow", false).in("Labels"));
	// Font
	public final ModeSetting fontName = add(new ModeSetting("Font", "Global", Fonts.NAMES_WITH_GLOBAL).in("Font"));
	public final BoolSetting bold = add(new BoolSetting("Bold", false).in("Font"));
	public final BoolSetting italic = add(new BoolSetting("Italic", false).in("Font"));
	// Colors
	public final ColorSetting idleBg = add(new ColorSetting("Background", 0x6F000000).in("Colors"));
	public final ColorSetting pressedBg = add(new ColorSetting("Pressed Background", 0xD9FFFFFF).in("Colors"));
	public final ColorSetting idleText = add(new ColorSetting("Text", 0xFFFFFFFF).in("Colors"));
	public final ColorSetting pressedText = add(new ColorSetting("Pressed Text", 0xFF000000).in("Colors"));
	public final NumberSetting fadeSpeed = add(new NumberSetting("Fade Speed", 18, 1, 60, 1).in("Colors"));
	// Shape
	public final NumberSetting radius = add(new NumberSetting("Corner Radius", 0, 0, 10, 1).in("Shape"));
	public final BoolSetting border = add(new BoolSetting("Border", false).in("Shape"));
	public final ColorSetting borderColor = add(new ColorSetting("Border Color", 0xFFFFFFFF).in("Shape").showIf(this.border::on));
	public final ColorSetting pressedBorderColor = add(new ColorSetting("Pressed Border Color", 0xFFFFFFFF).in("Shape").showIf(this.border::on));
	public final NumberSetting borderThickness = add(new NumberSetting("Border Thickness", 1, 1, 3, 1).in("Shape").showIf(this.border::on));

	private final float[] anim = new float[7];
	private long lastFrame = System.nanoTime();

	public KeystrokesModule() {
		super("keystrokes", "Keystrokes", Category.HUD, true, 0.004f, 0.12f);
	}

	private Fonts.TextStyle ts() {
		var gs = com.mirko.shiftmenu.module.ModuleManager.SETTINGS;
		boolean global = fontName.is("Global");
		return new Fonts.TextStyle(global ? gs.hudFont.get() : fontName.get(),
				bold.on() || (global && gs.hudBold.on()), italic.on() || (global && gs.hudItalic.on()), false, false);
	}

	private int k() {
		return keySize.get().intValue();
	}

	private int gp() {
		return gap.get().intValue();
	}

	private int fullW() {
		return k() * 3 + gp() * 2;
	}

	@Override
	public int width(boolean preview) {
		return fullW();
	}

	@Override
	public int height(boolean preview) {
		int h = k() * 2 + gp();
		if (showMouse.on()) h += gp() + k();
		if (showSpace.on()) h += gp() + spaceHeight.get().intValue();
		return h;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;

		long now = System.nanoTime();
		float dt = Math.min(0.1f, (now - lastFrame) / 1_000_000_000f);
		lastFrame = now;

		boolean[] down = {
				mc.options.keyUp.isDown(), mc.options.keyLeft.isDown(), mc.options.keyDown.isDown(),
				mc.options.keyRight.isDown(), mc.options.keyAttack.isDown(), mc.options.keyUse.isDown(),
				mc.options.keyJump.isDown()
		};
		if (mc.screen != null) {
			for (int i = 0; i < down.length; i++) down[i] = false;
		}
		float speed = fadeSpeed.getF();
		for (int i = 0; i < anim.length; i++) {
			float target = down[i] ? 1f : 0f;
			anim[i] = speed >= 60 ? target : anim[i] + (target - anim[i]) * Math.min(1f, dt * speed);
		}

		int K = k(), G = gp(), W = fullW();
		int y = 0;
		key(g, font, K + G, y, K, K, label(mc.options.keyUp, "W", "↑"), null, anim[0], 0);
		y += K + G;
		key(g, font, 0, y, K, K, label(mc.options.keyLeft, "A", "←"), null, anim[1], 1);
		key(g, font, K + G, y, K, K, label(mc.options.keyDown, "S", "↓"), null, anim[2], 2);
		key(g, font, (K + G) * 2, y, K, K, label(mc.options.keyRight, "D", "→"), null, anim[3], 3);
		y += K + G;

		if (showMouse.on()) {
			int mw = (W - G) / 2;
			String lc = showCps.on() ? Placeholders.apply(cpsFormat.get().replace("{value}", String.valueOf(Keys.leftCps()))) : null;
			String rc = showCps.on() ? Placeholders.apply(cpsFormat.get().replace("{value}", String.valueOf(Keys.rightCps()))) : null;
			key(g, font, 0, y, mw, K, Placeholders.apply(lmbLabel.get()), lc, anim[4], 4);
			key(g, font, mw + G, y, W - mw - G, K, Placeholders.apply(rmbLabel.get()), rc, anim[5], 5);
			y += K + G;
		}

		if (showSpace.on()) {
			int sh = spaceHeight.get().intValue();
			drawBox(g, 0, y, W, sh, anim[6], 6);
			int tc = Colors.lerp(idleText.color(6), pressedText.color(6), anim[6]);
			if (spaceStyle.is("Line")) {
				int cx = W / 2;
				g.fill(cx - W / 6, y + sh / 2 - 1, cx + W / 6, y + sh / 2, tc);
			} else if (spaceStyle.is("Text")) {
				String s = Placeholders.apply(spaceLabel.get());
				Fonts.draw(g, s, (W - Fonts.width(s, ts())) / 2, y + (sh - 8) / 2 + 1, tc, shadow.on() && anim[6] < 0.5f, ts());
			}
		}
	}

	private void drawBox(GuiGraphics g, int x, int y, int w, int h, float t, int idx) {
		int bg = Colors.lerp(idleBg.color(idx * 12), pressedBg.color(idx * 12), t);
		int bc = Colors.lerp(borderColor.color(idx * 12), pressedBorderColor.color(idx * 12), t);
		g.pose().pushMatrix();
		g.pose().translate((float) x, (float) y);
		Draw.box(g, 0, 0, w, h, radius.get().intValue(), true, bg, border.on(), borderThickness.get().intValue(), bc);
		g.pose().popMatrix();
	}

	private void key(GuiGraphics g, Font font, int x, int y, int w, int h, String label, String sub, float t, int idx) {
		drawBox(g, x, y, w, h, t, idx);
		int tc = Colors.lerp(idleText.color(idx * 12), pressedText.color(idx * 12), t);
		boolean sh = shadow.on() && t < 0.5f;
		Fonts.TextStyle st = ts();
		if (sub == null) {
			Fonts.draw(g, label, x + (w - Fonts.width(label, st)) / 2, y + (h - 8) / 2 + 1, tc, sh, st);
		} else {
			int top = y + Math.max(1, (h - 14) / 2);
			Fonts.draw(g, label, x + (w - Fonts.width(label, st)) / 2, top, tc, sh, st);
			g.pose().pushMatrix();
			g.pose().translate(x + w / 2f, top + 10f);
			g.pose().scale(0.5f, 0.5f);
			Fonts.draw(g, sub, -Fonts.width(sub, st) / 2, 0, tc, sh, st);
			g.pose().popMatrix();
		}
	}

	private String label(KeyMapping mapping, String wasd, String arrow) {
		if (labels.is("WASD")) return wasd;
		if (labels.is("Arrows")) return arrow;
		String s;
		try {
			s = mapping.getTranslatedKeyMessage().getString();
		} catch (Exception e) {
			return wasd;
		}
		if (s == null || s.isEmpty()) return wasd;
		Font font = Minecraft.getInstance().font;
		if (Fonts.width(s, ts()) > k() - 4) return s.substring(0, 1).toUpperCase();
		return s.toUpperCase();
	}
}
