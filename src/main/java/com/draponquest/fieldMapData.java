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
    public static final int TILE_CHEST = 14;

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

    public static void setCaveTile(int row, int col, int tile) {
        if (row >= 0 && row < mapDataCave.length && col >= 0 && col < mapDataCave[0].length) {
            mapDataCave[row][col] = tile;
        }
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
     * Helper to fill an area with land.
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
     * Helper to fill an area with a feature (randomly distributed).
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
     * Helper to generate beaches around land.
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

    private static void initializeCave() {
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
                char ch = maze[r].charAt(c);
                mapDataCave[r][c] = (ch == 'W') ? TILE_WALL : TILE_FLOOR;
            }
        }
        // Place a chest
        mapDataCave[2][13] = TILE_CHEST;
    }
}