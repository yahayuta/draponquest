package com.draponquest;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import java.io.*;
import java.nio.file.*;

// Audio system
import com.draponquest.AudioManager;

/**
 * DraponQuest JavaFX Application
 * Modern JavaFX version of the original DoJa mobile game.
 * Handles main game logic, rendering, and state transitions.
 *
 * @author Yakkun (Original)
 * @author Modern Migration
 */
public class DraponQuestFX extends Application {
    
    // Game constants (preserved from original)
    private static final int DISP_WIDTH = 512;
    private static final int DISP_HEIGHT = 512;
    private static final int WAIT_MSEC = 100;
    
    // Game status constants
    private static final int GAME_TITLE = 0;
    private static final int GAME_OPEN = 1;
    private static final int GAME_WAIT = 2;
    private static final int GAME_CONT = 3;
    private static final int GAME_OVER = 4;
    
    // Game modes
    static final int MODE_MOVE = 0;
    static final int MODE_COM = 1;
    static final int MODE_BATTLE = 2;
    static final int MODE_EVENT = 3;
    
    // Places
    private static final int PLACE_FIELD = 0;
    private static final int PLACE_BLDNG = 1;
    private static final int PLACE_CAVE = 2;
    
    // Commands
    private static final int COM_TALK = 1;
    private static final int COM_CHK = 2;
    private static final int COM_MGK = 3;
    private static final int COM_ITEM = 4;
    private static final int COM_STUS = 5;
    
    // Battle commands
    private static final int BCOM_ATK = 1;
    private static final int BCOM_MGK = 2;
    private static final int BCOM_ITEM = 3;
    private static final int BCOM_RUN = 4;
    
    // Game state variables
    private int currentGameStatus = GAME_TITLE;
    int currentMode = MODE_MOVE;
    private int currentPlace = PLACE_FIELD;
    private int currentCommand = COM_TALK;
    private int flip = 0;
    
    // Map variables
    private int fieldMapEndWidth = 16;
    private int fieldMapEndHeight = 16;
    
    // Script variables
    private String[] scriptLines = null;
    private int scriptID = 0;
    private int scriptLineIndex = 0;
    private int scriptAdvanceTick = 0;
    
    // JavaFX components
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private GameLoop gameLoop;
    private GameInputHandler inputHandler;
    private Image playerImage;
    private Image seaImage;
    private Image sandImage;
    private Image steppeImage;
    private Image forestImage;
    private Image monster1Image;
    private Image monster2Image;
    private Image monster3Image;
    private Image currentMonsterImage;
    private String currentMonsterName;
    private int playerHP = 40;
    private int maxPlayerHP = 40;
    private int monsterHP = 15;
    private boolean playerTurn = true;
    private boolean isDefending = false; // Track if player is defending
    private String battleMessage = "";
    
    // Message box for command actions
    private String commandMessage = null;
    private long commandMessageTime = 0;
    
    // Save/load data
    private String saveFileName = "draponquest_save.dat";
    private String saveMessage = null;
    private long saveMessageTime = 0;
    
    private int score = 0;
    private int battlesWon = 0; // Track number of battles won
    
    // Audio system
    private AudioManager audioManager;
    
    /**
     * Represents a monster with image, name, HP, and attack range.
     */
    private static class Monster {
        Image image;
        String name;
        int maxHP;
        int minAttack;
        int maxAttack;
        Monster(Image image, String name, int maxHP, int minAttack, int maxAttack) {
            this.image = image;
            this.name = name;
            this.maxHP = maxHP;
            this.minAttack = minAttack;
            this.maxAttack = maxAttack;
        }
    }

    private Monster[] monsters;
    private Monster currentMonster;
    
    /**
     * Initializes the game and sets up the JavaFX UI.
     * @param primaryStage The main application window.
     */
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Game started: Showing title screen");
        // Initialize game components
        initializeGame();
        
