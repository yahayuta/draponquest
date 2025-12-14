import com.nttdocomo.ui.*;

/*************************************************************************/
//  DraponQuest
//  programmed by Yakkun
//  ���[�v��������ł�
/*************************************************************************/
public class draponQuestMain extends IApplication {
  public void start() {
    //�o�b�N���C�g�I��
    PhoneSystem.setAttribute(PhoneSystem.DEV_BACKLIGHT, PhoneSystem.ATTR_BACKLIGHT_ON);
    Display.setCurrent(new canvas());
  }

  /*************************************************************************/
  //  �L�����o�X�N���X
  /*************************************************************************/
  class canvas extends Canvas implements Runnable {
    //�萔�̐錾
    //�����X�s�[�h
    final int WAIT_MSEC = 100;
    //��ʁi���j
    final int DISP_WIDTH = 256;
    //��ʁi�����j
    final int DISP_HEIGHT = 256;
    //�Q�[����ԁi�^�C�g���j
    final int GAME_TITLE = 0;
    //�Q�[����ԁi�I�[�v���j
    final int GAME_OPEN = 1;
    //�Q�[����ԁi�E�G�C�g�j
    final int GAME_WAIT = 2;
    //�Q�[����ԁi�R���e�B�j���[�j
    final int GAME_CONT = 3;
    //���[�h�i�ړ��j
    final int MODE_MOVE = 0;
    //���[�h�i�R�}���h�j
    final int MODE_COM = 1;
    //���[�h�i�o�g���j
    final int MODE_BATTLE = 2;
    //���[�h�i�C�x���g�j
    final int MODE_EVENT = 3;
    //�ꏊ�i�t�B�[���h�j
    final int PLACE_FIELD = 0;
    //�ꏊ�i�����n�j
    final int PLACE_BLDNG = 1;
    //�ꏊ�i���A�n�j
    final int PLACE_CAVE = 2;
    //�R�}���h�i�b���j
    final int COM_TALK = 1;
    //�R�}���h�i���ׂ�j
    final int COM_CHK = 2;
    //�R�}���h�i���@�j
    final int COM_MGK = 3;
    //�R�}���h�i�A�C�e���j
    final int COM_ITEM = 4;
    //�R�}���h�i�����j
    final int COM_STUS = 5;
    //�퓬�R�}���h�i�U���j
    final int BCOM_ATK = 1;
    //�퓬�R�}���h�i���@�j
    final int BCOM_MGK = 2;
    //�퓬�R�}���h�i�A�C�e���j
    final int BCOM_ITEM = 3;
    //�퓬�R�}���h�i������j
    final int BCOM_RUN = 4;
    //�I�u�W�F�N�g�^�ϐ��̐錾
    //�X���b�h
    Thread thDraponQuest = null;
    //�C���[�W
    Image imgMe1 = null;
    Image imgMe2 = null;
    Image imgFieldMap[][] = null;
    Image imgSea = null;
    Image imgSnd = null;
    Image imgsStp = null;
    Image imgFrst = null;
    //���f�B�A�C���[�W
    MediaImage miMe1 = null; 
    MediaImage miMe2 = null; 
    MediaImage miSea = null; 
    MediaImage miSnd = null;
    MediaImage miStp = null;
    MediaImage miFrst = null;
    //�v���~�e�B�u�ϐ�
    //�{�^�������̐���ϐ�
    boolean isHit = false;
    //���݂̃Q�[�����
    int currentGameStatus = GAME_TITLE;
    //���݂̃��[�h
    int currentMode = MODE_MOVE;
    //���݂̏ꏊ
    int currentPlace = PLACE_FIELD;
    //���݂̃R�}���h
    int currentCommand = COM_TALK;
    //�L�����N�^�A�j���[�V����
    int flip = 0;
    //�}�b�v�p
    int fieldMapEndWidth = 0;
    int fieldMapEndHeight = 0;
    int mapX = 0;
    int mapY = 0;
    //�X�N���v�g�p�ϐ�
    StringBuffer scriptBuffer[] = new StringBuffer[10];
    String currentChar = null;
    int scriptID = 0;
    int scriptLine = 0;
    int scriptNum = 0;
    int scriptheight = 0;

