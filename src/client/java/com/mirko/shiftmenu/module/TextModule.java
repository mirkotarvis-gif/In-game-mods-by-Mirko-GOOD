package com.mirko.shiftmenu.module;

import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.util.Draw;
import com.mirko.shiftmenu.util.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * The classic PvP-client text module: by default a translucent black 56x18 box with centered
 * white text. Every part of the look is a setting: text color, shadow, alignment, brackets,
 * background color, corner radius, padding, minimum width and a border.
 */
public abstract class TextModule extends HudModule {
	protected static final int LINE_H = 10;

	// Text
	public final ColorSetting textColor;
	public final BoolSetting shadow;
	public final ModeSetting align;
	public final NumberSetting lineSpacing;
	public final ModeSetting bracketStyle;
	public final BoolSetting customBracketColor;
	public final ColorSetting bracketColor;
	// Font
	public final ModeSetting fontName;
	public final BoolSetting bold;
	public final BoolSetting italic;
	public final BoolSetting underline;
	public final BoolSetting strike;
	public final BoolSetting uppercase;
	// Background
	public final BoolSetting background;
	public final ColorSetting bgColor;
	public final NumberSetting radius;
	public final NumberSetting padX;
	public final NumberSetting padY;
	public final NumberSetting minWidth;
	// Border
	public final BoolSetting border;
	public final ColorSetting borderColor;
	public final NumberSetting borderThickness;

	protected TextModule(String id, String name, Category category, boolean enabled, float x, float y,
						 boolean defBackground, boolean defBrackets) {
		super(id, name, category, enabled, x, y);
		textColor = add(new ColorSetting("Text Color", 0xFFFFFFFF).in("Text"));
		shadow = add(new BoolSetting("Text Shadow", true).in("Text"));
		align = add(new ModeSetting("Alignment", defaultAlign(), "Left", "Center", "Right").in("Text"));
		lineSpacing = add(new NumberSetting("Line Spacing", 0, -2, 8, 1).in("Text"));
		bracketStyle = add(new ModeSetting("Brackets", defBrackets ? "[ ]" : "None", "None", "[ ]", "( )", "< >", "{ }", "| |", "« »").in("Text"));
		customBracketColor = add(new BoolSetting("Custom Bracket Color", false).in("Text").showIf(() -> !bracketStyle.is("None")));
		bracketColor = add(new ColorSetting("Bracket Color", 0xFFAAAAAA).in("Text").showIf(() -> !bracketStyle.is("None") && customBracketColor.on()));

		fontName = add(new ModeSetting("Font", "Global", Fonts.NAMES_WITH_GLOBAL).in("Font"));
		bold = add(new BoolSetting("Bold", false).in("Font"));
		italic = add(new BoolSetting("Italic", false).in("Font"));
		underline = add(new BoolSetting("Underline", false).in("Font"));
		strike = add(new BoolSetting("Strikethrough", false).in("Font"));
		uppercase = add(new BoolSetting("Uppercase", false).in("Font"));

		background = add(new BoolSetting("Background", defBackground).in("Background"));
		bgColor = add(new ColorSetting("Background Color", 0x6F000000).in("Background").showIf(background::on));
		radius = add(new NumberSetting("Corner Radius", 0, 0, 9, 1).in("Background").showIf(this::boxed));
		padX = add(new NumberSetting("Padding X", 5, 0, 16, 1).in("Background").showIf(this::boxed));
		padY = add(new NumberSetting("Padding Y", 5, 1, 12, 1).in("Background").showIf(this::boxed));
		minWidth = add(new NumberSetting("Min Width", 56, 0, 160, 1).in("Background").showIf(this::boxed));

		border = add(new BoolSetting("Border", false).in("Border"));
		borderColor = add(new ColorSetting("Border Color", 0xFFFFFFFF).in("Border").showIf(border::on));
		borderThickness = add(new NumberSetting("Border Thickness", 1, 1, 4, 1).in("Border").showIf(border::on));
	}

	protected String defaultAlign() {
		return "Center";
	}

	/** Lines to show (already formatted). Return an empty list to hide the module. */
	protected abstract List<String> lines(boolean preview);

	protected Font font() {
		return Minecraft.getInstance().font;
	}

