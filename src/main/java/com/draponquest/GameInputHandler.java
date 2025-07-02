package com.draponquest;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles keyboard input for DraponQuest JavaFX
 * Replaces the DoJa key event system with modern JavaFX input handling
 */
public class GameInputHandler {
    
    private final DraponQuestFX game;
    private boolean isHit = false; // Prevents double key presses
    
    /**
     * Constructs a GameInputHandler for the given game instance.
     * @param game The DraponQuestFX game instance.
     */
    public GameInputHandler(DraponQuestFX game) {
        this.game = game;
    }
    
    /**
     * Handles key press events and routes them to the appropriate game logic.
     * @param event The JavaFX KeyEvent.
     */
    public void handleKeyPressed(KeyEvent event) {
        // Always allow ESC and ENTER to work
        if (event.getCode() == KeyCode.ESCAPE) {
            game.hitSoft2();
            return;
        }
        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
            game.hitKeySelect();
            return;
        }
        // Prevent double key presses for other keys
        if (isHit) {
            return;
        }
        isHit = true;
        // Battle mode input
        if (game.currentMode == DraponQuestFX.MODE_BATTLE) {
            game.handleBattleInput(event.getCode());
            return;
        }
        switch (event.getCode()) {
            case UP:
            case W:
                game.hitUp();
                break;
            case DOWN:
            case S:
                game.hitDown();
                break;
            case LEFT:
            case A:
                game.hitLeft();
                break;
            case RIGHT:
            case D:
                game.hitRight();
                break;
            case F5:
                game.saveGame();
                break;
            case F9:
                game.loadGame();
                break;
            case M:
                // Toggle music on/off
                game.toggleMusic();
                break;
            case T:
                // Toggle sound effects on/off
                game.toggleSound();
                break;
            case OPEN_BRACKET:
                // Decrease volume
                game.decreaseVolume();
                break;
            case CLOSE_BRACKET:
                // Increase volume
                game.increaseVolume();
                break;
            default:
                // Ignore other keys
                break;
        }
    }
    
    /**
     * Handles key release events and resets the hit flag.
     * @param event The JavaFX KeyEvent.
     */
    public void handleKeyReleased(KeyEvent event) {
        // Reset hit flag when key is released
        isHit = false;
    }
} 