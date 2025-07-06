# DraponQuest JavaFX

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.11.0-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A modern JavaFX port of the classic DoJa mobile game **DraponQuest**. This project preserves the original game logic while adapting it to run on modern desktop platforms using JavaFX.

## 🎮 About DraponQuest

DraponQuest is a classic RPG-style mobile game originally developed for the DoJa platform. This JavaFX version brings the nostalgic gaming experience to modern desktop systems while maintaining the original gameplay mechanics, graphics, and story elements.

### Features

- **Classic RPG Gameplay**: Explore maps, interact with NPCs, and engage in battles
- **Score System**: Each time you move, your score increases by 1. Try to get the highest score by surviving and exploring! Your total score is shown on the game over screen.
- **Retro Graphics**: Preserved original GIF sprites and tile graphics
- **Command System**: Traditional menu-based interaction system
- **Save/Load System**: Save your progress and continue later
- **Modern Controls**: Keyboard-based navigation adapted for desktop
- **Cross-Platform**: Runs on Windows, macOS, and Linux
- **English Test Script**: All in-game test dialogue is now in English
- **Javadoc Documentation**: All main Java files are fully documented
- **Debug Logging**: Console output for all major game logic (for developers)
- **Improved Movement**: Player starts in a walkable area; movement logic fixed
- **Battle System**: HP persists between battles, ESC only exits after battle is over
- **Random Encounters**: 3% chance of monster encounter when moving
- **Multiple Monsters**: Three different monsters with unique stats and appearances
- **Balanced Combat**: Reduced monster attack values for fair gameplay
- **Battle Win Counter**: Track and display the number of battles won
- **Modern UI**: Clean, centered battle interface with proper spacing
- **Audio System**: Complete authentic Final Fantasy-style sound effects and background music system
- **Audio Controls**: Toggle music/sound, volume control, and real-time audio status display

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
build-and-run.bat

# Or compile and run separately
compile.bat
run.bat
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

| Key | Action |
|-----|--------|
| **Arrow Keys** | Move player / Navigate menus |
| **Enter** | Select / Confirm |
| **F5** | Save game |
| **F9** | Load game |
| **ESC** | Cancel / Back (in battle, only after win/lose) |
| **M** | Toggle background music on/off |
| **S** | Toggle sound effects on/off |
| **[** | Decrease volume |
| **]** | Increase volume |

### Game Modes

- **Movement Mode**: Use arrow keys to explore the map
- **Command Mode**: Navigate through action menus (Talk, Check, Magic, Item, Status)
- **Battle Mode**: Engage in turn-based combat (ESC only exits after battle is over)
- **Event Mode**: Interact with story events and NPCs

## 📁 Project Structure

