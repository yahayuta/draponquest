package com.draponquest;

/**
 * Provides test script data and utility methods for DraponQuest.
 * Now supports multi-language localization.
 */
public class scriptData {
    // Scripts for NPCs
    /**
     * An array of predefined script lines for various NPCs, indexed by ID.
     */
    private static final String scripts[] = {
        "Welcome to Alefgard!", // ID 0
        "This is a test.", // ID 1
        "The king is troubled. A foul presence has settled over the land.", // ID 2
        "Have you seen the princess? She was taken by a fearsome dragon!", // ID 3
        "The princess is beautiful, is she not?", // ID 4
        "I am a humble servant of the king.", // ID 5
        "There is a secret passage in this castle." // ID 6
    };

    /**
     * Retrieves a script line by its ID.
     * If the ID is out of bounds, a default "..." string is returned.
     * @param id The ID of the script to retrieve.
     * @return The script string corresponding to the ID, or "..." if not found.
     */
    public static String getScript(int id) {
        if (id >= 0 && id < scripts.length) {
            return scripts[id];
        }
        return "...";
    }

    /**
     * Game instructions script that explains how to play.
     * Uses LocalizationManager for multi-language support.
     * '@' indicates a line break, 'H' and 'E' are control characters.
     */
    /**
     * Stores the game instructions script, which is dynamically loaded based on the current language.
     * It contains special characters ('@', 'H', 'E') for message formatting.
     */
    private static String[] testScript = null;
    
    /**
     * Initializes the {@code testScript} array with localized game instructions.
     * This method is called internally to ensure the script is up-to-date with the current language.
     */
    private static void initializeTestScript() {
        if (testScript == null) {
            testScript = new String[]{
                LocalizationManager.getText("welcome") + "@" +
                LocalizationManager.getText("explore") + "@" +
                LocalizationManager.getText("command_menu") + "@" +
                LocalizationManager.getText("score_info") + "@" +
                LocalizationManager.getText("goal") + "@" +
                LocalizationManager.getText("battle_info") + "@" +
                LocalizationManager.getText("defeat_info") + "@" +
                LocalizationManager.getText("save_info") + "HE"
            };
        }
    }
    
    /**
     * Refreshes the {@code testScript} by forcing a reload based on the currently active language.
     * This method should be called when the game's language setting changes.
     */
    public static void refreshScript() {
        testScript = null;
        initializeTestScript();
    }

    /**
     * Returns the entire script string for a given script ID.
     * Note: The {@code numString} parameter is currently unused.
     * @param scriptID The index of the script in the {@code testScript} array.
     * @param numString (Unused) A placeholder for character position within the script string.
     * @return The complete script string at the specified ID.
     */
    public static String returnTestScript(int scriptID, int numString) {
        initializeTestScript();
        return testScript[scriptID];
    }

    /**
     * Returns the length of the script string for a given script ID.
     * @param scriptID The index of the script in the {@code testScript} array.
     * @return The length of the script string at the specified ID.
     */
    public static int returnTestScriptLength(int scriptID) {
        initializeTestScript();
        return testScript[scriptID].length();
    }
} 