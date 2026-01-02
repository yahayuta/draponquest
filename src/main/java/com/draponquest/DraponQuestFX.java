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
import javafx.scene.paint.Color;
import java.io.*;
import java.nio.file.*;
import java.util.Random;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// Audio system
import com.draponquest.AudioManager;
import com.draponquest.Monster;
import com.draponquest.TreasureChest;

/**
 * DraponQuest JavaFX Application
 * Modern JavaFX version of the original DoJa mobile game.
 * Handles main game logic, rendering, and state transitions.
 *
 * @author Yakkun (Original)
 * @author Modern Migration
 */
public class DraponQuestFX extends Application {

    @Override
    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set. The stage is the
     *                     top-level container for a JavaFX application.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drapon Quest");

        // Initialize JavaFX components
        gameCanvas = new Canvas(DISP_WIDTH, DISP_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();
        StackPane root = new StackPane(gameCanvas);
        Scene scene = new Scene(root);

        // Initialize game logic
        initializeGame();
        resetGameState(); // Start with a fresh state

        // Set up input handling
        inputHandler = new GameInputHandler(this, scene);

        // Start the game loop
        gameLoop = new GameLoop();
        gameLoop.start();

        // Audio setup
        audioManager.playMusic(AudioManager.MUSIC_TITLE);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Game constants (preserved from original)
    /**
     * The width of the game display in pixels.
     * Preserved from the original game.
     */
    private static final int DISP_WIDTH = 512;
    /**
     * The height of the game display in pixels.
     * Preserved from the original game.
     */
    private static final int DISP_HEIGHT = 512;
    /**
     * The waiting time in milliseconds between game updates.
     * Preserved from the original game.
     */
    private static final int WAIT_MSEC = 100;

    /**
     * A random number generator used for various game mechanics like encounters and NPC movement.
     */
    private Random random = new Random();

    // Game status constants
    /**
     * Game status: Title screen.
     */
    private static final int GAME_TITLE = 0;
    /**
     * Game status: Game is open and playable.
     */
    private static final int GAME_OPEN = 1;
    /**
     * Game status: Waiting state (e.g., during transitions or loading).
     */
    private static final int GAME_WAIT = 2;
    /**
     * Game status: Continue screen (e.g., after game over with option to continue).
     */
    private static final int GAME_CONT = 3;
    /**
     * Game status: Game over screen.
     */
    private static final int GAME_OVER = 4;

    // Game modes
    /**
     * Game mode: Player is moving on the map.
     */
    static final int MODE_MOVE = 0;
    /**
     * Game mode: Command menu is open.
     */
    static final int MODE_COM = 1;
    /**
     * Game mode: Player is in battle.
     */
    static final int MODE_BATTLE = 2;
    /**
     * Game mode: An event (e.g., cutscene, dialogue sequence) is active.
     */
    static final int MODE_EVENT = 3;
    /**
     * Game mode: Player is in a shop.
     */
    static final int MODE_SHOP = 4;
    /**
     * Game mode: Player is viewing the status screen.
     */
    static final int MODE_STATUS = 5;
    /**
     * Game mode: Player is viewing their inventory.
     */
    static final int MODE_INVENTORY = 6;
    /**
     * Game mode: Player is in an inn.
     */
    static final int MODE_INN = 7;

    // Places
    /**
     * Current player location: Open field/overworld.
     */
    private static final int PLACE_FIELD = 0;
    /**
     * Current player location: Inside a building (town, castle, shop).
     */
    private static final int PLACE_BLDNG = 1;
    /**
     * Current player location: Inside a cave.
     */
    private static final int PLACE_CAVE = 2;

    // Commands
    /**
     * Command: Talk to an NPC.
     */
    private static final int COM_TALK = 1;
    /**
     * Command: Check surroundings (e.g., for treasure chests).
     */
    private static final int COM_CHK = 2;
    /**
     * Command: Use magic.
     */
    private static final int COM_MGK = 3;
    /**
     * Command: Use an item from inventory.
     */
    private static final int COM_ITEM = 4;
    /**
     * Command: View player status.
     */
    private static final int COM_STUS = 5;

    // Battle commands
    /**
     * Battle command: Attack.
     */
    private static final int BCOM_ATK = 1;
    /**
     * Battle command: Use magic in battle.
     */
    private static final int BCOM_MGK = 2;
    /**
     * Battle command: Use an item in battle.
     */
    private static final int BCOM_ITEM = 3;
    /**
     * Battle command: Attempt to run from battle.
     */
    private static final int BCOM_RUN = 4;

    // Game state variables
    /**
     * The current overall status of the game (e.g., title, in-game, game over).
     */
    public int currentGameStatus = GAME_TITLE;
    /**
     * The current mode of interaction within the game (e.g., movement, battle, shop).
     */
    public int currentMode = MODE_MOVE;
    /**
     * The current geographical place the player is in (e.g., field, building, cave).
     */
    private int currentPlace = PLACE_FIELD;
    /**
     * The currently selected command in a menu.
     */
    private int currentCommand = COM_TALK;
    /**
     * Used for player animation frames (e.g., 0 or 1 to switch between two sprites).
     */
    private int flip = 0;
    /**
     * The direction the player character is currently facing (0=Up, 1=Down, 2=Left, 3=Right).
     */
    private int playerDirection = 1; // 0=Up, 1=Down, 2=Left, 3=Right
    /**
     * Flag indicating whether the minimap is currently displayed.
     */
    private boolean showMinimap = true;

    // Shop state
    /**
     * The current mode within the shop interface (0: main menu, 1: buying, 2: selling).
     */
    private int shopMode = 0; // 0: main, 1: buying, 2: selling
    /**
     * The current selection index in the shop menu.
     */
    private int shopCursor = 0;

    // Inventory state
    /**
     * The current selection index in the inventory menu.
     */
    private int inventoryCursor = 0;


    // Map variables
    /**
     * The X-coordinate of the top-left corner of the currently displayed map segment.
     */
    private int fieldMapEndWidth = 40; // 40 + 8 = 48 (Tantegel X)
    /**
     * The Y-coordinate of the top-left corner of the currently displayed map segment.
     */
    private int fieldMapEndHeight = 48; // 48 + 8 = 56 (Tantegel Y)
    /**
     * Stores the X-coordinate of the map when transitioning to an indoor area,
     * so the player can return to the correct overworld location.
     */
    private int savedFieldMapX = 0;
    /**
     * Stores the Y-coordinate of the map when transitioning to an indoor area,
     * so the player can return to the correct overworld location.
     */
    private int savedFieldMapY = 0;

    // Script variables
    /**
     * Array storing lines of dialogue or script for events.
     */
    private String[] scriptLines = null;
    /**
     * The ID of the currently active script.
     */
    private int scriptID = 0;
    /**
     * The current line index within the active script.
     */
    private int scriptLineIndex = 0;
    /**
     * A tick counter for advancing script lines.
     */
    private int scriptAdvanceTick = 0;

    // JavaFX components
    /**
     * The main canvas where all game graphics are rendered.
     */
    private Canvas gameCanvas;
    /**
     * The graphics context for drawing on the game canvas.
     */
    private GraphicsContext gc;
    /**
     * The game loop responsible for updating game logic and rendering.
     */
    private GameLoop gameLoop;
    /**
     * Handles all user input for the game.
     */
    private GameInputHandler inputHandler;
    /**
     * The current image being used for the player character.
     */
    private Image playerImage;
    /**
     * The first image in the player character's animation cycle.
     */
    private Image playerImage1;
    /**
     * The second image in the player character's animation cycle.
     */
    private Image playerImage2;
    /**
     * Image representing the sea tile.
     */
    private Image seaImage;
    /**
     * Image representing the sand tile.
     */
    private Image sandImage;
    /**
     * Image representing the steppe tile.
     */
    private Image steppeImage;
    /**
     * Image representing the forest tile.
     */
    private Image forestImage;
    /**
     * Image representing the shop tile.
     */
    public Image shopImage;
    /**
     * Image representing the plains tile.
     */
    private Image plainsImage;
    /**
     * Image representing the mountain tile.
     */
    private Image mountainImage;
    /**
     * Image representing the town tile.
     */
    private Image townImage;
    /**
     * Image representing the castle tile.
     */
    private Image castleImage;
    /** Image representing the house tile. */
    private Image houseImage;
    /** Image representing the inn tile. */
    private Image innImage;
    /**
     * Image representing the bridge tile.
     */
    private Image bridgeImage;
    /**
     * Image representing the swamp tile.
     */
    private Image swampImage;
    /**
     * Image representing the wall tile for indoor areas.
     */
    private Image wallImage;
    /**
     * Image representing the floor tile for indoor areas.
     */
    private Image floorImage;
    /**
     * Image representing the cave tile.
     */
    private Image caveImage;
    /**
     * Image for monster type 1.
     */
    private Image monster1Image;
    /**
     * Image for monster type 2.
     */
    private Image monster2Image;
    /**
     * Image for monster type 3.
     */
    private Image monster3Image;
    /**
     * Image for monster type 4.
     */
    private Image monster4Image;
    /**
     * Image for monster type 5.
     */
    private Image monster5Image;

    /**
     * The player character's current hit points.
     */
    public int playerHP = 40;
    /**
     * The player character's maximum hit points.
     */
    public int maxPlayerHP = 40;
    /**
     * The player character's current experience points.
     */
    public int playerXP = 0;
    /**
     * The player character's current level.
     */
    public int playerLevel = 1;
    /**
     * The experience points required for the player to reach the next level.
     */
    public int xpToNextLevel = 10;
    /**
     * The player character's current gold amount.
     */
    public int playerGold = 0;
    /**
     * The player character's current attack power.
     */
    public int playerAttack = 5;
    /**
     * The player character's current defense power.
     */
    public int playerDefense = 2;
    /**
     * A message displayed temporarily to the player, often after a command or action.
     */
    public String commandMessage = null;
    /**
     * The timestamp when the command message was set, used for timing its display duration.
     */
    public long commandMessageTime = 0;

    // Message box for shop actions
    /**
     * A message displayed within the shop interface, typically for transaction feedback.
     */
    public String shopMessage = null;
    /**
     * The timestamp when the shop message was set, used for timing its display duration.
     */
    public long shopMessageTime = 0;

    // Save/load data
    /**
     * The name of the file used for saving and loading game progress.
     */
    private String saveFileName = "draponquest_save.dat";
    /**
     * A message displayed to the player regarding save/load operations.
     */
    private String saveMessage = null;
    /**
     * The timestamp when the save message was set, used for timing its display duration.
     */
    private long saveMessageTime = 0;

    // Battle reward message
    /**
     * Message displayed to the player after winning a battle, detailing rewards.
     */
    public String battleRewardMessage = null;
    /**
     * The timestamp when the battle reward message was set, used for timing its display duration.
     */
    public long battleRewardMessageTime = 0;

    // NES-style Message fields
    /**
     * The full text of the current message being displayed in the NES-style dialogue box.
     */
    private String currentFullMessage = "";
    /**
     * The portion of the current message that is currently visible in the dialogue box (typewriter effect).
     */
    private StringBuilder currentVisibleMessage = new StringBuilder();
    /**
     * The index of the next character to be revealed in the current message during the typewriter effect.
     */
    private int messageCharIndex = 0;
    /**
     * Flag indicating if the game is waiting for player input to advance the current message.
     */
    private boolean isWaitingForInput = false;
    /**
     * A tick counter used to control the speed of the typewriter effect for messages.
     */
    private int typewriterTick = 0;
    /**
     * The speed at which characters are revealed in the typewriter effect (ticks per character).
     */
    private static final int TYPEWRITER_SPEED = 1; // Ticks per character
    /**
     * A callback function to be executed once a message display is completed.
     */
    private Runnable messageCallback = null;

    /**
     * The player's current score.
     */
    private int score = 0;
    /**
     * The number of battles won by the player.
     */
    public int battlesWon = 0; // Track number of battles won
    /**
     * Stores the music track that was playing before a battle started, to resume it afterward.
     */
    private String preBattleMusic;

    // Audio system
    /**
     * Manages all audio playback (music and sound effects) for the game.
     */
    public AudioManager audioManager;

    /**
     * Array of available monster types in the game.
     */
    public Monster[] monsters;
    /**
     * The player's inventory, managing items collected.
     */
    private Inventory inventory;
    /**
     * The shop instance, handling buying and selling of items.
     */
    private Shop shop;
    /**
     * Manages all aspects of combat encounters.
     */
    public BattleManager battleManager;

    // Items
    /**
     * Represents a Potion item.
     */
    private Item potion;
    /**
     * Represents a Herb item.
     */
    private Item herb;
    /**
     * Represents an Antidote item.
     */
    private Item antidote;

    // NPC System
    /**
     * Array of Non-Player Characters (NPCs) in the game world.
     */
    private NPC[] npcs = new NPC[13];
    /**
     * Image for a soldier NPC.
     */
    private Image soldierImage;
    /**
     * Image for a merchant NPC.
     */
    private Image merchantImage;
    /**
     * Image for the king NPC.
     */
    private Image kingImage;
    /**
     * Array of treasure chests scattered throughout the game world.
     */
    private TreasureChest[] treasureChests;
    
    /**
     * NPC Inner Class
     */
    class NPC {
        /**
         * The unique identifier for this NPC.
         */
        public int id;
        /**
         * The world coordinates (X, Y) of the NPC's position.
         */
        public int x, y;
        /**
         * The type of NPC (e.g., 0=Soldier, 1=Merchant, 2=King).
         */
        public int type;
        /**
         * The direction the NPC is facing (0=Down, 1=Left, 2=Right, 3=Up).
         */
        public int direction;
        /**
         * The ID of the script associated with this NPC for dialogue or events.
         */
        public int scriptID;
        /**
         * The ID of the place where this NPC resides (e.g., 0=Field, 1=Building).
         */
        public int placeID;
        /**
         * Flag indicating whether the NPC is currently visible.
         */
        public boolean visible;

        /**
         * Constructs a new NPC.
         * @param id The unique identifier for this NPC.
         * @param x The initial X-coordinate of the NPC.
         * @param y The initial Y-coordinate of the NPC.
         * @param type The type of NPC.
         * @param dir The initial direction the NPC is facing.
         * @param script The ID of the script associated with this NPC.
         * @param place The ID of the place where this NPC resides.
         */
        public NPC(int id, int x, int y, int type, int dir, int script, int place) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.type = type;
            this.direction = dir;
            this.scriptID = script;
            this.placeID = place;
            this.visible = true;
        }
    }

