package com.draponquest;

/**
 * Represents a transition point from one map to another.
 */
public class MapTransition {
    public final int fromMapId;
    public final int fromX;
    public final int fromY;
    public final int toMapId;
    public final int toX;
    public final int toY;

    public MapTransition(int fromMapId, int fromX, int fromY, int toMapId, int toX, int toY) {
        this.fromMapId = fromMapId;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toMapId = toMapId;
        this.toX = toX;
        this.toY = toY;
    }
}
