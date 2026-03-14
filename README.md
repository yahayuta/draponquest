# DraponQuest

A JavaFX port of the classic DoJa mobile game DraponQuest, featuring authentic Final Fantasy 1 (NES)-style chiptune audio and modern enhancements.

## Multi-Language Support (English & Japanese)

DraponQuest now supports both English and Japanese for all major game text, menus, and messages.

- **Toggle Language:** Press the `L` key at any time in-game to instantly switch between English and Japanese. All UI, menus, and script text will update immediately.
- **Supported Text:**
  - Title, menus, battle messages, UI, and script/instructions
  - More text will be localized as the game evolves
- **How it works:**
  - All user-facing text is managed by `LocalizationManager.java`.
  - The current language is switched at runtime and all text is updated live.

### Adding/Editing Translations
- To add or update translations, edit `src/main/java/com/draponquest/LocalizationManager.java`.
- Add new keys or update existing ones in both the English and Japanese maps.
- Use `LocalizationManager.getText("key")` in code to fetch the correct string for the current language.

---

- **NES-style Message System**: Retro typewriter reveal with ultra-fast speed (10 chars/tick) and multi-page support.
- **Sequential Battle Logic**: Precise turn-based combat where monster actions and results are gated by message dismissal.
- **Improved Battle UI**: Raised HP bars and on-screen command prompts for better visibility.
- **Enhanced Level-Up**: Integrated attribute-increase messages into the unified dialogue box.
- Save/load system

# Controls
- Arrow keys: Move
- ENTER: Open command menu / select
- ESC: Exit battle or menu
- **L:** Toggle language (English/Japanese)
- F5: Save
- F9: Load
- ...

---

For more details, see the code and comments in `LocalizationManager.java` and `DraponQuestFX.java`.

# DraponQuest JavaFX

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.11.0-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A modern JavaFX port of the classic DoJa mobile game **DraponQuest**. This project preserves the original game logic while adapting it to run on modern desktop platforms using JavaFX.

- **High-Fidelity FF1 Music Remake**:
  - **100% Melodic Accuracy**: All music tracks have been remade with exact, researched note sequences from the original Final Fantasy 1 (NES) soundtrack.
  - **Complete Soundtrack**: Includes **Title (Prelude)**, **Main Theme**, **Town**, **Castle (Cornelia)**, **Dungeon**, **Battle**, **Victory Fanfare**, **Shop**, **Inn**, **Airship**, **Boss**, **Final Battle**, **Game Over**, and **Suspense**.
  - **Authentic Synthesis**: Enhanced instrumentation with polyphonic harmony layers, custom ADSR envelopes, and SNES-style waveforms (Square, Saw, Triangle).
  - **Dynamic Triggers**: Music now changes contextually during story dialogue, location entry, and different battle tiers (Standard, Boss, Final Boss).
- **Enemy Scaling**: Enemies now scale in difficulty as the player levels up, keeping combat challenging.

## 🎮 About DraponQuest

DraponQuest is a classic RPG-style mobile game originally developed for the DoJa platform. This JavaFX version brings the nostalgic gaming experience to modern desktop systems while maintaining the original gameplay mechanics, graphics, and story elements.

### Features