    /**
     * Resets all critical game state variables to their initial values,
     * effectively preparing the game for a new play session or after a game over.
     */
    private void resetGameState() {
        currentGameStatus = GAME_TITLE;
        currentMode = MODE_MOVE;
        currentPlace = PLACE_FIELD;
        currentCommand = COM_TALK;
        flip = 0;
        playerDirection = 1; // 0=Up, 1=Down, 2=Left, 3=Right
        showMinimap = true;

        fieldMapEndWidth = 40; // 40 + 8 = 48 (Tantegel X)
        fieldMapEndHeight = 48; // 48 + 8 = 56 (Tantegel Y)
        savedFieldMapX = 0;
        savedFieldMapY = 0;

        scriptLines = null;
        scriptID = 0;
        scriptLineIndex = 0;
        scriptAdvanceTick = 0;

        playerHP = 40;
        maxPlayerHP = 40;
        playerXP = 0;
        playerLevel = 1;
        xpToNextLevel = 10;
        playerGold = 0;
        playerAttack = 5;
        playerDefense = 2;
        commandMessage = null;
        commandMessageTime = 0;

        shopMessage = null;
        shopMessageTime = 0;

        saveMessage = null;
        saveMessageTime = 0;

        battleRewardMessage = null;
        battleRewardMessageTime = 0;

        currentFullMessage = "";
        currentVisibleMessage.setLength(0);
        messageCharIndex = 0;
        isWaitingForInput = false;
        typewriterTick = 0;
        messageCallback = null;

        score = 0;
        battlesWon = 0; // Track number of battles won

        // Re-initialize complex objects
        inventory = new Inventory();
        inventory.addItem(new Item("Potion", "Restores 20 HP", "heal_20", 10)); // Add initial potion
        shop = new Shop();
        battleManager = new BattleManager(this);
        initNPCs(); // Re-initialize NPCs with new random positions
    }

