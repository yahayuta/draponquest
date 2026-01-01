package com.draponquest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Provides field map data and utility methods for DraponQuest.
 * Handles the map layout and walkability.
 *
 * @author Yakkun (Original map data concept)
 * @author Modern Migration
 */
public class fieldMapData {
    /**
     * The width of the field map.
     */
    public static final int FIELD_MAP_WIDTH = 128;

    /**
     * The 2D array representing the main overworld map layout.
     * Each element stores an integer representing a tile type:
     * 0 = sea, 1 = sand, 2 = steppe, 3 = forest, 4 = shop, 5 = plains, 6 = mountain,
     * 7 = town, 8 = castle, 9 = bridge, 10 = swamp, 11 = wall, 12 = floor, 13 = cave, 14 = chest.
     */
    private static int[][] mapDataField = new int[FIELD_MAP_WIDTH][FIELD_MAP_WIDTH];

    /** Constant for Sea tile. */
    public static final int TILE_SEA = 0;
    /** Constant for Sand tile. */
    public static final int TILE_SAND = 1;
    /** Constant for Steppe tile. */
    public static final int TILE_STEPPE = 2;
    /** Constant for Forest tile. */
    public static final int TILE_FOREST = 3;
    /** Constant for Shop tile. */
    public static final int TILE_SHOP = 4;
    /** Constant for Plains tile. */
    public static final int TILE_PLAINS = 5;
    /** Constant for Mountain tile. */
    public static final int TILE_MOUNTAIN = 6;
    /** Constant for Town tile. */
    public static final int TILE_TOWN = 7;
    /** Constant for Castle tile. */
    public static final int TILE_CASTLE = 8;
    /** Constant for Bridge tile. */
    public static final int TILE_BRIDGE = 9;
    /** Constant for Swamp tile. */
    public static final int TILE_SWAMP = 10;
    /** Constant for Wall tile. */
    public static final int TILE_WALL = 11;
    /** Constant for Floor tile. */
    public static final int TILE_FLOOR = 12;
    /** Constant for Cave tile. */
    public static final int TILE_CAVE = 13;
    /** Constant for Chest tile. */
    public static final int TILE_CHEST = 14;

    /**
     * The 2D array representing the town map layout (16x16 tiles).
     */
    private static int[][] mapDataTown = new int[16][16];

    /**
     * The 2D array representing the cave map layout (16x16 tiles).
     */
    private static int[][] mapDataCave = new int[16][16];
    public static int[] caveChestLocation;

    /**
     * Returns the tile value from the overworld field map at the specified row and column.
     * If the coordinates are out of bounds, {@link #TILE_SEA} is returned.
     * @param row The row index of the tile.
     * @param col The column index of the tile.
     * @return The integer value representing the tile type.
     */
    public static int mapDataReturnField(int row, int col) {
        if (row < 0 || row >= mapDataField.length || col < 0 || col >= FIELD_MAP_WIDTH) {
            return TILE_SEA; // Return sea for out of bounds
        }
        return mapDataField[row][col];
    }

    /**
     * Returns the tile value from the town map at the specified row and column.
     * If the coordinates are out of bounds, {@link #TILE_WALL} is returned.
     * @param row The row index of the tile.
     * @param col The column index of the tile.
     * @return The integer value representing the tile type.
     */
    public static int mapDataReturnTown(int row, int col) {
        if (row < 0 || row >= mapDataTown.length || col < 0 || col >= mapDataTown[0].length) {
            return TILE_WALL; // Return wall for out of bounds
        }
        return mapDataTown[row][col];
    }

    /**
     * Returns the tile value from the cave map at the specified row and column.
     * If the coordinates are out of bounds, {@link #TILE_WALL} is returned.
     * @param row The row index of the tile.
     * @param col The column index of the tile.
     * @return The integer value representing the tile type.
     */
    public static int mapDataReturnCave(int row, int col) {
        if (row < 0 || row >= mapDataCave.length || col < 0 || col >= mapDataCave[0].length) {
            return TILE_WALL; // Return wall for out of bounds
        }
        return mapDataCave[row][col];
    }

    /**
     * Returns the number of rows in the main field map.
     * @return The number of rows in the main field map.
     */
    public static int getMapLength() {
        return mapDataField.length;
    }

    /**
     * Sets the tile value at the specified row and column in the cave map.
     * @param row The row index of the tile to set.
     * @param col The column index of the tile to set.
     * @param tile The new tile value to set.
     */
    public static void setCaveTile(int row, int col, int tile) {
        if (row >= 0 && row < mapDataCave.length && col >= 0 && col < mapDataCave[0].length) {
            mapDataCave[row][col] = tile;
        }
    }