    /*************************************************************************/
    //  �R���X�g���N�^
    //  �ϐ��̏������E�摜�f�[�^�̎擾���s��
    /*************************************************************************/
    public canvas() {
      //�X�N���v�g�o�b�t�@�̏�����
      scriptBuffer[0] = new StringBuffer();
      scriptBuffer[1] = new StringBuffer();
      scriptBuffer[2] = new StringBuffer();
      scriptBuffer[3] = new StringBuffer();
      scriptBuffer[4] = new StringBuffer();
      scriptBuffer[5] = new StringBuffer();
      scriptBuffer[6] = new StringBuffer();
      scriptBuffer[7] = new StringBuffer();
      scriptBuffer[8] = new StringBuffer();
      scriptBuffer[9] = new StringBuffer();
      //�X���b�h�̏�����
      thDraponQuest = new Thread(this);
      thDraponQuest.start();
    }
    
    /*************************************************************************/
    //  �C�x���g���擾����֐�
    /*************************************************************************/
    public void processEvent(int type,int param) {
      if(type == Display.KEY_PRESSED_EVENT) {
        //2�x�����̃`�F�b�N
        if (isHit) {
          //2�x�����̖h�~
          isHit = false;
          switch (param) {
            case Display.KEY_SELECT:
              //�Z���N�g�L�[����֐�
              hitKeySelect();
              break;
            case Display.KEY_UP:
              //���L�[����֐�
              hitUp();
              break;
            case Display.KEY_DOWN:
              //���L�[����֐�
              hitDown();
              break;
            case Display.KEY_RIGHT:
              //���L�[����֐�
              hitRight();
              break;
            case Display.KEY_LEFT:
              //���L�[����֐�
              hitLeft();
              break;
            case Display.KEY_SOFT2:
              //�E�\�t�g�L�[����֐�
              hitSoft2();
              break;
            default:
              System.out.println("processEvent():UNEXPECTED KEY VALUE:param = " + param);
              break;
          }
        }
      }
    }
    
    /*************************************************************************/
    //  �`�ʊ֐�
    /*************************************************************************/
    public void paint(Graphics g) {
      switch (currentGameStatus) {
        //�Q�[����ԁi�^�C�g���j
        case GAME_TITLE:
          g.lock();
          g.clearRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.BLACK));
          g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.LIME));
          g.drawString("DRAPON QUEST", ((int)DISP_WIDTH * 30 / 100), ((int)DISP_HEIGHT * 30 / 100));
          g.drawString("PRESS ENTER", ((int)DISP_WIDTH * 30 / 100), ((int)DISP_HEIGHT * 50 / 100));
          g.drawString("(c)2005", ((int)DISP_WIDTH * 35 / 100), ((int)DISP_HEIGHT * 80 / 100));
          g.drawString("Yakkun", ((int)DISP_WIDTH * 35 / 100), ((int)DISP_HEIGHT * 90 / 100));
          g.unlock(true);
          break;
        //�Q�[����ԁi�I�[�v���j
        case GAME_OPEN:
          g.lock();
          g.clearRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.GRAY));
          g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          switch (currentPlace) {
            //�ꏊ�i�t�B�[���h�j
            case PLACE_FIELD:
              //�}�b�v�`��
              for(int i = 0; i < 8; i++){
                for(int ii = 0; ii < 16; ii++){
                  g.drawImage(imgFieldMap[i + fieldMapEndHeight][ii + fieldMapEndWidth], mapX, mapY);
                  mapX+=16;
                  if(mapX == DISP_WIDTH){
                    mapX = 0;
                  }
                }
                mapY+=16;
                if(mapY == DISP_HEIGHT / 2){
                  mapY = 0;
                }
              }
              //�A�j���[�V�����t���b�v
              switch (flip) {
                case 0:
                  g.drawImage(imgMe1, DISP_WIDTH / 2, DISP_HEIGHT / 4);
                  break;
                case 1:
                  g.drawImage(imgMe2, DISP_WIDTH / 2, DISP_HEIGHT / 4);
                  break;
                default:
                  System.out.println("paint():ERROR UNEXPECTED VALUE:flip = " + flip);
                  break;
              }
              break;
            default:
              System.out.println("paint():ERROR UNEXPECTED VALUE:currentPlace = " + currentPlace);
              break;
          }
          g.setColor(Graphics.getColorOfName(Graphics.BLACK));
          g.fillRect(0, DISP_HEIGHT / 2, DISP_WIDTH, DISP_HEIGHT / 2);
          switch (currentMode) {
            //���[�h�i�ړ��j
            case MODE_MOVE:
              g.setColor(Graphics.getColorOfName(Graphics.LIME));
              g.drawString("�ړ�", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 55 / 100));
