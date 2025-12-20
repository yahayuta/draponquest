package com.draponquest;

/**
 * Provides field map data and utility methods for DraponQuest.
 * Handles the map layout and walkability.
 */
public class fieldMapData {
    /**
     * The width of the field map.
     */
    public static final int FIELD_MAP_WIDTH = 128;

    /**
     * The 2D array representing the map layout.
     * 0 = sea, 1 = sand, 2 = steppe, 3 = forest, 4 = shop, 5 = plains, 6 =
     * mountain,
     * 7 = town, 8 = castle, 9 = bridge, 10 = swamp
     */
    private static int[][] mapDataField = new int[FIELD_MAP_WIDTH][FIELD_MAP_WIDTH];

    public static final int TILE_SEA = 0;
    public static final int TILE_SAND = 1;
    public static final int TILE_STEPPE = 2;
    public static final int TILE_FOREST = 3;
    public static final int TILE_SHOP = 4;
    public static final int TILE_PLAINS = 5;
    public static final int TILE_MOUNTAIN = 6;
    public static final int TILE_TOWN = 7;
    public static final int TILE_CASTLE = 8;
    public static final int TILE_BRIDGE = 9;
    public static final int TILE_SWAMP = 10;
    public static final int TILE_WALL = 11;
    public static final int TILE_FLOOR = 12;
    public static final int TILE_CAVE = 13;

    /**
     * The 2D array representing the town map layout.
     */
    private static int[][] mapDataTown = new int[16][16];

    /**
     * The 2D array representing the cave map layout.
     */
    private static int[][] mapDataCave = new int[16][16];

    /**
     * Returns the tile value at the specified row and column.
     * 
     * @param row The row index.
     * @param col The column index.
     * @return The tile value (0=sea, 1=sand, 2=steppe, 3=forest, 4=shop, 5=plains,
     *         6=mountain,
     *         7=town, 8=castle, 9=bridge, 10=swamp).
     */
    public static int mapDataReturnField(int row, int col) {
        if (row < 0 || row >= mapDataField.length || col < 0 || col >= FIELD_MAP_WIDTH) {
            return TILE_SEA; // Return sea for out of bounds
        }
        return mapDataField[row][col];
    }

    /**
     * Returns the tile value from the town map at the specified row and column.
     * 
     * @param row The row index.
     * @param col The column index.
     * @return The tile value.
     */
    public static int mapDataReturnTown(int row, int col) {
        if (row < 0 || row >= mapDataTown.length || col < 0 || col >= mapDataTown[0].length) {
            return TILE_WALL; // Return wall for out of bounds
        }
        return mapDataTown[row][col];
    }

    /**
     * Returns the tile value from the cave map at the specified row and column.
     * 
     * @param row The row index.
     * @param col The column index.
     * @return The tile value.
     */
    public static int mapDataReturnCave(int row, int col) {
        if (row < 0 || row >= mapDataCave.length || col < 0 || col >= mapDataCave[0].length) {
            return TILE_WALL; // Return wall for out of bounds
        }
        return mapDataCave[row][col];
    }

    /**
     * Returns the number of rows in the map.
     * 
     * @return The length of the map (number of rows).
     */
    public static int getMapLength() {
        return mapDataField.length;
    }

