package com.draponquest;

/**
 * Provides test script data and utility methods for DraponQuest.
 */
public class scriptData {
    /**
     * Test script lines for demonstration purposes.
     * '@' indicates a line break, 'H' and 'E' are control characters.
     */
    private static final String[] testScript = {"This is a test.@Next line is also a test.HE"};

    /**
     * Returns a single character from the test script for the given script ID and position.
     *
     * @param scriptID The index of the script in the testScript array.
     * @param numString The character position within the script string.
     * @return The character at the specified position as a String.
     */
    public static String returnTestScript(int scriptID, int numString) {
        return testScript[scriptID].substring(numString, numString + 1);
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