//�X�N���v�g�����̃e�X�g_START
              currentChar = scriptData.returnTestScript(scriptID, scriptNum);
              System.out.println("paint():currentChar = " + currentChar);
              
              if ("@".equals(currentChar)) {
                scriptLine++;
              } else if ("E".equals(currentChar)) {
                System.out.println("paint():script end");
                scriptLine = 0;
                scriptheight = 0;
              } else {
                scriptBuffer[scriptLine].append(currentChar);
              }
              for (int i0 = 0; i0 <= scriptLine; i0++) {
                g.drawString(scriptBuffer[i0].toString(), ((int)DISP_WIDTH * 21 / 100), ((int)DISP_HEIGHT * (55 + scriptheight) / 100));
                scriptheight=+5;
              }
              scriptheight = 0;
              if (scriptNum < scriptData.returnTestScriptLength(scriptID) - 1) {
                scriptNum++;
              } else {
                System.out.println("paint():scriptNum = 0");
                scriptNum = 0;
              }
//�X�N���v�g�����̃e�X�g_END
              break;
            //���[�h�i�R�}���h�j
            case MODE_COM:
              g.setColor(Graphics.getColorOfName(Graphics.LIME));
              g.drawString("�R�}���h", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 55 / 100));
              switch (currentCommand) {
                //�R�}���h�i�b���j
                case COM_TALK:
                  g.drawString("�b��", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                //�R�}���h�i���ׂ�j
                case COM_CHK:
                  g.drawString("���ׂ�", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                //�R�}���h�i���@�j
                case COM_MGK:
                  g.drawString("���@", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                //�R�}���h�i�A�C�e���j
                case COM_ITEM:
                  g.drawString("�A�C�e��", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                //�R�}���h�i�����j
                case COM_STUS:
                  g.drawString("����", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                default:
                  System.out.println("paint():ERROR UNEXPECTED VALUE:currentCommand = " + currentCommand);
                  break;
              }
              break;
            default:
              System.out.println("paint():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
              break;
          }
          //���g�̕`��
          g.setColor(Graphics.getColorOfName(Graphics.WHITE));
          g.fillRect(0, ((int)DISP_HEIGHT * 50 / 100), ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 50 / 100));
          g.fillRect(((int)DISP_WIDTH * 20 / 100), ((int)DISP_HEIGHT * 50 / 100), ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 50 / 100));
          g.fillRect(((int)DISP_WIDTH * 99 / 100), ((int)DISP_HEIGHT * 50 / 100), ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 50 / 100));
          g.fillRect(0, ((int)DISP_HEIGHT * 50 / 100), DISP_WIDTH, ((int)DISP_HEIGHT * 1 / 100));
          g.fillRect(0, ((int)DISP_HEIGHT * 99 / 100), DISP_WIDTH, ((int)DISP_HEIGHT * 1 / 100));
          g.unlock(true);
          break;
        //�Q�[����ԁi�E�G�C�g�j
        case GAME_WAIT:
          g.lock();
          g.clearRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.BLACK));
          g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.LIME));
          g.drawString("�f�[�^�ǂݍ��ݒ�", ((int)DISP_WIDTH * 30 / 100), ((int)DISP_HEIGHT * 50 / 100));
          //�f�[�^�ǂݍ���
          readData();
          g.unlock(true);
          break;
        default:
          System.out.println("paint():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    /*************************************************************************/
    //  �X���b�h�֐�
    /*************************************************************************/
    public void run() {
      while(true) {
        try {
          //2�x�����̐���
          isHit = true;
          //���C�����[�v
          mainLoop();
          thDraponQuest.sleep(WAIT_MSEC);
        } catch (Throwable th) {
          System.out.println("run():SYSTEM ERROR: " + th.toString());
          break;
        }
      }
    }

    //���C�����[�v
    public void mainLoop() {
      //�Q�[���X�e�[�^�X�`�F�b�N
      chkGameStatus();
      //�ĕ`��
      repaint();
    }
  
    //�t�B�[���h�ړ��֐�
    public void moveFieldMap(int direction) {      
      switch (direction) {
        case Display.KEY_UP:
          fieldMapEndHeight--;
          if (fieldMapEndHeight < 0) {
            fieldMapEndHeight = 0;
          }
          break;
        case Display.KEY_DOWN:
          fieldMapEndHeight++;
          if (fieldMapEndHeight > fieldMapData.getMapLength() - 8) {
            fieldMapEndHeight = fieldMapData.getMapLength() - 8;
          }
          break;
        case Display.KEY_RIGHT:
          fieldMapEndWidth++;
          if (fieldMapEndWidth > fieldMapData.FIELD_MAP_WIDTH - 16) {
            fieldMapEndWidth = fieldMapData.FIELD_MAP_WIDTH - 16;
          }
          break;
        case Display.KEY_LEFT:
          fieldMapEndWidth--;
          if (fieldMapEndWidth < 0) {
            fieldMapEndWidth = 0;
          }
          break;
        default:
          System.out.println("moveFieldMap():ERROR UNEXPECTED VALUE:direction = " + direction);
          break;
      }
    }
    
    //�Q�[���X�e�[�^�X�`�F�b�N
    public void chkGameStatus() {
      switch (currentGameStatus) {
        //�Q�[����ԁi�^�C�g���j
        case GAME_TITLE:
          break;
        //�Q�[����ԁi�E�G�C�g�j
        case GAME_WAIT:
          break;
        //�Q�[����ԁi�I�[�v���j
        case GAME_OPEN:
          switch (currentPlace) {
            //�ꏊ�i�t�B�[���h�j
            case PLACE_FIELD:
              //�L�����N�^�A�j���[�V�����p
              switch (flip) {
                case 0:
                  flip = 1;
                  break;
                case 1:
                  flip = 0;
                  break;
                default:
                  System.out.println("chkGameStatus():ERROR UNEXPECTED VALUE:flip = " + flip);
                  break;
              }
            break;
          default:
            System.out.println("chkGameStatus():ERROR UNEXPECTED VALUE:currentPlace = " + currentPlace);
            break;
          }
          break;
        default:
          System.out.println("chkGameStatus():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    //�Z���N�g�L�[����֐�
    public void hitKeySelect() {
      switch (currentGameStatus) {
        //�Q�[����ԁi�^�C�g���j
        case GAME_TITLE:
          currentGameStatus = GAME_WAIT;
          break;
        //�Q�[����ԁi�I�[�v���j
        case GAME_OPEN:
          switch (currentMode) {
            //���[�h�i�ړ��j
            case MODE_MOVE:
              currentMode = MODE_COM;
              break;
            //���[�h�i�R�}���h�j
            case MODE_COM:
              break;
            default:
              System.out.println("hitKeySelect():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
              break;
          }
          break;
        default:
          System.out.println("hitKeySelect():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    //���L�[����֐�
    public void hitUp() {
      switch (currentGameStatus) {
        //�Q�[����ԁi�^�C�g���j
        case GAME_TITLE:
          break;
        //�Q�[����ԁi�I�[�v���j
        case GAME_OPEN:
          switch (currentMode) {
            //���[�h�i�ړ��j
            case MODE_MOVE:
              //�t�B�[���h�ړ��֐�
              moveFieldMap(Display.KEY_UP);
              //�R�}���h�Z���N�g�֐�
              selectCommand(Display.KEY_UP);
              break;
            //���[�h�i�R�}���h�j
            case MODE_COM:
              break;
            default:
              System.out.println("hitUp():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
              break;
          }
          break;
        default:
          System.out.println("hitUp():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    //���L�[����֐�
    public void hitDown() {
      switch (currentGameStatus) {
        //�Q�[����ԁi�^�C�g���j
        case GAME_TITLE:
          break;
        //�Q�[����ԁi�I�[�v���j
        case GAME_OPEN:
          switch (currentMode) {
            //���[�h�i�ړ��j
            case MODE_MOVE:
              //�t�B�[���h�ړ��֐�
              moveFieldMap(Display.KEY_DOWN);
              break;
            //���[�h�i�R�}���h�j
            case MODE_COM:
              //�R�}���h�Z���N�g�֐�
              selectCommand(Display.KEY_DOWN);
              break;
            default:
              System.out.println("hitDown():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
              break;
          }
          break;
        default:
          System.out.println("hitDown():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    //���L�[����֐�
    public void hitRight() {
      switch (currentGameStatus) {
        //�Q�[����ԁi�^�C�g���j
        case GAME_TITLE:
          break;
        //�Q�[����ԁi�I�[�v���j
        case GAME_OPEN:
          switch (currentMode) {
            //���[�h�i�ړ��j
            case MODE_MOVE:
              //�t�B�[���h�ړ��֐�
              moveFieldMap(Display.KEY_RIGHT);
              break;
            //���[�h�i�R�}���h�j
            case MODE_COM:
              break;
            default:
              System.out.println("hitRight():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
              break;
          }
          break;
        default:
          System.out.println("hitRight():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    //���L�[����֐�
    public void hitLeft() {
      switch (currentGameStatus) {
        //�Q�[����ԁi�^�C�g���j
        case GAME_TITLE:
          break;
        //�Q�[����ԁi�I�[�v���j
        case GAME_OPEN:
          switch (currentMode) {
            //���[�h�i�ړ��j
            case MODE_MOVE:
              //�t�B�[���h�ړ��֐�
              moveFieldMap(Display.KEY_LEFT);
              break;
            //���[�h�i�R�}���h�j
            case MODE_COM:
              break;
            default:
              System.out.println("hitLeft():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
              break;
          }
          break;
        default:
          System.out.println("hitLeft():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    //�E�\�t�g�L�[����֐�
    public void hitSoft2() {
      switch (currentGameStatus) {
        //�Q�[����ԁi�^�C�g���j
        case GAME_TITLE:
          break;
        //�Q�[����ԁi�I�[�v���j
        case GAME_OPEN:
          switch (currentMode) {
            //���[�h�i�ړ��j
            case MODE_MOVE:
              break;
            //���[�h�i�R�}���h�j
            case MODE_COM:
              selectCommand(Display.KEY_SOFT2);
              currentMode = MODE_MOVE;
              break;
            default:
              System.out.println("hitSoft2():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
              break;
          }
          break;
        default:
          System.out.println("hitSoft2():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    //�R�}���h�Z���N�g�֐�
    public void selectCommand(int keyName) {
      if(currentMode == MODE_COM){
        switch (keyName) {
          case Display.KEY_UP:
            currentCommand--;
            if (currentCommand < COM_TALK) {
              currentCommand = COM_STUS;
            }
            break;
          case Display.KEY_DOWN:
            currentCommand++;
            if (currentCommand > COM_STUS) {
              currentCommand = COM_TALK;
            }
            break;
          case Display.KEY_SOFT2:
            currentCommand = COM_TALK;
            break;
          default:
            System.out.println("selectCommand():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
            break;
        }
      }
    }

    //�f�[�^�ǂݍ���
    public void readData() {
      switch (currentPlace) {
        //�ꏊ�i�t�B�[���h�j
        case PLACE_FIELD:
          //�摜�ǂݍ���
          miMe1 = MediaManager.getImage("resource:///images/me1.gif");
          miMe2 = MediaManager.getImage("resource:///images/me2.gif");
          miSea = MediaManager.getImage("resource:///images/sea.gif");
          miSnd = MediaManager.getImage("resource:///images/snd.gif");
          miStp = MediaManager.getImage("resource:///images/stp.gif");
          miFrst = MediaManager.getImage("resource:///images/wd.gif");
          
          try{
            miMe1.use();
            miMe2.use();
            miSea.use();
            miSnd.use();
            miStp.use();
            miFrst.use();
          }catch(Throwable th){
            System.out.println("canvas():SYSTEM ERROR: " + th.toString());
          }
    
          //�C���[�W�I�u�W�F�N�g�i�[
          imgMe1 = miMe1.getImage();
          imgMe2 = miMe2.getImage();
          imgSea = miSea.getImage();
          imgSnd = miSnd.getImage();
          imgsStp = miStp.getImage();
          imgFrst = miFrst.getImage();

          //�}�b�v�p
          imgFieldMap = new Image[fieldMapData.getMapLength()][fieldMapData.FIELD_MAP_WIDTH];
          //�}�b�v�������[���ۑ�
          for(int i = 0; i < fieldMapData.getMapLength(); i++){
            for(int ii = 0; ii < fieldMapData.FIELD_MAP_WIDTH; ii++){
              switch (fieldMapData.mapDataReturnField(i, ii)) {
                case 0:
                  imgFieldMap[i][ii] = imgSea;
                  break;
                case 1:
                  imgFieldMap[i][ii] = imgSnd;
                  break;
                case 2:
                  imgFieldMap[i][ii] = imgsStp;
                  break;
                case 3:
                  imgFieldMap[i][ii] = imgFrst;
                  break;
                default:
                  System.out.println("readData():ERROR UNEXPECTED VALUE:fieldMapData.mapDataReturnField(i, ii) = " + fieldMapData.mapDataReturnField(i, ii));
                  break;
              }
            }
          }
          //�\�t�g�L�[�̐ݒ聨�N���A
          setSoftLabel(SOFT_KEY_2, "CLEAR");
          currentGameStatus = GAME_OPEN;
          break;
        default:
          System.out.println("readData():ERROR UNEXPECTED VALUE:currentPlace = " + currentPlace);
          break;
      }
    }
  }
}