    /**
     * Initializes the entire game world map data. This method populates the overworld,
     * town, and cave maps with their respective tile layouts,
     * including landmasses, features, landmarks, and specific indoor designs.
     * The overworld map is designed to resemble the NES Dragon Quest 1 Alefgard layout.
     */
    public static void initialize() {
        // Fill entire map with sea initially
        for (int r = 0; r < mapDataField.length; r++) {
            for (int c = 0; c < mapDataField[r].length; c++) {
                mapDataField[r][c] = TILE_SEA;
            }
        }

        // --- PHASE 1: MAIN LANDMASS CONTOURS (Matching the Image) ---

        // WESTERN CONTINENT (The "C" shape)
        fillLand(10, 15, 120, 45); // General vertical strip
        fillLand(15, 45, 110, 60); // Tantegel area extension
        fillLand(10, 10, 30, 80); // Garinham/North Coast bridge
        fillLand(80, 15, 120, 80); // Domdora/West Coast extension

        // NORTHERN BRIDGE (Connecting West and East)
        fillLand(10, 80, 20, 110); // Northern bridge point

        // EASTERN CONTINENT (Kol Area)
        fillLand(15, 100, 60, 125); // Kol landmass
        fillLand(40, 90, 55, 110); // Bridge point to Kol

        // CENTER ISLAND (Charlock)
        fillLand(55, 55, 75, 75); // Charlock Island

        // SOUTHEAST ISLAND (Rimuldar)
        fillLand(75, 90, 115, 120); // Rimuldar mass

        // SOUTH CENTRAL (Cantlin Area)
        fillLand(90, 60, 125, 95); // Cantlin Area

        // --- PHASE 2: DETAILED FEATURES (Forests, Mountains, Swamps) ---

        // Garinham area (NW) - Mountainous and Forested
        fillFeature(15, 15, 25, 25, TILE_MOUNTAIN, 2);
        fillFeature(25, 15, 35, 30, TILE_FOREST, 3);

        // North Central Mountain range
        fillFeature(10, 40, 20, 70, TILE_MOUNTAIN, 2);

        // Kol area (NE) - Dense Forest
        fillFeature(20, 105, 40, 120, TILE_FOREST, 4);
        fillFeature(40, 105, 55, 115, TILE_MOUNTAIN, 3);

        // Central Area (Tantegel) - Plains with some hills
        fillFeature(50, 40, 60, 55, TILE_STEPPE, 3);

        // Rimuldar Island (SE) - Moat and internal mountains
        fillFeature(90, 95, 105, 105, TILE_MOUNTAIN, 2);
        fillFeature(85, 85, 115, 115, TILE_SWAMP, 5); // Ring of poison

        // Domdora Area (SW) - Large Desert
        fillFeature(85, 20, 105, 45, TILE_SAND, 1); // Exact desert block
        fillFeature(80, 10, 115, 20, TILE_MOUNTAIN, 2); // Coastal mountains

        // Cantlin Area (S) - Mountain Fortress
        fillFeature(105, 80, 120, 95, TILE_MOUNTAIN, 4);

        // Charlock (Center) - High mountains and desert
        fillFeature(60, 60, 70, 70, TILE_MOUNTAIN, 2);
        fillFeature(62, 62, 68, 68, TILE_SAND, 1);

        // Coastal Sand generation
        generateBeaches();

        // --- PHASE 3: LANDMARKS (Coordinates from Reference Image) ---

        // 1. TANTEGEL CASTLE
        mapDataField[56][48] = TILE_CASTLE; // 1. Tantegel
        // 2. BRECCONARY
        mapDataField[56][53] = TILE_TOWN; // 2. Brecconary
        // 3. GARINHAM
        mapDataField[15][18] = TILE_TOWN; // 3. Garinham
        // 4. KOL
        mapDataField[28][112] = TILE_TOWN; // 4. Kol
        // 5. RIMULDAR
        mapDataField[98][102] = TILE_TOWN; // 5. Rimuldar
        // 6. DOMDORA
        mapDataField[92][30] = TILE_TOWN; // 6. Domdora
        // 7. CANTLIN
        mapDataField[112][88] = TILE_TOWN; // 7. Cantlin
        // 8. CHARLOCK CASTLE
        mapDataField[64][64] = TILE_CASTLE; // 8. Charlock

        // --- CAVES (A-F) ---
        mapDataField[20][72] = TILE_CAVE; // A: Erdrick's Grave
        mapDataField[45][58] = TILE_CAVE; // B: Mountain Cave
        mapDataField[12][120] = TILE_CAVE; // C: Northwest Shrine
        mapDataField[48][115] = TILE_CAVE; // D: Swamp Cave North
        mapDataField[110][115] = TILE_CAVE; // E: Holy Shrine
        mapDataField[115][95] = TILE_CAVE; // F: Swamp Cave South

        // --- BRIDGES ---
        fillFeature(38, 85, 42, 98, TILE_BRIDGE, 1); // Bridge to Kol continent
        fillFeature(70, 105, 85, 105, TILE_BRIDGE, 1); // Bridge to Rimuldar island
        fillFeature(64, 55, 64, 59, TILE_BRIDGE, 1); // Rainbow Bridge to Charlock

        // --- PHASE 4: SOUTH TIP AND SWAMP F ---
        fillLand(110, 100, 125, 115); // Small island E
        fillFeature(110, 50, 125, 80, TILE_SWAMP, 1); // Exact huge swamp block F

        // --- PHASE 4: TOWN & CAVE INITIALIZATION ---
        initializeTown();
        initializeCave();
    }

