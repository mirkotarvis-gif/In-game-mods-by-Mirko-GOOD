package com.mirko.shiftmenu.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * {placeholder} replacement + &-color codes, used by every customizable text.
 */
public final class Placeholders {
	public static final String HELP =
			"{fps} {cps} {rcps} {ping} {x} {y} {z} {facing} {axis} {time} {time12} {date} "
					+ "{speed} {mem} {server} {players} {name} {health} {food} {level} {combo} {reach} {session}. "
					+ "Colors: &0-&9 &a-&f, &l bold, &o italic, &n underline, &m strike, &r reset.";

	private static final Pattern COLOR = Pattern.compile("&([0-9a-fk-orA-FK-OR])");
	private static final DateTimeFormatter T24 = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
	private static final DateTimeFormatter T12 = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);

	// session timer + speed, updated every tick
	private static long sessionStart = -1;
	private static double lastX, lastZ, speed;
	private static boolean hasLast;

	private Placeholders() {}

	public static void tick(Minecraft mc) {
		LocalPlayer p = mc.player;
		if (p == null) {
			sessionStart = -1;
			hasLast = false;
			speed = 0;
			return;
		}
		if (sessionStart < 0) sessionStart = System.currentTimeMillis();
		if (hasLast) {
			double dx = p.getX() - lastX, dz = p.getZ() - lastZ;
			double now = Math.sqrt(dx * dx + dz * dz) * 20.0;
			speed = speed * 0.6 + now * 0.4;
		}
		lastX = p.getX();
		lastZ = p.getZ();
		hasLast = true;
	}

	public static double speed() {
		return speed;
	}

	public static String session() {
		long ms = sessionStart < 0 ? 0 : System.currentTimeMillis() - sessionStart;
		long s = ms / 1000;
		long h = s / 3600, m = (s % 3600) / 60, sec = s % 60;
		return h > 0 ? String.format("%d:%02d:%02d", h, m, sec) : String.format("%d:%02d", m, sec);
	}

	public static int ping() {
		Minecraft mc = Minecraft.getInstance();
		ClientPacketListener conn = mc.getConnection();
		if (conn == null || mc.player == null) return 0;
		PlayerInfo info = conn.getPlayerInfo(mc.player.getUUID());
		return info == null ? 0 : info.getLatency();
	}

	public static int players() {
		ClientPacketListener conn = Minecraft.getInstance().getConnection();
		return conn == null ? 1 : conn.getOnlinePlayers().size();
	}

	public static String server() {
		ServerData data = Minecraft.getInstance().getCurrentServer();
		return data == null ? "Singleplayer" : data.ip;
	}

	public static String facing(Direction d) {
		return switch (d) {
			case NORTH -> "North";
			case SOUTH -> "South";
			case EAST -> "East";
			case WEST -> "West";
			case UP -> "Up";
			case DOWN -> "Down";
		};
	}

	public static String axis(Direction d) {
		return switch (d) {
			case NORTH -> "-Z";
			case SOUTH -> "+Z";
			case EAST -> "+X";
			case WEST -> "-X";
			default -> "";
		};
	}

	public static String memPercent() {
		Runtime rt = Runtime.getRuntime();
		return ((rt.totalMemory() - rt.freeMemory()) * 100 / Math.max(1, rt.maxMemory())) + "%";
	}

	/** Converts &-codes to Minecraft section-sign codes. */
	public static String colors(String s) {
		return COLOR.matcher(s).replaceAll("§$1");
	}

	public static String apply(String s) {
		if (s.indexOf('{') >= 0) {
			Minecraft mc = Minecraft.getInstance();
			LocalPlayer p = mc.player;
			s = rep(s, "{fps}", () -> String.valueOf(mc.getFps()));
			s = rep(s, "{cps}", () -> String.valueOf(Keys.leftCps()));
			s = rep(s, "{rcps}", () -> String.valueOf(Keys.rightCps()));
			s = rep(s, "{ping}", () -> String.valueOf(ping()));
			s = rep(s, "{x}", () -> p == null ? "0" : String.valueOf((int) Math.floor(p.getX())));
			s = rep(s, "{y}", () -> p == null ? "64" : String.valueOf((int) Math.floor(p.getY())));
			s = rep(s, "{z}", () -> p == null ? "0" : String.valueOf((int) Math.floor(p.getZ())));
			s = rep(s, "{facing}", () -> p == null ? "North" : facing(p.getDirection()));
			s = rep(s, "{axis}", () -> p == null ? "-Z" : axis(p.getDirection()));
			s = rep(s, "{time}", () -> LocalTime.now().format(T24));
			s = rep(s, "{time12}", () -> LocalTime.now().format(T12));
			s = rep(s, "{date}", () -> LocalDate.now().format(DATE));
			s = rep(s, "{speed}", () -> String.format("%.2f", speed));
			s = rep(s, "{mem}", Placeholders::memPercent);
			s = rep(s, "{server}", Placeholders::server);
			s = rep(s, "{players}", () -> String.valueOf(players()));
			s = rep(s, "{name}", () -> p == null ? "Player" : p.getName().getString());
			s = rep(s, "{health}", () -> p == null ? "20" : String.valueOf((int) Math.ceil(p.getHealth())));
			s = rep(s, "{food}", () -> p == null ? "20" : String.valueOf(p.getFoodData().getFoodLevel()));
			s = rep(s, "{level}", () -> p == null ? "0" : String.valueOf(p.experienceLevel));
			s = rep(s, "{combo}", () -> String.valueOf(CombatTracker.combo()));
			s = rep(s, "{reach}", () -> String.format("%.2f", CombatTracker.lastReach()));
			s = rep(s, "{session}", Placeholders::session);
		}
		if (s.indexOf('&') >= 0) s = colors(s);
		return s;
	}

	private static String rep(String s, String key, java.util.function.Supplier<String> value) {
		return s.contains(key) ? s.replace(key, value.get()) : s;
	}
}
