package com.mirko.shiftmenu.gui;

import com.mirko.shiftmenu.Config;
import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.ModuleManager;
import com.mirko.shiftmenu.module.impl.GeneralSettings;
import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.ColorSetting;
import com.mirko.shiftmenu.module.setting.InfoSetting;
import com.mirko.shiftmenu.module.setting.ModeSetting;
import com.mirko.shiftmenu.module.setting.NumberSetting;
import com.mirko.shiftmenu.module.setting.Setting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.Colors;
import com.mirko.shiftmenu.util.Draw;
import com.mirko.shiftmenu.util.Keys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

/**
 * The mod menu: a rounded panel with a grid of module cards (live preview, name, options button,
 * ENABLED / DISABLED bar) and a settings page for each module with grouped sections, switches,
 * sliders, option pickers, text inputs and full color pickers.
 */
public class ModMenuScreen extends Screen {
	private int TEXT = 0xFFFFFFFF;
	private int TEXT_DIM = 0xFF9A9AA5;
	private static final int PREVIEW_BG = 0xFF141418;
	private static final int TRACK = 0xFF3A3A42;
	private static final String[] TABS = {"ALL", "ON", "HUD", "COMBAT", "INFO", "MECHANIC"};
	private static final int[] PRESETS = {0xFFFFFFFF, 0xFFAAAAAA, 0xFF000000, 0xFFFF5555, 0xFFFFAA00, 0xFFFFFF55,
			0xFF55FF55, 0xFF55FFFF, 0xFF3B82F6, 0xFFAA55FF, 0xFFFF77C8};

	private HudModule settingsFor;
	private String tab = "ALL";
	private EditBox search;
	private double scroll;
	private int maxScroll;
	private boolean listeningForKey;
	private com.mirko.shiftmenu.module.setting.KeySetting listeningSetting;
	private final long openedAt = System.currentTimeMillis();

	private record Drag(int x, int w, DoubleConsumer set) {}

	private Drag drag;

	private record Hit(int x, int y, int w, int h, IntConsumer action) {}

	private final List<Hit> hits = new ArrayList<>();
	private int clipX1, clipY1, clipX2, clipY2;
	private boolean clipping;

	private final Map<Setting<?>, EditBox> boxes = new HashMap<>();
	private final Set<ColorSetting> expanded = new HashSet<>();

	private int px, py, pw, ph;

	public ModMenuScreen(HudModule settingsFor) {
		super(Component.literal("Mods"));
		this.settingsFor = settingsFor;
	}

	private static GeneralSettings cfg() {
		return ModuleManager.SETTINGS;
	}

	private static int accent() {
		return cfg().accent.color();
	}

	private static int card() {
		return cfg().cardColor.color();
	}

	private static int cardHover() {
		return Colors.lerp(card(), 0xFFFFFFFF, 0.06f);
	}

	private static int button() {
		return Colors.lerp(card(), 0xFFFFFFFF, 0.05f);
	}

	private static int buttonHover() {
		return Colors.lerp(card(), 0xFFFFFFFF, 0.12f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		if (cfg().blur.on()) super.renderBackground(g, mouseX, mouseY, partialTick);
		else g.fill(0, 0, this.width, this.height, 0x66000000);
	}

	private void layout() {
		pw = Math.min(cfg().menuWidth.get().intValue(), this.width - 20);
		ph = Math.min(cfg().menuHeight.get().intValue(), this.height - 20);
		px = (this.width - pw) / 2;
		py = (this.height - ph) / 2;
	}

	@Override
	protected void init() {
		layout();
		String prev = search != null ? search.getValue() : "";
		search = new EditBox(this.font, 0, 0, 92, 10, Component.literal("Search"));
		search.setBordered(false);
		search.setMaxLength(32);
		search.setTextColor(0xFFFFFFFF);
		search.setHint(Component.literal("Search...").withStyle(ChatFormatting.DARK_GRAY));
		search.setValue(prev);
		search.setResponder(s -> scroll = 0);
		this.addRenderableWidget(search);
		boxes.clear(); // children were cleared by the screen
	}

	private void openPage(HudModule m) {
		for (EditBox b : boxes.values()) this.removeWidget(b);
		boxes.clear();
		expanded.clear();
		settingsFor = m;
		scroll = 0;
		drag = null;
		search.setFocused(false);
		this.setFocused(null);
		Config.save();
	}

	// ------------------------------------------------------------------ rendering

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		layout();
		hits.clear();
		TEXT = cfg().menuText.color();
		TEXT_DIM = cfg().menuTextDim.color();

		float open = cfg().animations.on() ? Math.min(1f, (System.currentTimeMillis() - openedAt) / 120f) : 1f;
		int panel = cfg().panelColor.color();
		int panelColor = Colors.withAlpha(panel, (int) (((panel >>> 24) & 0xFF) * open));
		Draw.rounded(g, px, py, px + pw, py + ph, cfg().panelRadius.get().intValue(), panelColor);

		if (settingsFor == null) {
			renderGridHeader(g, mouseX, mouseY);
			renderGrid(g, mouseX, mouseY);
		} else {
			search.visible = false;
			renderSettings(g, mouseX, mouseY, partialTick);
		}
		renderFooter(g, mouseX, mouseY);

		super.render(g, mouseX, mouseY, partialTick);
	}

