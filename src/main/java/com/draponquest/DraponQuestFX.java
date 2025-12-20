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
import com.draponquest.Monster;

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
    static final int MODE_SHOP = 4;
    static final int MODE_STATUS = 5;

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
    public int currentGameStatus = GAME_TITLE;
    public int currentMode = MODE_MOVE;
    private int currentPlace = PLACE_FIELD;
    private int currentCommand = COM_TALK;
    private int flip = 0;

    // Map variables
    private int fieldMapEndWidth = 40; // 40 + 8 = 48 (Tantegel X)
    private int fieldMapEndHeight = 48; // 48 + 8 = 56 (Tantegel Y)
    private int savedFieldMapX = 0;
    private int savedFieldMapY = 0;

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
    private Image playerImage1;
    private Image playerImage2;
    private Image seaImage;
    private Image sandImage;
    private Image steppeImage;
    private Image forestImage;
    public Image shopImage;
    private Image plainsImage;
    private Image mountainImage;
    private Image townImage;
    private Image castleImage;
    private Image bridgeImage;
    private Image swampImage;
    private Image wallImage;
    private Image floorImage;
    private Image caveImage;
    private Image monster1Image;
    private Image monster2Image;
    private Image monster3Image;
    private Image monster4Image;
    private Image monster5Image;

    public int playerHP = 40;
    public int maxPlayerHP = 40;
    public int playerXP = 0;
    public int playerLevel = 1;
    public int xpToNextLevel = 10;
    public int playerGold = 0;
    public int playerAttack = 5;
    public int playerDefense = 2;
    public String commandMessage = null;
    public long commandMessageTime = 0;

    // Message box for shop actions
    public String shopMessage = null;
    public long shopMessageTime = 0;

    // Save/load data
    private String saveFileName = "draponquest_save.dat";
    private String saveMessage = null;
    private long saveMessageTime = 0;

    // Battle reward message
    public String battleRewardMessage = null;
    public long battleRewardMessageTime = 0;

    private int score = 0;
    public int battlesWon = 0; // Track number of battles won

    // Audio system
    public AudioManager audioManager;

    public Monster[] monsters;
    private Inventory inventory;
    private Shop shop;
    public BattleManager battleManager;

    /**
     * Initializes the game and sets up the JavaFX UI.
     * 
     * @param primaryStage The main application window.
     */
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Game started: Showing title screen");
        // Initialize game components
        initializeGame();
        inventory = new Inventory();
        inventory.addItem(new Item("Potion", "Restores 20 HP", "heal_20", 10));
        shop = new Shop();
        battleManager = new BattleManager(this);

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
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(inputHandler::handleKeyReleased);

        // Setup stage
        primaryStage.setTitle("DraponQuest JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Start game loop
        gameLoop.start();
    }

    private void handleKeyPressed(KeyEvent event) {
        // Always allow ENTER or SPACE to work for game state transitions (e.g.,
        // restarting from Game Over)
        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
            hitKeySelect();
            return; // Consume the event so other handlers don't process it
        }

        if (currentMode == MODE_BATTLE) {
            battleManager.handleBattleInput(event.getCode());
        } else if (currentMode == MODE_SHOP) {

            if (event.getCode() == KeyCode.B) {
                Item potion = shop.getItems().stream().filter(item -> item.getName().equals("Potion")).findFirst()
                        .orElse(null);
                if (potion != null) {
                    if (playerGold >= potion.getValue()) {
                        playerGold -= potion.getValue();
                        inventory.addItem(potion);
                        shopMessage = "You bought a potion!";
                        shopMessageTime = System.currentTimeMillis();
                    } else {
                        shopMessage = "You don't have enough gold.";
                        shopMessageTime = System.currentTimeMillis();
                    }
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                currentMode = MODE_MOVE;
            }
        } else if (currentMode == MODE_STATUS) {
            if (event.getCode() == KeyCode.ESCAPE) {
                currentMode = MODE_MOVE;
            }
        } else {
            inputHandler.handleKeyPressed(event);
        }

        if (event.getCode() == KeyCode.I) {
            System.out.println(inventory.toString());
        }

        if (event.getCode() == KeyCode.P) {
            Item potion = inventory.getItems().stream().filter(item -> item.getName().equals("Potion")).findFirst()
                    .orElse(null);
            if (potion != null) {
                playerHP += 20;
                if (playerHP > maxPlayerHP) {
                    playerHP = maxPlayerHP;
                }
                inventory.removeItem(potion);
                System.out.println("You used a potion and restored 20 HP. You have "
                        + inventory.getItems().stream().filter(item -> item.getName().equals("Potion")).count()
                        + " potions left.");
            } else {
                System.out.println("You have no potions left.");
            }
        }
    }

    /**
     * Initializes game components, images, and script buffers.
     */
    private void initializeGame() {
        System.out.println("Initializing game components");
        // Initialize map data
        fieldMapData.initialize();
        // No need to initialize scriptBuffer anymore
        // Initialize map data
        fieldMapData.initialize();

        // Initialize audio system
        audioManager = AudioManager.getInstance();

        // Load player images for animation
        try {
            playerImage1 = new Image(getClass().getResourceAsStream("/images/me1.gif"));
        } catch (Exception e) {
            playerImage1 = null;
        }
        try {
            playerImage2 = new Image(getClass().getResourceAsStream("/images/me2.gif"));
        } catch (Exception e) {
            playerImage2 = null;
        }

        // Load tile images
        try {
            seaImage = new Image(getClass().getResourceAsStream("/images/sea.gif"));
        } catch (Exception e) {
            seaImage = null;
        }
        try {
            sandImage = new Image(getClass().getResourceAsStream("/images/snd.gif"));
        } catch (Exception e) {
            sandImage = null;
        }
        try {
            steppeImage = new Image(getClass().getResourceAsStream("/images/stp.gif"));
        } catch (Exception e) {
            steppeImage = null;
        }
        try {
            forestImage = new Image(getClass().getResourceAsStream("/images/wd.gif"));
        } catch (Exception e) {
            forestImage = null;
        }
        try {
            shopImage = new Image(getClass().getResourceAsStream("/images/shop.gif"));
        } catch (Exception e) {
            shopImage = null;
        }
        try {
            plainsImage = new Image(getClass().getResourceAsStream("/images/plains.gif"));
        } catch (Exception e) {
            plainsImage = null;
        }
        try {
            mountainImage = new Image(getClass().getResourceAsStream("/images/mountain.gif"));
        } catch (Exception e) {
            mountainImage = null;
        }
        try {
            townImage = new Image(getClass().getResourceAsStream("/images/town.gif"));
        } catch (Exception e) {
            townImage = null;
        }
        try {
            castleImage = new Image(getClass().getResourceAsStream("/images/castle.gif"));
        } catch (Exception e) {
            castleImage = null;
        }
        try {
            bridgeImage = new Image(getClass().getResourceAsStream("/images/bridge.gif"));
        } catch (Exception e) {
            bridgeImage = null;
        }
        try {
            swampImage = new Image(getClass().getResourceAsStream("/images/swamp.gif"));
        } catch (Exception e) {
            swampImage = null;
        }
        try {
            wallImage = new Image(getClass().getResourceAsStream("/images/wall.gif"));
        } catch (Exception e) {
            wallImage = null;
        }
        try {
            floorImage = new Image(getClass().getResourceAsStream("/images/floor.gif"));
        } catch (Exception e) {
            floorImage = null;
        }
        try {
            caveImage = new Image(getClass().getResourceAsStream("/images/cave.gif"));
        } catch (Exception e) {
            caveImage = null;
        }
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
        try {
            monster4Image = new Image(getClass().getResourceAsStream("/images/monster4.gif"));
        } catch (Exception e) {
            monster4Image = null;
        }
        try {
            monster5Image = new Image(getClass().getResourceAsStream("/images/monster5.gif"));
        } catch (Exception e) {
            monster5Image = null;
        }
        // Initialize monsters array
        monsters = new Monster[] {
                new Monster(monster1Image, "Tung Tung Tung Sahur", 4, 2, 1, 5, 10),
                new Monster(monster2Image, "Tralalero Tralala", 6, 4, 2, 8, 15),
                new Monster(monster3Image, "Bombardiro Crocodilo", 9, 6, 3, 12, 20),
                new Monster(monster4Image, "Ballerina Cappuccina", 8, 5, 2, 15, 25),
                new Monster(monster5Image, "Cappuccino Assassino", 12, 7, 4, 25, 40)
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
        if (levelUpMessage != null && System.currentTimeMillis() - levelUpMessageTime > 2000) {
            System.out.println("Level up message cleared");
            levelUpMessage = null;
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
                int tile;
                if (currentPlace == PLACE_BLDNG) {
                    tile = fieldMapData.mapDataReturnTown(i + fieldMapEndHeight, j + fieldMapEndWidth);
                } else if (currentPlace == PLACE_CAVE) {
                    tile = fieldMapData.mapDataReturnCave(i + fieldMapEndHeight, j + fieldMapEndWidth);
                } else {
                    tile = fieldMapData.mapDataReturnField(i + fieldMapEndHeight, j + fieldMapEndWidth);
                }
                Image tileImage = null;
                switch (tile) {
                    case fieldMapData.TILE_SEA:
                        tileImage = seaImage;
                        break;
                    case fieldMapData.TILE_SAND:
                        tileImage = sandImage;
                        break;
                    case fieldMapData.TILE_STEPPE:
                        tileImage = steppeImage;
                        break;
                    case fieldMapData.TILE_FOREST:
                        tileImage = forestImage;
                        break;
                    case fieldMapData.TILE_SHOP:
                        tileImage = shopImage;
                        break;
                    case fieldMapData.TILE_PLAINS:
                        tileImage = plainsImage;
                        break;
                    case fieldMapData.TILE_MOUNTAIN:
                        tileImage = mountainImage;
                        break;
                    case fieldMapData.TILE_TOWN:
                        tileImage = townImage;
                        break;
                    case fieldMapData.TILE_CASTLE:
                        tileImage = castleImage;
                        break;
                    case fieldMapData.TILE_BRIDGE:
                        tileImage = bridgeImage;
                        break;
                    case fieldMapData.TILE_SWAMP:
                        tileImage = swampImage;
                        break;
                    case fieldMapData.TILE_WALL:
                        tileImage = wallImage;
                        break;
                    case fieldMapData.TILE_FLOOR:
                        tileImage = floorImage;
                        break;
                    case fieldMapData.TILE_CAVE:
                        tileImage = caveImage;
                        break;
                }
                if (tileImage != null && !tileImage.isError()) {
                    gc.drawImage(tileImage, j * 32, i * 32, 32, 32);
                } else {
                    switch (tile) {
                        case 0:
                            gc.setFill(javafx.scene.paint.Color.DEEPSKYBLUE);
                            break;
                        case 1:
                            gc.setFill(javafx.scene.paint.Color.GOLD);
                            break;
                        case 2:
                            gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                            break;
                        case 3:
                            gc.setFill(javafx.scene.paint.Color.FORESTGREEN);
                            break;
                        case fieldMapData.TILE_SHOP:
                            gc.setFill(javafx.scene.paint.Color.BROWN);
                            break; // Shop
                        case fieldMapData.TILE_PLAINS:
                            gc.setFill(javafx.scene.paint.Color.LIMEGREEN);
                            break; // Plains
                        case fieldMapData.TILE_MOUNTAIN:
                            gc.setFill(javafx.scene.paint.Color.DARKGRAY);
                            break; // Mountain
                        case fieldMapData.TILE_TOWN:
                            gc.setFill(javafx.scene.paint.Color.ORANGE);
                            break; // Town
                        case fieldMapData.TILE_CASTLE:
                            gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                            break; // Castle
                        case fieldMapData.TILE_BRIDGE:
                            gc.setFill(javafx.scene.paint.Color.SADDLEBROWN);
                            break; // Bridge
                        case fieldMapData.TILE_SWAMP:
                            gc.setFill(javafx.scene.paint.Color.DARKGREEN);
                            break; // Swamp
                        case fieldMapData.TILE_WALL:
                            gc.setFill(javafx.scene.paint.Color.DARKSLATEGRAY);
                            break; // Wall
                        case fieldMapData.TILE_FLOOR:
                            gc.setFill(javafx.scene.paint.Color.rgb(200, 180, 150));
                            break; // Floor
                        case fieldMapData.TILE_CAVE:
                            gc.setFill(javafx.scene.paint.Color.BLACK);
                            break; // Cave
                        default:
                            gc.setFill(javafx.scene.paint.Color.BLACK);
                            break;
                    }
                    gc.fillRect(j * 32, i * 32, 32, 32);
                }
            }
        }

        // Draw player sprite (scaled up)
        Image currentPlayerImage = (flip == 0) ? playerImage1 : playerImage2;
        if (currentPlayerImage != null && !currentPlayerImage.isError()) {
            gc.drawImage(currentPlayerImage, 8 * 32, 8 * 32, 32, 32);
        }

        // Display HP and score on map
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 20));
        gc.fillText("HP: " + playerHP + "/" + maxPlayerHP, 10, 30);
        gc.fillText("Level: " + playerLevel, 10, 60);
        gc.fillText("XP: " + playerXP + "/" + xpToNextLevel, 10, 90);
        gc.fillText("Gold: " + playerGold, 10, 120);
        gc.fillText("Battles Won: " + battlesWon, 10, 150);

        // Display audio status
        gc.setFont(javafx.scene.text.Font.font("Arial", 14));
        gc.setFill(audioManager.isMusicEnabled() ? javafx.scene.paint.Color.LIME : javafx.scene.paint.Color.RED);
        gc.fillText("Music: " + (audioManager.isMusicEnabled() ? "ON" : "OFF"), 10, 180);
        gc.setFill(audioManager.isSoundEnabled() ? javafx.scene.paint.Color.LIME : javafx.scene.paint.Color.RED);
        gc.fillText("Sound: " + (audioManager.isSoundEnabled() ? "ON" : "OFF"), 10, 200);
        gc.setFill(javafx.scene.paint.Color.YELLOW);
        gc.fillText("Vol: " + (int) (audioManager.getMusicVolume() * 100) + "%", 10, 220);
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
        // Display battle reward message
        if (battleRewardMessage != null && System.currentTimeMillis() - battleRewardMessageTime < 3000) { // Display for
                                                                                                          // 3 seconds
            gc.setFill(javafx.scene.paint.Color.YELLOW);
            gc.fillRect(0, DISP_HEIGHT / 2 - 24, DISP_WIDTH, 48);
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font("Arial", 28));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.fillText(battleRewardMessage, DISP_WIDTH / 2, DISP_HEIGHT / 2 + 8);
            gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT); // Reset alignment
        }
        if (levelUpMessage != null) {
            if (System.currentTimeMillis() - levelUpMessageTime < 2000) {
                gc.setFill(javafx.scene.paint.Color.YELLOW);
                gc.fillRect(0, 0, DISP_WIDTH, 48);
                gc.setFill(javafx.scene.paint.Color.BLACK);
                gc.setFont(javafx.scene.text.Font.font("Arial", 24));
                gc.fillText(levelUpMessage, 16, 32);
            } else {
                levelUpMessage = null;
            }
        }
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
            String[] commands = {
                    LocalizationManager.getText("menu_talk"),
                    LocalizationManager.getText("menu_check"),
                    LocalizationManager.getText("menu_magic"),
                    LocalizationManager.getText("menu_item"),
                    LocalizationManager.getText("menu_status")
            };
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
            gc.fillText(LocalizationManager.getText("battle_title"), DISP_WIDTH / 2, 60);
            // Draw monster name centered above image
            if (battleManager.getCurrentMonster() != null && battleManager.getCurrentMonster().image != null
                    && !battleManager.getCurrentMonster().image.isError()) {
                gc.setFont(javafx.scene.text.Font.font("Arial", 28));
                gc.setFill(javafx.scene.paint.Color.YELLOW);
                gc.fillText(battleManager.getCurrentMonster().name, DISP_WIDTH / 2, 120);
                // Draw monster image centered
                gc.drawImage(battleManager.getCurrentMonster().image, (DISP_WIDTH - 128) / 2, 130, 128, 128);
            } else {
                gc.setFill(javafx.scene.paint.Color.DARKRED);
                gc.fillRect((DISP_WIDTH - 128) / 2, 130, 128, 128);
                gc.setFill(javafx.scene.paint.Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font("Arial", 18));
                gc.fillText(LocalizationManager.getText("no_monster_image"), DISP_WIDTH / 2, 190);
            }
            // Draw HP bars, each on its own line, centered
            gc.setFont(javafx.scene.text.Font.font("Arial", 26));
            gc.setFill(javafx.scene.paint.Color.LIME);
            String playerHpStr = LocalizationManager.getText("player_hp") + playerHP + "/" + maxPlayerHP + " | ATK: "
                    + playerAttack + " | DEF: " + playerDefense;
            gc.fillText(playerHpStr, DISP_WIDTH / 2, 290);
            gc.setFill(javafx.scene.paint.Color.RED);
            String monsterHpStr = battleManager.getCurrentMonster().name + LocalizationManager.getText("monster_hp")
                    + battleManager.getMonsterHP() + " | ATK: " + battleManager.getCurrentMonster().attack + " | DEF: "
                    + battleManager.getCurrentMonster().defense;
            gc.fillText(monsterHpStr, DISP_WIDTH / 2, 330);
            // Draw battle message
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 20));
            gc.fillText(battleManager.getBattleMessage(), DISP_WIDTH / 2, DISP_HEIGHT - 80);
            // Draw action options
            gc.setFill(javafx.scene.paint.Color.YELLOW);
            gc.setFont(javafx.scene.text.Font.font("Arial", 24));
            gc.fillText(LocalizationManager.getText("battle_actions"), DISP_WIDTH / 2, DISP_HEIGHT - 30);
            gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT); // Reset to default
        }
        if (currentMode == MODE_SHOP) {
            gc.setFill(javafx.scene.paint.Color.rgb(32, 64, 32, 0.85));
            gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 40));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.fillText("Welcome to the Shop!", DISP_WIDTH / 2, 60);

            gc.setFont(javafx.scene.text.Font.font("Arial", 28));
            gc.setFill(javafx.scene.paint.Color.YELLOW);
            gc.fillText("Items for sale:", DISP_WIDTH / 2, 120);

            java.util.List<Item> items = shop.getItems();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                String text = (i + 1) + ". " + item.getName() + " - " + item.getValue() + " gold";
                gc.fillText(text, DISP_WIDTH / 2, 160 + i * 40);
            }

            gc.setFont(javafx.scene.text.Font.font("Arial", 24));
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillText("Press 'B' to buy a Potion.", DISP_WIDTH / 2, DISP_HEIGHT - 80);
            gc.fillText("Press ESC to exit.", DISP_WIDTH / 2, DISP_HEIGHT - 40);

            if (shopMessage != null) {
                gc.setFill(javafx.scene.paint.Color.YELLOW);
                gc.fillText(shopMessage, DISP_WIDTH / 2, DISP_HEIGHT - 120);
                if (System.currentTimeMillis() - shopMessageTime > 2000) {
                    shopMessage = null;
                }
            }
        }
        if (currentMode == MODE_STATUS) {
            renderStatusScreen();
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

    private void renderStatusScreen() {
        gc.setFill(javafx.scene.paint.Color.rgb(32, 32, 32, 0.85));
        gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 40));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText("Status", DISP_WIDTH / 2, 60);

        gc.setFont(javafx.scene.text.Font.font("Arial", 28));
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillText("Level: " + playerLevel, DISP_WIDTH / 2, 120);
        gc.fillText("HP: " + playerHP + "/" + maxPlayerHP, DISP_WIDTH / 2, 160);
        gc.fillText("XP: " + playerXP + "/" + xpToNextLevel, DISP_WIDTH / 2, 200);
        gc.fillText("Gold: " + playerGold, DISP_WIDTH / 2, 240);

        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("Press ESC to exit.", DISP_WIDTH / 2, DISP_HEIGHT - 40);
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
        gc.fillText(LocalizationManager.getText("game_over"), DISP_WIDTH * 0.25, DISP_HEIGHT * 0.4);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText(LocalizationManager.getText("press_enter_restart"), DISP_WIDTH * 0.25, DISP_HEIGHT * 0.6);
        gc.fillText(LocalizationManager.getText("total_score") + score, DISP_WIDTH * 0.25, DISP_HEIGHT * 0.7);
        gc.fillText(LocalizationManager.getText("battles_won") + battlesWon, DISP_WIDTH * 0.25, DISP_HEIGHT * 0.8);
    }

    /**
     * Handles ENTER/SPACE key logic for state transitions and command selection.
     */
    public void hitKeySelect() {
        System.out.println("hitKeySelect called - currentGameStatus: " + currentGameStatus);
        if (currentGameStatus == GAME_OVER) {
            System.out.println("Restarting game...");
            // Restart game
            maxPlayerHP = 40;
            playerHP = maxPlayerHP;
            playerLevel = 1;
            playerXP = 0;
            xpToNextLevel = 10;
            playerGold = 0;
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
        if (commandMessage != null) {
            commandMessage = null;
            return;
        }
        if (currentMode == MODE_MOVE) {
            moveFieldMap(0); // Up direction
        } else if (currentMode == MODE_COM) {
            currentCommand--;
            if (currentCommand < COM_TALK)
                currentCommand = COM_STUS;
            System.out.println("Command menu up: " + currentCommand);
        }
    }

    /**
     * Handles DOWN key/menu navigation.
     */
    public void hitDown() {
        System.out.println("hitDown called - currentMode: " + currentMode);
        if (commandMessage != null) {
            commandMessage = null;
            return;
        }
        if (currentMode == MODE_MOVE) {
            moveFieldMap(1); // Down direction
        } else if (currentMode == MODE_COM) {
            currentCommand++;
            if (currentCommand > COM_STUS)
                currentCommand = COM_TALK;
            System.out.println("Command menu down: " + currentCommand);
        }
    }

    /**
     * Handles LEFT key.
     */
    public void hitLeft() {
        System.out.println("hitLeft called - currentMode: " + currentMode);
        if (commandMessage != null) {
            commandMessage = null;
            return;
        }
        if (currentMode == MODE_MOVE) {
            moveFieldMap(2); // Left direction
        }
    }

    /**
     * Handles RIGHT key.
     */
    public void hitRight() {
        System.out.println("hitRight called - currentMode: " + currentMode);
        if (commandMessage != null) {
            commandMessage = null;
            return;
        }
        if (currentMode == MODE_MOVE) {
            moveFieldMap(3); // Right direction
        }
    }

    /**
     * Handles ESC key for exiting menus, events, or battle (if over).
     */
    public void hitSoft2() {
        System.out.println("ESC pressed. currentMode=" + currentMode + ", playerHP=" + playerHP + ", monsterHP="
                + battleManager.getMonsterHP());
        if (commandMessage != null) {
            commandMessage = null;
            return;
        }
        if (currentMode == MODE_COM || currentMode == MODE_EVENT) {
            System.out.println("ESC: Exiting command/event mode");
            currentMode = MODE_MOVE;
            // Return to field music if exiting from command/event mode
            if (currentGameStatus == GAME_OPEN) {
                audioManager.playMusic(AudioManager.MUSIC_FIELD);
            }
        } else if (currentMode == MODE_BATTLE) {
            // Only exit battle if battle is over
            if (playerHP <= 0 || battleManager.getMonsterHP() <= 0) {
                System.out.println("ESC: Exiting battle mode (battle over)");
                currentMode = MODE_MOVE;
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
     * 
     * @param row The map row.
     * @param col The map column.
     * @return True if walkable, false otherwise.
     */
    private boolean isWalkable(int row, int col) {
        int tile;
        if (currentPlace == PLACE_BLDNG) {
            tile = fieldMapData.mapDataReturnTown(row, col);
        } else if (currentPlace == PLACE_CAVE) {
            tile = fieldMapData.mapDataReturnCave(row, col);
        } else {
            tile = fieldMapData.mapDataReturnField(row, col);
        }
        // Sea (0) and Wall (11) are unwalkable
        boolean walkable = tile != 0 && tile != fieldMapData.TILE_WALL;
        System.out.println("isWalkable: row=" + row + ", col=" + col + ", tile=" + tile + ", walkable=" + walkable);
        return walkable;
    }

    /**
     * Moves the player on the map and triggers random encounters.
     * 
     * @param direction 0=up, 1=down, 2=left, 3=right
     */
    private void moveFieldMap(int direction) {
        System.out.println("moveFieldMap called - direction: " + direction);
        // 0: Up, 1: Down, 2: Left, 3: Right
        int newRow = fieldMapEndHeight;
        int newCol = fieldMapEndWidth;
        switch (direction) {
            case 0:
                newRow = fieldMapEndHeight - 1;
                break;
            case 1:
                newRow = fieldMapEndHeight + 1;
                break;
            case 2:
                newCol = fieldMapEndWidth - 1;
                break;
            case 3:
                newCol = fieldMapEndWidth + 1;
                break;
        }
        int playerRow = newRow + 8;
        int playerCol = newCol + 8;
        System.out.println("Attempting move to: row=" + playerRow + ", col=" + playerCol);

        // EXIT LOGIC FOR TOWN/CAVE (New)
        if (currentPlace != PLACE_FIELD) {
            int currentPlayerRow = fieldMapEndHeight + 8;
            int currentPlayerCol = fieldMapEndWidth + 8;
            // If at the entrance and moving down, exit to overworld
            if (currentPlayerRow == 15 && direction == 1 && (currentPlayerCol == 7 || currentPlayerCol == 8)) {
                currentPlace = PLACE_FIELD;
                fieldMapEndWidth = savedFieldMapX;
                fieldMapEndHeight = savedFieldMapY;
                audioManager.playMusic(AudioManager.MUSIC_FIELD);
                audioManager.playSound(AudioManager.SOUND_MOVE);
                System.out.println("Exited area by stepping DOWN at: " + fieldMapEndHeight + "," + fieldMapEndWidth);
                return;
            }
        }

        if (playerRow >= 0 && playerRow < fieldMapData.getMapLength() &&
                playerCol >= 0 && playerCol < fieldMapData.FIELD_MAP_WIDTH &&
                isWalkable(playerRow, playerCol)) {
            fieldMapEndHeight = newRow;
            fieldMapEndWidth = newCol;

            // Transition logic
            int currentTile;
            if (currentPlace == PLACE_FIELD) {
                currentTile = fieldMapData.mapDataReturnField(playerRow, playerCol);
                // Enter town, castle, or cave if on appropriate tile
                if (currentTile == fieldMapData.TILE_TOWN || currentTile == fieldMapData.TILE_CASTLE
                        || currentTile == fieldMapData.TILE_CAVE) {
                    savedFieldMapX = fieldMapEndWidth;
                    savedFieldMapY = fieldMapEndHeight;

                    if (currentTile == fieldMapData.TILE_CAVE) {
                        currentPlace = PLACE_CAVE;
                        audioManager.playMusic(AudioManager.MUSIC_BATTLE); // Use battle music for caves for now
                    } else {
                        currentPlace = PLACE_BLDNG;
                        if (currentTile == fieldMapData.TILE_CASTLE) {
                            audioManager.playMusic(AudioManager.MUSIC_TITLE);
                        } else {
                            audioManager.playMusic(AudioManager.MUSIC_TOWN);
                        }
                    }

                    // Start at the bottom of the area (entrance)
                    fieldMapEndWidth = 0;
                    fieldMapEndHeight = 7; // 7 + 8 = 15 (bottom row)
                    System.out.println("Entered area from field at: " + savedFieldMapY + "," + savedFieldMapX);
                }
            }
            // (Exit logic removed from here as it's now handled at the start of the method)

            if (currentPlace == PLACE_FIELD) {
                if (fieldMapEndHeight < 0)
                    fieldMapEndHeight = 0;
                if (fieldMapEndHeight > fieldMapData.FIELD_MAP_WIDTH - 16)
                    fieldMapEndHeight = fieldMapData.FIELD_MAP_WIDTH - 16;
                if (fieldMapEndWidth < 0)
                    fieldMapEndWidth = 0;
                if (fieldMapEndWidth > fieldMapData.FIELD_MAP_WIDTH - 16)
                    fieldMapEndWidth = fieldMapData.FIELD_MAP_WIDTH - 16;
            } else {
                // Town/Cave is 16x16, player centered at 8
                if (fieldMapEndHeight < -8)
                    fieldMapEndHeight = -8;
                if (fieldMapEndHeight > 7)
                    fieldMapEndHeight = 7;
                if (fieldMapEndWidth < -8)
                    fieldMapEndWidth = -8;
                if (fieldMapEndWidth > 7)
                    fieldMapEndWidth = 7;
            }

            System.out.println("Player moved to: fieldMapEndHeight=" + fieldMapEndHeight + ", fieldMapEndWidth="
                    + fieldMapEndWidth);
            // Random encounter: 3% chance in field, 8% in cave
            double encounterRate = (currentPlace == PLACE_CAVE) ? 0.08 : 0.03;
            if ((currentPlace == PLACE_FIELD || currentPlace == PLACE_CAVE) && Math.random() < encounterRate) {
                System.out.println("Random encounter triggered!");
                battleManager.startBattle();
            }
            // Play movement sound
            audioManager.playSound(AudioManager.SOUND_MOVE);
            score += 1;
        } else {
            System.out.println("Move blocked: not walkable or out of bounds");
        }
    }

    /**
     * Saves the current game state to a file.
     */
    public void saveGame() {
        System.out.println("Saving game...");
        try {
            String saveData = String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                    currentGameStatus, currentMode, currentPlace, currentCommand,
                    fieldMapEndWidth, fieldMapEndHeight, scriptID, scriptLineIndex, flip,
                    playerXP, playerLevel, xpToNextLevel, maxPlayerHP, playerGold);
            Files.write(Paths.get(saveFileName), saveData.getBytes());
            saveMessage = LocalizationManager.getText("save_success");
            saveMessageTime = System.currentTimeMillis();
            System.out.println("Game saved.");
            // Play save sound
            audioManager.playSound(AudioManager.SOUND_SAVE);
        } catch (IOException e) {
            saveMessage = LocalizationManager.getText("save_failed") + e.getMessage();
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
            if (parts.length >= 14) {
                currentGameStatus = Integer.parseInt(parts[0]);
                currentMode = Integer.parseInt(parts[1]);
                currentPlace = Integer.parseInt(parts[2]);
                currentCommand = Integer.parseInt(parts[3]);
                fieldMapEndWidth = Integer.parseInt(parts[4]);
                fieldMapEndHeight = Integer.parseInt(parts[5]);
                scriptID = Integer.parseInt(parts[6]);
                scriptLineIndex = Integer.parseInt(parts[7]);
                flip = Integer.parseInt(parts[8]);
                playerXP = Integer.parseInt(parts[9]);
                playerLevel = Integer.parseInt(parts[10]);
                xpToNextLevel = Integer.parseInt(parts[11]);
                maxPlayerHP = Integer.parseInt(parts[12]);
                playerGold = Integer.parseInt(parts[13]);
                saveMessage = LocalizationManager.getText("load_success");
                saveMessageTime = System.currentTimeMillis();
                System.out.println("Game loaded.");
                // Play load sound
                audioManager.playSound(AudioManager.SOUND_LOAD);
            }
        } catch (IOException e) {
            saveMessage = LocalizationManager.getText("load_failed") + e.getMessage();
            saveMessageTime = System.currentTimeMillis();
            System.out.println("Load failed: " + e.getMessage());
        }
    }

    private String levelUpMessage = null;
    private long levelUpMessageTime = 0;

    public void levelUp() {
        playerLevel++;
        xpToNextLevel = (int) (xpToNextLevel * 1.5);
        maxPlayerHP += 10;
        playerHP = maxPlayerHP;
        playerAttack += 2;
        playerDefense += 1;
        levelUpMessage = "You have reached level " + playerLevel + "!";
        levelUpMessageTime = System.currentTimeMillis();
    }

    /**
     * Handles command menu selection logic.
     */
    private void handleCommandSelection() {
        System.out.println("handleCommandSelection called: currentCommand=" + currentCommand);
        int playerRow = fieldMapEndHeight + 8;
        int playerCol = fieldMapEndWidth + 8;
        int tile = fieldMapData.mapDataReturnField(playerRow, playerCol);

        if (currentCommand == COM_TALK && tile == 4) {
            currentMode = MODE_SHOP;
            shopMessage = "Welcome to the shop! What would you like to buy?";
            shopMessageTime = System.currentTimeMillis();
            return;
        }
        // Show message for selected command or switch to battle/event
        String[] commands = { "TALK", "CHECK", "MAGIC", "ITEM", "STATUS" };
        if (currentCommand == COM_MGK) {
            System.out.println("MAGIC selected: starting battle");
            battleManager.startBattle();
        } else if (currentCommand == COM_STUS) {
            System.out.println("STATUS selected: entering event mode");
            currentMode = MODE_STATUS;
        } else {
            commandMessage = LocalizationManager.getText("command_selected") + commands[currentCommand - 1];
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
     * Toggle between English and Japanese language
     */
    public void toggleLanguage() {
        LocalizationManager.toggleLanguage();
        scriptData.refreshScript();
        // Reset script lines to force reload with new language
        scriptLines = null;
        System.out.println("Language changed to: " + LocalizationManager.getLanguageDisplayName());
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
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}