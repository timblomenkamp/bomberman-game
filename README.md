# Bomberman Game

A feature-rich Bomberman implementation built with **LibGDX**, demonstrating advanced game development concepts and software engineering best practices. The project implements a complete game loop with dynamic enemy AI, progressive difficulty scaling, and persistent state management—all with clean, maintainable architecture.

**Tech Stack:** Java, LibGDX, Gradle 

## Gameplay

**Core Mechanics:**
- **Dynamic Map System**: Load custom game maps from `.properties` files with automatic validation and fallback generation
- **Intelligent Enemy AI**: Real-time pathfinding with distance-based aggression scaling—enemies drift slowly at range, hunt aggressively when close
- **Combat Mechanics**: Place bombs with chainable explosions, collect power-ups (bombs, blast radius, speed), navigate destructible/indestructible walls
- **Multiple Game Modes**: Single map play or progressive “Voyage” mode with increasing difficulty
- **Difficulty Scaling**: Three difficulty levels with dynamic enemy behavior, time pressure, and movement speed modulation
- **Skin/Control Customization**: Multiple character skins and dual input systems (WASD + Arrow Keys)
- **Pause & Resume**: Full game state persistence across pause cycles 


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
- **Score Scope**: Tied to GameMap instance for per-session tracking with persistent high-score file I/O

**Constraints & Scalability:**
- Power-up cap at 8 units (enforced in collection logic) to maintain gameplay balance
- Dynamic entity destruction via `removeMarkedBodies()` to prevent memory leaks during prolonged sessions


## Beyond Requirements

### Progressive Gameplay System (Voyage Mode)
Sequential map progression with difficulty ramping: Easy (16 enemies) → Medium (complex layout) → Hard (50+ enemies, open-ended exploration). Persistent progression state with custom victory screen. Demonstrates state machine design and long-term engagement mechanics.

### Dynamic Enemy AI with Context-Aware Behavior
- **Distance-Based Aggression**: Dual-mode movement (drift → hunt) triggered by configurable threshold
- **Runtime Direction Tracking**: `getCurrentAppearance()` computes enemy animation based on player position vectors, updated each frame via centralized tick mechanism
- **Per-Map Configuration**: Difficulty multipliers adjust movement speed, aggression threshold, and spawn counts without code changes

### Procedural Spawn System
Robust fallback for map incompleteness:
- `createMapWithGround()`: Fills unmapped coordinates with ground tiles
- `filterGround()`: Extracts valid spawn locations
- Random selection ensures entry/exit placement on any `.properties` file without validation crashes

### Power-Up Architecture
Custom `SpeedPowerUp` implementation with 1-5 random placement under destructible walls. Parameterized mechanics (0.5f speed increment) enable easy tuning without hardcoding.

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