- **Classic RPG Gameplay**: Explore maps, interact with NPCs, and engage in battles
- **Pixel-Accurate 128x128 Alefgard Map**: A high-fidelity reconstruction of the NES Dragon Quest 1 world. Every coastline, island, and landmark (Tantegel, Rimuldar, Kol, etc.) is placed at its authentic pixel coordinates on a massive 128x128 grid.
- **Enhanced Cave System**: Explorable 16x16 cave mazes with "wide-path" designs (2-tile wide corridors) for smoother navigation and authentic dungeon exploration.
- **Retro Graphics**: Preserved original GIF sprites and tile graphics
- **Command System**: Traditional menu-based interaction system
- **Save/Load System**: Save your progress and continue later
- **Modern Controls**: Keyboard-based navigation adapted for desktop
- **Cross-Platform**: Runs on Windows, macOS, and Linux
- **English Test Script & Comments**: All in-game test dialogue and code comments are now in English, improving readability and maintainability.
- ✅ **Javadoc Documentation**: All main Java files are fully documented in code (generation failed due to environment setup)
- **Debug Logging**: Console output for all major game logic (for developers)
- **Improved Movement**: Player starts in a walkable area; movement logic fixed
- **Battle System**: HP persists between battles, ESC only exits after battle is over
- **Random Encounters**: 3% chance of monster encounter when moving
- **Multiple Monsters**: Encounter various monsters with unique stats and appearances. (Monster parameters have been adjusted to be weaker for a more forgiving experience.)
- **Balanced Combat**: Reduced monster attack values for fair gameplay
- **Battle Win Counter**: Track and display the number of battles won
- **NES-style Message System**: A unified dialogue box with retro typewriter reveal and multi-page support. Includes special markers for page breaks (`@`), pauses (`H`), and end-of-message (`E`).
- **Ultra-Fast Narrative**: Text reveals at lightning speed (10 characters per tick), maintaining the retro aesthetic while ensuring modern responsiveness.
- **Sequential Battle Logic**: Turn-based combat events are sequentially ordered using callbacks, ensuring player actions, monster attacks, and victory/defeat messages are never skipped.
- **Enhanced Level-Up**: Property increases (Level, HP, Attack, Defense) are clearly displayed in sequential message pages upon victory.
- **Minimap System**: A real-time 128x128 pixel minimap in the top-right corner. It provides a global world view on the field and automatically switches to a detailed local view in towns. (Automatically hidden during battles and in caves for a clean experience.)
- **Magic System**:
  - **Heal**: Restores 10-15 HP.
  - **Fireball**: Deals 10-15 damage to enemies in battle.
  - **Return**: Warps the player back to Tantegel Castle when on the field.
  - **Outside**: Warps the player out of caves directly to the field map.
  - **MP Management**: Spells cost Magic Points (MP), which are displayed in the status windows.

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **JavaFX SDK 21.0.2** (download from [OpenJFX](https://openjfx.io/))
- **Maven 3.6+** (optional, for Maven builds)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/draponquest.git
   cd draponquest
   ```

2. **Download JavaFX SDK**
   - Visit [OpenJFX Downloads](https://openjfx.io/)
   - Download JavaFX SDK 21.0.2 for your platform
   - Extract to a location like `C:\javafx-sdk-21.0.2`

3. **Set JavaFX Path**
   - Update the `JAVAFX_PATH` variable in the batch files to point to your JavaFX installation
   - Or set the `JAVAFX_HOME` environment variable

### Running the Game

#### Windows (Recommended)
```bash
# Build and run in one command
cmd /c build-and-run.bat

# Or compile and run separately
cmd /c compile.bat
cmd /c run.bat
```

#### Manual Compilation
```bash
# Compile the project
javac --module-path "C:\javafx-sdk-21.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -d target/classes src/main/java/com/draponquest/*.java

# Run the game
java --module-path "C:\javafx-sdk-21.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target/classes com.draponquest.DraponQuestFX
```

#### Using Maven
```bash
# Clean and compile
mvn clean compile

# Run with Maven
mvn javafx:run
```

## 🎯 Game Controls

| **Key(s)**             | **Action**                                         |
| :--------------------- | :------------------------------------------------- |
| **Arrow Keys / WASD**  | Move player / Navigate menus                       |
| **A**                  | **Attack** in battle / **Dismiss** message         |
| **Enter / Space**      | Select / Confirm / **Use item** / Dismiss message  |
| **D**                  | **Defend** in battle                               |
| **R**                  | **Run** (try to escape)                            |
| **F5**                 | Save game                                          |
| **F9**                 | Load game                                          |
| **ESC**                | Cancel / Back / **Exit current menu/shop**         |
| **L**                  | Toggle language (English/Japanese)                 |
| **M**                  | Toggle background music on/off                     |
| **T**                  | Toggle sound effects on/off                        |
| **[**                  | Decrease volume                                    |
| **]**                  | Increase volume                                    |

### Game Modes

- **Movement Mode**: Use arrow keys to explore the map
- **Command Mode**: Navigate through action menus (Talk, Check, Magic, Item, Status)
- **Magic Mode**: Select and cast spells from the Magic menu
- **Battle Mode**: Engage in turn-based combat (ESC only exits after battle is over)
- **Event Mode**: Interact with story events and NPCs

## 📁 Project Structure

```
draponquest/
├── src/
│   ├── draponQuestMain.java             # Legacy DoJa main application (comments translated)
│   ├── eventMapData.java                # Legacy DoJa event map data (empty)
│   ├── fieldMapData.java                # Legacy DoJa field map data (comments translated)
│   ├── scriptData.java                  # Legacy DoJa script data (comments translated)
│   └── main/
│       ├── java/com/draponquest/
│       │   ├── AudioManager.java      # Manages game audio
│       │   ├── BattleManager.java     # Manages battle logic
│       │   ├── DraponQuestFX.java     # Main JavaFX game application
│       │   ├── GameInputHandler.java  # Handles user input
│       │   ├── Inventory.java         # Manages player inventory
│       │   ├── Item.java              # Represents game items
│       │   ├── LocalizationManager.java# Handles multi-language support
│       │   ├── Monster.java           # Represents game monsters
│       │   ├── Shop.java              # Manages shop interactions
│       │   └── fieldMapData.java      # Provides main game map data and logic
│       │   └── scriptData.java        # Provides main game script data and logic
│       └── resources/
│           ├── images/                  # Game graphics (GIF sprites)
│           └── sounds/                  # Game audio files (WAV)
├── target/                              # Compiled classes and resources (ignored by Git)
├── bin/                                 # Original DoJa compiled files
├── pom.xml                             # Maven configuration
├── compile.bat                         # Windows compilation script
├── run.bat                            # Windows run script
├── build-and-run.bat                  # Combined build and run
└── clean.bat                          # Clean build artifacts
├── soundgen/                           # Music and sound generation logic (Python)
│   ├── generate_sounds.py              # Main script to generate all FF1 sounds/music
│   └── (legacy scripts...)             # Older iteration scripts
```

## 🛠️ Development

### Building from Source

1. **Clone and setup**
   ```bash
   git clone https://github.com/yourusername/draponquest.git
   cd draponquest
   ```

2. **Install dependencies**
   - Ensure Java 17+ is installed
   - Download and extract JavaFX SDK
   - Update batch files with correct JavaFX path

3. **Compile**
   ```bash
   compile.bat
   ```

4. **Run**
   ```bash
   run.bat
   ```

### Project Architecture

- **DraponQuestFX**: Main application class with JavaFX integration
- **GameLoop**: Animation timer for game updates and rendering
- **GameInputHandler**: Keyboard event processing
- **fieldMapData**: Map rendering and collision detection
- **scriptData**: Dialogue system and event handling
- **AudioManager**: Sound effects and background music management

### Key Features Implemented

- ✅ **Redesigned UI System**: A new, persistent status window is always on-screen, and the command menu and status screen have been restyled for a more consistent and user-friendly experience.
- ✅ **Pixel-Accurate 128x128 Alefgard Map**: An exact 1:1 clone of the original NES map, 4x larger than previous versions, featuring precise continent shaping and accurate landmark positioning.
- ✅ **Cave Maze Overhaul**: Redesigned cave interiors with structured mazes and wide 2-tile corridors to prevent movement issues in tight spaces.
- ✅ Player movement and collision detection (starts in walkable area)
- ✅ Command menu system
- ✅ Dialogue and script system (English test script)
- ✅ Save/load functionality
- ✅ Battle and event screen overlays
- ✅ Input handling for desktop controls
- ✅ HP persists between battles, resets only on game over
- ✅ ESC only exits battle after win/lose
- ✅ Javadoc documentation for all main files
- ✅ All code comments translated to English for improved readability
- ✅ Debug logging for all major logic (see console output)
- ✅ Random monster encounters (3% chance on movement)
- ✅ Multiple monster types with unique stats and appearances
- ✅ Monster class with individual HP and attack ranges
- ✅ Balanced combat with reduced monster attack values (monsters are weaker)
- ✅ Battle rewards displayed: Gain XP and Gold after each victory, displayed prominently.
- ✅ Modern battle UI with centered, spaced elements
- ✅ Complete audio system with authentic Final Fantasy-style sound effects and background music
- ✅ Audio controls (M/T keys for toggle, [ ] for volume)
- ✅ Real-time audio status display on main screen
- ✅ All authentic Final Fantasy-style sound files generated and integrated with exact FF notes and melodies using precise frequencies (B4: 493.88 Hz, G4: 392.00 Hz, A4: 440.00 Hz, C5: 523.25 Hz, A#4: 466.16 Hz, D#5: 622.25 Hz, D5: 587.33 Hz, G#4: 415.30 Hz, F5: 698.46 Hz)
- ✅ **Complete audio integration**: All generated sound files (`victory_music.wav`, `game_over.wav`, `title.wav`) are now properly loaded and played at the correct game events
- ✅ **Minimap System**: Dynamic minimap, featuring global/local switching and automatic hiding in battles and caves.
- ✅ **Stylish Title Screen**: The game title is now presented in a classic, NES-style bordered window for a more polished and retro look.
- ✅ **Improved Input Handling**: Enhanced responsiveness and control across all menus (Command, Shop, Inventory) with consistent `ENTER`/`SPACE` for selection and `ESC` for exiting/backing.
- ✅ **Consistent NPC Interaction**: NPCs now require the player to be directly facing them to interact, aligning interaction logic with treasure chests.
- ✅ **Accessible Town Shop**: A doorway has been added to the town shop, allowing players to enter and interact with the merchant.
- ✅ **Basic Inventory Item Usage**: Players can now "use" items from their inventory, with initial functionality displaying a message upon use.

## 🎨 Graphics and Assets

The game uses the original GIF sprites and tile graphics:
- `me1.gif` - Player character sprite
- `monster1.gif` - First monster sprite (Tung Tung Tung Sahur)
- `monster2.gif` - Second monster sprite (Tralalero Tralala)
- `monster3.gif` - Third monster sprite (Bombardiro Crocodilo)
- `sea.gif` - Water tiles
- `snd.gif` - Sand tiles  
- `stp.gif` - Steppe/grass tiles
- `wd.gif` - Forest/wood tiles

All graphics are preserved from the original DoJa version for authentic retro gaming experience.

## 🎵 Audio System

DraponQuest features a comprehensive audio system with both sound effects and background music. All audio files are procedurally generated 8-bit style sounds that perfectly match the retro aesthetic of the game:

### Sound Effects
All sound effects are procedurally generated 8-bit style audio files:
- **Movement** (`move.wav`): Quick ascending arpeggios (like FF menu navigation)
- **Battle Start** (`battle_start.wav`): Dramatic chord progressions (like FF battle transitions)
- **Attack** (`attack.wav`): Sharp impact with harmonics and descending sweeps
- **Defend** (`defend.wav`): Soft block with resonance frequencies
- **Escape** (`escape.wav`): Whoosh sound when successfully escaping battle
- **Victory** (`victory.wav`): Iconic FF victory fanfare with exact notes
- **Defeat** (`defeat.wav`): Descending minor scale (melancholic FF-style)
- **Menu Select** (`menu_select.wav`): Click sound for menu navigation
- **Save** (`save.wav`): Chime for save operations
- **Load** (`load.wav`): Different chime for load operations
- **Game Over** (`game_over.wav`): Dramatic descending sequence (like FF game over)
- **Title** (`title.wav`): Rich chord progressions with melody layers
- **Victory Music** (`victory_music.wav`): Extended fanfare with memorable themes

### High-Fidelity Music Tracks
The game now features **17 high-fidelity music tracks** remade for 100% note accuracy to FF1 (NES):

| Track Name | FF1 (NES) Inspiration | Scene / Trigger |
| :--- | :--- | :--- |
| **Title** | Prelude / Bridge Theme | Application Start / Game Restart |
| **Field** | Main Theme | Overworld Exploration |
| **Town** | Town | Entering Towns |
| **Castle** | Cornelia Castle | Entering Castles |
| **Cave** | Dungeon | Dungeons and Shrines |
| **Shop** | Shop | Interacting with Merchants |
| **Inn** | Inn | Resting at an Inn |
| **Battle** | Battle | Standard Random Encounters |
| **Boss** | Boss Battle | Mid-boss Encounters |
| **Final Boss** | Final Battle | Final Battle with Chaos |
| **Victory** | Victory Fanfare | Winning a Battle |
| **Suspense** | Chaos Shrine | Dramatic Narrative Moments |
| **Game Over** | Game Over | Defeat Screen |
| **Airship** | Airship Theme | *Bonus Content* |
| **Prologue** | Prelude (Secondary) | (Mapped to Title) |
| **Love** | Town (Secondary) | (Mapped to Town) |
| **Tower** | Dungeon (Secondary) | (Mapped to Cave) |

### Final Fantasy 1 (NES) Synthesis Characteristics
- **100% Melodic Accuracy**: All melodies and basslines are researched from original NES music transcriptions.
- **8-bit Aesthetic**: Features multiple layers (Melody, Harmony, Bass, Percussion) playing simultaneously with authentic NES waveforms.
- **Square Wave Purity**: Uses precise square waves, triangle waves, and white noise for percussion to match the 1987 sound.
- **Dynamic Envelopes**: Custom ADSR envelopes designed to mimic the sharp attack and quick decay of early chiptunes.

### Audio Controls
- **M Key**: Toggle background music on/off
- **T Key**: Toggle sound effects on/off
- **[ Key**: Decrease volume
- **] Key**: Increase volume

### Audio Status Display
The main game screen shows real-time audio status in the top-right corner:
- **Music ON/OFF indicator** (green/red) - Shows if background music is enabled
- **Sound ON/OFF indicator** (green/red) - Shows if sound effects are enabled  
- **Current volume percentage** (yellow) - Shows current volume level (0-100%)

If any of these files are missing, you can regenerate them using the provided Python scripts:

```bash
# Generate basic 8-bit style sounds
python soundgen/generate_missing_sounds.py

# Generate authentic Final Fantasy-style sounds with exact notes
python soundgen/generate_ff_exact.py
```

The `generate_ff_exact.py` script creates audio with **exact frequencies and notes** from classic Final Fantasy games, including the precise notes used in the iconic victory fanfare: "b b b b, g a b a b, C a# C a# a#, D# D# D D# D D, C a# g# a# g, C a# C a# a#, D# D# D D# D D, C a# C D# F".

### Regenerating All Sounds

To regenerate all sound and music files, use the provided batch file:

```bat
cd soundgen
generate_all_sounds.bat
```

This will run all sound generation scripts and automatically move the generated .wav files to the resource directory (`src/main/resources/sounds/`).

**Note:**
After running the batch file, no .wav files will remain in the `soundgen/` folder—all generated files are moved to the correct location for the game to use them.

## ⚔️ Battle System

The game features a turn-based battle system with the following mechanics:

### Random Encounters
- **3% chance** of encountering a monster when moving on the map
- Encounters occur after movement, not during

### Monster Types
- **Tung Tung Tung Sahur**: 8 HP, attacks for 1-2 damage
- **Tralalero Tralala**: 12 HP, attacks for 1-3 damage
- **Bombardiro Crocodilo**: 18 HP, attacks for 2-4 damage
- Each monster has unique appearance and stats

### Battle Mechanics
- **Sequential Logic**: Each action (Player Attack -> Monster Attack -> Victory) is gated by a message. You must dismiss the message (A/Space/Enter) to proceed to the next turn.
- **Player HP**: Persists between battles, only resets on game over
- **Turn-based combat**: Player and monster take turns
- **Actions**: Attack (deals 3-6 damage), Defend (reduces incoming damage by 50%), Run (try to escape)
- **Victory**: Defeat the monster to gain XP and Gold. If you level up, a detailed summary will be displayed.
- **Defeat**: Game over screen with your total score and restart option

### Score System
- **Score increases by 1** every time you move one block
- **Score is displayed** under HP on the main screen
- **Battles Won counter** shows your combat victories in real-time
- **Game Over screen** shows your total score and final battle count

### Battle Controls
- **A**: Attack (and dismiss message)
- **D**: Defend
- **R**: Run (try to escape)
- **ESC**: Only exits after battle is complete (win or lose)

## 🔧 Configuration

### JavaFX Path Setup

Update the JavaFX path in the batch files:
```batch
set JAVAFX_PATH=C:\javafx-sdk-21.0.2
```

### Game Settings

Key game constants in `DraponQuestFX.java`:
- `DISP_WIDTH = 512` - Display width
- `DISP_HEIGHT = 512` - Display height
- `WAIT_MSEC = 100` - Game loop timing
- `fieldMapEndHeight = 16` - Initial player Y position (walkable)
- `fieldMapEndWidth = 16` - Initial player X position (walkable)

## 🐛 Troubleshooting

### Common Issues

1. **Player cannot move at start**
   - Make sure you pressed ENTER on the title screen to start the game
   - Player now starts in a walkable area; if you still cannot move, check for debug output in the console
   - If you see "Move blocked: not walkable or out of bounds", you may be surrounded by sea tiles (should not happen with default settings)

2. **"JavaFX runtime components are missing"**
   - Ensure JavaFX SDK is downloaded and path is correct
   - Update `JAVAFX_PATH` in batch files

3. **"Cannot find main class"**
   - Run `clean.bat` then `compile.bat`
   - Check that all source files are present

4. **Graphics not loading**
   - Verify image files exist in `src/main/resources/images/`

5. **Debugging**
   - All major game logic prints debug output to the console (movement, battle, state changes, audio events)
   - Audio system provides detailed logging of sound effects and music playback
   - Use this output to trace and diagnose issues

6. **Javadoc Generation Fails**
   - If `javadoc.exe` or `mvn` commands are not found when trying to generate Javadoc documentation, ensure that Java Development Kit (JDK) is correctly installed and its `bin` directory is in your system's PATH.
   - For Maven, ensure Maven is installed and its `bin` directory is also in your system's PATH.
   - Alternatively, you can use an IDE like IntelliJ IDEA or Eclipse to generate Javadocs if Maven is not configured.

7. **PowerShell cannot find run.bat**
   - In PowerShell, you must run batch files with `./run.bat` or `.\run.bat` instead of just `run.bat`.
   - Example:
     ```powershell
     .\run.bat
     ```
   - This is a PowerShell security feature.

---

Enjoy classic RPG gameplay with modern JavaFX enhancements and a complete retro audio experience!

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Guidelines

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Areas for Improvement

- Inventory management
- More sophisticated save system
- Additional monster types and encounters
- Level progression and character stats
- More game content and story elements
- Additional background music tracks for different areas
- Sound effect variations for different monster types

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Original Author**: Yakkun (DoJa version)
- **JavaFX Migration**: Modern development team
- **OpenJFX**: For the excellent JavaFX framework
- **DoJa Platform**: Original mobile gaming platform

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Troubleshooting](#troubleshooting) section
2. Search existing [Issues](../../issues)
3. Create a new issue with detailed information

---

**Enjoy your journey in DraponQuest!** 🐉⚔️ 

## 📝 In-Game Instructions

The in-game script at the bottom of the screen explains:
- How to move and open the command menu
- That each move increases your score by 1
- How to survive, fight, defend, and run
- That your total score is shown if you are defeated
- How to save/load your game

### Audio Instructions
- **M Key**: Toggle background music on/off
- **T Key**: Toggle sound effects on/off
- **[ Key**: Decrease volume
- **] Key**: Increase volume

### Minimap Instructions
- Minimap is automatically hidden during battles and in caves.
- Audio status is displayed in the top-right corner of the game screen 