	private void renderGridHeader(GuiGraphics g, int mx, int my) {
		int hx = px + 12;
		int hy = py + 10;
		Draw.text(g, "§lMODS", hx, hy, TEXT);
		int tx = hx + Draw.width("§lMODS") + 12;
		for (String t : TABS) {
			int tw = Draw.width(t) + 10;
			boolean sel = t.equals(tab);
			boolean hov = Draw.inside(mx, my, tx, hy - 4, tw, 16);
			Draw.rounded(g, tx, hy - 4, tx + tw, hy + 12, 3, sel ? accent() : (hov ? buttonHover() : button()));
			Draw.centered(g, t, tx + tw / 2, hy, sel || hov ? TEXT : TEXT_DIM);
			final String tabName = t;
			hit(tx, hy - 4, tw, 16, b -> {
				tab = tabName;
				scroll = 0;
			});
			tx += tw + 3;
		}

		int sw = 96, sh = 16;
		int sx = px + pw - 12 - sw, sy = hy - 4;
		search.visible = sx > tx + 4;
		if (search.visible) {
			Draw.rounded(g, sx, sy, sx + sw, sy + sh, 3, search.isFocused() ? buttonHover() : button());
			search.setX(sx + 6);
			search.setY(sy + 4);
			search.setWidth(sw - 12);
		}
		g.fill(px + 8, py + 28, px + pw - 8, py + 29, 0x1FFFFFFF);
	}

	private List<HudModule> filtered() {
		String q = search.getValue().trim().toLowerCase(Locale.ROOT);
		List<HudModule> out = new ArrayList<>();
		for (HudModule m : ModuleManager.MODULES) {
			if (tab.equals("ON") ? !m.isEnabled() : (!tab.equals("ALL") && !m.category.name().equals(tab))) continue;
			if (!q.isEmpty() && !m.name.toLowerCase(Locale.ROOT).contains(q)) continue;
			out.add(m);
		}
		return out;
	}

	private int contentX() {
		return px + 10;
	}

	private int contentY() {
		return py + 34;
	}

	private int contentW() {
		return pw - 20;
	}

	private int contentH() {
		return ph - 34 - 32;
	}

