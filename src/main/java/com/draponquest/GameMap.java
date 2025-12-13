package com.draponquest;

/**
 * Represents a single game map, holding its layout and properties.
 */
public class GameMap {
    public final int mapId;
    public final String mapName;
    public final int width;
    public final int height;
    private final int[][] mapData;

    /**
     * Constructor for GameMap.
     * @param mapId The unique ID of this map.
     * @param mapName The name of this map.
     * @param mapData The 2D array representing the map layout.
     */
    public GameMap(int mapId, String mapName, int[][] mapData) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.mapData = mapData;
        this.height = mapData.length;
        this.width = mapData[0].length; // Assumes rectangular map
    }

    /**
     * Returns the tile value at the specified row and column.
     * @param row The row index.
     * @param col The column index.
     * @return The tile value (0=sea, 1=sand, 2=steppe, 3=forest).
     */
    public int getTile(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            // Or throw an exception, depending on desired behavior for out-of-bounds
            return 0; // Treat out-of-bounds as sea for now
        }
        return mapData[row][col];
    }
}
