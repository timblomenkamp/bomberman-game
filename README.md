# Bomberman Game

A feature-rich Bomberman implementation built with **LibGDX**, demonstrating advanced game development concepts and software engineering best practices. The project implements a complete game loop with dynamic enemy AI, progressive difficulty scaling, and persistent state management—all with clean, maintainable architecture.

**Tech Stack:** Java, LibGDX, Gradle

## Getting Started

**Requirements:** Java 17 or newer (JDK).

Clone the repository and launch the desktop game with the bundled Gradle wrapper:

```bash
git clone https://github.com/timblomenkamp/bomberman-game.git
cd bomberman-game
./gradlew desktop:run
```

On Windows, use `gradlew.bat desktop:run` instead.

**Running from an IDE:** Import the project as a Gradle project, then run
`com.timblomenkamp.bomberquest.DesktopLauncher`. On macOS the launcher
automatically restarts the JVM with the required `-XstartOnFirstThread` flag,
so the IDE "Run" button works out of the box.

## Gameplay

**Core Mechanics:**

- **Dynamic Map System**: Load custom game maps from `.properties` files with automatic validation and fallback generation
- **Intelligent Enemy AI**: Real-time pathfinding with distance-based aggression scaling—enemies drift slowly at range, hunt aggressively when close
- **Combat Mechanics**: Place bombs with chainable explosions, collect power-ups (bombs, blast radius, speed), navigate destructible/indestructible walls
- **Multiple Game Modes**: Single map play or progressive "Voyage" mode with increasing difficulty
- **Difficulty Scaling**: Three difficulty levels with dynamic enemy behavior, time pressure, and movement speed modulation
- **Skin/Control Customization**: Multiple character skins and dual input systems (WASD + Arrow Keys)
- **Pause & Resume**: Full game state persistence across pause cycles

## How to Play

**Objective:** Defeat all enemies, then reach the exit to progress.

**Controls:**

| Action      | Key                  |
|-------------|----------------------|
| Move        | Arrow Keys or WASD   |
| Place Bomb  | Spacebar             |
| Pause / Menu| Escape               |

To drop a bomb, walk onto a tile and press **Spacebar**. The bomb detonates after a short delay, sending out a cross-shaped blast that destroys nearby walls, triggers other bombs, and damages enemies (and you—keep your distance!).

**Game Flow:**

1. Select **Voyage** (three progressive maps) or **Choose File** (custom `.properties` maps)
2. Pick difficulty: EASY, MEDIUM, or HARD (affects time limits and enemy aggression)
3. Optionally change character skin (Steve or Bomberman)
4. Navigate the map: destroy walls, collect power-ups, avoid/defeat enemies
5. Once all enemies are defeated, the exit opens (the HUD indicates the status)
6. Reach the exit to win and progress to the next map (Voyage mode)

**Strategy Tips:**

- **Power-ups** are hidden under destructible walls—destroy walls to find them
- **Enemies aggro** when you get close; stay at distance or use bombs tactically
- **Chained explosions** from bomb blasts destroy walls and can hurt multiple enemies
- **Speed power-ups** are rare but game-changing—seek them out

**Files:**

- Built-in maps: `assets/maps/EasyMap.properties`, `assets/maps/MediumMap.properties`, `assets/maps/HardMap.properties`
- Custom maps: place any `.properties` file in the `assets/maps/` directory and load it via "Choose File"

## Architecture & Design

**Core Design Patterns:**

- **Single Responsibility**: Each game object (Player, Enemy, Bomb, Wall) manages its own state, rendering, and lifecycle
- **Centralized State Management**: `GameMap` class orchestrates all entities, with lists for efficient add/remove cycles during destruction
- **Composition over Inheritance**: Game objects composed from reusable components (animation, texture, audio)
- **Separation of Concerns**: Distinct packages for map logic, rendering, audio, and UI screens

**Key Technical Decisions:**

- **HashMap with 2D Coordinates** (`MapKey`): Efficient spatial lookups for tile-based collision and pathfinding
- **Lazy Initialization**: Map creation deferred until gameplay starts, enabling seamless transitions between modes
- **File-to-Object Pipeline**: Properties files → Strings → HashMap → Entity placement, with automatic fallback for missing entries/exits
- **Collections Strategy**: ArrayLists for entity storage (optimal for frequent add/remove operations during gameplay)
- **Score Scope**: Tied to the `GameMap` instance for per-session tracking with persistent high-score file I/O

**Constraints & Scalability:**

- Power-up cap at 8 units (enforced in collection logic) to maintain gameplay balance
- Dynamic entity destruction via `removeMarkedBodies()` to prevent memory leaks during prolonged sessions

## Beyond Requirements

### Progressive Gameplay System (Voyage Mode)

Sequential map progression with difficulty ramping: Easy (16 enemies) → Medium (complex layout) → Hard (50+ enemies, open-ended exploration). Persistent progression state with a custom victory screen. Demonstrates state machine design and long-term engagement mechanics.

### Dynamic Enemy AI with Context-Aware Behavior

- **Distance-Based Aggression**: Dual-mode movement (drift → hunt) triggered by a configurable threshold
- **Runtime Direction Tracking**: `getCurrentAppearance()` computes enemy animation based on player position vectors, updated each frame via a centralized tick mechanism
- **Per-Map Configuration**: Difficulty multipliers adjust movement speed, aggression threshold, and spawn counts without code changes

### Procedural Spawn System

Robust fallback for map incompleteness:

- `createMapWithGround()`: Fills unmapped coordinates with ground tiles
- `filterGround()`: Extracts valid spawn locations
- Random selection ensures entry/exit placement on any `.properties` file without validation crashes

### Power-Up Architecture

Custom `SpeedPowerUp` implementation with 1–5 random placements under destructible walls. Parameterized mechanics (0.5f speed increment) enable easy tuning without hardcoding.

### Persistent State Management

- **High Score File I/O**: Score comparison and write-back on game completion; survives application restart
- **Multi-Screen Navigation**: Pause/resume preserves full game state (enemies, projectiles, timers) across screen transitions
- **Control Customization**: WASD/Arrow key polymorphism via input handler without mode switching

### Difficulty Tuning with UI Feedback

Three difficulty presets with HUD color coding:

- Time pressure (green → red at <60s)
- Threat visualization (red until enemies cleared)
- Exit state indication (visual confirmation of progression readiness)
- Regenerated backgrounds per screen context

## Assets & Attribution

- **Audio**: OpenGameArt.org, Pixabay.com
- **Visuals**: DALL-E 3 generated backgrounds, sprite sheets from community archives
- **Framework**: LibGDX for cross-platform game loop and rendering
- **Method-level documentation**: Links to external references included in JavaDoc where applicable