	private void renderGrid(GuiGraphics g, int mx, int my) {
		int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();
		List<HudModule> list = filtered();

		int gap = 6;
		int cols = cfg().columns.is("Auto") ? Math.max(2, (cw + gap) / (96 + gap)) : Integer.parseInt(cfg().columns.get());
		int cardW = (cw - (cols - 1) * gap) / cols;
		int cardH = cfg().cardHeight.get().intValue();
		int rows = (list.size() + cols - 1) / cols;
		int totalH = rows * (cardH + gap) - gap;
		maxScroll = Math.max(0, totalH - ch);
		scroll = Math.max(0, Math.min(scroll, maxScroll));

		if (list.isEmpty()) {
			Draw.centered(g, "No mods found", cx + cw / 2, cy + ch / 2 - 4, TEXT_DIM);
			return;
		}

		int on = cfg().enabledColor.color(), off = cfg().disabledColor.color();
		beginClip(g, cx, cy, cx + cw, cy + ch);
		boolean inView = Draw.inside(mx, my, cx, cy, cw, ch);
		for (int i = 0; i < list.size(); i++) {
			HudModule m = list.get(i);
			int x = cx + (i % cols) * (cardW + gap);
			int y = cy + (i / cols) * (cardH + gap) - (int) scroll;
			if (y + cardH < cy || y > cy + ch) continue;

			boolean hovCard = inView && Draw.inside(mx, my, x, y, cardW, cardH);
			Draw.rounded(g, x, y, x + cardW, y + cardH, 5, hovCard ? cardHover() : card());
			hit(x, y, cardW, cardH, b -> {
				if (b == 1) openPage(m);
				else m.toggle();
			});

			int ax = x + 5, ay = y + 5, aw = cardW - 10, ah = 50;
			Draw.rounded(g, ax, ay, ax + aw, ay + ah, 4, PREVIEW_BG);
			drawPreview(g, m, ax, ay, aw, ah);

			String name = m.name;
			while (Draw.width(name) > cardW - 8 && name.length() > 3) name = name.substring(0, name.length() - 1);
			Draw.centered(g, name, x + cardW / 2, y + 60, TEXT);

			int by = y + cardH - 19, bh = 14;
			int ox = x + 5, ow = 14;
			boolean hovOpt = inView && Draw.inside(mx, my, ox, by, ow, bh);
			Draw.rounded(g, ox, by, ox + ow, by + bh, 3, hovOpt ? buttonHover() : button());
			drawOptionsGlyph(g, ox + ow / 2, by + bh / 2, hovOpt ? TEXT : TEXT_DIM);
			hit(ox, by, ow, bh, b -> openPage(m));

			int tx = ox + ow + 4, tw = x + cardW - 5 - tx;
			boolean hovTog = inView && Draw.inside(mx, my, tx, by, tw, bh);
			int col = m.isEnabled() ? on : off;
			if (hovTog) col = Colors.lerp(col, 0xFFFFFFFF, 0.12f);
			Draw.rounded(g, tx, by, tx + tw, by + bh, 3, col);
			Draw.centered(g, m.isEnabled() ? "§lENABLED" : "§lDISABLED", tx + tw / 2, by + 3, TEXT);
			hit(tx, by, tw, bh, b -> m.toggle());
		}
		endClip(g);
		drawScrollbar(g, cx + cw + 3, cy, ch, totalH);
	}

	private void drawPreview(GuiGraphics g, HudModule m, int ax, int ay, int aw, int ah) {
		int w = Math.max(1, m.width(true)), h = Math.max(1, m.height(true));
		float s = Math.min(1.5f, Math.min((aw - 10) / (float) w, (ah - 10) / (float) h));
		g.pose().pushMatrix();
		g.pose().translate(ax + aw / 2f - w * s / 2f, ay + ah / 2f - h * s / 2f);
		g.pose().scale(s, s);
		m.render(g, true);
		g.pose().popMatrix();
	}

	private static void drawOptionsGlyph(GuiGraphics g, int cx, int cy, int color) {
		g.fill(cx - 4, cy - 3, cx + 4, cy - 2, color);
		g.fill(cx - 4, cy, cx + 4, cy + 1, color);
		g.fill(cx - 4, cy + 3, cx + 4, cy + 4, color);
	}

	// ------------------------------------------------------------------ settings page

	private static final int ROW = 20, GAP = 3, SUB = 14;

