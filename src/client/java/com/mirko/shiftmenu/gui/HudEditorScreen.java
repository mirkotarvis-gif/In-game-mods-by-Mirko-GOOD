package com.mirko.shiftmenu.gui;

import com.mirko.shiftmenu.Config;
import com.mirko.shiftmenu.module.HudModule;
import com.mirko.shiftmenu.module.ModuleManager;
import com.mirko.shiftmenu.module.impl.GeneralSettings;
import com.mirko.shiftmenu.util.Colors;
import com.mirko.shiftmenu.util.Draw;
import com.mirko.shiftmenu.util.Keys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

/**
 * The HUD layout editor that opens on Left Shift: drag modules around (they snap to the center,
 * edges, other modules and an optional grid), scroll to resize, right-click for settings,
 * and click MODS for the full mod menu.
 */
public class HudEditorScreen extends Screen {
	private static final int BTN_W = 86;
	private static final int BTN_H = 24;
	private static final int SNAP = 4;

	private HudModule dragging;
	private int dragOffX, dragOffY;
	private int guideX = -1, guideY = -1;
	private final long openedAt = System.currentTimeMillis();

	public HudEditorScreen() {
		super(Component.literal("Edit HUD Layout"));
	}

	private static GeneralSettings cfg() {
		return ModuleManager.SETTINGS;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		float t = cfg().animations.on() ? Math.min(1f, (System.currentTimeMillis() - openedAt) / 150f) : 1f;
		int a = (int) (cfg().editorDim.get() / 100.0 * 255 * t);
		g.fill(0, 0, this.width, this.height, a << 24);
		int grid = cfg().gridSnap.get().intValue();
		if (grid > 0 && cfg().showGrid.on()) {
			for (int x = 0; x < this.width; x += grid) g.fill(x, 0, x + 1, this.height, 0x18FFFFFF);
			for (int y = 0; y < this.height; y += grid) g.fill(0, y, this.width, y + 1, 0x18FFFFFF);
		}
	}

	private int btnX() {
		return this.width / 2 - BTN_W / 2;
	}

	private int btnY() {
		return this.height / 2 - BTN_H / 2;
	}

	private boolean overButton(double mx, double my) {
		return Draw.inside(mx, my, btnX(), btnY(), BTN_W, BTN_H);
	}