	/** The resolved font + style for this module's text. */
	public Fonts.TextStyle textStyle() {
		var gs = ModuleManager.SETTINGS;
		boolean global = fontName.is("Global");
		return new Fonts.TextStyle(global ? gs.hudFont.get() : fontName.get(),
				bold.on() || (global && gs.hudBold.on()),
				italic.on() || (global && gs.hudItalic.on()),
				underline.on(), strike.on());
	}

	private List<String> shown(boolean preview) {
		List<String> l = lines(preview);
		if (!uppercase.on()) return l;
		List<String> out = new java.util.ArrayList<>(l.size());
		for (String s : l) out.add(upper(s));
		return out;
	}

	/** Uppercase that leaves section-sign color codes alone. */
	private static String upper(String s) {
		StringBuilder b = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\u00a7' && i + 1 < s.length()) {
				b.append(c).append(s.charAt(++i));
			} else {
				b.append(Character.toUpperCase(c));
			}
		}
		return b.toString();
	}

	/** Extra space on the left for an icon (item counters etc.). */
	protected int iconSpace(boolean preview) {
		return 0;
	}

	protected void renderIcon(GuiGraphics g, int x, int y, boolean preview) {}

	private String open() {
		String b = bracketStyle.get();
		if (b.equals("None")) return "";
		return b.substring(0, b.indexOf(' '));
	}

	private String close() {
		String b = bracketStyle.get();
		if (b.equals("None")) return "";
		return b.substring(b.indexOf(' ') + 1);
	}

	private boolean boxed() {
		return (background != null && background.on()) || (border != null && border.on());
	}

	private int lineStep() {
		return LINE_H + lineSpacing.get().intValue();
	}

	private int lineWidth(String s) {
		Fonts.TextStyle ts = textStyle();
		return Fonts.width(open(), ts) + Fonts.width(s, ts) + Fonts.width(close(), ts);
	}

	private int contentWidth(List<String> lines) {
		int w = 0;
		for (String s : lines) w = Math.max(w, lineWidth(s));
		return w;
	}

	@Override
	public int width(boolean preview) {
		List<String> l = shown(preview);
		if (l.isEmpty()) return 0;
		int cw = contentWidth(l) + iconSpace(preview);
		return boxed() ? Math.max(minWidth.get().intValue(), cw + padX.get().intValue() * 2) : cw;
	}

	@Override
	public int height(boolean preview) {
		List<String> l = shown(preview);
		if (l.isEmpty()) return 0;
		int textH = (l.size() - 1) * lineStep() + 8;
		return boxed() ? textH + padY.get().intValue() * 2 : textH + 1;
	}

	@Override
	public void render(GuiGraphics g, boolean preview) {
		List<String> l = shown(preview);
		if (l.isEmpty()) return;
		Fonts.TextStyle ts = textStyle();
		int w = width(preview);
		int h = height(preview);
		boolean boxed = boxed();
		if (boxed) {
			Draw.box(g, 0, 0, w, h, radius.get().intValue(), background.on(), bgColor.color(),
					border.on(), borderThickness.get().intValue(), borderColor.color());
		}
		int px = boxed ? padX.get().intValue() : 0;
		int py = boxed ? padY.get().intValue() : 0;
		String open = open(), close = close();
		boolean sh = shadow.on();
		int icon = iconSpace(preview);
		for (int i = 0; i < l.size(); i++) {
			String s = l.get(i);
			int lw = lineWidth(s) + icon;
			int x = switch (align.get()) {
				case "Left" -> px;
				case "Right" -> w - px - lw;
				default -> (w - lw) / 2;
			};
			if (icon > 0 && i == 0) renderIcon(g, x, py + i * lineStep(), preview);
			x += icon;
			int y = py + i * lineStep();
			int tc = textColor.color(i * 12);
			int bc = customBracketColor.on() ? bracketColor.color(i * 12) : tc;
			if (!open.isEmpty()) {
				Fonts.draw(g, open, x, y, bc, sh, ts);
				x += Fonts.width(open, ts);
			}
			Fonts.draw(g, s, x, y, tc, sh, ts);
			x += Fonts.width(s, ts);
			if (!close.isEmpty()) {
				Fonts.draw(g, close, x, y, bc, sh, ts);
			}
		}
	}
}
