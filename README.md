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
   - All major game logic prints debug output to the console (movement, battle, state changes)
   - Use this output to trace and diagnose issues

---

Enjoy classic RPG gameplay with modern JavaFX enhancements!

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Guidelines

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Areas for Improvement

- Sound effects and music
- Enhanced battle system with more actions (magic, items)
- Inventory management
- More sophisticated save system
- Additional monster types and encounters
- Level progression and character stats
- More game content and story elements

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