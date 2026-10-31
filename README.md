# ShiftMenu HUD (Fabric, Minecraft 1.21.11)

A client-side mod with a clean PvP-client style HUD and an in-game mod menu that opens with **Left Shift**.
59 modules, ~1,500 settings, 14 fonts.

## Controls
- **Tap Left Shift** in-game: opens the HUD editor (holding Shift to sneak still works).
  - Drag modules to move them (snaps to center, edges, other modules, optional grid)
  - Scroll over a module to resize it, middle-click to reset its size
  - Right-click a module for its settings
  - Click **MODS** for the mod menu
- **Mod menu**: cards with ENABLED / DISABLED, tabs (All, On, HUD, Combat, Info, Mechanic), search,
  the options button for each module, **SETTINGS** for global options, and the menu key changer.
- ESC goes back / closes. The menu key also closes it.
- Key settings (zoom key, stopwatch keys...): click, then press a key. Right-click to unbind.

## Modules (59)
- **HUD:** FPS, Keystrokes, Crosshair, Potion Effects, Custom Text 1-3, Watermark, Vanilla HUD (hide any vanilla HUD part)
- **Combat:** CPS, Armor Status, Reach Display, Combo Counter, Health, Armor Points, Held Durability,
  Arrow / Totem / Pearl / Golden Apple / Potion Counters, Target Info (with health bar), Target Distance,
  Total Hits, Best Combo, Accuracy, Nearby Players
- **Info:** Coordinates, Ping, Direction, Clock, Memory, Speed, Server Address, Session Time, Player Count,
  Hunger, Saturation, XP Level, Oxygen, Held Item, Held Item Counter, Block Counter, Rotation, Nether Coords,
  Chunk Position, Vertical Speed, Fall Distance, Game Mode, Weather, Date, System Info, Entity Count,
  Performance Graph (FPS/CPS/ping), Compass, Stopwatch
- **Mechanic:** Toggle Sprint/Sneak, Zoom (hold or toggle, smooth), Fullbright

## What you can customize
- **Fonts:** Minecraft, Inter, Roboto, Montserrat, Poppins, Ubuntu, Nunito, Rubik, Lexend, Comfortaa,
  JetBrains Mono, Unicode, Enchanting, Illager. Per module, or a global HUD font, plus a separate menu font.
  Bold, italic, underline, strikethrough and uppercase per module.
- **Every text module:** scale, format text (placeholders and &-color codes), text color, shadow, alignment,
  line spacing, brackets and bracket color, background color, corner radius, padding, minimum width,
  border color and thickness, and an item icon for counters.
- **Colors:** full picker (hue, saturation, brightness, opacity), hex input, presets, chroma (rainbow).
- **Menu:** accent, panel, card, enabled/disabled and text colors, menu width/height, card height,
  corner radius, columns, font, blur, animations, sounds.
- **Editor:** snapping (center, edges, modules), grid snap and grid, dim, outlines.
- **HUD:** global scale, default font, hide in menus. **Controls:** tap or press to open, tap time, menu key.

Placeholders: `{fps} {cps} {rcps} {ping} {x} {y} {z} {facing} {axis} {time} {time12} {date} {speed}
{mem} {server} {players} {name} {health} {food} {level} {combo} {reach} {session}`.
Color codes: `&0`-`&9`, `&a`-`&f`, `&l` bold, `&o` italic, `&n` underline, `&m` strikethrough, `&r` reset.

Settings save to `.minecraft/config/shiftmenu.json`.

## Notes
- **Zoom** works by temporarily lowering your FOV (the game's minimum is 30) and sensitivity, then restoring them.
  Default key is C. If the game crashes mid-zoom your FOV might stay low; just set it back in Options.
- **Fullbright** gives your client a hidden night vision effect.
- The bundled fonts are under the SIL Open Font License; see `assets/shiftmenu/font/licenses`.

## Building
Requires JDK 21 or newer.

```
./gradlew build        # Windows: gradlew.bat build
```

The jar ends up at `build/libs/shiftmenu-1.2.0.jar`. Put it in `.minecraft/mods` together with
**Fabric API** for 1.21.11, and launch with **Fabric Loader** for 1.21.11.

No JDK? Push this folder to a GitHub repo: the included workflow builds it automatically, and
the jar shows up under the run's **Artifacts**.
