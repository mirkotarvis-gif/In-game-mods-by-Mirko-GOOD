package com.mirko.shiftmenu.module;

import com.mirko.shiftmenu.module.setting.BoolSetting;
import com.mirko.shiftmenu.module.setting.TextSetting;
import com.mirko.shiftmenu.util.Placeholders;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A single-line text module whose text is a customizable format, e.g. "{value} FPS".
 * The format also accepts every global placeholder ({ping}, {x}, ...) and &-color codes.
 * Modules can show an item icon next to the text (counters).
 */
public abstract class ValueModule extends TextModule {
	public final TextSetting format;
	public final BoolSetting showIcon;

	protected ValueModule(String id, String name, Category category, boolean enabled, float x, float y, String defaultFormat) {
		super(id, name, category, enabled, x, y, true, false);
		format = add(new TextSetting("Format", defaultFormat).in("Text"));
		showIcon = add(new BoolSetting("Show Icon", true).in("Text").showIf(this::hasIcon));
		// show format + icon first in their section
		settings.remove(format);
		settings.remove(showIcon);
		int i = settings.indexOf(textColor);
		settings.add(i, showIcon);
		settings.add(i, format);
	}

	/** The raw value inserted for {value}. Return null to hide the module. */
	protected abstract String value(boolean preview);

	protected boolean hasIcon() {
		return false;
	}

	/** Item drawn left of the text when hasIcon() is true. */
	protected ItemStack icon(boolean preview) {
		return ItemStack.EMPTY;
	}

	@Override
	protected int iconSpace(boolean preview) {
		return hasIcon() && showIcon.on() ? 14 : 0;
	}

	@Override
	protected void renderIcon(GuiGraphics g, int x, int y, boolean preview) {
		ItemStack st = icon(preview);
		if (st == null || st.isEmpty()) return;
		g.pose().pushMatrix();
		g.pose().translate((float) x, (float) (y - 2));
		g.pose().scale(0.75f, 0.75f);
		g.renderItem(st, 0, 0);
		g.pose().popMatrix();
	}

	@Override
	protected List<String> lines(boolean preview) {
		String v = value(preview);
		if (v == null) return List.of();
		return List.of(Placeholders.apply(format.get().replace("{value}", v)));
	}
}
