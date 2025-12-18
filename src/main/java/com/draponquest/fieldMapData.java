package com.draponquest;

/**
 * Provides field map data and utility methods for DraponQuest.
 * Handles the map layout and walkability.
 */
public class fieldMapData {
    /**
     * The width of the field map.
     */
    public static final int FIELD_MAP_WIDTH = 64;

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

    /**
     * The 2D array representing the town map layout.
     */
    private static int[][] mapDataTown = new int[16][16];

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

        // === MAIN CONTINENT (Crescent-shaped western landmass) ===
        // Northern section (rows 8-25)
        for (int r = 8; r < 26; r++) {
            for (int c = 6; c < 40; c++) {
                boolean isLand = true;

                // Northwestern inlet
                if (r < 12 && c < 10)
                    isLand = false;
                // Northern coast shaping
                if (r < 10 && c < 15)
                    isLand = false;
                if (r < 9 && c < 20)
                    isLand = false;
                // Eastern edge of northern section
                if (r < 15 && c > 36)
                    isLand = false;
                if (r < 12 && c > 33)
                    isLand = false;

                if (isLand) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // Central section (rows 26-42)
        for (int r = 26; r < 43; r++) {
            for (int c = 8; c < 38; c++) {
                boolean isLand = true;

                // Western coast
                if (c < 10 && r > 40)
                    isLand = false;
                // Eastern narrowing
                if (c > 35 && r > 35)
                    isLand = false;

                if (isLand) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // Southern section (rows 43-56) - Where Tantegel is located
        for (int r = 43; r < 57; r++) {
            for (int c = 12; c < 36; c++) {
                boolean isLand = true;

                // Southwestern inlet
                if (r > 54 && c < 18)
                    isLand = false;
                // Southern coast shaping
                if (r > 53 && c < 16)
                    isLand = false;
                // Southeastern narrowing
                if (r > 52 && c > 32)
                    isLand = false;

                if (isLand) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // === EASTERN CONTINENT (Kol's landmass) ===
        for (int r = 8; r < 28; r++) {
            for (int c = 48; c < 60; c++) {
                boolean isLand = true;

                // Northern shaping
                if (r < 11 && c > 56)
                    isLand = false;
                if (r < 10 && c > 54)
                    isLand = false;
                // Western edge
                if (c < 50 && r < 12)
                    isLand = false;
                if (c < 51 && r > 24)
                    isLand = false;
                // Southern edge
                if (r > 25 && c > 57)
                    isLand = false;
                if (r > 24 && c < 52)
                    isLand = false;

                if (isLand) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // === CHARLOCK ISLAND (Dragonlord's Castle) ===
        // Small island south of Tantegel
        for (int r = 52; r < 58; r++) {
            for (int c = 30; c < 36; c++) {
                if (r >= 53 && r <= 56 && c >= 31 && c <= 34) {
                    mapDataField[r][c] = TILE_PLAINS;
                }
            }
        }

        // === COASTAL SAND ===
        for (int r = 1; r < 63; r++) {
            for (int c = 1; c < 63; c++) {
                if (mapDataField[r][c] == TILE_SEA) {
                    boolean adjacentToLand = false;
                    if (r > 0 && mapDataField[r - 1][c] == TILE_PLAINS)
                        adjacentToLand = true;
                    if (r < 63 && mapDataField[r + 1][c] == TILE_PLAINS)
                        adjacentToLand = true;
                    if (c > 0 && mapDataField[r][c - 1] == TILE_PLAINS)
                        adjacentToLand = true;
                    if (c < 63 && mapDataField[r][c + 1] == TILE_PLAINS)
                        adjacentToLand = true;

                    if (adjacentToLand) {
                        mapDataField[r][c] = TILE_SAND;
                    }
                }
            }
        }

        // === MAJOR LANDMARKS ===

        // TANTEGEL CASTLE - South-central main continent (the starting point)
        mapDataField[48][24] = TILE_CASTLE;

        // BRECCONARY - Castle town south of Tantegel
        mapDataField[50][24] = TILE_TOWN;

        // GARINHAM - Northwest region
        mapDataField[14][12] = TILE_TOWN;

        // KOL - Far northeast on eastern continent (mountain village)
        mapDataField[12][54] = TILE_TOWN;

        // RIMULDAR - Southwest near poison swamp
        mapDataField[50][16] = TILE_TOWN;

        // CANTLIN - North-central, surrounded by mountains (walled city)
        mapDataField[16][30] = TILE_TOWN;

        // CHARLOCK CASTLE - Dragonlord's island
        mapDataField[54][32] = TILE_CASTLE;

        // === BRIDGE SYSTEM ===

        // Bridge 1: East from central continent toward eastern continent
        mapDataField[20][42] = TILE_BRIDGE;
        mapDataField[20][43] = TILE_BRIDGE;
        mapDataField[21][44] = TILE_BRIDGE;

        // Bridge 2: Final connection to Kol's continent
        mapDataField[18][46] = TILE_BRIDGE;
        mapDataField[18][47] = TILE_BRIDGE;

        // Bridge 3: Western passage
        mapDataField[32][12] = TILE_BRIDGE;
        mapDataField[33][12] = TILE_BRIDGE;

        // Rainbow Bridge to Charlock (initially just water, player uses Rainbow Drop)
        mapDataField[51][28] = TILE_BRIDGE;
        mapDataField[51][29] = TILE_BRIDGE;

        // === MOUNTAIN RANGES ===

        // Mountains around Cantlin (creating walled city effect)
        for (int r = 14; r < 19; r++) {
            for (int c = 27; c < 34; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    // Leave gap for Cantlin and entrance
                    if (!(r == 16 && c >= 29 && c <= 31)) {
                        if (r == 14 || r == 18 || c == 27 || c == 33) {
                            mapDataField[r][c] = TILE_MOUNTAIN;
                        }
                    }
                }
            }
        }

        // Northern mountain range
        for (int r = 10; r < 16; r++) {
            for (int c = 18; c < 26; c++) {
                if (mapDataField[r][c] == TILE_PLAINS && (r + c) % 3 == 0) {
                    mapDataField[r][c] = TILE_MOUNTAIN;
                }
            }
        }

        // Central-western mountains
        for (int r = 28; r < 36; r++) {
            for (int c = 10; c < 16; c++) {
                if (mapDataField[r][c] == TILE_PLAINS && (r + c) % 2 == 0) {
                    mapDataField[r][c] = TILE_MOUNTAIN;
                }
            }
        }

        // Eastern continent mountains (around Kol)
        for (int r = 10; r < 16; r++) {
            for (int c = 52; c < 58; c++) {
                if (mapDataField[r][c] == TILE_PLAINS && !(r == 12 && c == 54)) {
                    if ((r + c) % 3 != 0) {
                        mapDataField[r][c] = TILE_MOUNTAIN;
                    }
                }
            }
        }

        // Southern mountains
        for (int r = 46; r < 52; r++) {
            for (int c = 20; c < 24; c++) {
                if (mapDataField[r][c] == TILE_PLAINS && r < 48) {
                    mapDataField[r][c] = TILE_MOUNTAIN;
                }
            }
        }

        // === FORESTS ===

        // Northwestern forest (near Garinham)
        for (int r = 16; r < 22; r++) {
            for (int c = 10; c < 16; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    mapDataField[r][c] = TILE_FOREST;
                }
            }
        }

        // Northern forest
        for (int r = 12; r < 18; r++) {
            for (int c = 26; c < 32; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    mapDataField[r][c] = TILE_FOREST;
                }
            }
        }

        // Central forest
        for (int r = 34; r < 40; r++) {
            for (int c = 18; c < 26; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    mapDataField[r][c] = TILE_FOREST;
                }
            }
        }

        // Eastern continent forest
        for (int r = 18; r < 24; r++) {
            for (int c = 52; c < 58; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    mapDataField[r][c] = TILE_FOREST;
                }
            }
        }

        // Southern forest patches
        for (int r = 52; r < 56; r++) {
            for (int c = 18; c < 24; c++) {
                if (mapDataField[r][c] == TILE_PLAINS && (r + c) % 2 == 0) {
                    mapDataField[r][c] = TILE_FOREST;
                }
            }
        }

        // === POISON SWAMPS ===

        // Poison swamp near Rimuldar (southwest) - famous dangerous area
        for (int r = 48; r < 52; r++) {
            for (int c = 12; c < 18; c++) {
                if (mapDataField[r][c] == TILE_PLAINS && !(r == 50 && c == 16)) {
                    mapDataField[r][c] = TILE_SWAMP;
                }
            }
        }

        // Northern swamp
        for (int r = 20; r < 24; r++) {
            for (int c = 34; c < 38; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    mapDataField[r][c] = TILE_SWAMP;
                }
            }
        }

        // Western swamp patches
        for (int r = 38; r < 42; r++) {
            for (int c = 10; c < 14; c++) {
                if (mapDataField[r][c] == TILE_PLAINS && (r + c) % 2 == 1) {
                    mapDataField[r][c] = TILE_SWAMP;
                }
            }
        }

        // === DESERT/STEPPE ===

        // Southern desert region
        for (int r = 52; r < 56; r++) {
            for (int c = 26; c < 30; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    mapDataField[r][c] = TILE_STEPPE;
                }
            }
        }

        // Central steppe patches
        for (int r = 42; r < 46; r++) {
            for (int c = 28; c < 34; c++) {
                if (mapDataField[r][c] == TILE_PLAINS && (r + c) % 3 == 0) {
                    mapDataField[r][c] = TILE_STEPPE;
                }
            }
        }

        // === SHOPS ===

        // Shop near Garinham (northwest)
        mapDataField[18][14] = TILE_SHOP;

        // Shop in central area
        mapDataField[36][22] = TILE_SHOP;

        // Shop on eastern continent
        mapDataField[22][54] = TILE_SHOP;

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
    }
}