	private void renderSettings(GuiGraphics g, int mx, int my, float pt) {
		HudModule m = settingsFor;
		int hx = px + 12, hy = py + 10;

		String back = "<  BACK";
		int bw = Draw.width(back) + 12;
		boolean hb = Draw.inside(mx, my, hx, hy - 4, bw, 16);
		Draw.rounded(g, hx, hy - 4, hx + bw, hy + 12, 3, hb ? buttonHover() : button());
		Draw.centered(g, back, hx + bw / 2, hy, TEXT);
		hit(hx, hy - 4, bw, 16, b -> openPage(null));
		Draw.centered(g, "§l" + m.name.toUpperCase(Locale.ROOT), px + pw / 2, hy, TEXT);
		g.fill(px + 8, py + 28, px + pw - 8, py + 29, 0x1FFFFFFF);

		int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();

		boolean hasPreview = m.hasToggle() && m.width(true) > 0;
		int prevW = hasPreview ? Math.min(130, cw / 3) : 0;
		int listW = hasPreview ? cw - prevW - 10 : cw - 6;
		if (hasPreview) {
			int pvx = cx + listW + 10;
			Draw.rounded(g, pvx, cy, pvx + prevW, cy + 80, 4, PREVIEW_BG);
			drawPreview(g, m, pvx, cy, prevW, 80);
			Draw.centered(g, "Live Preview", pvx + prevW / 2, cy + 84, TEXT_DIM);
		}

		// group visible settings by section, keeping first-appearance order
		LinkedHashMap<String, List<Setting<?>>> sections = new LinkedHashMap<>();
		for (Setting<?> s : m.settings) {
			if (!s.isVisible()) continue;
			sections.computeIfAbsent(s.section(), k -> new ArrayList<>()).add(s);
		}

		for (EditBox b : boxes.values()) b.visible = false;

		beginClip(g, cx, cy, cx + listW, cy + ch);
		boolean inView = Draw.inside(mx, my, cx, cy, listW, ch);
		int y = cy - (int) scroll;

		if (m.hasToggle()) {
			rowBg(g, cx, y, listW, ROW);
			Draw.text(g, "§lEnabled", cx + 8, y + 6, TEXT);
			Draw.toggleSwitch(g, cx + listW - 8 - 18, y + 5, m.isEnabled());
			hit(cx, y, listW, ROW, b -> m.toggle());
			y += ROW + GAP;
		}

		for (Map.Entry<String, List<Setting<?>>> e : sections.entrySet()) {
			y += 4;
			Draw.text(g, e.getKey().toUpperCase(Locale.ROOT), cx + 2, y, Colors.withAlpha(accent(), 0xFF));
			y += 12;
			for (Setting<?> s : e.getValue()) {
				y = renderSetting(g, s, cx, y, listW, mx, my, inView, pt);
			}
		}

		// reset row
		y += 4;
		int half = (listW - GAP) / 2;
		boolean h1 = inView && Draw.inside(mx, my, cx, y, half, ROW);
		boolean h2 = inView && Draw.inside(mx, my, cx + half + GAP, y, listW - half - GAP, ROW);
		if (m.isDraggable()) {
			Draw.rounded(g, cx, y, cx + half, y + ROW, 3, h1 ? buttonHover() : button());
			Draw.centered(g, "Reset Position", cx + half / 2, y + 6, TEXT);
			hit(cx, y, half, ROW, b -> m.resetPosition());
		}
		int rx2 = m.isDraggable() ? cx + half + GAP : cx;
		int rw2 = m.isDraggable() ? listW - half - GAP : listW;
		int danger = cfg().disabledColor.color();
		Draw.rounded(g, rx2, y, rx2 + rw2, y + ROW, 3, h2 || (!m.isDraggable() && h1) ? danger : Colors.lerp(danger, 0xFF000000, 0.55f));
		Draw.centered(g, "Reset Settings", rx2 + rw2 / 2, y + 6, TEXT);
		hit(rx2, y, rw2, ROW, b -> {
			m.resetSettings();
			expanded.clear();
		});
		y += ROW;

		int totalH = y + (int) scroll - cy;
		maxScroll = Math.max(0, totalH - ch);
		scroll = Math.max(0, Math.min(scroll, maxScroll));

		endClip(g);
		drawScrollbar(g, cx + listW + 3, cy, ch, totalH);
	}

