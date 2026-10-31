package com.mirko.shiftmenu.module;

import com.mirko.shiftmenu.module.impl.*;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
	// must be first: other modules read the global scale from it
	public static final GeneralSettings SETTINGS = new GeneralSettings();

	public static final FpsModule FPS = new FpsModule();
	public static final CpsModule CPS = new CpsModule();
	public static final KeystrokesModule KEYSTROKES = new KeystrokesModule();
	public static final ToggleSprintModule TOGGLE_SPRINT = new ToggleSprintModule();
	public static final CrosshairModule CROSSHAIR = new CrosshairModule();
	public static final ArmorStatusModule ARMOR = new ArmorStatusModule();
	public static final PotionEffectsModule POTIONS = new PotionEffectsModule();
	public static final ReachModule REACH = new ReachModule();
	public static final ComboModule COMBO = new ComboModule();
	public static final CoordinatesModule COORDINATES = new CoordinatesModule();
	public static final PingModule PING = new PingModule();
	public static final DirectionModule DIRECTION = new DirectionModule();
	public static final ClockModule CLOCK = new ClockModule();
	public static final MemoryModule MEMORY = new MemoryModule();
	public static final SpeedModule SPEED = new SpeedModule();
	public static final ServerAddressModule SERVER = new ServerAddressModule();
	public static final SessionTimeModule SESSION = new SessionTimeModule();
	public static final PlayerCountModule PLAYERS = new PlayerCountModule();
	public static final CustomTextModule CUSTOM_1 = new CustomTextModule(1, 0.5f, 0.15f, "&bHello, &f{name}");
	public static final CustomTextModule CUSTOM_2 = new CustomTextModule(2, 0.5f, 0.2f, "&7{date} &f{time}");
	public static final CustomTextModule CUSTOM_3 = new CustomTextModule(3, 0.5f, 0.25f, "&cHP &f{health}  &6Food &f{food}");

	public static final WatermarkModule WATERMARK = new WatermarkModule();
	public static final TargetInfoModule TARGET_INFO = new TargetInfoModule();
	public static final FpsGraphModule FPS_GRAPH = new FpsGraphModule();
	public static final CompassModule COMPASS = new CompassModule();
	public static final StopwatchModule STOPWATCH = new StopwatchModule();
	public static final ZoomModule ZOOM = new ZoomModule();
	public static final FullbrightModule FULLBRIGHT = new FullbrightModule();
	public static final VanillaHudModule VANILLA_HUD = new VanillaHudModule();

	/** Modules shown in the mod menu grid / on the HUD. */
	public static final List<HudModule> MODULES = List.of(
			FPS, CPS, KEYSTROKES, TOGGLE_SPRINT, CROSSHAIR, ARMOR, POTIONS, REACH, COMBO, COORDINATES,
			PING, DIRECTION, CLOCK, MEMORY, SPEED, SERVER, SESSION, PLAYERS, CUSTOM_1, CUSTOM_2, CUSTOM_3,
			WATERMARK, TARGET_INFO, FPS_GRAPH, COMPASS, STOPWATCH, ZOOM, FULLBRIGHT, VANILLA_HUD,
			new HealthModule(), new FoodModule(), new SaturationModule(), new ArmorPointsModule(), new XpLevelModule(),
			new AirModule(), new HeldItemModule(), new HeldDurabilityModule(), new ItemCounterModule(),
			new ArrowCounterModule(), new TotemCounterModule(), new PearlCounterModule(), new GappleCounterModule(),
			new BlockCounterModule(), new PotionCounterModule(), new RotationModule(), new NetherCoordsModule(),
			new ChunkModule(), new VelocityModule(), new FallDistanceModule(), new GameModeModule(), new WeatherModule(),
			new DateModule(), new TargetDistanceModule(), new TotalHitsModule(), new MaxComboModule(),
			new AccuracyModule(), new SystemInfoModule(), new EntityCountModule(), new NearbyPlayersModule()
	);

	/** Everything that gets saved (modules + the general settings page). */
	public static final List<HudModule> ALL;

	static {
		List<HudModule> all = new ArrayList<>(MODULES);
		all.add(SETTINGS);
		ALL = List.copyOf(all);
	}

	private ModuleManager() {}

	/** Forces class init. */
	public static void init() {}
}