        // Create JavaFX UI
        gameCanvas = new Canvas(DISP_WIDTH, DISP_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();
        
        // Create input handler
        inputHandler = new GameInputHandler(this);
        
        // Create game loop
        gameLoop = new GameLoop();
        
        // Setup scene
        StackPane root = new StackPane();
        root.getChildren().add(gameCanvas);
        
        Scene scene = new Scene(root, DISP_WIDTH, DISP_HEIGHT);
        scene.setOnKeyPressed(inputHandler::handleKeyPressed);
        scene.setOnKeyReleased(inputHandler::handleKeyReleased);
        
        // Setup stage
        primaryStage.setTitle("DraponQuest JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Start game loop
        gameLoop.start();
    }
    
    /**
     * Initializes game components, images, and script buffers.
     */
    private void initializeGame() {
        System.out.println("Initializing game components");
        // No need to initialize scriptBuffer anymore
        // Initialize map data
        fieldMapData.initialize();
        
        // Initialize audio system
        audioManager = AudioManager.getInstance();
        
        // Load player image
        try {
            playerImage = new Image(getClass().getResourceAsStream("/images/me1.gif"));
        } catch (Exception e) {
            playerImage = null;
        }
        
        // Load tile images
        try { seaImage = new Image(getClass().getResourceAsStream("/images/sea.gif")); } catch (Exception e) { seaImage = null; }
        try { sandImage = new Image(getClass().getResourceAsStream("/images/snd.gif")); } catch (Exception e) { sandImage = null; }
        try { steppeImage = new Image(getClass().getResourceAsStream("/images/stp.gif")); } catch (Exception e) { steppeImage = null; }
        try { forestImage = new Image(getClass().getResourceAsStream("/images/wd.gif")); } catch (Exception e) { forestImage = null; }
        try {
            monster1Image = new Image(getClass().getResourceAsStream("/images/monster1.gif"));
        } catch (Exception e) {
            monster1Image = null;
        }
        try {
            monster2Image = new Image(getClass().getResourceAsStream("/images/monster2.gif"));
        } catch (Exception e) {
            monster2Image = null;
        }
        try {
            monster3Image = new Image(getClass().getResourceAsStream("/images/monster3.gif"));
        } catch (Exception e) {
            monster3Image = null;
        }
        // Initialize monsters array
        monsters = new Monster[] {
            new Monster(monster1Image, "Tung Tung Tung Sahur", 8, 1, 2),
            new Monster(monster2Image, "Tralalero Tralala", 12, 1, 3),
            new Monster(monster3Image, "Bombardiro Crocodilo", 18, 2, 4)
        };
    }
    
    /**
     * Main game loop using JavaFX AnimationTimer.
     */
    private class GameLoop extends AnimationTimer {
        private long lastUpdate = 0;
        
        @Override
        public void handle(long now) {
            if (now - lastUpdate >= WAIT_MSEC * 1_000_000) { // Convert to nanoseconds
                updateGame();
                renderGame();
                lastUpdate = now;
            }
        }
    }
    
    /**
     * Updates the game logic based on the current state.
     */
    private void updateGame() {
        // Update game logic based on current state
        if (commandMessage != null && System.currentTimeMillis() - commandMessageTime > 1000) {
            System.out.println("Command message cleared");
            commandMessage = null;
        }
        if (saveMessage != null && System.currentTimeMillis() - saveMessageTime > 2000) {
            System.out.println("Save message cleared");
            saveMessage = null;
        }
        switch (currentGameStatus) {
            case GAME_TITLE:
                // Title screen logic
                break;
            case GAME_OPEN:
                // Game open logic
                break;
            case GAME_WAIT:
                // Game wait logic
                break;
            case GAME_CONT:
                // Game continue logic
                break;
            case GAME_OVER:
                // Game over logic
                break;
        }
    }
    
    /**
     * Renders the game based on the current state.
     */
    private void renderGame() {
        // Clear canvas
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
        
        // Render based on game state
        switch (currentGameStatus) {
            case GAME_TITLE:
                renderTitleScreen();
                break;
            case GAME_OPEN:
                renderGameScreen();
                break;
            case GAME_WAIT:
                renderWaitScreen();
                break;
            case GAME_CONT:
                renderContinueScreen();
                break;
            case GAME_OVER:
                renderGameOverScreen();
                break;
        }
    }
    
    /**
     * Renders the title screen.
     */
    private void renderTitleScreen() {
        gc.setFill(javafx.scene.paint.Color.LIME);
        gc.setFont(javafx.scene.text.Font.font("Arial", 32));
        
        gc.fillText("DRAPON QUEST", DISP_WIDTH * 0.3, DISP_HEIGHT * 0.3);
        gc.fillText("PRESS ENTER", DISP_WIDTH * 0.3, DISP_HEIGHT * 0.5);
        
        // Audio controls help
        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillText("Audio Controls:", DISP_WIDTH * 0.1, DISP_HEIGHT * 0.7);
        gc.fillText("M: Toggle Music  S: Toggle Sound", DISP_WIDTH * 0.1, DISP_HEIGHT * 0.75);
        gc.fillText("[ ]: Volume Control", DISP_WIDTH * 0.1, DISP_HEIGHT * 0.8);
        
        gc.setFill(javafx.scene.paint.Color.LIME);
        gc.setFont(javafx.scene.text.Font.font("Arial", 20));
        gc.fillText("(c)2025", DISP_WIDTH * 0.35, DISP_HEIGHT * 0.85);
        gc.fillText("yahayuta", DISP_WIDTH * 0.35, DISP_HEIGHT * 0.9);
    }
    
    /**
     * Renders the main game screen (map, player, UI).
     */
    private void renderGameScreen() {
        // Render field map
        renderFieldMap();
        
        // Render player
        renderPlayer();
        
        // Render UI elements
        renderUI();
    }
    
    /**
     * Renders the field map and player sprite.
     */
    private void renderFieldMap() {
        // Draw 16x16 tiles, each 32x32 pixels (fills 512x512 window)
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int tile = fieldMapData.mapDataReturnField(i + fieldMapEndHeight, j + fieldMapEndWidth);
                Image tileImage = null;
                switch (tile) {
                    case 0: tileImage = seaImage; break;
                    case 1: tileImage = sandImage; break;
                    case 2: tileImage = steppeImage; break;
                    case 3: tileImage = forestImage; break;
                }
                if (tileImage != null && !tileImage.isError()) {
                    gc.drawImage(tileImage, j * 32, i * 32, 32, 32);
                } else {
                    switch (tile) {
                        case 0: gc.setFill(javafx.scene.paint.Color.DEEPSKYBLUE); break;
                        case 1: gc.setFill(javafx.scene.paint.Color.GOLD); break;
                        case 2: gc.setFill(javafx.scene.paint.Color.LIGHTGRAY); break;
                        case 3: gc.setFill(javafx.scene.paint.Color.FORESTGREEN); break;
                        default: gc.setFill(javafx.scene.paint.Color.BLACK); break;
                    }
                    gc.fillRect(j * 32, i * 32, 32, 32);
                }
            }
        }
        