```
draponquest/
├── src/
│   └── main/
│       ├── java/com/draponquest/
│       │   ├── DraponQuestFX.java      # Main game application (Javadoc documented)
│       │   ├── GameInputHandler.java   # Input handling (Javadoc documented)
│       │   ├── fieldMapData.java       # Map data and logic (Javadoc documented)
│       │   └── scriptData.java         # Dialogue and script system (Javadoc documented)
│       └── resources/
│           └── images/                  # Game graphics (GIF sprites)
├── target/                              # Compiled classes
├── bin/                                 # Original DoJa files
├── pom.xml                             # Maven configuration
├── compile.bat                         # Windows compilation script
├── run.bat                            # Windows run script
├── build-and-run.bat                  # Combined build and run
└── clean.bat                          # Clean build artifacts
├── soundgen/                           # All sound generation scripts (Python)
│   ├── generate_ff_victory.py
│   ├── generate_ff_battle.py
│   ├── generate_battle_music.py
│   ├── generate_ff_exact.py
│   ├── generate_ff_authentic.py
│   ├── generate_ff_sounds.py
│   └── generate_missing_sounds.py
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

- ✅ Map rendering with tile-based graphics
- ✅ Player movement and collision detection (starts in walkable area)
- ✅ Command menu system
- ✅ Dialogue and script system (English test script)
- ✅ Save/load functionality
- ✅ Battle and event screen overlays
- ✅ Input handling for desktop controls
- ✅ HP persists between battles, resets only on game over
- ✅ ESC only exits battle after win/lose
- ✅ Javadoc documentation for all main files
- ✅ Debug logging for all major logic (see console output)
- ✅ Random monster encounters (3% chance on movement)
- ✅ Multiple monster types with unique stats and appearances
- ✅ Monster class with individual HP and attack ranges
- ✅ Balanced combat with reduced monster attack values
- ✅ Battle win counter with real-time display
- ✅ Modern battle UI with centered, spaced elements
- ✅ Complete audio system with authentic Final Fantasy-style sound effects and background music
- ✅ Audio controls (M/T keys for toggle, [ ] for volume)
- ✅ Real-time audio status display on main screen
- ✅ All authentic Final Fantasy-style sound files generated and integrated with exact FF notes and melodies using precise frequencies (B4: 493.88 Hz, G4: 392.00 Hz, A4: 440.00 Hz, C5: 523.25 Hz, A#4: 466.16 Hz, D#5: 622.25 Hz, D5: 587.33 Hz, G#4: 415.30 Hz, F5: 698.46 Hz)
- ✅ **Complete audio integration**: All generated sound files (`victory_music.wav`, `game_over.wav`, `title.wav`) are now properly loaded and played at the correct game events

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

### Background Music
All background music tracks are procedurally generated chiptune-style loops:
- **Field Music** (`bgm_field.wav`): Simple ambient loop while exploring the map
- **Battle Music** (`bgm_battle.wav`): More intense loop during combat
- Music tracks loop indefinitely and can be toggled on/off

### Final Fantasy-Style Audio Characteristics
The audio system features authentic Final Fantasy-style sound design with **exact notes and frequencies** from classic FF games:
- **Iconic Victory Fanfare**: Classic "b b b b, g a b a b, C a# C a# a#" melody with precise notes
- **Exact FF Notes**: Uses precise frequencies like B4 (493.88 Hz), G4 (392.00 Hz), A4 (440.00 Hz), C5 (523.25 Hz), A#4 (466.16 Hz), D#5 (622.25 Hz), D5 (587.33 Hz), G#4 (415.30 Hz), F5 (698.46 Hz)
- **SNES Sound Chip Emulation**: Square, triangle, and saw waves with authentic harmonics
- **Rich Harmonics**: Multiple harmonic layers for depth (2nd, 3rd, 4th harmonics)
- **Authentic Note Patterns**: Exact same melodies and chord progressions as classic FF games
- **Dramatic Sound Effects**: Impactful sound design like FF games with precise timing

### Audio Controls
- **M Key**: Toggle background music on/off
- **T Key**: Toggle sound effects on/off
- **[ Key**: Decrease volume (both music and sound)
- **] Key**: Increase volume (both music and sound)

### Audio Status Display
The main game screen shows real-time audio status in the top-right corner:
- **Music ON/OFF indicator** (green/red) - Shows if background music is enabled
- **Sound ON/OFF indicator** (green/red) - Shows if sound effects are enabled  
- **Current volume percentage** (yellow) - Shows current volume level (0-100%)

### Audio File Locations
All audio files are stored in `src/main/resources/sounds/`:
- Sound effects: `move.wav`, `battle_start.wav`, `attack.wav`, `defend.wav`, `escape.wav`, `victory.wav`, `defeat.wav`, `menu_select.wav`, `save.wav`, `load.wav`, `game_over.wav`
- Background music: `bgm_field.wav`, `bgm_battle.wav`, `title.wav`, `victory_music.wav`

**✅ All generated sound files are now properly loaded and played in the game!** The AudioManager has been updated to ensure that:
- `SOUND_GAME_OVER` plays `game_over.wav` when the player is defeated
- `MUSIC_TITLE` plays `title.wav` on the title screen
- `MUSIC_VICTORY` plays `victory_music.wav` after winning battles
- All other sound effects and music tracks are correctly mapped and functional

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
- **Player HP**: Persists between battles, only resets on game over
- **Turn-based combat**: Player and monster take turns
- **Actions**: Attack (deals 3-6 damage), Defend (reduces incoming damage), Run (try to escape)
- **Victory**: Defeat the monster to continue exploring
- **Defeat**: Game over screen with your total score and restart option

### Score System
- **Score increases by 1** every time you move one block
- **Score is displayed** under HP on the main screen
- **Battles Won counter** shows your combat victories in real-time
- **Game Over screen** shows your total score and final battle count

### Battle Controls
- **A**: Attack
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

6. **PowerShell cannot find run.bat**
   - In PowerShell, you must run batch files with `./run.bat` or `.\run.bat` instead of just `run.bat`.
   - Example:
     ```powershell
     .\run.bat
     ```
   - This is a PowerShell security feature.

---

Enjoy classic RPG gameplay with modern JavaFX enhancements and a complete retro audio experience!

## 🎯 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Guidelines

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Areas for Improvement

- Enhanced battle system with more actions (magic, items)
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
- Audio status is displayed in the top-right corner of the game screen 