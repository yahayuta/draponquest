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
     * 0 = sea, 1 = sand, 2 = steppe, 3 = forest, 4 = shop, 5 = plains, 6 = mountain,
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
    
    /**
     * Returns the tile value at the specified row and column.
     * @param row The row index.
     * @param col The column index.
     * @return The tile value (0=sea, 1=sand, 2=steppe, 3=forest, 4=shop, 5=plains, 6=mountain,
     * 7=town, 8=castle, 9=bridge, 10=swamp).
     */
    public static int mapDataReturnField(int row, int col) {
        if (row < 0 || row >= mapDataField.length || col < 0 || col >= FIELD_MAP_WIDTH) {
            return TILE_SEA; // Return sea for out of bounds
        }
        return mapDataField[row][col];
    }

    /**
     * Returns the number of rows in the map.
     * @return The length of the map (number of rows).
     */
    public static int getMapLength() {
        return mapDataField.length;
    }

    /**
     * Initializes the map data with a more varied terrain, resembling Alefgard.
     */
    public static void initialize() {
        // Fill entire map with sea initially
        for(int r = 0; r < mapDataField.length; r++) {
            for(int c = 0; c < mapDataField[r].length; c++) {
                mapDataField[r][c] = TILE_SEA;
            }
        }

        // Create a basic continent shape
        for(int r = 8; r < mapDataField.length - 8; r++) {
            for(int c = 8; c < FIELD_MAP_WIDTH - 8; c++) {
                mapDataField[r][c] = TILE_PLAINS;
            }
        }

        // Add some sand along the coast
        for(int r = 7; r < mapDataField.length - 7; r++) {
            for(int c = 7; c < FIELD_MAP_WIDTH - 7; c++) {
                if (mapDataField[r][c] == TILE_SEA && (r >= 8 && r < mapDataField.length - 8 && c >= 8 && c < FIELD_MAP_WIDTH - 8)) {
                    // Check if adjacent to plains
                    boolean adjacentToPlains = 
                        mapDataField[r-1][c] == TILE_PLAINS || mapDataField[r+1][c] == TILE_PLAINS ||
                        mapDataField[r][c-1] == TILE_PLAINS || mapDataField[r][c+1] == TILE_PLAINS;
                    if (adjacentToPlains) {
                        mapDataField[r][c] = TILE_SAND;
                    }
                }
            }
        }
        
        // Add forests
        for(int r = 10; r < 20; r++) {
            for(int c = 10; c < 20; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    mapDataField[r][c] = TILE_FOREST;
                }
            }
        }
        for(int r = 25; r < 35; r++) {
            for(int c = 30; c < 40; c++) {
                if (mapDataField[r][c] == TILE_PLAINS) {
                    mapDataField[r][c] = TILE_FOREST;
                }
            }
        }

        // Add mountains
        for(int r = 15; r < 25; r++) {
            for(int c = 25; c < 30; c++) {
                if (mapDataField[r][c] == TILE_PLAINS || mapDataField[r][c] == TILE_FOREST) {
                    mapDataField[r][c] = TILE_MOUNTAIN;
                }
            }
        }
        for(int r = 30; r < 40; r++) {
            for(int c = 10; c < 15; c++) {
                if (mapDataField[r][c] == TILE_PLAINS || mapDataField[r][c] == TILE_FOREST) {
                    mapDataField[r][c] = TILE_MOUNTAIN;
                }
            }
        }

        // Add a town
        mapDataField[12][40] = TILE_TOWN;
        
        // Add a castle
        mapDataField[20][15] = TILE_CASTLE;

        // Add a shop (keeping the previous shop location as an example)
        mapDataField[24][24] = TILE_SHOP;

        // Add some swamp
        for(int r = 40; r < 45; r++) {
            for(int c = 20; c < 30; c++) {
                if (mapDataField[r][c] == TILE_PLAINS || mapDataField[r][c] == TILE_FOREST) {
                    mapDataField[r][c] = TILE_SWAMP;
                }
            }
        }

        // Add a bridge (e.g., across a small river/sea inlet)
        mapDataField[10][35] = TILE_BRIDGE;
        mapDataField[10][36] = TILE_BRIDGE;
    }
}