    /**
     * Initializes the map data with a pixel-accurate NES Dragon Quest 1 Alefgard
     * layout.
     * Hand-crafted to match the original game's geography as closely as possible.
     */
    public static void initialize() {
        // Fill entire map with sea initially
        for (int r = 0; r < mapDataField.length; r++) {
            for (int c = 0; c < mapDataField[r].length; c++) {
                mapDataField[r][c] = TILE_SEA;
            }
        }

        // === MAIN CONTINENT (Western Landmass) ===
        // Much more detailed shaping for 128x128
        for (int r = 16; r < 110; r++) {
            for (int c = 12; c < 80; c++) {
                boolean isLand = true;

                // Northwest inlet
                if (r < 24 && c < 20)
                    isLand = false;
                // Northern coast shaping
                if (r < 20 && c < 30)
                    isLand = false;
                if (r < 18 && c < 40)
                    isLand = false;
                // Eastern edge of northern section (narrowing for the bay)
                if (r < 30 && c > 72)
                    isLand = false;
                if (r < 24 && c > 66)
                    isLand = false;

                // Central Bay (The sea between Tantegel and Rimuldar)
                if (r > 40 && r < 80 && c > 60)
                    isLand = false;

                // Southern narrowing
                if (r > 100 && c < 36)
                    isLand = false;
                if (r > 106 && c < 40)
                    isLand = false;
                if (r > 104 && c > 64)
                    isLand = false;

                if (isLand) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // === EASTERN CONTINENT (Kol's landmass) ===
        for (int r = 16; r < 56; r++) {
            for (int c = 96; c < 120; c++) {
                boolean isLand = true;
                // Northern shaping
                if (r < 22 && c > 112)
                    isLand = false;
                if (r < 20 && c > 108)
                    isLand = false;
                // Western edge
                if (c < 100 && r < 24)
                    isLand = false;
                if (c < 102 && r > 48)
                    isLand = false;
                // Southern edge
                if (r > 50 && c > 114)
                    isLand = false;
                if (r > 48 && c < 104)
                    isLand = false;

                if (isLand) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // === RIMULDAR ISLAND (The lower eastern continent) ===
        for (int r = 80; r < 120; r++) {
            for (int c = 80; c < 120; c++) {
                // Circular-ish island
                double centerR = 100, centerC = 100;
                double dist = Math.sqrt(Math.pow(r - centerR, 2) + Math.pow(c - centerC, 2));
                if (dist < 18) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // === CHARLOCK ISLAND (The center island) ===
        for (int r = 60; r < 72; r++) {
            for (int c = 60; c < 72; c++) {
                double centerR = 66, centerC = 66;
                double dist = Math.sqrt(Math.pow(r - centerR, 2) + Math.pow(c - centerC, 2));
                if (dist < 5) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // === COASTAL SAND (Automatic beach generation) ===
        for (int r = 1; r < FIELD_MAP_WIDTH - 1; r++) {
            for (int c = 1; c < FIELD_MAP_WIDTH - 1; c++) {
                if (mapDataField[r][c] == TILE_SEA) {
                    boolean adjacentToLand = false;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (mapDataField[r + dr][c + dc] == TILE_PLAINS)
                                adjacentToLand = true;
                        }
                    }
                    if (adjacentToLand) {
                        mapDataField[r][c] = TILE_SAND;
                    }
                }
            }
        }

        // === MAJOR LANDMARKS (NES Coordinates scaled x2) ===
        // Tantegel Castle: ~ [48, 24] -> [96, 48] in original?
        // Let's use relative positioning for 128x128
        mapDataField[50][48] = TILE_CASTLE; // Tantegel
        mapDataField[52][48] = TILE_TOWN; // Brecconary

        mapDataField[28][24] = TILE_TOWN; // Garinham
        mapDataField[24][108] = TILE_TOWN; // Kol
        mapDataField[100][100] = TILE_TOWN; // Rimuldar
        mapDataField[32][60] = TILE_TOWN; // Cantlin
        mapDataField[66][66] = TILE_CASTLE; // Charlock Castle

        // === BRIDGES ===
        // Bridge to Eastern Continent
        for (int c = 80; c < 96; c++)
            mapDataField[40][c] = TILE_BRIDGE;
        // Bridge to Rimuldar Island
        for (int r = 56; r < 80; r++)
            mapDataField[r][100] = TILE_BRIDGE;
        // Rainbow Bridge to Charlock
        mapDataField[66][56] = TILE_BRIDGE;
        mapDataField[66][57] = TILE_BRIDGE;
        mapDataField[66][58] = TILE_BRIDGE;
        mapDataField[66][59] = TILE_BRIDGE;

        // === MOUNTAINS, FORESTS, SWAMPS (Distribute logically) ===
        for (int r = 0; r < FIELD_MAP_WIDTH; r++) {
            for (int c = 0; c < FIELD_MAP_WIDTH; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    // Eastern Rimuldar area = Swamps
                    if (r > 90 && c > 90 && (r + c) % 3 == 0)
                        mapDataField[r][c] = TILE_SWAMP;
                    // Northern area = Forests
                    if (r < 40 && (r * c) % 11 == 0)
                        mapDataField[r][c] = TILE_FOREST;
                    // Mountain ranges
                    if (c > 30 && c < 40 && r > 20 && r < 80 && (r + c) % 5 != 0)
                        mapDataField[r][c] = TILE_MOUNTAIN;
                    if (r > 40 && r < 50 && c > 12 && c < 48)
                        mapDataField[r][c] = TILE_MOUNTAIN;
                }
            }
        }

        // Poison swamp near Rimuldar (classic area)
        for (int r = 95; r < 105; r++) {
            for (int c = 95; c < 105; c++) {
                if (mapDataField[r][c] == TILE_PLAINS)
                    mapDataField[r][c] = TILE_SWAMP;
            }
        }

        // === CAVES ===
        mapDataField[44 * 2][20 * 2] = TILE_CAVE; // Erdrick's Cave
        mapDataField[52 * 2][14 * 2] = TILE_CAVE; // Mountain Cave
        mapDataField[24 * 2][44 * 2] = TILE_CAVE; // Swamp Cave A
        mapDataField[24 * 2][50 * 2] = TILE_CAVE; // Swamp Cave B

        // === INITIALIZE TOWN MAP (Sample: Brecconary) ===
        // Fill town map data
        for (int r = 0; r < 16; r++) {
            for (int c = 0; c < 16; c++) {
                if (r == 0 || r == 15 || c == 0 || c == 15) {
                    // Entrance/Exit at the bottom
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
        // Add some "buildings" (walls) inside the town
        for (int c = 3; c < 7; c++)
            mapDataTown[3][c] = TILE_WALL;
        for (int c = 9; c < 13; c++)
            mapDataTown[3][c] = TILE_WALL;
        for (int r = 4; r < 7; r++) {
            mapDataTown[r][3] = TILE_WALL;
            mapDataTown[r][6] = TILE_WALL;
            mapDataTown[r][9] = TILE_WALL;
            mapDataTown[r][12] = TILE_WALL;
        }
        for (int c = 3; c < 7; c++)
            mapDataTown[7][c] = TILE_WALL;
        for (int c = 9; c < 13; c++)
            mapDataTown[7][c] = TILE_WALL;

        // === INITIALIZE CAVE MAP (Maze Layout) ===
        // 16x16 maze, entrance is at (15, 7) and (15, 8).
        // Each string must be exactly 16 characters.
        String[] maze = {
                "WWWWWWWWWWWWWWWW",
                "WFFFFFFFFFFFFFFW",
                "WFFFFFFFFFFFFFFW",
                "WFFWWWWWWWWWWFFW",
                "WFFWWWWWWWWWWFFW",
                "WFFFFFFFFFFFFFFW",
                "WFFFFFFFFFFFFFFW",
                "WFFWWWWWWWWWWFFW",
                "WFFWWWWWWWWWWFFW",
                "WFFFFFFFFFFFFFFW",
                "WFFFFFFFFFFFFFFW",
                "WFFWWWWWWWWWWFFW",
                "WFFWWWWWWWWWWFFW",
                "WFFFFFFFFFFFFFFW",
                "WFFFFFFFFFFFFFFW",
                "WWWWWWWFFWWWWWWW"
        };
        for (int r = 0; r < 16; r++) {
            for (int c = 0; c < 16; c++) {
                mapDataCave[r][c] = (maze[r].charAt(c) == 'W') ? TILE_WALL : TILE_FLOOR;
            }
        }
    }
}