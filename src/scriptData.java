// Script class
public class scriptData {
  // @ new line
  // E script end
  // Test script
  private static final String testScript[] = {"This is a test.@This is the main message.HE"};
  // Test script return function
  public static String returnTestScript(int scriptID, int numString) {
    return testScript[scriptID].substring(numString, numString + 1);
  }
  // Test script length return function
  public static int returnTestScriptLength(int scriptID) {
    return testScript[scriptID].length();
  }
}