    /**
     * Initializes core game components that persist across game resets,
     * such as map data, audio system, and loading of static game assets like images.
     */
    private void initializeGame() {
        System.out.println("Initializing game components");
        // Initialize map data
        fieldMapData.initialize();

        // Initialize audio system
        audioManager = AudioManager.getInstance();

        // Initialize Items
        potion = new Item("Potion", "Restores 20 HP", "heal_20", 10);
        herb = new Item("Herb", "Restores 10 HP", "heal_10", 5);
        antidote = new Item("Antidote", "Cures poison", "cure_poison", 20);

        // Load player images for animation
        try {
            playerImage1 = new Image(getClass().getResourceAsStream("/images/me1.png"));
        } catch (Exception e) {
            playerImage1 = null;
        }
        try {
            playerImage2 = new Image(getClass().getResourceAsStream("/images/me2.png"));
        } catch (Exception e) {
            playerImage2 = null;
        }

        // Load tile images
        try {
            seaImage = new Image(getClass().getResourceAsStream("/images/sea.png"));
        } catch (Exception e) {
            seaImage = null;
        }
        try {
            sandImage = new Image(getClass().getResourceAsStream("/images/snd.png"));
        } catch (Exception e) {
            sandImage = null;
        }
        try {
            steppeImage = new Image(getClass().getResourceAsStream("/images/stp.png"));
        } catch (Exception e) {
            steppeImage = null;
        }
        try {
            forestImage = new Image(getClass().getResourceAsStream("/images/wd.png"));
        } catch (Exception e) {
            forestImage = null;
        }
        try {
            shopImage = new Image(getClass().getResourceAsStream("/images/shop.png"));
        } catch (Exception e) {
            shopImage = null;
        }
        try {
            plainsImage = new Image(getClass().getResourceAsStream("/images/plains.png"));
        } catch (Exception e) {
            plainsImage = null;
        }
        try {
            mountainImage = new Image(getClass().getResourceAsStream("/images/mountain.png"));
        } catch (Exception e) {
            mountainImage = null;
        }
        try {
            townImage = new Image(getClass().getResourceAsStream("/images/town.png"));
        } catch (Exception e) {
            townImage = null;
        }
        try {
            castleImage = new Image(getClass().getResourceAsStream("/images/castle.png"));
        } catch (Exception e) {
            castleImage = null;
        }
        try {
            houseImage = new Image(getClass().getResourceAsStream("/images/shop.png"));
        } catch (Exception e) {
            houseImage = null;
        }
        try {
            innImage = new Image(getClass().getResourceAsStream("/images/shop.png"));
        } catch (Exception e) {
            innImage = null;
        }
        try {
            bridgeImage = new Image(getClass().getResourceAsStream("/images/bridge.png"));
        } catch (Exception e) {
            bridgeImage = null;
        }
        try {
            swampImage = new Image(getClass().getResourceAsStream("/images/swamp.png"));
        } catch (Exception e) {
            swampImage = null;
        }
        try {
            wallImage = new Image(getClass().getResourceAsStream("/images/wall.png"));
        } catch (Exception e) {
            // wall.png might not exist yet, keeping fallback or try png
            wallImage = null;
        }
        try {
            floorImage = new Image(getClass().getResourceAsStream("/images/floor.png"));
        } catch (Exception e) {
            floorImage = null;
        }
        try {
            caveImage = new Image(getClass().getResourceAsStream("/images/cave.png"));
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

        // Load NPC images
        try {
            soldierImage = new Image(getClass().getResourceAsStream("/images/soldier1.png"));
        } catch (Exception e) {
            soldierImage = playerImage1;
        }
        try {
            merchantImage = new Image(getClass().getResourceAsStream("/images/merchant1.png"));
        } catch (Exception e) {
            merchantImage = playerImage1;
        }
        try {
            kingImage = new Image(getClass().getResourceAsStream("/images/king1.png"));
        } catch (Exception e) {
            kingImage = playerImage1;
        }
        // Initialize monsters array
        monsters = new Monster[] {
                new Monster(monster1Image, "Tung Tung Tung Sahur", 4, 2, 1, 5, 10, herb, 0.2),
                new Monster(monster2Image, "Tralalero Tralala", 6, 4, 2, 8, 15, herb, 0.3),
                new Monster(monster3Image, "Bombardiro Crocodilo", 9, 6, 3, 12, 20, potion, 0.2),
                new Monster(monster4Image, "Ballerina Cappuccina", 8, 5, 2, 15, 25, potion, 0.3),
                new Monster(monster5Image, "Cappuccino Assassino", 12, 7, 4, 25, 40, antidote, 0.1)
        };
        
        treasureChests = new TreasureChest[] {
            new TreasureChest(potion, fieldMapData.caveChestLocation[1], fieldMapData.caveChestLocation[0], PLACE_CAVE)
        };
    }

    /**
     * Initializes the Non-Player Characters (NPCs) with their starting positions, types,
     * directions, and associated script IDs. This method is typically called upon game initialization
     * or when an area is loaded.
     */
    private void initNPCs() {
        // King, Soldier, and Merchant will have random walkable positions in the building
        int[] kingPos = generateRandomWalkableCoord(PLACE_BLDNG);
        npcs[2] = new NPC(2, kingPos[0], kingPos[1], 2, random.nextInt(4), 0, PLACE_BLDNG); // King with Script ID 0

        int[] soldierPos = generateRandomWalkableCoord(PLACE_BLDNG);
        npcs[3] = new NPC(3, soldierPos[0], soldierPos[1], 0, random.nextInt(4), 2, PLACE_BLDNG); // Soldier with Script ID 2

        int[] merchantPos = generateRandomWalkableCoord(PLACE_BLDNG);
        npcs[4] = new NPC(4, merchantPos[0], merchantPos[1], 1, random.nextInt(4), 3, PLACE_BLDNG); // Merchant with Script ID 3

        int[] soldierPos2 = generateRandomWalkableCoord(PLACE_BLDNG);
        npcs[5] = new NPC(5, soldierPos2[0], soldierPos2[1], 0, random.nextInt(4), 4, PLACE_BLDNG);

        int[] merchantPos2 = generateRandomWalkableCoord(PLACE_BLDNG);
        npcs[6] = new NPC(6, merchantPos2[0], merchantPos2[1], 1, random.nextInt(4), 5, PLACE_BLDNG);

        int[] soldierPos3 = generateRandomWalkableCoord(PLACE_BLDNG);
        npcs[7] = new NPC(7, soldierPos3[0], soldierPos3[1], 0, random.nextInt(4), 6, PLACE_BLDNG);
    }
    
    /**
     * Generates random walkable coordinates for NPCs within a given place.
     * This ensures NPCs don't spawn on walls or in the sea.
     * @param placeID The ID of the place for which to generate coordinates (e.g., PLACE_BLDNG, PLACE_CAVE).
     * @return An int array containing the {x, y} coordinates of a random walkable tile.
     */
    private int[] generateRandomWalkableCoord(int placeID) {
        int x, y;
        int mapWidth = 0;
        int mapHeight = 0;
        int minX = 0, minY = 0;

        // Determine bounds based on placeID
        if (placeID == PLACE_BLDNG || placeID == PLACE_CAVE) {
            mapWidth = 16;
            mapHeight = 16;
            minX = 1; // Avoid walls on edges
            minY = 1; // Avoid walls on edges
        } else { // PLACE_FIELD
            mapWidth = fieldMapData.FIELD_MAP_WIDTH;
            mapHeight = fieldMapData.FIELD_MAP_WIDTH;
            minX = 0;
            minY = 0;
        }

        while (true) {
            x = random.nextInt(mapWidth - minX * 2) + minX;
            y = random.nextInt(mapHeight - minY * 2) + minY;

            int tile;
            if (placeID == PLACE_BLDNG) {
                tile = fieldMapData.mapDataReturnTown(y, x);
            } else if (placeID == PLACE_CAVE) {
                tile = fieldMapData.mapDataReturnCave(y, x);
            } else {
                tile = fieldMapData.mapDataReturnField(y, x);
            }

            // Check if the tile is walkable and not the player's initial spawn point (for building)
            if (isWalkable(y, x) && !(placeID == PLACE_BLDNG && x == 8 && y == 15)) { // Player spawns at (8,15) relative in building
                // Ensure no other NPC is already at this position
                boolean collision = false;
                for (NPC npc : npcs) {
                    if (npc != null && npc.placeID == placeID && npc.x == x && npc.y == y) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    return new int[]{x, y};
                }
            }
        }
    }

    /**
     * Main game loop using JavaFX AnimationTimer.
     */
    private class GameLoop extends AnimationTimer {
        private long lastUpdate = 0;

        @Override
        /**
         * This method is called repeatedly by the JavaFX AnimationTimer.
         * It updates the game state and renders the game frame if enough time has passed.
         * @param now The current time in nanoseconds.
         */
        public void handle(long now) {
            if (now - lastUpdate >= WAIT_MSEC * 1_000_000) { // Convert to nanoseconds
                updateGame();
                renderGame();
                lastUpdate = now;
            }
        }
    }

    /**
     * Updates the game state, handling logic such as message display,
     * game status transitions, and NPC movements. This method is called
     * periodically by the game loop.
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

        // NES-style Typewriter logic
        if (currentFullMessage != null && !currentFullMessage.isEmpty() && !isWaitingForInput) {
            if (typewriterTick > 0) {
                typewriterTick--;
            } else {
                // Reveal up to 10 characters per tick for near-instant display
                for (int i = 0; i < 10; i++) {
                    if (messageCharIndex < currentFullMessage.length() && !isWaitingForInput) {
                        char nextChar = currentFullMessage.charAt(messageCharIndex);
                        if (nextChar == 'E') {
                            isWaitingForInput = true;
                            messageCharIndex++; // Increment to point past E
                        } else if (nextChar == '@' || nextChar == 'H') {
                            isWaitingForInput = true;
                            messageCharIndex++;
                        } else {
                            currentVisibleMessage.append(nextChar);
                            messageCharIndex++;
                        }
                    }
                }
                if (messageCharIndex >= currentFullMessage.length()) {
                    isWaitingForInput = true;
                }
                typewriterTick = TYPEWRITER_SPEED;
            }
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
        
        // Update NPCs if not in battle or a menu
        if (currentMode == MODE_MOVE) {
            updateNPCs();
        }

        if (currentMode == MODE_INN) {
            playerHP = maxPlayerHP;
            displayMessage("You feel rested and your HP has been restored.E", () -> {
                currentMode = MODE_MOVE;
            });
            audioManager.playSound(AudioManager.SOUND_HEAL);
            currentMode = MODE_EVENT; // Use event mode to show the message
        }
    }

    /**
     * Updates the positions and directions of NPCs, allowing them to move randomly
     * within walkable areas and handling collisions with other NPCs or the player.
     */
    private void updateNPCs() {
        // Chance for an NPC to move, e.g., 10% per tick
        final double moveChance = 0.02;

        for (NPC npc : npcs) {
            // Only move NPCs that exist, are in the current area
            if (npc != null && npc.placeID == currentPlace && random.nextDouble() < moveChance) {
                
                int direction = random.nextInt(4); // 0=Up, 1=Down, 2=Left, 3=Right
                
                int targetX = npc.x;
                int targetY = npc.y;

                switch (direction) {
                    case 0: targetY--; break; // Up
                    case 1: targetY++; break; // Down
                    case 2: targetX--; break; // Left
                    case 3: targetX++; break; // Right
                }

                // --- Collision Detection ---
                // 1. Check for map boundaries and walkable tiles
                if (!isWalkable(targetY, targetX)) {
                    continue; // Skip move if target is a wall or out of bounds
                }

                // 2. Check for collision with other NPCs
                if (isNpcAt(targetX, targetY, currentPlace)) {
                    continue; // Skip move if another NPC is there
                }

                // 3. Check for collision with the player
                int playerX = fieldMapEndWidth + 8;
                int playerY = fieldMapEndHeight + 8;
                if (targetX == playerX && targetY == playerY) {
                    continue; // Skip move if player is there
                }

                // If all checks pass, move the NPC
                npc.x = targetX;
                npc.y = targetY;
                npc.direction = direction; // Update direction so sprite can face correctly
            }
        }
    }

    /**
     * Renders the current game frame, clearing the canvas and drawing elements
     * based on the current game status (title, game screen, game over, etc.).
     */
    private void renderGame() {
        // Clear canvas
        gc.setFill(Color.BLACK);
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
     * Renders the game's title screen, displaying the game logo, "Press Enter" prompt, and copyright information.
     */
    private void renderTitleScreen() {
        // Clear canvas with black
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);

        // --- Draw Window ---
        double windowWidth = DISP_WIDTH - 40; // Window with 20px padding
        double windowHeight = 120;
        double windowX = 20;
        double windowY = DISP_HEIGHT * 0.35 - 70; // Center around where text will be
        
        gc.setFill(Color.BLACK);
        gc.fillRect(windowX, windowY, windowWidth, windowHeight);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.strokeRect(windowX, windowY, windowWidth, windowHeight);
        gc.setLineWidth(2);
        gc.strokeRect(windowX + 5, windowY + 5, windowWidth - 10, windowHeight - 10);

        // Title text "DRAPON QUEST"
        String title = "DRAPON QUEST";
        
        // Dynamic font sizing for the title to fit inside the window
        double titleFontSize = getFittingFontSize(title, Font.font("Garamond", FontWeight.BOLD, 72), windowWidth * 0.9);
        gc.setFont(Font.font("Garamond", FontWeight.BOLD, titleFontSize));
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Shadow for title
        gc.setFill(Color.BLACK);
        gc.fillText(title, DISP_WIDTH / 2 + 4, DISP_HEIGHT * 0.35 + 4);
        
        // Main title text
        gc.setFill(Color.rgb(220, 200, 120)); // Gold-like color
        gc.fillText(title, DISP_WIDTH / 2, DISP_HEIGHT * 0.35);

        // "Press Enter" text with blink
        if ((System.currentTimeMillis() / 700) % 2 == 0) {
            gc.setFont(Font.font("Garamond", 32));
            gc.setFill(Color.WHITE);
            gc.fillText("PRESS ENTER", DISP_WIDTH / 2, DISP_HEIGHT * 0.7);
        }

        // Copyright info
        gc.setFont(Font.font("Arial", 16));
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("(c)2025 yahayuta", DISP_WIDTH / 2, DISP_HEIGHT * 0.95);
        gc.setTextAlign(TextAlignment.LEFT); // Reset to default
    }

    /**
     * Renders the main game screen, including the field map, player character, UI elements, and minimap.
     */
    private void renderGameScreen() {
        // Render field map
        renderFieldMap();

        // Render player
        renderPlayer();

        // Render UI elements
        renderUI();

        // Render minimap if enabled and not in battle or cave
        if (showMinimap && currentMode != MODE_BATTLE && currentPlace != PLACE_CAVE) {
            renderMinimap();
        }
    }

    /**
     * Renders the game map (field, town, or cave) by drawing individual tiles
     * and overlaying NPCs and the player character.
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
                    case fieldMapData.TILE_HOUSE:
                        tileImage = houseImage;
                        break;
                    case fieldMapData.TILE_INN:
                        tileImage = innImage;
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
                    case fieldMapData.TILE_CHEST:
                        // Will be colored below
                        break;
                }
                if (tileImage != null && !tileImage.isError()) {
                    gc.drawImage(tileImage, j * 32, i * 32, 32, 32);
                } else {
                    switch (tile) {
                        case 0:
                            gc.setFill(Color.DEEPSKYBLUE);
                            break;
                        case 1:
                            gc.setFill(Color.GOLD);
                            break;
                        case 2:
                            gc.setFill(Color.LIGHTGRAY);
                            break;
                        case 3:
                            gc.setFill(Color.FORESTGREEN);
                            break;
                        case fieldMapData.TILE_SHOP:
                            gc.setFill(Color.BROWN); // Shop
                            break;
                        case fieldMapData.TILE_PLAINS:
                            gc.setFill(Color.LIMEGREEN); // Plains
                            break;
                        case fieldMapData.TILE_MOUNTAIN:
                            gc.setFill(Color.DARKGRAY); // Mountain
                            break;
                        case fieldMapData.TILE_TOWN:
                            gc.setFill(Color.ORANGE); // Town
                            break;
                        case fieldMapData.TILE_CASTLE:
                            gc.setFill(Color.LIGHTGRAY); // Castle
                            break;
                        case fieldMapData.TILE_BRIDGE:
                            gc.setFill(Color.SADDLEBROWN); // Bridge
                            break;
                        case fieldMapData.TILE_SWAMP:
                            gc.setFill(Color.DARKGREEN); // Swamp
                            break;
                        case fieldMapData.TILE_WALL:
                            gc.setFill(Color.DARKSLATEGRAY); // Wall
                            break;
                        case fieldMapData.TILE_FLOOR:
                            gc.setFill(Color.rgb(200, 180, 150)); // Floor
                            break;
                        case fieldMapData.TILE_CAVE:
                            gc.setFill(Color.BLACK); // Cave
                            break;
                        case fieldMapData.TILE_CHEST:
                            gc.setFill(Color.GOLD); // Chest
                            break;
                        default:
                            gc.setFill(Color.BLACK);
                            break;
                    }
                    gc.fillRect(j * 32, i * 32, 32, 32);
                }
            }
        }

        // Draw NPCs
        for (

                int i = 0; i < npcs.length; i++) {
            if (npcs[i] != null && npcs[i].placeID == currentPlace) {
                // Correct logic: find offset from top-left tile
                int tileX = npcs[i].x - fieldMapEndWidth;
                int tileY = npcs[i].y - fieldMapEndHeight;

                if (tileX >= 0 && tileX < 16 && tileY >= 0 && tileY < 16) {
                    // Draw NPC sprite based on type
                    Image sprite = playerImage1;
                    switch (npcs[i].type) {
                        case 0:
                            sprite = (soldierImage != null) ? soldierImage : playerImage1;
                            break;
                        case 1:
                            sprite = (merchantImage != null) ? merchantImage : playerImage1;
                            break;
                        case 2:
                            sprite = (kingImage != null) ? kingImage : playerImage1;
                            break;
                        case 4:
                            sprite = (merchantImage != null) ? merchantImage : playerImage1;
                            break;
                    }
                    if (sprite != null) {
                        gc.drawImage(sprite, tileX * 32, tileY * 32, 32, 32);
                    }
                }
            }
        }

        // Draw player sprite (scaled up)
        Image currentPlayerImage = (flip == 0) ? playerImage1
                : playerImage2;
        if (currentPlayerImage != null && !currentPlayerImage.isError()) {
            gc.drawImage(currentPlayerImage, 8 * 32, 8 * 32, 32, 32);
        }

        // Player stats are now rendered in a separate UI window
    }

    /**
     * Placeholder method. The player is drawn as part of the `renderFieldMap()` method.
     */
    private void renderPlayer() {
        // Remove player drawing here to avoid double rendering
        // Player is already drawn in renderFieldMap()
    }

    /**
     * Renders all user interface elements, including status windows, command menus,
     * shop interfaces, inventory, battle overlays, and the NES-style dialogue box.
     */
    private void renderUI() {
        if (currentGameStatus == GAME_OPEN) {
            renderStatusWindow();
        }

        // Command menu in GAME_OPEN and MODE_COM
        if (currentGameStatus == GAME_OPEN && currentMode == MODE_COM) {
            // Draw NES-style menu box
            int boxX = 210; // Positioned to the right of the status window
            int boxY = 20;
            int boxWidth = 200;
            int boxHeight = 220;

            gc.setFill(Color.BLACK);
            gc.fillRect(boxX, boxY, boxWidth, boxHeight);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(4);
            gc.strokeRect(boxX, boxY, boxWidth, boxHeight);
            gc.setLineWidth(2);
            gc.strokeRect(boxX + 5, boxY + 5, boxWidth - 10, boxHeight - 10);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("MS Gothic", 28));
            String[] commands = {
                    LocalizationManager.getText("menu_talk"),
                    LocalizationManager.getText("menu_check"),
                    LocalizationManager.getText("menu_magic"),
                    LocalizationManager.getText("menu_item"),
                    LocalizationManager.getText("menu_status")
            };
            for (int i = 0; i < commands.length; i++) {
                int y = boxY + 40 + i * 36;
                if (currentCommand == i + 1) {
                    gc.fillText(">", boxX + 20, y);
                }
                gc.fillText(commands[i], boxX + 50, y);
            }
        }
        // Command action message (scaled up)
        if (currentGameStatus == GAME_OPEN && commandMessage != null) {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, DISP_HEIGHT - 64, DISP_WIDTH, 64);
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font("Arial", 28));
            gc.fillText(commandMessage, 32, DISP_HEIGHT - 24);
        }
        // Battle screen (scaled up)
        if (currentGameStatus == GAME_OPEN && currentMode == MODE_BATTLE) {
            gc.setFill(Color.rgb(32, 32, 64, 0.85));
            gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 40));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.fillText(LocalizationManager.getText("battle_title"), DISP_WIDTH / 2, 60);
            // Draw monster name centered above image
            if (battleManager.getCurrentMonster() != null && battleManager.getCurrentMonster().image != null
                    && !battleManager.getCurrentMonster().image.isError()) {
                gc.setFont(javafx.scene.text.Font.font("Arial", 28));
                gc.setFill(Color.YELLOW);
                gc.fillText(battleManager.getCurrentMonster().name, DISP_WIDTH / 2, 120);
                // Draw monster image centered
                gc.drawImage(battleManager.getCurrentMonster().image, (DISP_WIDTH - 128) / 2, 130, 128, 128);
            } else {
                gc.setFill(Color.DARKRED);
                gc.fillRect((DISP_WIDTH - 128) / 2, 130, 128, 128);
                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font("Arial", 18));
                gc.fillText(LocalizationManager.getText("no_monster_image"), DISP_WIDTH / 2, 190);
            }
            // Draw HP bars, moved up to avoid message box overlap
            gc.setFont(javafx.scene.text.Font.font("Arial", 22));
            gc.setFill(Color.LIME);
            String playerHpStr = LocalizationManager.getText("player_hp") + playerHP + "/" + maxPlayerHP + " | ATK: "
                    + playerAttack + " | DEF: " + playerDefense;
            gc.fillText(playerHpStr, DISP_WIDTH / 2, 270);

            gc.setFill(Color.RED);
            String monsterHpStr = battleManager.getCurrentMonster().name + LocalizationManager.getText("monster_hp")
                    + battleManager.getMonsterHP() + " | ATK: " + battleManager.getCurrentMonster().attack + " | DEF: "
                    + battleManager.getCurrentMonster().defense;
            gc.fillText(monsterHpStr, DISP_WIDTH / 2, 305);

            // Draw action options (A: Attack D: Defend R: Run)
            gc.setFill(Color.YELLOW);
            gc.setFont(javafx.scene.text.Font.font("Arial", 24));
            gc.fillText("A: Attack   D: Defend   R: Run", DISP_WIDTH / 2, DISP_HEIGHT - 30);
            gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT); // Reset to default
        }
        if (currentMode == MODE_SHOP) {
            // Background
            gc.setFill(Color.rgb(32, 64, 32, 0.95));
            gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
            gc.setTextAlign(TextAlignment.CENTER);

            // Title
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 40));
            gc.fillText("Shop", DISP_WIDTH / 2, 60);

            if (shopMode == 0) { // Main menu: Buy/Sell
                gc.setFont(Font.font("Arial", 28));
                String[] options = {"Buy", "Sell", "Exit"};
                for (int i = 0; i < options.length; i++) {
                    if (i == shopCursor) {
                        gc.setFill(Color.YELLOW);
                    } else {
                        gc.setFill(Color.WHITE);
                    }
                    gc.fillText(options[i], DISP_WIDTH / 2, 150 + i * 50);
                }
            } else if (shopMode == 1) { // Buying
                gc.setFont(Font.font("Arial", 28));
                gc.setFill(Color.YELLOW);
                gc.fillText("Items for sale:", DISP_WIDTH / 2, 120);
                
                java.util.List<Item> items = shop.getItemsForSale();
                for (int i = 0; i < items.size(); i++) {
                    if (i == shopCursor) {
                        gc.setFill(Color.YELLOW);
                    } else {
                        gc.setFill(Color.WHITE);
                    }
                    Item item = items.get(i);
                    String text = (i + 1) + ". " + item.getName() + " - " + item.getValue() + " gold";
                    gc.fillText(text, DISP_WIDTH / 2, 160 + i * 40);
                }
                 gc.setFont(Font.font("Arial", 24));
                 gc.setFill(Color.WHITE);
                 gc.fillText("Press ESC to go back.", DISP_WIDTH / 2, DISP_HEIGHT - 40);

            } else if (shopMode == 2) { // Selling
                gc.setFont(Font.font("Arial", 28));
                gc.setFill(Color.YELLOW);
                gc.fillText("Your items to sell:", DISP_WIDTH / 2, 120);

                java.util.List<Item> playerItems = getInventory().getItems();
                if (playerItems.isEmpty()) {
                    gc.setFill(Color.GRAY);
                    gc.fillText("You have no items to sell.", DISP_WIDTH / 2, 180);
                } else {
                    for (int i = 0; i < playerItems.size(); i++) {
                        if (i == shopCursor) {
                            gc.setFill(Color.YELLOW);
                        } else {
                            gc.setFill(Color.WHITE);
                        }
                        Item item = playerItems.get(i);
                        String text = (i + 1) + ". " + item.getName() + " (Sell: " + (item.getValue() / 2) + " gold)";
                        gc.fillText(text, DISP_WIDTH / 2, 160 + i * 40);
                    }
                }
                gc.setFont(Font.font("Arial", 24));
                gc.setFill(Color.WHITE);
                gc.fillText("Press ESC to go back.", DISP_WIDTH / 2, DISP_HEIGHT - 40);
            }

            // Shop message
            if (shopMessage != null) {
                gc.setFill(Color.YELLOW);
                gc.setFont(Font.font("Arial", 24));
                gc.fillText(shopMessage, DISP_WIDTH / 2, DISP_HEIGHT - 80);
                if (System.currentTimeMillis() - shopMessageTime > 2000) {
                    shopMessage = null;
                }
            }
        }
        if (currentMode == MODE_STATUS) {
            renderStatusScreen();
        }
        if (currentMode == MODE_INVENTORY) {
            renderInventoryScreen();
        }
        // Event screen (scaled up)
        if (currentGameStatus == GAME_OPEN && currentMode == MODE_EVENT) {
            gc.setFill(Color.rgb(64, 32, 32, 0.85));
            gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 40));
            gc.fillText("EVENT! (ESC to exit)", 64, DISP_HEIGHT / 2);
        }
        // Save/load message (scaled up)
        if (saveMessage != null && System.currentTimeMillis() - saveMessageTime < 2000) {
            gc.setFill(Color.YELLOW);
            gc.fillRect(0, 0, DISP_WIDTH, 48);
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font("Arial", 24));
            gc.fillText(saveMessage, 16, 32);
        }

        // Unified NES-style Dialogue box (moved to end to ensure it overlays
        // everything)
        if (currentGameStatus == GAME_OPEN && currentFullMessage != null && !currentFullMessage.isEmpty()) {
            // Draw NES-style dialogue box
            gc.setFill(Color.BLACK);
            gc.fillRect(10, DISP_HEIGHT - 160, DISP_WIDTH - 20, 150);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(4);
            gc.strokeRect(10, DISP_HEIGHT - 160, DISP_WIDTH - 20, 150);
            gc.setLineWidth(2);
            gc.strokeRect(15, DISP_HEIGHT - 155, DISP_WIDTH - 30, 140);

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("MS Gothic", 24));

            String wrappedText = wrapText(currentVisibleMessage.toString(), DISP_WIDTH - 60);
            String[] lines = wrappedText.split("\n");
            for (int i = 0; i < lines.length; i++) {
                gc.fillText(lines[i], 30, DISP_HEIGHT - 120 + i * 28);
            }

            // Blinking cursor if waiting
            if (isWaitingForInput && (System.currentTimeMillis() / 500) % 2 == 0) {
                gc.fillText("▼", DISP_WIDTH - 50, DISP_HEIGHT - 30);
            }
        }
    }

    /**
     * Wraps a given text string to fit within a specified maximum width.
     * This is used for formatting dialogue and other in-game messages.
     * @param text The input text string to wrap.
     * @param maxWidth The maximum width in pixels the text should occupy.
     * @return The wrapped text string with newline characters inserted as needed.
     */
    private String wrapText(String text, double maxWidth) {
        StringBuilder wrappedText = new StringBuilder();
        String[] lines = text.split("\n");

        for (String line : lines) {
            StringBuilder currentLine = new StringBuilder();
            String[] words = line.split(" ");

            for (String word : words) {
                // Create a test line with the new word
                String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
                
                // Measure the width of the test line
                javafx.scene.text.Text textNode = new javafx.scene.text.Text(testLine);
                textNode.setFont(gc.getFont());
                
                if (textNode.getLayoutBounds().getWidth() <= maxWidth) {
                    // If it fits, append the word
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                } else {
                    // If it doesn't fit, start a new line
                    wrappedText.append(currentLine).append("\n");
                    currentLine = new StringBuilder(word);
                }
            }
            wrappedText.append(currentLine).append("\n");
        }
        return wrappedText.toString().trim();
    }
    
    /**
     * Calculates a font size that makes the given text fit within a maximum width.
     * @param text The text string for which to calculate the fitting font size.
     * @param initialFont The initial font to use as a base for family, style, and a starting size for adjustment.
     * @param maxWidth The maximum width that the text should not exceed.
     * @return The adjusted font size that makes the text fit within the maximum width.
     */
    private double getFittingFontSize(String text, javafx.scene.text.Font initialFont, double maxWidth) {
        double fontSize = initialFont.getSize();
        // Create a Text node to measure the width accurately
        javafx.scene.text.Text tempText = new javafx.scene.text.Text(text);
        tempText.setFont(initialFont);

        FontWeight weight = FontWeight.NORMAL;
        if (initialFont.getStyle().toLowerCase().contains("bold")) {
            weight = FontWeight.BOLD;
        }

        // Reduce font size until text fits within maxWidth
        while (tempText.getLayoutBounds().getWidth() > maxWidth && fontSize > 1) {
            fontSize -= 1;
            tempText.setFont(javafx.scene.text.Font.font(initialFont.getFamily(), weight, fontSize));
        }
        return fontSize;
    }

    /**
     * Renders a small status window on the game screen, displaying key player statistics
     * such as level, current HP, and gold.
     */
    private void renderStatusWindow() {
        int boxX = 20;
        int boxY = 20;
        int boxWidth = 180;
        int boxHeight = 120;

        gc.setFill(Color.BLACK);
        gc.fillRect(boxX, boxY, boxWidth, boxHeight);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.strokeRect(boxX, boxY, boxWidth, boxHeight);
        gc.setLineWidth(2);
        gc.strokeRect(boxX + 5, boxY + 5, boxWidth - 10, boxHeight - 10);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("MS Gothic", 24));
        gc.setTextAlign(TextAlignment.LEFT);

        gc.fillText("LV", boxX + 15, boxY + 35);
        gc.fillText("HP", boxX + 15, boxY + 65);
        gc.fillText("G", boxX + 15, boxY + 95);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(String.valueOf(playerLevel), boxX + boxWidth - 20, boxY + 35);
        gc.fillText(String.valueOf(playerHP), boxX + boxWidth - 20, boxY + 65);
        gc.fillText(String.valueOf(playerGold), boxX + boxWidth - 20, boxY + 95);
        
        gc.setTextAlign(TextAlignment.LEFT); // Reset alignment
    }

    /**
     * Renders the dedicated status screen, providing a detailed overview of the player's attributes,
     * including level, HP, XP, gold, attack, and defense.
     */
    private void renderStatusScreen() {
        // Draw NES-style dialogue box background
        gc.setFill(Color.BLACK);
        gc.fillRect(10, 10, DISP_WIDTH - 20, DISP_HEIGHT - 20);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.strokeRect(10, 10, DISP_WIDTH - 20, DISP_HEIGHT - 20);
        gc.setLineWidth(2);
        gc.strokeRect(15, 15, DISP_WIDTH - 30, DISP_HEIGHT - 30);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("MS Gothic", 28));
        
        // Title
        gc.fillText("Status", 40, 60);

        // Stats
        gc.fillText("Level: " + playerLevel, 40, 120);
        gc.fillText("HP: " + playerHP + "/" + maxPlayerHP, 40, 160);
        gc.fillText("XP: " + playerXP + "/" + xpToNextLevel, 40, 200);
        gc.fillText("Gold: " + playerGold, 40, 240);
        gc.fillText("Attack: " + playerAttack, 40, 280);
        gc.fillText("Defense: " + playerDefense, 40, 320);

        gc.setFont(Font.font("MS Gothic", 24));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Press ESC to exit.", DISP_WIDTH / 2, DISP_HEIGHT - 40);
        gc.setTextAlign(TextAlignment.LEFT); // Reset alignment
    }

    /**
     * Renders the player's inventory screen, displaying all items currently held
     * and allowing selection.
     */
    private void renderInventoryScreen() {
        // Background
        gc.setFill(Color.rgb(32, 32, 32, 0.95));
        gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);

        // Title
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 40));
        gc.fillText("Inventory", DISP_WIDTH / 2, 60);

        java.util.List<Item> playerItems = getInventory().getItems();
        if (playerItems.isEmpty()) {
            gc.setFill(Color.GRAY);
            gc.setFont(Font.font("Arial", 28));
            gc.fillText("Your inventory is empty.", DISP_WIDTH / 2, 180);
        } else {
            gc.setFont(Font.font("Arial", 28));
            for (int i = 0; i < playerItems.size(); i++) {
                if (i == inventoryCursor) {
                    gc.setFill(Color.YELLOW);
                } else {
                    gc.setFill(Color.WHITE);
                }
                Item item = playerItems.get(i);
                String text = (i + 1) + ". " + item.getName();
                gc.fillText(text, DISP_WIDTH / 2, 160 + i * 40);
            }
        }
        gc.setFont(Font.font("Arial", 24));
        gc.setFill(Color.WHITE);
        gc.fillText("Press ESC to exit.", DISP_WIDTH / 2, DISP_HEIGHT - 40);
    }

    /**
     * Renders a placeholder or "waiting" screen, typically used during loading or pauses.
     * Currently, this method is not fully implemented and serves as a placeholder.
     */
    private void renderWaitScreen() {
        // TODO: Implement wait screen
    }

    /**
     * Renders the "continue" screen, typically shown after game over, offering options to continue or restart.
     * Currently, this method is not fully implemented and serves as a placeholder.
     */
    private void renderContinueScreen() {
        // TODO: Implement continue screen
    }

    /**
     * Renders the game over screen, displaying a game over message,
     * options to restart, and final player statistics like score and battles won.
     */
    private void renderGameOverScreen() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
        gc.setFill(Color.RED);
        gc.setFont(javafx.scene.text.Font.font("Arial", 48));
        gc.fillText(LocalizationManager.getText("game_over"), DISP_WIDTH * 0.25, DISP_HEIGHT * 0.4);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText(LocalizationManager.getText("press_enter_restart"), DISP_WIDTH * 0.25, DISP_HEIGHT * 0.6);
        gc.fillText(LocalizationManager.getText("total_score") + score, DISP_WIDTH * 0.25, DISP_HEIGHT * 0.7);
        gc.fillText("Battles Won: " + battlesWon, DISP_WIDTH * 0.25, DISP_HEIGHT * 0.8);
    }

    /**
     * Renders a small, overlaid minimap of the current area, showing player position and map features.
     * The minimap scales its display based on whether the player is in the field or an indoor area.
     */
    private void renderMinimap() {
        int x = DISP_WIDTH - 138;
        int y = 10;
        int size = 128;

        // Draw border and background
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(x - 2, y - 2, size + 4, size + 4);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x - 1, y - 1, size + 2, size + 2);

        if (currentPlace == PLACE_FIELD) {
            // Field map is 128x128, render at 1:1 scale
            for (int r = 0; r < 128; r++) {
                for (int c = 0; c < 128; c++) {
                    int tile = fieldMapData.mapDataReturnField(r, c);
                    gc.getPixelWriter().setColor(x + c, y + r, getMinimapTileColor(tile));
                }
            }
            // Draw player position
            int playerRow = fieldMapEndHeight + 8;
            int playerCol = fieldMapEndWidth + 8;
            gc.setFill(Color.WHITE);
            gc.fillRect(x + playerCol - 1, y + playerRow - 1, 3, 3);
        } else {
            // Town/Cave is 16x16, render at 8:1 scale (128x128)
            for (int r = 0; r < 16; r++) {
                for (int c = 0; c < 16; c++) {
                    int tile;
                    if (currentPlace == PLACE_BLDNG) {
                        tile = fieldMapData.mapDataReturnTown(r, c);
                    } else {
                        tile = fieldMapData.mapDataReturnCave(r, c);
                    }
                    gc.setFill(getMinimapTileColor(tile));
                    gc.fillRect(x + c * 8, y + r * 8, 8, 8);
                }
            }
            // Draw player position
            int playerRow = fieldMapEndHeight + 8;
            int playerCol = fieldMapEndWidth + 8;
            gc.setFill(Color.WHITE);
            gc.fillRect(x + playerCol * 8, y + playerRow * 8, 8, 8);
        }
    }

    /**
     * Returns a simplified color representation for a given tile type, used for rendering the minimap.
     * @param tile The integer ID of the tile.
     * @return A JavaFX Color object corresponding to the tile type.
     */
    private javafx.scene.paint.Color getMinimapTileColor(int tile) {
        switch (tile) {
            case fieldMapData.TILE_SEA:
                return Color.DEEPSKYBLUE;
            case fieldMapData.TILE_SAND:
                return Color.GOLD;
            case fieldMapData.TILE_STEPPE:
                return Color.LIGHTGRAY;
            case fieldMapData.TILE_FOREST:
                return Color.FORESTGREEN;
            case fieldMapData.TILE_SHOP:
                return Color.BROWN;
            case fieldMapData.TILE_PLAINS:
                return Color.LIMEGREEN;
            case fieldMapData.TILE_MOUNTAIN:
                return Color.DARKGRAY;
            case fieldMapData.TILE_TOWN:
                return Color.ORANGE;
            case fieldMapData.TILE_CASTLE:
                return Color.LIGHTGRAY;
            case fieldMapData.TILE_HOUSE:
                return Color.BROWN;
            case fieldMapData.TILE_INN:
                return Color.DEEPPINK;
            case fieldMapData.TILE_BRIDGE:
                return Color.SADDLEBROWN;
            case fieldMapData.TILE_SWAMP:
                return Color.PURPLE;
            case fieldMapData.TILE_WALL:
                return Color.DARKSLATEGRAY;
            case fieldMapData.TILE_FLOOR:
                return Color.rgb(200, 180, 150);
            case fieldMapData.TILE_CAVE:
                return Color.BLACK;
            case fieldMapData.TILE_CHEST:
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }

    /**
     * Handles the logic for the ENTER or SPACE key, used for advancing dialogue,
     * selecting menu options, or initiating game actions based on the current game state.
     */
    public void hitKeySelect() {
        System.out.println("hitKeySelect called - currentGameStatus: " + currentGameStatus + ", currentMode: " + currentMode);

        // Handle NES-style message box input first
        if (currentFullMessage != null && !currentFullMessage.isEmpty()) {
            if (!isWaitingForInput) {
                // Skip typewriter effect
                while (messageCharIndex < currentFullMessage.length()) {
                    char c = currentFullMessage.charAt(messageCharIndex);
                    if (c == 'E' || c == '@' || c == 'H') {
                        // Don't skip past markers, just let updateGame handle them next frame or now
                        break;
                    }
                    currentVisibleMessage.append(c);
                    messageCharIndex++;
                }
                isWaitingForInput = true;
                return;
            } else {
                // Continue to next page or close
                if (messageCharIndex < currentFullMessage.length()) {
                    char lastMarker = currentFullMessage.charAt(messageCharIndex - 1);
                    if (lastMarker == '@' || lastMarker == 'H') {
                        currentVisibleMessage.setLength(0);
                        isWaitingForInput = false;
                        return;
                    }
                }
                // Close message
                currentFullMessage = "";
                currentVisibleMessage.setLength(0);
                messageCharIndex = 0;
                isWaitingForInput = false;
                if (messageCallback != null) {
                    Runnable callback = messageCallback;
                    messageCallback = null;
                    callback.run();
                }
                return;
            }
        }

        if (currentGameStatus == GAME_OVER) {
            System.out.println("Restarting game...");
            resetGameState(); // Reset all game state
            System.out.println("Game restarted - new status: " + currentGameStatus);
            // Play title music
            audioManager.playMusic(AudioManager.MUSIC_TITLE);
        } else if (currentGameStatus == GAME_TITLE) {
            System.out.println("Starting game...");
            currentGameStatus = GAME_OPEN;
            // Play field music
            audioManager.playMusic(AudioManager.MUSIC_FIELD);
            // NES-style message test
            displayMessage(
                    "Welcome to the\nworld of Drapon Quest!@The King awaits you\nin the castle.H@Be careful out\nthere!E");
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
        } else if (currentMode == MODE_SHOP) {
            handleShopInput(KeyCode.ENTER);
        } else if (currentMode == MODE_INVENTORY) {
            handleInventoryInput(KeyCode.ENTER);
        }
    }

    /**
     * Handles the UP key input, used for moving the player character upwards or
     * navigating up in menus.
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
     * Handles the DOWN key input, used for moving the player character downwards or
     * navigating down in menus.
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
     * Handles the LEFT key input, used for moving the player character leftwards.
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
     * Handles the RIGHT key input, used for moving the player character rightwards.
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
     * Handles the ESCAPE key input, used for exiting menus, events, or battles (if the battle has concluded).
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
                // Return to pre-battle music
                audioManager.playMusic(getPreBattleMusic());
            } else {
                System.out.println("ESC: Battle ongoing, not exiting");
            }
        } else if (currentMode == MODE_SHOP) {
            handleShopInput(KeyCode.ESCAPE);
        } else if (currentMode == MODE_INVENTORY) {
            handleInventoryInput(KeyCode.ESCAPE);
        } else if (currentMode == MODE_STATUS) {
            System.out.println("ESC: Exiting status mode");
            currentMode = MODE_MOVE;
        }
        // TODO: Implement soft key 2 functionality for other modes if needed
    }

    /**
     * Triggers a NES-style message box to display a given message without a callback.
     * @param msg The message string to display. Special characters like '@', 'H', and 'E'
     *            can be embedded to control message flow (page breaks, waiting for input).
     */
    public void displayMessage(String msg) {
        displayMessage(msg, null);
    }

    /**
     * Triggers a NES-style message box to display a given message and executes a callback
     * function once the message display is completed and acknowledged by the player.
     * @param msg The message string to display. Special characters like '@', 'H', and 'E'
     *            can be embedded to control message flow (page breaks, waiting for input).
     * @param callback A Runnable to be executed after the message box is closed.
     */
    public void displayMessage(String msg, Runnable callback) {
        currentFullMessage = msg;
        currentVisibleMessage.setLength(0);
        messageCharIndex = 0;
        isWaitingForInput = false;
        typewriterTick = 0;
        messageCallback = callback;
    }

    /**
     * Checks if a specific map tile at the given row and column coordinates is walkable by the player.
     * Unwalkable tiles include sea (0) and walls (11).
     * @param row The row index of the tile to check.
     * @param col The column index of the tile to check.
     * @return True if the tile is walkable, false otherwise.
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
     * Checks if there is an NPC at the specified map coordinates within a given place.
     * @param targetX The X-coordinate to check for an NPC.
     * @param targetY The Y-coordinate to check for an NPC.
     * @param targetPlaceId The ID of the place (e.g., PLACE_BLDNG, PLACE_FIELD) where the check is performed.
     * @return True if an NPC is found at the target coordinates in the specified place, false otherwise.
     */
    private boolean isNpcAt(int targetX, int targetY, int targetPlaceId) {
        for (NPC npc : npcs) {
            if (npc != null && npc.placeID == targetPlaceId && npc.x == targetX && npc.y == targetY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the player on the map and triggers random encounters.
     * 
     * @param direction The direction in which the player is attempting to move (0=up, 1=down, 2=left, 3=right).
     */
    private void moveFieldMap(int direction) {
        System.out.println("moveFieldMap called - direction: " + direction);
        this.playerDirection = direction;
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

        // Check for NPC collision before attempting to move
        if (isNpcAt(playerCol, playerRow, currentPlace)) {
            System.out.println("Move blocked: NPC at target location.");
            audioManager.playSound(AudioManager.SOUND_MOVE); // Play a 'bump' sound or similar
            return; // Block movement
        }

        // EXIT LOGIC FOR TOWN/CAVE
        if (currentPlace != PLACE_FIELD) {
            // Player's new position after move
            int targetPlayerRow = newRow + 8;
            int targetPlayerCol = newCol + 8;

            // Check if moving onto the exit tile from inside
            if (direction == 1 && targetPlayerRow == 15 && (targetPlayerCol == 7 || targetPlayerCol == 8)) {
                currentPlace = PLACE_FIELD;
                fieldMapEndWidth = savedFieldMapX;
                fieldMapEndHeight = savedFieldMapY;
                audioManager.playMusic(AudioManager.MUSIC_FIELD);
                audioManager.playSound(AudioManager.SOUND_MOVE);
                System.out.println("Exited area by stepping onto exit tile at: " + (fieldMapEndHeight + 8) + "," + (fieldMapEndWidth + 8));
                return;
            }
        }

        if (playerRow >= 0 && playerRow < fieldMapData.getMapLength() &&
                playerCol >= 0 && playerCol < fieldMapData.FIELD_MAP_WIDTH &&
                isWalkable(playerRow, playerCol)) {
            fieldMapEndHeight = newRow;
            fieldMapEndWidth = newCol;

            // Check for entering a shop
            if (currentPlace == PLACE_BLDNG) {
                int playerX = fieldMapEndWidth + 8;
                int playerY = fieldMapEndHeight + 8;
                int tile = fieldMapData.mapDataReturnTown(playerY, playerX);
                if (tile == fieldMapData.TILE_SHOP) {
                    currentMode = MODE_SHOP;
                    shopMode = 0;
                    shopCursor = 0;
                    shopMessage = "Welcome!";
                    shopMessageTime = System.currentTimeMillis();
                    // Play a sound or music for entering shop
                    // audioManager.playMusic(AudioManager.MUSIC_TOWN); // or a specific shop theme
                } else if (tile == fieldMapData.TILE_INN) {
                    currentMode = MODE_INN;
                }
            }

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
                        audioManager.playMusic(AudioManager.MUSIC_CAVE);
                    } else {
                        currentPlace = PLACE_BLDNG;
                        if (currentTile == fieldMapData.TILE_CASTLE) {
                            audioManager.playMusic(AudioManager.MUSIC_CASTLE);
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
     * Saves the current game state to a persistent file.
     * This includes player stats, map position, and other critical game data.
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
     * Loads the game state from a previously saved file.
     * Restores player stats, map position, and other critical game data.
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

    /**
     * Increases the player's level, updating their stats (HP, Attack, Defense)
     * and calculating the new experience requirement for the next level.
     * Displays a message informing the player of their level up.
     */
    public void levelUp(Runnable callback) {
        playerLevel++;
        xpToNextLevel = (int) (xpToNextLevel * 1.5);
        maxPlayerHP += 10;
        playerHP = maxPlayerHP;
        playerAttack += 2;
        playerDefense += 1;

        String msg = "You have reached level " + playerLevel + "!@" +
                "Max HP increased by 10!\n" +
                "Attack +2, Defense +1E";
        displayMessage(msg, callback);
    }

    /**
     * Increases the player's level, updating their stats (HP, Attack, Defense)
     * and calculating the new experience requirement for the next level.
     * Displays a message informing the player of their level up.
     */
    public void levelUp() {
        levelUp(null);
    }

    /**
     * Retrieves the name of the music track that was playing before the current battle started.
     * @return The name of the pre-battle music track.
     */
    public String getPreBattleMusic() {
        return preBattleMusic;
    }

    /**
     * Sets the music track that should resume playing after the current battle concludes.
     * @param music The name of the music track to set as pre-battle music.
     */
    public void setPreBattleMusic(String music) {
        this.preBattleMusic = music;
    }

    /**
     * Checks for an NPC in the direction the player is currently facing.
     * If an NPC is found, their associated dialogue script is displayed.
     */
    private void checkTalk() {
        int playerRow = fieldMapEndHeight + 8;
        int playerCol = fieldMapEndWidth + 8;

        int targetRow = playerRow;
        int targetCol = playerCol;

        switch (playerDirection) {
            case 0: targetRow--; break; // Up
            case 1: targetRow++; break; // Down
            case 2: targetCol--; break; // Left
            case 3: targetCol++; break; // Right
        }

        boolean found = false;
        for (int j = 0; j < npcs.length; j++) {
            if (npcs[j] != null && npcs[j].placeID == currentPlace) {
                // Check if NPC is at target coordinates
                if (npcs[j].x == targetCol && npcs[j].y == targetRow) {
                    found = true;
                    // Use the NPC's assigned scriptID to get the correct dialogue
                    String msg = scriptData.getScript(npcs[j].scriptID) + "E";
                    displayMessage(msg);
                    // Optional: make NPC face player
                    // npcs[j].direction = (playerDirection + 1) % 4; // Simplified logic to face player
                    break; // Found an NPC, stop checking
                }
            }
        }

        if (!found) {
            displayMessage("There is no one there.E");
        }
    }

    /**
     * Checks for a treasure chest in the direction the player is currently facing.
     * If a chest is found and unopened, its contents are added to the player's inventory.
     */
    private void checkTreasure() {
        System.out.println("checkTreasure called. Player direction: " + playerDirection + ", currentPlace: " + currentPlace);
        int playerRow = fieldMapEndHeight + 8;
        int playerCol = fieldMapEndWidth + 8;

        int targetRow = playerRow;
        int targetCol = playerCol;

        switch (playerDirection) {
            case 0: targetRow--; break; // Up
            case 1: targetRow++; break; // Down
            case 2: targetCol--; break; // Left
            case 3: targetCol++; break; // Right
        }
        System.out.println("Player at (" + playerCol + ", " + playerRow + "), checking tile at (" + targetCol + ", " + targetRow + ")");

        if (currentPlace == PLACE_CAVE) {
            int tile = fieldMapData.mapDataReturnCave(targetRow, targetCol);
            System.out.println("Tile at target is: " + tile);
            if (tile == fieldMapData.TILE_CHEST) {
                System.out.println("Found a chest tile!");
                for (TreasureChest chest : treasureChests) {
                    if (chest.getPlaceID() == currentPlace && chest.getX() == targetCol && chest.getY() == targetRow) {
                        System.out.println("Matching chest object found.");
                        if (!chest.isOpen()) {
                            Item item = chest.open();
                            getInventory().addItem(item);
                            displayMessage("You found a " + item.getName() + "!E");
                            fieldMapData.setCaveTile(targetRow, targetCol, fieldMapData.TILE_FLOOR);
                        } else {
                            displayMessage("The chest is empty.E");
                        }
                        return;
                    }
                }
            }
        }
        
        displayMessage("There is nothing to check here.E");
    }

    /**
     * Executes the logic associated with the currently selected command from the in-game menu.
     * This method handles actions like talking to NPCs, checking for treasures, using magic or items,
     * or viewing the player's status.
     */
    private void handleCommandSelection() {
        System.out.println("handleCommandSelection called: currentCommand=" + currentCommand);
        int playerRow = fieldMapEndHeight + 8;
        int playerCol = fieldMapEndWidth + 8;
        int tile = fieldMapData.mapDataReturnField(playerRow, playerCol);

        if (currentCommand == COM_TALK) {
            checkTalk();
            currentMode = MODE_MOVE;
            return;
        }

        if (currentCommand == COM_CHK) {
            checkTreasure();
            currentMode = MODE_MOVE;
            return;
        }

        // Show message for selected command or switch to battle/event
        String[] commands = { "TALK", "CHECK", "MAGIC", "ITEM", "STATUS" };
        if (currentCommand == COM_MGK) {
            System.out.println("MAGIC selected: starting battle");
            battleManager.startBattle();
        } else if (currentCommand == COM_ITEM) {
            System.out.println("ITEM selected: entering inventory mode");
            currentMode = MODE_INVENTORY;
            inventoryCursor = 0;
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
     * Handles keyboard input specific to the shop interface, allowing navigation
     * through shop menus (buy, sell, exit) and item selection.
     * @param keyCode The KeyCode representing the key pressed by the user.
     */
    public void handleShopInput(KeyCode keyCode) {
        if (shopMode == 0) { // Main menu
            if (keyCode == KeyCode.UP || keyCode == KeyCode.W) {
                shopCursor--;
                if (shopCursor < 0) shopCursor = 2;
            } else if (keyCode == KeyCode.DOWN || keyCode == KeyCode.S) {
                shopCursor++;
                if (shopCursor > 2) shopCursor = 0;
            } else if (keyCode == KeyCode.ENTER || keyCode == KeyCode.SPACE) {
                if (shopCursor == 0) { // Buy
                    shopMode = 1;
                    shopCursor = 0;
                } else if (shopCursor == 1) { // Sell
                    shopMode = 2;
                    shopCursor = 0;
                } else { // Exit
                    currentMode = MODE_MOVE;
                    shopMode = 0;
                    shopCursor = 0;
                }
            } else if (keyCode == KeyCode.ESCAPE) {
                currentMode = MODE_MOVE;
                shopMode = 0;
                shopCursor = 0;
            }
        } else if (shopMode == 1) { // Buying
            java.util.List<Item> items = shop.getItemsForSale();
            if (keyCode == KeyCode.UP || keyCode == KeyCode.W) {
                shopCursor--;
                if (shopCursor < 0) shopCursor = items.size() - 1;
            } else if (keyCode == KeyCode.DOWN || keyCode == KeyCode.S) {
                shopCursor++;
                if (shopCursor >= items.size()) shopCursor = 0;
            } else if (keyCode == KeyCode.ENTER || keyCode == KeyCode.SPACE) {
                if (!items.isEmpty()) {
                    Item selectedItem = items.get(shopCursor);
                    shopMessage = shop.buyItem(selectedItem, this);
                    shopMessageTime = System.currentTimeMillis();
                }
            } else if (keyCode == KeyCode.ESCAPE) {
                shopMode = 0;
                shopCursor = 0;
            }
        } else if (shopMode == 2) { // Selling
            java.util.List<Item> playerItems = getInventory().getItems();
            if (keyCode == KeyCode.UP || keyCode == KeyCode.W) {
                shopCursor--;
                if (shopCursor < 0) shopCursor = playerItems.size() - 1;
            } else if (keyCode == KeyCode.DOWN || keyCode == KeyCode.S) {
                shopCursor++;
                if (shopCursor >= playerItems.size()) shopCursor = 0;
            } else if (keyCode == KeyCode.ENTER || keyCode == KeyCode.SPACE) {
                if (!playerItems.isEmpty()) {
                    Item selectedItem = playerItems.get(shopCursor);
                    shopMessage = shop.sellItem(selectedItem, this);
                    shopMessageTime = System.currentTimeMillis();
                    // After selling, cursor might be out of bounds if it was the last item
                    if (shopCursor >= getInventory().getItems().size() && getInventory().getItems().size() > 0) {
                        shopCursor = getInventory().getItems().size() - 1;
                    }
                }
            } else if (keyCode == KeyCode.ESCAPE) {
                shopMode = 0;
                shopCursor = 0;
            }
        }
    }

    /**
     * Handles keyboard input specific to the inventory screen, allowing navigation
     * through items and their selection/use.
     * @param keyCode The KeyCode representing the key pressed by the user.
     */
    public void handleInventoryInput(KeyCode keyCode) {
        java.util.List<Item> playerItems = getInventory().getItems();
        if (playerItems.isEmpty()) {
            if (keyCode == KeyCode.ESCAPE) {
                currentMode = MODE_MOVE;
            }
            return; // No items to navigate or use
        }

        if (keyCode == KeyCode.UP || keyCode == KeyCode.W) {
            inventoryCursor--;
            if (inventoryCursor < 0) {
                inventoryCursor = playerItems.size() - 1;
            }
        } else if (keyCode == KeyCode.DOWN || keyCode == KeyCode.S) {
            inventoryCursor++;
            if (inventoryCursor >= playerItems.size()) {
                inventoryCursor = 0;
            }
        } else if (keyCode == KeyCode.ENTER || keyCode == KeyCode.SPACE) {
            Item selectedItem = playerItems.get(inventoryCursor);
            // "Use" the item - for now, just display a message
            displayMessage("You used the " + selectedItem.getName() + ".E");
            // Here you would add the actual effect of the item
            // For example, healing the player:
            // if (selectedItem.getEffect().equals("heal_20")) {
            //     playerHP += 20;
            //     if (playerHP > maxPlayerHP) playerHP = maxPlayerHP;
            //     getInventory().removeItem(selectedItem);
            // }
            // After using, we can either close the inventory or wait.
            // For now, let's just show the message and the user can press ESC to close.
        } else if (keyCode == KeyCode.ESCAPE) {
            currentMode = MODE_MOVE;
        }
    }

    /**
     * Toggles the background music on or off. If music is enabled and the game is in an open/move state,
     * the field music will start playing.
     */
    public void toggleMusic() {
        audioManager.setMusicEnabled(!audioManager.isMusicEnabled());
        if (audioManager.isMusicEnabled() && currentGameStatus == GAME_OPEN && currentMode == MODE_MOVE) {
            audioManager.playMusic(AudioManager.MUSIC_FIELD);
        }
    }

    /**
     * Toggles the sound effects on or off throughout the game.
     */
    public void toggleSound() {
        audioManager.setSoundEnabled(!audioManager.isSoundEnabled());
    }

    /**
     * Toggles the game's display language between English and Japanese.
     * This also refreshes script data to reflect the new language.
     */
    public void toggleLanguage() {
        LocalizationManager.toggleLanguage();
        scriptData.refreshScript();
        // Reset script lines to force reload with new language
        scriptLines = null;
        System.out.println("Language changed to: " + LocalizationManager.getLanguageDisplayName());
    }

    /**
     * Decreases the volume for both background music and sound effects by a fixed increment.
     * The volume will not go below 0.0.
     */
    public void decreaseVolume() {
        double newMusicVol = Math.max(0.0, audioManager.getMusicVolume() - 0.1);
        double newSoundVol = Math.max(0.0, audioManager.getSoundVolume() - 0.1);
        audioManager.setMusicVolume(newMusicVol);
        audioManager.setSoundVolume(newSoundVol);
    }

    /**
     * Increases the volume for both background music and sound effects by a fixed increment.
     * The volume will not exceed 1.0.
     */
    public void increaseVolume() {
        double newMusicVol = Math.min(1.0, audioManager.getMusicVolume() + 0.1);
        double newSoundVol = Math.min(1.0, audioManager.getSoundVolume() + 0.1);
        audioManager.setMusicVolume(newMusicVol);
        audioManager.setSoundVolume(newSoundVol);
    }

    /**
     * Returns the player's inventory object.
     * @return The Inventory instance associated with the player.
     */
    public Inventory getInventory() {
        return inventory;
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