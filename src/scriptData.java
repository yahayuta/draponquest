//�X�N���v�g�p�N���X
public class scriptData {
  //@ ���s
  //E �X�N���v�g�I��
  //�e�X�g�X�N���v�g
  private static final String testScript[] = {"����̓e�X�g�ł�@����Ȋ����ł����HE"};
  //�e�X�g�X�N���v�g�ԋp�֐�
  public static String returnTestScript(int scriptID, int numString) {
    return testScript[scriptID].substring(numString, numString + 1);
  }
  //�e�X�g�X�N���v�g�����ԋp�֐�
  public static int returnTestScriptLength(int scriptID) {
    return testScript[scriptID].length();
  }
}