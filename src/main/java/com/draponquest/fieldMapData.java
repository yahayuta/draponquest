package com.draponquest;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 0 = sea, 1 = sand, 2 = steppe, 3 = forest, 4 = shop, 5 = plains, 6 =
     * mountain,
     * 7 = town, 8 = castle, 9 = bridge, 10 = swamp, 11 = wall, 12 = floor, 13 =
     * cave, 14 = chest.
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
    /** Constant for House tile. */
    public static final int TILE_HOUSE = 15;
    /** Constant for Inn tile. */
    public static final int TILE_INN = 16;

    /**
     * The 2D array representing the town map layout (16x16 tiles).
     */
    private static int[][] mapDataTown = new int[16][16];

    /**
     * The 2D array representing the cave map layout (16x16 tiles).
     */
    private static int[][] mapDataCave = new int[16][16];
    public static int[] caveChestLocation;

    // Maps for storing names of towns, castles, and caves
    private static Map<Point, String> townNames = new HashMap<>();
    private static Map<Point, String> castleNames = new HashMap<>();
    private static Map<Point, String> caveNames = new HashMap<>();

    /**
     * Returns the tile value from the overworld field map at the specified row and
     * column.
     * If the coordinates are out of bounds, {@link #TILE_SEA} is returned.
     * 
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
     * 
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
     * 
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
     * 
     * @return The number of rows in the main field map.
     */
    public static int getMapLength() {
        return mapDataField.length;
    }

    /**
     * Sets the tile value at the specified row and column in the cave map.
     * 
     * @param row  The row index of the tile to set.
     * @param col  The column index of the tile to set.
     * @param tile The new tile value to set.
     */
    public static void setCaveTile(int row, int col, int tile) {
        if (row >= 0 && row < mapDataCave.length && col >= 0 && col < mapDataCave[0].length) {
            mapDataCave[row][col] = tile;
        }
    }

    /**
     * Initializes the entire game world map data. This method populates the
     * overworld,
     * town, and cave maps with their respective tile layouts,
     * including landmasses, features, landmarks, and specific indoor designs.
     * The overworld map is designed to resemble the NES Dragon Quest 1 Alefgard
     * layout.
     */
    public static void initialize() {
        try {
            java.io.InputStream is = fieldMapData.class.getResourceAsStream("/alefgard_data.txt");
            if (is == null) {
                System.err.println("Map data file not found!");
                // Fallback to empty sea if file missing
                for (int r = 0; r < FIELD_MAP_WIDTH; r++) {
                    for (int c = 0; c < FIELD_MAP_WIDTH; c++) {
                        mapDataField[r][c] = TILE_SEA;
                    }
                }
                return;
            }
            java.util.Scanner scanner = new java.util.Scanner(is);
            for (int r = 0; r < FIELD_MAP_WIDTH; r++) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] tokens = line.trim().split("\\s+");
                    for (int c = 0; c < FIELD_MAP_WIDTH; c++) {
                        if (c < tokens.length) {
                            try {
                                mapDataField[r][c] = Integer.parseInt(tokens[c]);
                            } catch (NumberFormatException e) {
                                mapDataField[r][c] = TILE_SEA; // Default on error
                            }
                        }
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeTown();
        initializeCave();
    }

    /**
     * Helper method to fill a rectangular area of the {@code mapDataField} with
     * plains tiles.
     * Used during map initialization to define main landmass contours.
     * 
     * @param r1 The starting row of the area (inclusive).
     * @param c1 The starting column of the area (inclusive).
     * @param r2 The ending row of the area (exclusive).
     * @param c2 The ending column of the area (exclusive).
     */

    /**
     * Helper method to fill a rectangular area of the {@code mapDataField} with a
     * specific tile type.
     * The feature can be randomly distributed based on frequency, or solidly
     * filled.
     * 
     * @param r1        The starting row of the area (inclusive).
     * @param c1        The starting column of the area (inclusive).
     * @param r2        The ending row of the area (exclusive).
     * @param c2        The ending column of the area (exclusive).
     * @param type      The tile type to fill with (e.g., TILE_MOUNTAIN,
     *                  TILE_FOREST).
     * @param frequency Controls the randomness of the fill; 1 for solid fill,
     *                  higher for sparser.
     */

    /**
     * Helper method to generate {@link #TILE_SAND} tiles around landmasses next to
     * {@link #TILE_SEA}.
     * This creates natural-looking beaches after the main land contours are
     * defined.
     */

    /**
     * Initializes the layout of a generic town map.
     * This method sets up walls, floors, and specific features like a shop within
     * the town's 16x16 grid.
     */
    private static void initializeTown() {
        // Clear the map with floor tiles
        for (int r = 0; r < 16; r++) {
            for (int c = 0; c < 16; c++) {
                mapDataTown[r][c] = TILE_FLOOR;
            }
        }

        // Outer walls
        for (int i = 0; i < 16; i++) {
            mapDataTown[0][i] = TILE_WALL;
            mapDataTown[15][i] = TILE_WALL;
            mapDataTown[i][0] = TILE_WALL;
            mapDataTown[i][15] = TILE_WALL;
        }

        // Entrance
        mapDataTown[15][7] = TILE_FLOOR;
        mapDataTown[15][8] = TILE_FLOOR;

        // --- Buildings ---
        // Building 1 (Shop)
        for (int r = 2; r < 6; r++) {
            for (int c = 2; c < 6; c++) {
                if (r == 2 || r == 5 || c == 2 || c == 5) {
                    mapDataTown[r][c] = TILE_WALL;
                }
            }
        }
        mapDataTown[5][3] = TILE_FLOOR; // Door
        mapDataTown[3][3] = TILE_SHOP;

        // Building 2 (Inn)
        for (int r = 2; r < 6; r++) {
            for (int c = 9; c < 14; c++) {
                if (r == 2 || r == 5 || c == 9 || c == 13) {
                    mapDataTown[r][c] = TILE_WALL;
                }
            }
        }
        mapDataTown[5][11] = TILE_FLOOR; // Door
        mapDataTown[3][11] = TILE_INN;

        // Building 3 (L-shaped house)
        for (int r = 8; r < 14; r++) {
            mapDataTown[r][2] = TILE_WALL;
            mapDataTown[r][6] = TILE_WALL;
        }
        for (int c = 2; c < 7; c++) {
            mapDataTown[8][c] = TILE_WALL;
            mapDataTown[13][c] = TILE_WALL;
        }
        mapDataTown[13][4] = TILE_FLOOR; // Door
        mapDataTown[10][4] = TILE_HOUSE;

        // Building 4 (another house)
        for (int r = 8; r < 12; r++) {
            for (int c = 9; c < 13; c++) {
                if (r == 8 || r == 11 || c == 9 || c == 12) {
                    mapDataTown[r][c] = TILE_WALL;
                }
            }
        }
        mapDataTown[11][10] = TILE_FLOOR; // Door
        mapDataTown[9][10] = TILE_HOUSE;
    }

    /**
     * Initializes the layout of a generic cave map by generating a random maze.
     * The maze will have an entrance at the bottom and a treasure chest at a
     * distant dead-end.
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
        stack.push(new int[] { startR, startC });

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int r = current[0];
            int c = current[1];

            List<int[]> neighbors = new ArrayList<>();
            // Check neighbors (2 cells away)
            int[] dr = { -2, 2, 0, 0 };
            int[] dc = { 0, 0, -2, 2 };

            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                if (nr > 0 && nr < height - 1 && nc > 0 && nc < width - 1 && maze[nr][nc] == TILE_WALL) {
                    neighbors.add(new int[] { nr, nc });
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
        maze[height - 2][exitC - 1] = TILE_FLOOR;
        maze[height - 2][exitC] = TILE_FLOOR;

        return maze;
    }

    /**
     * Finds a suitable dead-end in the cave and places a treasure chest there.
     * The chosen location is the dead-end farthest from the entrance.
     * 
     * @return An int array {row, col} indicating the location of the placed
     *         treasure chest,
     *         or a default {2, 2} if no suitable dead-end is found.
     */
    private static void placeTreasureChestInCave() {
        List<int[]> deadEnds = new ArrayList<>();
        for (int r = 1; r < 15; r++) {
            for (int c = 1; c < 15; c++) {
                if (mapDataCave[r][c] == TILE_FLOOR) {
                    int floorNeighbors = 0;
                    if (mapDataCave[r - 1][c] == TILE_FLOOR)
                        floorNeighbors++;
                    if (mapDataCave[r + 1][c] == TILE_FLOOR)
                        floorNeighbors++;
                    if (mapDataCave[r][c - 1] == TILE_FLOOR)
                        floorNeighbors++;
                    if (mapDataCave[r][c + 1] == TILE_FLOOR)
                        floorNeighbors++;

                    if (floorNeighbors == 1) {
                        deadEnds.add(new int[] { r, c });
                    }
                }
            }
        }

        int[] bestLocation = { 2, 2 }; // Default location if no dead-end found
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
            // If no dead ends, place chest at a default (e.g., near start, but not
            // entrance)
            mapDataCave[bestLocation[0]][bestLocation[1]] = TILE_CHEST;
        }

        caveChestLocation = bestLocation;
    }

    /**
     * Retrieves the name of a location based on its map coordinates.
     * 
     * @param row The row index on the main field map.
     * @param col The column index on the main field map.
     * @return The name of the location, or null if no name is associated with these
     *         coordinates.
     */
    public static String getLocationName(int row, int col) {
        Point p = new Point(col, row); // awt Point is (x, y) -> (col, row)
        if (townNames.containsKey(p))
            return townNames.get(p);
        if (castleNames.containsKey(p))
            return castleNames.get(p);
        if (caveNames.containsKey(p))
            return caveNames.get(p);
        return null;
    }

    static {
        // Initialize Location Names
        // Coordinates must match those in generate_alefgard.py

        // Castles
        castleNames.put(new Point(56, 56), "Tantegel Castle");
        castleNames.put(new Point(65, 65), "Charlock Castle");

        // Towns
        townNames.put(new Point(59, 56), "Brecconary");
        townNames.put(new Point(20, 18), "Garinham");
        townNames.put(new Point(110, 25), "Kol");
        townNames.put(new Point(105, 100), "Rimuldar");
        townNames.put(new Point(30, 95), "Domdora");
        townNames.put(new Point(80, 110), "Cantlin");

        // Caves
        caveNames.put(new Point(70, 20), "Erdrick's Cave");
        caveNames.put(new Point(115, 45), "Staff of Rain Shrine");
        caveNames.put(new Point(115, 115), "Holy Shrine");
    }
}