	private int renderSetting(GuiGraphics g, Setting<?> s, int cx, int y, int listW, int mx, int my, boolean inView, float pt) {
		int right = cx + listW - 8;
		if (s instanceof InfoSetting info) {
			List<String> lines = wrap(info.get(), listW - 16);
			int h = lines.size() * 10 + 8;
			rowBg(g, cx, y, listW, h);
			for (int i = 0; i < lines.size(); i++) Draw.text(g, lines.get(i), cx + 8, y + 5 + i * 10, TEXT_DIM);
			return y + h + GAP;
		}

		rowBg(g, cx, y, listW, ROW);
		Draw.text(g, s.name, cx + 8, y + 6, TEXT);

		if (s instanceof BoolSetting bs) {
			Draw.toggleSwitch(g, right - 18, y + 5, bs.on());
			hit(cx, y, listW, ROW, b -> bs.toggle());
		} else if (s instanceof NumberSetting ns) {
			int valW = 34;
			int slW = Math.max(40, Math.min(120, listW - 16 - Draw.width(s.name) - 14 - valW));
			int slX = right - valW - slW;
			slider(g, slX, y + 9, slW, 2, ns.progress(), mx, my, inView, y);
			String v = ns.display();
			Draw.text(g, v, right - Draw.width(v), y + 6, TEXT_DIM);
			hit(slX - 3, y, slW + 6, ROW, b -> drag = new Drag(slX, slW, ns::setProgress));
		} else if (s instanceof ModeSetting ms) {
			int bw = Math.max(90, Draw.width(ms.get()) + 26);
			int bx = right - bw;
			boolean hov = inView && Draw.inside(mx, my, bx, y + 3, bw, ROW - 6);
			Draw.rounded(g, bx, y + 3, bx + bw, y + ROW - 3, 3, hov ? buttonHover() : button());
			Draw.text(g, "<", bx + 5, y + 6, TEXT_DIM);
			Draw.text(g, ">", bx + bw - 5 - Draw.width(">"), y + 6, TEXT_DIM);
			Draw.centered(g, ms.get(), bx + bw / 2, y + 6, TEXT);
			hit(bx, y + 3, bw, ROW - 6, b -> {
				if (b == 1) ms.previous();
				else ms.next();
			});
		} else if (s instanceof TextSetting ts) {
			int bw = Math.min(180, listW - 24 - Draw.width(s.name));
			int bx = right - bw;
			EditBox box = boxFor(ts, ts.maxLength, ts::set);
			if (!box.isFocused() && !box.getValue().equals(ts.get())) box.setValue(ts.get());
			renderBox(g, box, bx, y + 3, bw, ROW - 6, mx, my, pt);
		} else if (s instanceof ColorSetting cs) {
			return renderColor(g, cs, cx, y, listW, mx, my, inView, pt);
		} else if (s instanceof com.mirko.shiftmenu.module.setting.KeySetting ks) {
			boolean listening = listeningSetting == ks;
			String label = listening ? "PRESS A KEY..." : (ks.isBound() ? Keys.keyName(ks.get()).toUpperCase(Locale.ROOT) : "NONE");
			int bw = Math.max(80, Draw.width(label) + 16);
			int bx = right - bw;
			boolean hov = inView && Draw.inside(mx, my, bx, y + 3, bw, ROW - 6);
			Draw.rounded(g, bx, y + 3, bx + bw, y + ROW - 3, 3, listening ? accent() : (hov ? buttonHover() : button()));
			Draw.centered(g, label, bx + bw / 2, y + 6, TEXT);
			hit(bx, y + 3, bw, ROW - 6, b -> {
				if (b == 1) {
					ks.set(-1);
					listeningSetting = null;
				} else {
					listeningSetting = listening ? null : ks;
				}
			});
		}
		return y + ROW + GAP;
	}

