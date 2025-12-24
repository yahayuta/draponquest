package com.draponquest;

/**
 * Provides test script data and utility methods for DraponQuest.
 * Now supports multi-language localization.
 */
public class scriptData {
    // Scripts for NPCs
    private static final String scripts[] = {
        "Welcome to Alefgard!", // ID 0
        "This is a test.", // ID 1
        "The king is troubled. A foul presence has settled over the land.", // ID 2
        "Have you seen the princess? She was taken by a fearsome dragon!" // ID 3
    };

    // Get script by ID
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
    private static String[] testScript = null;
    
    /**
     * Initialize the test script based on current language
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
     * Refresh the test script when language changes
     */
    public static void refreshScript() {
        testScript = null;
        initializeTestScript();
    }

    /**
     * Returns a single character from the test script for the given script ID and position.
     *
     * @param scriptID The index of the script in the testScript array.
     * @param numString The character position within the script string.
     * @return The character at the specified position as a String.
     */
    public static String returnTestScript(int scriptID, int numString) {
        initializeTestScript();
        return testScript[scriptID];
    }

    /**
     * Returns the length of the test script for the given script ID.
     *
     * @param scriptID The index of the script in the testScript array.
     * @return The length of the script string.
     */
    public static int returnTestScriptLength(int scriptID) {
        initializeTestScript();
        return testScript[scriptID].length();
    }
} 