	private HudModule moduleAt(double mx, double my) {
		for (int i = ModuleManager.MODULES.size() - 1; i >= 0; i--) {
			HudModule m = ModuleManager.MODULES.get(i);
			if (!m.isEnabled() || !m.isDraggable()) continue;
			int x = m.getX(this.width, true), y = m.getY(this.height, true);
			int w = Math.max(4, m.scaledWidth(true)), h = Math.max(4, m.scaledHeight(true));
			if (Draw.inside(mx, my, x - 1, y - 1, w + 2, h + 2)) return m;
		}
		return null;
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		int accent = cfg().accent.color();

		if (dragging != null) {
			if (guideX >= 0) g.fill(guideX, 0, guideX + 1, this.height, Colors.withAlpha(accent, 0xCC));
			if (guideY >= 0) g.fill(0, guideY, this.width, guideY + 1, Colors.withAlpha(accent, 0xCC));
		}

		// crosshair preview so you can see it while arranging
		if (ModuleManager.CROSSHAIR.isEnabled()) ModuleManager.CROSSHAIR.drawCentered(g);

		HudModule hovered = dragging != null ? dragging : moduleAt(mouseX, mouseY);
		for (HudModule m : ModuleManager.MODULES) {
			if (!m.isEnabled() || !m.isDraggable()) continue;
			m.renderAt(g, true);
			int x = m.getX(this.width, true), y = m.getY(this.height, true);
			int w = m.scaledWidth(true), h = m.scaledHeight(true);
			if (m == hovered) {
				g.fill(x, y, x + w, y + h, 0x22FFFFFF);
				Draw.outline(g, x - 1, y - 1, x + w + 1, y + h + 1, 0xFFFFFFFF);
				String label = m.name + "  " + Math.round(m.scale.get() * 100) + "%";
				int ly = y > 12 ? y - 11 : y + h + 3;
				int lw = Draw.width(label) + 6;
				Draw.rounded(g, x, ly - 1, x + lw, ly + 9, 2, 0xCC000000);
				Draw.text(g, label, x + 3, ly, 0xFFFFFFFF);
			} else if (cfg().outlines.on()) {
				Draw.dashed(g, x - 1, y - 1, x + w + 1, y + h + 1, 0x88FFFFFF);
			}
		}

		int bx = btnX(), by = btnY();
		boolean hb = dragging == null && overButton(mouseX, mouseY);
		Draw.rounded(g, bx, by, bx + BTN_W, by + BTN_H, 5, hb ? 0xF0262629 : 0xE0141416);
		if (hb) Draw.rounded(g, bx + 1, by + BTN_H - 2, bx + BTN_W - 1, by + BTN_H, 1, accent);
		Draw.centered(g, "§lMODS", bx + BTN_W / 2, by + (BTN_H - 8) / 2, 0xFFFFFFFF);

		String hint = "Drag to move  •  Scroll to resize  •  Right-click for settings";
		Draw.centered(g, hint, this.width / 2, by + BTN_H + 8, 0xAAFFFFFF);
		String keyHint = "Press " + Keys.keyName(Config.menuKey) + " or ESC to close";
		Draw.centered(g, keyHint, this.width / 2, by + BTN_H + 20, 0x77FFFFFF);

		super.render(g, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x(), my = event.y();
		int button = event.button();

		if (button == 0 && overButton(mx, my)) {
			click();
			Config.save();
			this.minecraft.setScreen(new ModMenuScreen(null));
			return true;
		}

		HudModule m = moduleAt(mx, my);
		if (m != null) {
			if (button == 0) {
				dragging = m;
				dragOffX = (int) mx - m.getX(this.width, true);
				dragOffY = (int) my - m.getY(this.height, true);
			} else if (button == 1) {
				click();
				Config.save();
				this.minecraft.setScreen(new ModMenuScreen(m));
			} else if (button == 2) {
				m.scale.reset();
			}
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (dragging == null) return super.mouseDragged(event, dx, dy);
		int w = dragging.scaledWidth(true), h = dragging.scaledHeight(true);
		int nx = (int) event.x() - dragOffX;
		int ny = (int) event.y() - dragOffY;

		int grid = cfg().gridSnap.get().intValue();
		if (grid > 0) {
			nx = Math.round(nx / (float) grid) * grid;
			ny = Math.round(ny / (float) grid) * grid;
		}

		// collect snap lines
		java.util.List<Integer> xs = new java.util.ArrayList<>();
		java.util.List<Integer> ys = new java.util.ArrayList<>();
		if (cfg().snapCenter.on()) {
			xs.add(this.width / 2);
			ys.add(this.height / 2);
		}
		if (cfg().snapEdges.on()) {
			xs.add(0);
			xs.add(this.width);
			ys.add(0);
			ys.add(this.height);
		}
		if (cfg().snapModules.on()) {
			for (HudModule o : ModuleManager.MODULES) {
				if (o == dragging || !o.isEnabled() || !o.isDraggable()) continue;
				int ox = o.getX(this.width, true), oy = o.getY(this.height, true);
				int ow = o.scaledWidth(true), oh = o.scaledHeight(true);
				xs.add(ox);
				xs.add(ox + ow);
				xs.add(ox + ow / 2);
				ys.add(oy);
				ys.add(oy + oh);
				ys.add(oy + oh / 2);
			}
		}

		guideX = -1;
		int bestX = SNAP + 1;
		int[] myX = {nx, nx + w, nx + w / 2};
		for (int line : xs) {
			for (int k = 0; k < 3; k++) {
				int d = line - myX[k];
				if (Math.abs(d) < Math.abs(bestX)) {
					bestX = d;
					guideX = line;
				}
			}
		}
		if (guideX >= 0) nx += bestX;

		guideY = -1;
		int bestY = SNAP + 1;
		int[] myY = {ny, ny + h, ny + h / 2};
		for (int line : ys) {
			for (int k = 0; k < 3; k++) {
				int d = line - myY[k];
				if (Math.abs(d) < Math.abs(bestY)) {
					bestY = d;
					guideY = line;
				}
			}
		}
		if (guideY >= 0) ny += bestY;

		nx = Math.max(0, Math.min(nx, this.width - w));
		ny = Math.max(0, Math.min(ny, this.height - h));
		dragging.setPosition(nx, ny, this.width, this.height);
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (dragging != null) {
			dragging = null;
			guideX = guideY = -1;
			Config.save();
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
		HudModule m = moduleAt(mx, my);
		if (m != null && scrollY != 0) {
			m.scale.set(m.scale.get() + (scrollY > 0 ? 0.05 : -0.05));
			Config.save();
			return true;
		}
		return super.mouseScrolled(mx, my, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == Config.menuKey) {
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

	static void click() {
		if (!ModuleManager.SETTINGS.sounds.on()) return;
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
}
