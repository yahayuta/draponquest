//スクリプト用クラス
public class scriptData {
  //@ 改行
  //E スクリプト終了
  //テストスクリプト
  private static final String testScript[] = {"これはテストです@こんな感じですか？E"};
  //テストスクリプト返却関数
  public static String returnTestScript(int scriptID, int numString) {
    return testScript[scriptID].substring(numString, numString + 1);
  }
  //テストスクリプト長さ返却関数
  public static int returnTestScriptLength(int scriptID) {
    return testScript[scriptID].length();
  }
}