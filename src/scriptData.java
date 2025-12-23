// Script class
public class scriptData {
  // @ new line
  // E script end
  // Test script
  // Scripts
  private static final String scripts[] = {
      "Welcome to Alefgard!", // ID 0
      "This is a test." // ID 1
  };

  // Get script by ID
  public static String getScript(int id) {
    if (id >= 0 && id < scripts.length) {
      return scripts[id];
    }
    return "...";
  }

  // Test script return function (Legacy)
  public static String returnTestScript(int scriptID, int numString) {
    if (scriptID >= 0 && scriptID < scripts.length) {
      if (numString < scripts[scriptID].length()) {
        return scripts[scriptID].substring(numString, numString + 1);
      }
    }
    return "";
  }

  // Test script length return function (Legacy)
  public static int returnTestScriptLength(int scriptID) {
    if (scriptID >= 0 && scriptID < scripts.length) {
      return scripts[scriptID].length();
    }
    return 0;
  }
}