	private int renderColor(GuiGraphics g, ColorSetting cs, int cx, int y, int listW, int mx, int my, boolean inView, float pt) {
		int right = cx + listW - 8;
		boolean open = expanded.contains(cs);
		// swatch
		int sx = right - 24, sy = y + 4;
		checker(g, sx, sy, 24, 12);
		g.fill(sx, sy, sx + 24, sy + 12, cs.color());
		Draw.outline(g, sx - 1, sy - 1, sx + 25, sy + 13, 0x55FFFFFF);
		String label = cs.chroma ? "Chroma" : cs.hex();
		Draw.text(g, label, sx - 6 - Draw.width(label), y + 6, TEXT_DIM);
		Draw.text(g, open ? "-" : "+", cx + listW - 8 - 24 - 6 - Draw.width(label) - 14, y + 6, TEXT_DIM);
		hit(cx, y, listW, ROW, b -> {
			if (!expanded.remove(cs)) expanded.add(cs);
		});
		y += ROW;
		if (!open) return y + GAP;

		// expanded picker
		int ex = cx + 8, ew = listW - 16;
		int bodyH = SUB * 6 + 6;
		Draw.rounded(g, cx, y - 2, cx + listW, y + bodyH, 3, Colors.lerp(card(), 0xFF000000, 0.25f));
		int labelW = 64;
		int barX = ex + labelW, barW = ew - labelW - 4;
		y += 2;

		// hue
		Draw.text(g, "Hue", ex, y + 3, TEXT_DIM);
		for (int i = 0; i < barW; i += 2) g.fill(barX + i, y + 2, barX + Math.min(i + 2, barW), y + 10, Colors.hsv(i / (float) barW, 1f, 1f));
		knob(g, barX + Math.round(cs.hue * barW), y + 1, 10);
		hit(barX - 3, y, barW + 6, SUB, b -> drag = new Drag(barX, barW, p -> cs.setHsba((float) p, cs.sat, cs.bri, cs.alpha)));
		y += SUB;
		// saturation
		Draw.text(g, "Saturation", ex, y + 3, TEXT_DIM);
		for (int i = 0; i < barW; i += 2) g.fill(barX + i, y + 2, barX + Math.min(i + 2, barW), y + 10, Colors.hsv(cs.hue, i / (float) barW, Math.max(0.2f, cs.bri)));
		knob(g, barX + Math.round(cs.sat * barW), y + 1, 10);
		hit(barX - 3, y, barW + 6, SUB, b -> drag = new Drag(barX, barW, p -> cs.setHsba(cs.hue, (float) p, cs.bri, cs.alpha)));
		y += SUB;
		// brightness
		Draw.text(g, "Brightness", ex, y + 3, TEXT_DIM);
		for (int i = 0; i < barW; i += 2) g.fill(barX + i, y + 2, barX + Math.min(i + 2, barW), y + 10, Colors.hsv(cs.hue, cs.sat, i / (float) barW));
		knob(g, barX + Math.round(cs.bri * barW), y + 1, 10);
		hit(barX - 3, y, barW + 6, SUB, b -> drag = new Drag(barX, barW, p -> cs.setHsba(cs.hue, cs.sat, (float) p, cs.alpha)));
		y += SUB;
		// opacity
		Draw.text(g, "Opacity", ex, y + 3, TEXT_DIM);
		checker(g, barX, y + 2, barW, 8);
		int opaque = cs.color() | 0xFF000000;
		for (int i = 0; i < barW; i += 2) g.fill(barX + i, y + 2, barX + Math.min(i + 2, barW), y + 10, Colors.withAlpha(opaque, Math.round(i / (float) barW * 255)));
		knob(g, barX + Math.round(cs.alpha * barW), y + 1, 10);
		hit(barX - 3, y, barW + 6, SUB, b -> drag = new Drag(barX, barW, p -> cs.setHsba(cs.hue, cs.sat, cs.bri, (float) p)));
		y += SUB;
		// chroma + presets
		Draw.text(g, "Chroma", ex, y + 3, TEXT_DIM);
		Draw.toggleSwitch(g, barX, y + 2, cs.chroma);
		hit(barX, y, 22, SUB, b -> cs.chroma = !cs.chroma);
		int px0 = barX + 28;
		for (int i = 0; i < PRESETS.length; i++) {
			int sx2 = px0 + i * 12;
			if (sx2 + 10 > barX + barW) break;
			g.fill(sx2, y + 2, sx2 + 10, y + 12, PRESETS[i]);
			Draw.outline(g, sx2 - 1, y + 1, sx2 + 11, y + 13, 0x55FFFFFF);
			final int preset = PRESETS[i];
			hit(sx2, y + 1, 10, 12, b -> {
				cs.chroma = false;
				cs.setArgb((Math.round(cs.alpha * 255) << 24) | (preset & 0xFFFFFF));
			});
		}
		y += SUB;
		// hex input
		Draw.text(g, "Hex", ex, y + 3, TEXT_DIM);
		EditBox box = boxFor(cs, 9, v -> cs.setHex(v));
		if (!box.isFocused() && !box.getValue().equalsIgnoreCase(cs.hex())) box.setValue(cs.hex());
		renderBox(g, box, barX, y, Math.min(90, barW), 13, mx, my, pt);
		y += SUB + 4;
		return y + GAP;
	}

	private EditBox boxFor(Setting<?> key, int maxLen, java.util.function.Consumer<String> onChange) {
		EditBox b = boxes.get(key);
		if (b == null) {
			b = new EditBox(this.font, 0, 0, 100, 10, Component.literal(key.name));
			b.setBordered(false);
			b.setMaxLength(maxLen);
			b.setTextColor(0xFFFFFFFF);
			b.setResponder(onChange);
			boxes.put(key, b);
			this.addWidget(b);
		}
		return b;
	}