        // Draw player sprite (scaled up)
        if (playerImage != null && !playerImage.isError()) {
            gc.drawImage(playerImage, 8 * 32, 8 * 32, 32, 32);
        }
        
        // Display HP and score on map
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 20));
        gc.fillText("HP: " + playerHP + "/" + maxPlayerHP, 10, 30);
        gc.fillText("Score: " + score, 10, 60);
        gc.fillText("Battles Won: " + battlesWon, 10, 90);
        
        // Display audio status
        gc.setFont(javafx.scene.text.Font.font("Arial", 14));
        gc.setFill(audioManager.isMusicEnabled() ? javafx.scene.paint.Color.LIME : javafx.scene.paint.Color.RED);
        gc.fillText("Music: " + (audioManager.isMusicEnabled() ? "ON" : "OFF"), 10, 120);
        gc.setFill(audioManager.isSoundEnabled() ? javafx.scene.paint.Color.LIME : javafx.scene.paint.Color.RED);
        gc.fillText("Sound: " + (audioManager.isSoundEnabled() ? "ON" : "OFF"), 10, 140);
        gc.setFill(javafx.scene.paint.Color.YELLOW);
        gc.fillText("Vol: " + (int)(audioManager.getMusicVolume() * 100) + "%", 10, 160);
    }
    
    /**
     * (No-op) Player is drawn in renderFieldMap().
     */
    private void renderPlayer() {
        // Remove player drawing here to avoid double rendering
        // Player is already drawn in renderFieldMap()
    }
    
    /**
     * Renders UI elements such as dialogue, menus, and battle overlays.
     */
    private void renderUI() {
        // Dialogue box in GAME_OPEN and MODE_MOVE
        if (currentGameStatus == GAME_OPEN && currentMode == MODE_MOVE) {
            // Draw dialogue box (scaled up)
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillRect(0, DISP_HEIGHT - 96, DISP_WIDTH, 96);
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font("MS Gothic", 16));

            // Initialize script lines if needed
            if (scriptLines == null) {
                String rawScript = scriptData.returnTestScript(scriptID, 0);
                scriptLines = java.util.Arrays.stream(rawScript.split("@"))
                    .map(String::trim)
                    .map(line -> line.replaceAll("(H|E|HE)$", "").trim())
                    .filter(line -> !line.isEmpty())
                    .toArray(String[]::new);
                scriptLineIndex = 0;
                scriptAdvanceTick = 0;
            }
            // Draw up to 3 lines at a time
            for (int i = 0; i < 3; i++) {
                int idx = scriptLineIndex + i;
                if (scriptLines != null && idx < scriptLines.length) {
                    gc.fillText(scriptLines[idx], 32, DISP_HEIGHT - 64 + i * 32);
                }
            }
            // Advance to next set of lines every 30 ticks (about 1 second)
            scriptAdvanceTick++;
            if (scriptAdvanceTick > 30) {
                scriptLineIndex += 3;
                if (scriptLineIndex >= scriptLines.length) {
                    scriptLineIndex = 0;
                }
                scriptAdvanceTick = 0;
            }
        }
        // Command menu in GAME_OPEN and MODE_COM
        if (currentGameStatus == GAME_OPEN && currentMode == MODE_COM) {
            // Draw menu background (scaled up)
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillRect(0, DISP_HEIGHT / 2, 192, 192);
            gc.setFont(javafx.scene.text.Font.font("Arial", 32));
            String[] commands = {"TALK", "CHECK", "MAGIC", "ITEM", "STATUS"};
            for (int i = 0; i < commands.length; i++) {
                int y = DISP_HEIGHT / 2 + 16 + i * 36;
                if (currentCommand == i + 1) {
                    gc.setFill(javafx.scene.paint.Color.LIME);
                    gc.fillRect(0, y - 24, 192, 36);
                    gc.setFill(javafx.scene.paint.Color.BLACK);
                } else {
                    gc.setFill(javafx.scene.paint.Color.BLACK);
                }
                gc.fillText(commands[i], 16, y);
            }
        }
        // Command action message (scaled up)
        if (currentGameStatus == GAME_OPEN && commandMessage != null) {
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillRect(0, DISP_HEIGHT - 64, DISP_WIDTH, 64);
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font("Arial", 28));
            gc.fillText(commandMessage, 32, DISP_HEIGHT - 24);
        }
        // Battle screen (scaled up)
        if (currentGameStatus == GAME_OPEN && currentMode == MODE_BATTLE) {
            gc.setFill(javafx.scene.paint.Color.rgb(32, 32, 64, 0.85));
            gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 40));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.fillText("BATTLE! (ESC to exit)", DISP_WIDTH/2, 60);
            // Draw monster name centered above image
            if (currentMonster != null && currentMonster.image != null && !currentMonster.image.isError()) {
                gc.setFont(javafx.scene.text.Font.font("Arial", 28));
                gc.setFill(javafx.scene.paint.Color.YELLOW);
                gc.fillText(currentMonster.name, DISP_WIDTH/2, 120);
                // Draw monster image centered
                gc.drawImage(currentMonster.image, (DISP_WIDTH-128)/2, 130, 128, 128);
            } else {
                gc.setFill(javafx.scene.paint.Color.DARKRED);
                gc.fillRect((DISP_WIDTH-128)/2, 130, 128, 128);
                gc.setFill(javafx.scene.paint.Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font("Arial", 18));
                gc.fillText("No monster image", DISP_WIDTH/2, 190);
            }
            // Draw HP bars, each on its own line, centered
            gc.setFont(javafx.scene.text.Font.font("Arial", 26));
            gc.setFill(javafx.scene.paint.Color.LIME);
            String playerHpStr = "Player HP: " + playerHP + "/" + maxPlayerHP;
            gc.fillText(playerHpStr, DISP_WIDTH/2, 290);
            gc.setFill(javafx.scene.paint.Color.RED);
            String monsterHpStr = currentMonster.name + " HP: " + monsterHP;
            gc.fillText(monsterHpStr, DISP_WIDTH/2, 330);
            // Draw battle message
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 20));
            gc.fillText(battleMessage, DISP_WIDTH/2, DISP_HEIGHT-80);
            // Draw action options
            gc.setFill(javafx.scene.paint.Color.YELLOW);
            gc.setFont(javafx.scene.text.Font.font("Arial", 24));
            gc.fillText("A: Attack   D: Defend   R: Run", DISP_WIDTH/2, DISP_HEIGHT-30);
            gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT); // Reset to default
        }
        // Event screen (scaled up)
        if (currentGameStatus == GAME_OPEN && currentMode == MODE_EVENT) {
            gc.setFill(javafx.scene.paint.Color.rgb(64, 32, 32, 0.85));
            gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 40));
            gc.fillText("EVENT! (ESC to exit)", 64, DISP_HEIGHT / 2);
        }
        // Save/load message (scaled up)
        if (saveMessage != null && System.currentTimeMillis() - saveMessageTime < 2000) {
            gc.setFill(javafx.scene.paint.Color.YELLOW);
            gc.fillRect(0, 0, DISP_WIDTH, 48);
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font("Arial", 24));
            gc.fillText(saveMessage, 16, 32);
        }
    }
    
    /**
     * Renders the wait screen (not implemented).
     */
    private void renderWaitScreen() {
        // TODO: Implement wait screen
    }
    
    /**
     * Renders the continue screen (not implemented).
     */
    private void renderContinueScreen() {
        // TODO: Implement continue screen
    }
    
    /**
     * Renders the game over screen.
     */
    private void renderGameOverScreen() {
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.setFont(javafx.scene.text.Font.font("Arial", 48));
        gc.fillText("GAME OVER", DISP_WIDTH * 0.25, DISP_HEIGHT * 0.4);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("Press ENTER to restart", DISP_WIDTH * 0.25, DISP_HEIGHT * 0.6);
        gc.fillText("Total Score: " + score, DISP_WIDTH * 0.25, DISP_HEIGHT * 0.7);
        gc.fillText("Battles Won: " + battlesWon, DISP_WIDTH * 0.25, DISP_HEIGHT * 0.8);
    }
    
    /**
     * Handles ENTER/SPACE key logic for state transitions and command selection.
     */
    public void hitKeySelect() {
        System.out.println("hitKeySelect called - currentGameStatus: " + currentGameStatus);
        if (currentGameStatus == GAME_OVER) {
            System.out.println("Restarting game...");
            // Restart game
            playerHP = maxPlayerHP;
            currentGameStatus = GAME_TITLE;
            currentMode = MODE_MOVE;
            fieldMapEndHeight = 16;
            fieldMapEndWidth = 16;
            score = 0;
            battlesWon = 0; // Reset battle counter
            System.out.println("Game restarted - new status: " + currentGameStatus);
            // Play title music
            audioManager.playMusic(AudioManager.MUSIC_TITLE);
        } else if (currentGameStatus == GAME_TITLE) {
            System.out.println("Starting game...");
            currentGameStatus = GAME_OPEN;
            // Play field music
            audioManager.playMusic(AudioManager.MUSIC_FIELD);
        } else if (currentMode == MODE_MOVE) {
            System.out.println("Opening command menu");
            currentMode = MODE_COM;
            // Play menu open sound
            audioManager.playSound(AudioManager.SOUND_MENU_OPEN);
        } else if (currentMode == MODE_COM) {
            System.out.println("Command selected: " + currentCommand);
            // Play menu select sound
            audioManager.playSound(AudioManager.SOUND_MENU_SELECT);
            // Handle command selection
            handleCommandSelection();
        }
    }
    
    /**
     * Handles UP key/menu navigation.
     */
    public void hitUp() {
        System.out.println("hitUp called - currentMode: " + currentMode);
        if (commandMessage != null) { commandMessage = null; return; }
        if (currentMode == MODE_MOVE) {
            moveFieldMap(0); // Up direction
        } else if (currentMode == MODE_COM) {
            currentCommand--;
            if (currentCommand < COM_TALK) currentCommand = COM_STUS;
            System.out.println("Command menu up: " + currentCommand);
        }
    }
    
    /**
     * Handles DOWN key/menu navigation.
     */
    public void hitDown() {
        System.out.println("hitDown called - currentMode: " + currentMode);
        if (commandMessage != null) { commandMessage = null; return; }
        if (currentMode == MODE_MOVE) {
            moveFieldMap(1); // Down direction
        } else if (currentMode == MODE_COM) {
            currentCommand++;
            if (currentCommand > COM_STUS) currentCommand = COM_TALK;
            System.out.println("Command menu down: " + currentCommand);
        }
    }
    
    /**
     * Handles LEFT key.
     */
    public void hitLeft() {
        System.out.println("hitLeft called - currentMode: " + currentMode);
        if (commandMessage != null) { commandMessage = null; return; }
        if (currentMode == MODE_MOVE) {
            moveFieldMap(2); // Left direction
        }
    }
    
    /**
     * Handles RIGHT key.
     */
    public void hitRight() {
        System.out.println("hitRight called - currentMode: " + currentMode);
        if (commandMessage != null) { commandMessage = null; return; }
        if (currentMode == MODE_MOVE) {
            moveFieldMap(3); // Right direction
        }
    }
    
    /**
     * Handles ESC key for exiting menus, events, or battle (if over).
     */
    public void hitSoft2() {
        System.out.println("ESC pressed. currentMode=" + currentMode + ", playerHP=" + playerHP + ", monsterHP=" + monsterHP);
        if (commandMessage != null) { commandMessage = null; return; }
        if (currentMode == MODE_COM || currentMode == MODE_EVENT) {
            System.out.println("ESC: Exiting command/event mode");
            currentMode = MODE_MOVE;
            // Return to field music if exiting from command/event mode
            if (currentGameStatus == GAME_OPEN) {
                audioManager.playMusic(AudioManager.MUSIC_FIELD);
            }
        } else if (currentMode == MODE_BATTLE) {
            // Only exit battle if battle is over
            if (playerHP <= 0 || monsterHP <= 0) {
                System.out.println("ESC: Exiting battle mode (battle over)");
                currentMode = MODE_MOVE;
                battleMessage = "";
                // Return to field music
                audioManager.playMusic(AudioManager.MUSIC_FIELD);
            } else {
                System.out.println("ESC: Battle ongoing, not exiting");
            }
        }
        // TODO: Implement soft key 2 functionality for other modes if needed
    }
    
    /**
     * Checks if a map tile is walkable (not sea).
     * @param row The map row.
     * @param col The map column.
     * @return True if walkable, false otherwise.
     */
    private boolean isWalkable(int row, int col) {
        int tile = fieldMapData.mapDataReturnField(row, col);
        boolean walkable = tile != 0;
        System.out.println("isWalkable: row=" + row + ", col=" + col + ", tile=" + tile + ", walkable=" + walkable);
        return walkable; // Only sea (tile 0) is unwalkable
    }

    /**
     * Moves the player on the map and triggers random encounters.
     * @param direction 0=up, 1=down, 2=left, 3=right
     */
    private void moveFieldMap(int direction) {
        System.out.println("moveFieldMap called - direction: " + direction);
        // 0: Up, 1: Down, 2: Left, 3: Right
        int newRow = fieldMapEndHeight;
        int newCol = fieldMapEndWidth;
        switch (direction) {
            case 0: newRow = fieldMapEndHeight - 1; break;
            case 1: newRow = fieldMapEndHeight + 1; break;
            case 2: newCol = fieldMapEndWidth - 1; break;
            case 3: newCol = fieldMapEndWidth + 1; break;
        }
        int playerRow = newRow + 8;
        int playerCol = newCol + 8;
        System.out.println("Attempting move to: row=" + playerRow + ", col=" + playerCol);
        if (playerRow >= 0 && playerRow < fieldMapData.getMapLength() &&
            playerCol >= 0 && playerCol < fieldMapData.FIELD_MAP_WIDTH &&
            isWalkable(playerRow, playerCol)) {
            fieldMapEndHeight = newRow;
            fieldMapEndWidth = newCol;
            if (fieldMapEndHeight < 0) fieldMapEndHeight = 0;
            if (fieldMapEndHeight > fieldMapData.getMapLength() - 16)
                fieldMapEndHeight = fieldMapData.getMapLength() - 16;
            if (fieldMapEndWidth < 0) fieldMapEndWidth = 0;
            if (fieldMapEndWidth > fieldMapData.FIELD_MAP_WIDTH - 16)
                fieldMapEndWidth = fieldMapData.FIELD_MAP_WIDTH - 16;
            System.out.println("Player moved to: fieldMapEndHeight=" + fieldMapEndHeight + ", fieldMapEndWidth=" + fieldMapEndWidth);
            // Random encounter: 3% chance
            if (Math.random() < 0.03) {
                System.out.println("Random encounter triggered!");
                startBattle();
            }
            // Play movement sound
            audioManager.playSound(AudioManager.SOUND_MOVE);
            score += 1;
        } else {
            System.out.println("Move blocked: not walkable or out of bounds");
        }
    }
    
    /**
     * Starts a new battle (resets monster HP, not player HP).
     */
    private void startBattle() {
        System.out.println("Battle started. playerHP=" + playerHP);
        currentMode = MODE_BATTLE;
        // Randomly select a monster
        currentMonster = monsters[(int)(Math.random() * monsters.length)];
        System.out.println("Selected monster: " + currentMonster.name + " (HP: " + currentMonster.maxHP + ", Attack: " + currentMonster.minAttack + "-" + currentMonster.maxAttack + ")");
        monsterHP = currentMonster.maxHP;
        playerTurn = true;
        isDefending = false; // Reset defending state
        battleMessage = "";
        
        // Play battle start sound and music
        audioManager.playSound(AudioManager.SOUND_BATTLE_START);
        audioManager.playMusic(AudioManager.MUSIC_BATTLE);
    }
    
    /**
     * Saves the current game state to a file.
     */
    public void saveGame() {
        System.out.println("Saving game...");
        try {
            String saveData = String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d",
                currentGameStatus, currentMode, currentPlace, currentCommand,
                fieldMapEndWidth, fieldMapEndHeight, scriptID, scriptLineIndex, flip);
            Files.write(Paths.get(saveFileName), saveData.getBytes());
            saveMessage = "Game saved successfully!";
            saveMessageTime = System.currentTimeMillis();
            System.out.println("Game saved.");
            // Play save sound
            audioManager.playSound(AudioManager.SOUND_SAVE);
        } catch (IOException e) {
            saveMessage = "Save failed: " + e.getMessage();
            saveMessageTime = System.currentTimeMillis();
            System.out.println("Save failed: " + e.getMessage());
        }
    }
    
    /**
     * Loads the game state from a file.
     */
    public void loadGame() {
        System.out.println("Loading game...");
        try {
            String saveData = Files.readString(Paths.get(saveFileName));
            String[] parts = saveData.split(",");
            if (parts.length >= 9) {
                currentGameStatus = Integer.parseInt(parts[0]);
                currentMode = Integer.parseInt(parts[1]);
                currentPlace = Integer.parseInt(parts[2]);
                currentCommand = Integer.parseInt(parts[3]);
                fieldMapEndWidth = Integer.parseInt(parts[4]);
                fieldMapEndHeight = Integer.parseInt(parts[5]);
                scriptID = Integer.parseInt(parts[6]);
                scriptLineIndex = Integer.parseInt(parts[7]);
                flip = Integer.parseInt(parts[8]);
                saveMessage = "Game loaded successfully!";
                saveMessageTime = System.currentTimeMillis();
                System.out.println("Game loaded.");
                // Play load sound
                audioManager.playSound(AudioManager.SOUND_LOAD);
            }
        } catch (IOException e) {
            saveMessage = "Load failed: " + e.getMessage();
            saveMessageTime = System.currentTimeMillis();
            System.out.println("Load failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles battle input (A/D keys) and updates battle state.
     * @param keyCode The key pressed.
     */
    public void handleBattleInput(KeyCode keyCode) {
        System.out.println("Battle input: " + keyCode + ", playerTurn=" + playerTurn + ", playerHP=" + playerHP + ", monsterHP=" + monsterHP);
        if (playerHP <= 0 || monsterHP <= 0) {
            System.out.println("Battle input ignored: battle is over");
            return; // Battle is over
        }
        
        if (playerTurn) {
            switch (keyCode) {
                case A:
                    int damage = (int)(Math.random() * 5) + 3; // 3-7 damage (player)
                    monsterHP -= damage;
                    battleMessage = "You deal " + damage + " damage!";
                    System.out.println("Player attacks: monsterHP=" + monsterHP);
                    playerTurn = false;
                    // Play attack sound
                    audioManager.playSound(AudioManager.SOUND_ATTACK);
                    break;
                case D:
                    battleMessage = "You defend!";
                    System.out.println("Player defends");
                    playerTurn = false;
                    isDefending = true;
                    // Play defend sound
                    audioManager.playSound(AudioManager.SOUND_DEFEND);
                    break;
                case R:
                    // Try to escape: 50% chance
                    if (Math.random() < 0.5) {
                        battleMessage = "You escaped successfully!";
                        System.out.println("Player escaped from battle");
                        currentMode = MODE_MOVE;
                        // Play escape sound and return to field music
                        audioManager.playSound(AudioManager.SOUND_ESCAPE);
                        audioManager.playMusic(AudioManager.MUSIC_FIELD);
                        return; // Exit battle immediately
                    } else {
                        battleMessage = "Escape failed!";
                        System.out.println("Player failed to escape");
                        playerTurn = false;
                        // Play escape failed sound
                        audioManager.playSound(AudioManager.SOUND_DEFEAT);
                    }
                    break;
            }
        }
        
        // Monster's turn
        if (!playerTurn) {
            int monsterDamage = (int)(Math.random() * (currentMonster.maxAttack - currentMonster.minAttack + 1)) + currentMonster.minAttack;
            if (isDefending) {
                monsterDamage = (int)(monsterDamage * 0.5); // Reduce damage by 50% if defending
                isDefending = false;
            }
            playerHP -= monsterDamage;
            battleMessage = currentMonster.name + " deals " + monsterDamage + " damage!";
            System.out.println("Monster attacks: playerHP=" + playerHP);
            playerTurn = true;
            
            // Check for game over after monster attack
            if (playerHP <= 0) {
                playerHP = 0;
                battleMessage = "You were defeated!";
                currentGameStatus = GAME_OVER;
                System.out.println("Player defeated. GAME_OVER");
                // Play defeat sound and game over music
                audioManager.playSound(AudioManager.SOUND_DEFEAT);
                audioManager.playSound(AudioManager.SOUND_GAME_OVER);
                return; // Exit battle immediately
            }
        }
        
        // Check for battle victory (only if player is still alive)
        if (playerHP > 0 && monsterHP <= 0) {
            monsterHP = 0;
            battlesWon++;
            battleMessage = "You won the battle!";
            System.out.println("Monster defeated. Player wins. Total battles won: " + battlesWon);
            // Play victory sound and music, then return to field music
            audioManager.playSound(AudioManager.SOUND_VICTORY);
            audioManager.playMusic(AudioManager.MUSIC_VICTORY);
            // Return to field music after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Wait 2 seconds
                    audioManager.playMusic(AudioManager.MUSIC_FIELD);
                } catch (InterruptedException e) {
                    // Ignore interruption
                }
            }).start();
        }
    }
    
    /**
     * Handles command menu selection logic.
     */
    private void handleCommandSelection() {
        System.out.println("handleCommandSelection called: currentCommand=" + currentCommand);
        // Show message for selected command or switch to battle/event
        String[] commands = {"TALK", "CHECK", "MAGIC", "ITEM", "STATUS"};
        if (currentCommand == COM_MGK) {
            System.out.println("MAGIC selected: starting battle");
            currentMode = MODE_BATTLE;
            // Reset only monster and battle state, keep player HP
            monsterHP = currentMonster.maxHP;
            playerTurn = true;
            battleMessage = "";
        } else if (currentCommand == COM_STUS) {
            System.out.println("STATUS selected: entering event mode");
            currentMode = MODE_EVENT;
        } else {
            commandMessage = "You selected " + commands[currentCommand - 1];
            commandMessageTime = System.currentTimeMillis();
            currentMode = MODE_MOVE;
            System.out.println("Command message set: " + commandMessage);
        }
    }
    
    /**
     * Toggle background music on/off
     */
    public void toggleMusic() {
        audioManager.setMusicEnabled(!audioManager.isMusicEnabled());
        if (audioManager.isMusicEnabled() && currentGameStatus == GAME_OPEN && currentMode == MODE_MOVE) {
            audioManager.playMusic(AudioManager.MUSIC_FIELD);
        }
    }
    
    /**
     * Toggle sound effects on/off
     */
    public void toggleSound() {
        audioManager.setSoundEnabled(!audioManager.isSoundEnabled());
    }
    
    /**
     * Decrease volume for both music and sound effects
     */
    public void decreaseVolume() {
        double newMusicVol = Math.max(0.0, audioManager.getMusicVolume() - 0.1);
        double newSoundVol = Math.max(0.0, audioManager.getSoundVolume() - 0.1);
        audioManager.setMusicVolume(newMusicVol);
        audioManager.setSoundVolume(newSoundVol);
    }
    
    /**
     * Increase volume for both music and sound effects
     */
    public void increaseVolume() {
        double newMusicVol = Math.min(1.0, audioManager.getMusicVolume() + 0.1);
        double newSoundVol = Math.min(1.0, audioManager.getSoundVolume() + 0.1);
        audioManager.setMusicVolume(newMusicVol);
        audioManager.setSoundVolume(newSoundVol);
    }
    
    /**
     * Main entry point for the application.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
} 