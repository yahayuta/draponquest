package com.draponquest;

/**
 * Provides test script data and utility methods for DraponQuest.
 */
public class scriptData {
    /**
     * Game instructions script that explains how to play.
     * '@' indicates a line break, 'H' and 'E' are control characters.
     */
    private static final String[] testScript = {
        "Welcome to DraponQuest!@Explore the world using the arrow keys.@Open the command menu with ENTER.@Each time you move, your score increases by 1.@Try to get the highest score by surviving and exploring!@Fight monsters, defend to reduce damage, or run from tough battles.@If you are defeated, your total score will be shown.@Save with F5, load with F9. Good luck, hero!HE"
    };

    /**
     * Returns a single character from the test script for the given script ID and position.
     *
     * @param scriptID The index of the script in the testScript array.
     * @param numString The character position within the script string.
     * @return The character at the specified position as a String.
     */
    public static String returnTestScript(int scriptID, int numString) {
        return testScript[scriptID];
    }

    /**
     * Returns the length of the test script for the given script ID.
     *
     * @param scriptID The index of the script in the testScript array.
     * @return The length of the script string.
     */
    public static int returnTestScriptLength(int scriptID) {
        return testScript[scriptID].length();
    }
} 