	private void renderBox(GuiGraphics g, EditBox box, int x, int y, int w, int h, int mx, int my, float pt) {
		boolean f = box.isFocused();
		Draw.rounded(g, x, y, x + w, y + h, 3, f ? Colors.lerp(card(), 0xFF000000, 0.4f) : button());
		if (f) Draw.roundedBorder(g, x, y, x + w, y + h, 3, 1, accent());
		box.setX(x + 4);
		box.setY(y + (h - 8) / 2);
		box.setWidth(w - 8);
		box.visible = y + h > clipY1 && y < clipY2;
		if (box.visible) box.render(g, mx, my, pt);
	}

	private void slider(GuiGraphics g, int x, int y, int w, int h, double progress, int mx, int my, boolean inView, int rowY) {
		g.fill(x, y, x + w, y + h, TRACK);
		int filled = (int) Math.round(progress * w);
		g.fill(x, y, x + filled, y + h, accent());
		boolean hov = inView && Draw.inside(mx, my, x - 3, rowY, w + 6, ROW);
		int kx = x + filled;
		Draw.rounded(g, kx - 3, y - 3, kx + 3, y + h + 3, 2, hov ? 0xFFFFFFFF : 0xFFDADAE0);
	}

	private static void knob(GuiGraphics g, int x, int y, int h) {
		g.fill(x - 2, y - 1, x + 2, y + h + 1, 0xFF000000);
		g.fill(x - 1, y, x + 1, y + h, 0xFFFFFFFF);
	}

	private static void checker(GuiGraphics g, int x, int y, int w, int h) {
		for (int i = 0; i < w; i += 4) {
			for (int j = 0; j < h; j += 4) {
				boolean dark = ((i / 4) + (j / 4)) % 2 == 0;
				g.fill(x + i, y + j, x + Math.min(i + 4, w), y + Math.min(j + 4, h), dark ? 0xFF8A8A8A : 0xFFCFCFCF);
			}
		}
	}

	private List<String> wrap(String text, int width) {
		List<String> out = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		for (String word : text.split(" ")) {
			String test = line.isEmpty() ? word : line + " " + word;
			if (Draw.width(test) > width && !line.isEmpty()) {
				out.add(line.toString());
				line = new StringBuilder(word);
			} else {
				line = new StringBuilder(test);
			}
		}
		if (!line.isEmpty()) out.add(line.toString());
		return out;
	}

	private void rowBg(GuiGraphics g, int x, int y, int w, int h) {
		Draw.rounded(g, x, y, x + w, y + h, 3, card());
	}

	private void drawScrollbar(GuiGraphics g, int x, int y, int h, int totalH) {
		if (maxScroll <= 0) return;
		int barH = Math.max(16, (int) (h * (h / (double) totalH)));
		int barY = y + (int) ((h - barH) * (scroll / maxScroll));
		g.fill(x, y, x + 2, y + h, 0x22FFFFFF);
		g.fill(x, barY, x + 2, barY + barH, 0x99FFFFFF);
	}

	// ------------------------------------------------------------------ footer

	private void renderFooter(GuiGraphics g, int mx, int my) {
		int fy = py + ph - 24, fh = 16;
		g.fill(px + 8, fy - 6, px + pw - 8, fy - 5, 0x1FFFFFFF);

		String edit = "EDIT HUD LAYOUT";
		int ew = Draw.width(edit) + 16;
		int ex = px + 10;
		footerButton(g, mx, my, ex, fy, ew, fh, edit, accent(), Colors.lerp(accent(), 0xFFFFFFFF, 0.15f), b -> {
			Config.save();
			this.minecraft.setScreen(new HudEditorScreen());
		});

		String key = listeningForKey ? "PRESS A KEY..." : "MENU KEY: " + Keys.keyName(Config.menuKey).toUpperCase(Locale.ROOT);
		int kw = Draw.width(key) + 16;
		int kx = px + pw - 10 - kw;
		footerButton(g, mx, my, kx, fy, kw, fh, key, listeningForKey ? 0xFF8A6D1F : button(), listeningForKey ? 0xFFA07F25 : buttonHover(),
				b -> listeningForKey = !listeningForKey);

		String set = "SETTINGS";
		int sw = Draw.width(set) + 16;
		int sx = kx - 6 - sw;
		if (sx > ex + ew + 6) {
			boolean active = settingsFor == ModuleManager.SETTINGS;
			footerButton(g, mx, my, sx, fy, sw, fh, set, active ? accent() : button(), active ? accent() : buttonHover(),
					b -> openPage(active ? null : ModuleManager.SETTINGS));
		}
	}