    /**
     * Helper method to fill a rectangular area of the {@code mapDataField} with plains tiles.
     * Used during map initialization to define main landmass contours.
     * @param r1 The starting row of the area (inclusive).
     * @param c1 The starting column of the area (inclusive).
     * @param r2 The ending row of the area (exclusive).
     * @param c2 The ending column of the area (exclusive).
     */
    private static void fillLand(int r1, int c1, int r2, int c2) {
        for (int r = r1; r < r2; r++) {
            for (int c = c1; c < c2; c++) {
                if (r >= 0 && r < FIELD_MAP_WIDTH && c >= 0 && c < FIELD_MAP_WIDTH) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }
    }

    /**
     * Helper method to fill a rectangular area of the {@code mapDataField} with a specific tile type.
     * The feature can be randomly distributed based on frequency, or solidly filled.
     * @param r1 The starting row of the area (inclusive).
     * @param c1 The starting column of the area (inclusive).
     * @param r2 The ending row of the area (exclusive).
     * @param c2 The ending column of the area (exclusive).
     * @param type The tile type to fill with (e.g., TILE_MOUNTAIN, TILE_FOREST).
     * @param frequency Controls the randomness of the fill; 1 for solid fill, higher for sparser.
     */
    private static void fillFeature(int r1, int c1, int r2, int c2, int type, int frequency) {
        for (int r = r1; r < r2; r++) {
            for (int c = c1; c < c2; c++) {
                if (r >= 0 && r < FIELD_MAP_WIDTH && c >= 0 && c < FIELD_MAP_WIDTH) {
                    if (mapDataField[r][c] != TILE_SEA && (r + c * 7) % frequency == 0) {
                        mapDataField[r][c] = type;
                    }
                    if (frequency == 1)
                        mapDataField[r][c] = type; // Solid fill
                }
            }
        }
    }

    /**
     * Helper method to generate {@link #TILE_SAND} tiles around landmasses next to {@link #TILE_SEA}.
     * This creates natural-looking beaches after the main land contours are defined.
     */
    private static void generateBeaches() {
        for (int r = 1; r < FIELD_MAP_WIDTH - 1; r++) {
            for (int c = 1; c < FIELD_MAP_WIDTH - 1; c++) {
                if (mapDataField[r][c] == TILE_SEA) {
                    boolean nearLand = false;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int nr = r + dr;
                            int nc = c + dc;
                            int tile = mapDataField[nr][nc];
                            if (tile == TILE_PLAINS || tile == TILE_STEPPE || tile == TILE_MOUNTAIN
                                    || tile == TILE_FOREST) {
                                nearLand = true;
                                break;
                            }
                        }
                        if (nearLand)
                            break;
                    }
                    if (nearLand)
                        mapDataField[r][c] = TILE_SAND;
                }
            }
        }
    }

    /**
     * Initializes the layout of a generic town map.
     * This method sets up walls, floors, and specific features like a shop within the town's 16x16 grid.
     */
    private static void initializeTown() {
        for (int r = 0; r < 16; r++) {
            for (int c = 0; c < 16; c++) {
                if (r == 0 || r == 15 || c == 0 || c == 15) {
                    if (r == 15 && (c == 7 || c == 8)) {
                        mapDataTown[r][c] = TILE_FLOOR;
                    } else {
                        mapDataTown[r][c] = TILE_WALL;
                    }
                } else {
                    mapDataTown[r][c] = TILE_FLOOR;
                }
            }
        }
        // Basic town buildings
        for (int c = 3; c < 13; c++) {
            if (c < 7 || c > 9)
                mapDataTown[3][c] = TILE_WALL;
            if (c < 7 || c > 9)
                mapDataTown[7][c] = TILE_WALL;
        }
        for (int r = 4; r < 7; r++) {
            mapDataTown[r][3] = TILE_WALL;
            mapDataTown[r][6] = TILE_WALL;
            mapDataTown[r][9] = TILE_WALL;
            mapDataTown[r][12] = TILE_WALL;
        }
        // Add a shop inside a building
        mapDataTown[6][10] = TILE_SHOP;
        // Add a door to the shop building
        mapDataTown[7][10] = TILE_FLOOR;
    }

    /**
     * Initializes the layout of a generic cave map by generating a random maze.
     * The maze will have an entrance at the bottom and a treasure chest at a distant dead-end.
     */
    private static void initializeCave() {
        mapDataCave = generateRandomMaze(16, 16);
        // Ensure entrance is clear
        mapDataCave[15][7] = TILE_FLOOR;
        mapDataCave[15][8] = TILE_FLOOR;

        // Place a treasure chest at a suitable location (a distant dead-end)
        placeTreasureChestInCave();
    }

    /**
     * Generates a random maze using a recursive backtracking algorithm.
     *
     * @param width  The width of the maze.
     * @param height The height of the maze.
     * @return A 2D integer array representing the maze with walls and floors.
     */
    private static int[][] generateRandomMaze(int width, int height) {
        int[][] maze = new int[height][width];
        // Initialize maze with walls
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                maze[r][c] = TILE_WALL;
            }
        }

        Random rand = new Random();
        Stack<int[]> stack = new Stack<>();
        int startR = 1; // Start inside the border
        int startC = 1;
        maze[startR][startC] = TILE_FLOOR;
        stack.push(new int[]{startR, startC});

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int r = current[0];
            int c = current[1];

            List<int[]> neighbors = new ArrayList<>();
            // Check neighbors (2 cells away)
            int[] dr = {-2, 2, 0, 0};
            int[] dc = {0, 0, -2, 2};

            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                if (nr > 0 && nr < height - 1 && nc > 0 && nc < width - 1 && maze[nr][nc] == TILE_WALL) {
                    neighbors.add(new int[]{nr, nc});
                }
            }

            if (!neighbors.isEmpty()) {
                Collections.shuffle(neighbors, rand);
                int[] next = neighbors.get(0);
                int nr = next[0];
                int nc = next[1];

                // Carve path to neighbor
                maze[nr][nc] = TILE_FLOOR;
                maze[r + (nr - r) / 2][c + (nc - c) / 2] = TILE_FLOOR;
                stack.push(next);
            } else {
                stack.pop();
            }
        }

        // Carve a path down to the exit
        // This ensures the generated maze is always connected to the bottom entrance
        int exitC = width / 2;
        if (maze[height - 3][exitC] == TILE_WALL) {
             maze[height - 3][exitC] = TILE_FLOOR;
        }
        maze[height - 2][exitC -1] = TILE_FLOOR;
        maze[height - 2][exitC] = TILE_FLOOR;

        return maze;
    }

    /**
     * Finds a suitable dead-end in the cave and places a treasure chest there.
     * The chosen location is the dead-end farthest from the entrance.
     * @return An int array {row, col} indicating the location of the placed treasure chest,
     *         or a default {2, 2} if no suitable dead-end is found.
     */
    private static void placeTreasureChestInCave() {
        List<int[]> deadEnds = new ArrayList<>();
        for (int r = 1; r < 15; r++) {
            for (int c = 1; c < 15; c++) {
                if (mapDataCave[r][c] == TILE_FLOOR) {
                    int floorNeighbors = 0;
                    if (mapDataCave[r - 1][c] == TILE_FLOOR) floorNeighbors++;
                    if (mapDataCave[r + 1][c] == TILE_FLOOR) floorNeighbors++;
                    if (mapDataCave[r][c - 1] == TILE_FLOOR) floorNeighbors++;
                    if (mapDataCave[r][c + 1] == TILE_FLOOR) floorNeighbors++;

                    if (floorNeighbors == 1) {
                        deadEnds.add(new int[]{r, c});
                    }
                }
            }
        }

        int[] bestLocation = {2, 2}; // Default location if no dead-end found
        double maxDistance = -1;
        int entranceR = 15; // Assuming entrance is at the bottom center
        int entranceC = 7;

        if (!deadEnds.isEmpty()) {
            for (int[] loc : deadEnds) {
                // Calculate Euclidean distance from entrance
                double dist = Math.sqrt(Math.pow(loc[0] - entranceR, 2) + Math.pow(loc[1] - entranceC, 2));
                if (dist > maxDistance) {
                    maxDistance = dist;
                    bestLocation = loc;
                }
            }
            mapDataCave[bestLocation[0]][bestLocation[1]] = TILE_CHEST;
        } else {
            // If no dead ends, place chest at a default (e.g., near start, but not entrance)
            mapDataCave[bestLocation[0]][bestLocation[1]] = TILE_CHEST;
        }

        caveChestLocation = bestLocation;
    }
}