	private void footerButton(GuiGraphics g, int mx, int my, int x, int y, int w, int h, String label, int color, int hover, IntConsumer action) {
		boolean hov = Draw.inside(mx, my, x, y, w, h);
		Draw.rounded(g, x, y, x + w, y + h, 3, hov ? hover : color);
		Draw.centered(g, label, x + w / 2, y + (h - 8) / 2, TEXT);
		hit(x, y, w, h, action);
	}

	// ------------------------------------------------------------------ hit testing / clipping

	private void beginClip(GuiGraphics g, int x1, int y1, int x2, int y2) {
		g.enableScissor(x1, y1, x2, y2);
		clipping = true;
		clipX1 = x1;
		clipY1 = y1;
		clipX2 = x2;
		clipY2 = y2;
	}

	private void endClip(GuiGraphics g) {
		g.disableScissor();
		clipping = false;
	}

	private void hit(int x, int y, int w, int h, IntConsumer action) {
		if (clipping) {
			int x1 = Math.max(x, clipX1), y1 = Math.max(y, clipY1);
			int x2 = Math.min(x + w, clipX2), y2 = Math.min(y + h, clipY2);
			if (x2 <= x1 || y2 <= y1) return;
			hits.add(new Hit(x1, y1, x2 - x1, y2 - y1, action));
		} else {
			hits.add(new Hit(x, y, w, h, action));
		}
	}

	// ------------------------------------------------------------------ input

	private EditBox focusedBox() {
		if (search.isFocused()) return search;
		for (EditBox b : boxes.values()) if (b.isFocused()) return b;
		return null;
	}

	private void unfocusAll() {
		search.setFocused(false);
		for (EditBox b : boxes.values()) b.setFocused(false);
		this.setFocused(null);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x(), my = event.y();

		// text inputs first
		if (search.visible && settingsFor == null && Draw.inside(mx, my, search.getX() - 6, search.getY() - 4, search.getWidth() + 12, 16)) {
			unfocusAll();
			this.setFocused(search);
			search.setFocused(true);
			return super.mouseClicked(event, doubleClick);
		}
		for (EditBox b : boxes.values()) {
			if (b.visible && Draw.inside(mx, my, b.getX() - 4, b.getY() - 4, b.getWidth() + 8, 16)) {
				unfocusAll();
				this.setFocused(b);
				b.setFocused(true);
				b.mouseClicked(event, doubleClick);
				return true;
			}
		}
		unfocusAll();

		if (listeningForKey) listeningForKey = false;
		com.mirko.shiftmenu.module.setting.KeySetting wasListening = listeningSetting;

		for (int i = hits.size() - 1; i >= 0; i--) {
			Hit h = hits.get(i);
			if (Draw.inside(mx, my, h.x, h.y, h.w, h.h)) {
				HudEditorScreen.click();
				h.action.accept(event.button());
				// clicking anything other than a key row cancels key listening
				if (wasListening != null && listeningSetting == wasListening) listeningSetting = null;
				if (drag != null) applyDrag(mx);
				return true;
			}
		}
		listeningSetting = null;
		return super.mouseClicked(event, doubleClick);
	}

	private void applyDrag(double mx) {
		if (drag == null || drag.w <= 0) return;
		drag.set.accept(Math.max(0, Math.min(1, (mx - drag.x) / drag.w)));
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (drag != null) {
			applyDrag(event.x());
			return true;
		}
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (drag != null) {
			drag = null;
			Config.save();
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
		scroll = Math.max(0, Math.min(maxScroll, scroll - scrollY * 20));
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int key = event.key();
		if (listeningForKey) {
			if (key != GLFW.GLFW_KEY_ESCAPE) {
				Config.menuKey = key;
				Config.save();
				Keys.suppressMenuKey();
			}
			listeningForKey = false;
			return true;
		}
		if (listeningSetting != null) {
			if (key != GLFW.GLFW_KEY_ESCAPE) listeningSetting.set(key);
			listeningSetting = null;
			Config.save();
			return true;
		}
		EditBox focused = focusedBox();
		if (focused != null) {
			if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
				unfocusAll();
				Config.save();
				return true;
			}
			return super.keyPressed(event);
		}
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			if (settingsFor != null) openPage(null);
			else this.onClose();
			return true;
		}
		if (key == Config.menuKey) {
			Keys.suppressMenuKey();
			this.onClose();
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		Config.save();
		super.onClose();
	}
}
