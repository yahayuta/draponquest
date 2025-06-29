import com.nttdocomo.ui.*;

/*************************************************************************/
//  DraponQuest
//  programmed by Yakkun
//  ロープレ処女作です
/*************************************************************************/
public class draponQuestMain extends IApplication {
  public void start() {
    //バックライトオン
    PhoneSystem.setAttribute(PhoneSystem.DEV_BACKLIGHT, PhoneSystem.ATTR_BACKLIGHT_ON);
    Display.setCurrent(new canvas());
  }

  /*************************************************************************/
  //  キャンバスクラス
  /*************************************************************************/
  class canvas extends Canvas implements Runnable {
    //定数の宣言
    //同期スピード
    final int WAIT_MSEC = 100;
    //画面（幅）
    final int DISP_WIDTH = 256;
    //画面（高さ）
    final int DISP_HEIGHT = 256;
    //ゲーム状態（タイトル）
    final int GAME_TITLE = 0;
    //ゲーム状態（オープン）
    final int GAME_OPEN = 1;
    //ゲーム状態（ウエイト）
    final int GAME_WAIT = 2;
    //ゲーム状態（コンティニュー）
    final int GAME_CONT = 3;
    //モード（移動）
    final int MODE_MOVE = 0;
    //モード（コマンド）
    final int MODE_COM = 1;
    //モード（バトル）
    final int MODE_BATTLE = 2;
    //モード（イベント）
    final int MODE_EVENT = 3;
    //場所（フィールド）
    final int PLACE_FIELD = 0;
    //場所（建物系）
    final int PLACE_BLDNG = 1;
    //場所（洞窟系）
    final int PLACE_CAVE = 2;
    //コマンド（話す）
    final int COM_TALK = 1;
    //コマンド（調べる）
    final int COM_CHK = 2;
    //コマンド（魔法）
    final int COM_MGK = 3;
    //コマンド（アイテム）
    final int COM_ITEM = 4;
    //コマンド（強さ）
    final int COM_STUS = 5;
    //戦闘コマンド（攻撃）
    final int BCOM_ATK = 1;
    //戦闘コマンド（魔法）
    final int BCOM_MGK = 2;
    //戦闘コマンド（アイテム）
    final int BCOM_ITEM = 3;
    //戦闘コマンド（逃げる）
    final int BCOM_RUN = 4;
    //オブジェクト型変数の宣言
    //スレッド
    Thread thDraponQuest = null;
    //イメージ
    Image imgMe1 = null;
    Image imgMe2 = null;
    Image imgFieldMap[][] = null;
    Image imgSea = null;
    Image imgSnd = null;
    Image imgsStp = null;
    Image imgFrst = null;
    //メディアイメージ
    MediaImage miMe1 = null; 
    MediaImage miMe2 = null; 
    MediaImage miSea = null; 
    MediaImage miSnd = null;
    MediaImage miStp = null;
    MediaImage miFrst = null;
    //プリミティブ変数
    //ボタン押しの制御変数
    boolean isHit = false;
    //現在のゲーム状態
    int currentGameStatus = GAME_TITLE;
    //現在のモード
    int currentMode = MODE_MOVE;
    //現在の場所
    int currentPlace = PLACE_FIELD;
    //現在のコマンド
    int currentCommand = COM_TALK;
    //キャラクタアニメーション
    int flip = 0;
    //マップ用
    int fieldMapEndWidth = 0;
    int fieldMapEndHeight = 0;
    int mapX = 0;
    int mapY = 0;
    //スクリプト用変数
    StringBuffer scriptBuffer[] = new StringBuffer[10];
    String currentChar = null;
    int scriptID = 0;
    int scriptLine = 0;
    int scriptNum = 0;
    int scriptheight = 0;

    /*************************************************************************/
    //  コンストラクタ
    //  変数の初期化・画像データの取得を行う
    /*************************************************************************/
    public canvas() {
      //スクリプトバッファの初期化
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
      //スレッドの初期化
      thDraponQuest = new Thread(this);
      thDraponQuest.start();
    }
    
    /*************************************************************************/
    //  イベントを取得する関数
    /*************************************************************************/
    public void processEvent(int type,int param) {
      if(type == Display.KEY_PRESSED_EVENT) {
        //2度押しのチェック
        if (isHit) {
          //2度押しの防止
          isHit = false;
          switch (param) {
            case Display.KEY_SELECT:
              //セレクトキー分岐関数
              hitKeySelect();
              break;
            case Display.KEY_UP:
              //↑キー分岐関数
              hitUp();
              break;
            case Display.KEY_DOWN:
              //↓キー分岐関数
              hitDown();
              break;
            case Display.KEY_RIGHT:
              //→キー分岐関数
              hitRight();
              break;
            case Display.KEY_LEFT:
              //←キー分岐関数
              hitLeft();
              break;
            case Display.KEY_SOFT2:
              //右ソフトキー分岐関数
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
    //  描写関数
    /*************************************************************************/
    public void paint(Graphics g) {
      switch (currentGameStatus) {
        //ゲーム状態（タイトル）
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
        //ゲーム状態（オープン）
        case GAME_OPEN:
          g.lock();
          g.clearRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.GRAY));
          g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          switch (currentPlace) {
            //場所（フィールド）
            case PLACE_FIELD:
              //マップ描写
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
              //アニメーションフリップ
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
            //モード（移動）
            case MODE_MOVE:
              g.setColor(Graphics.getColorOfName(Graphics.LIME));
              g.drawString("移動", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 55 / 100));
//スクリプト処理のテスト_START
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
//スクリプト処理のテスト_END
              break;
            //モード（コマンド）
            case MODE_COM:
              g.setColor(Graphics.getColorOfName(Graphics.LIME));
              g.drawString("コマンド", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 55 / 100));
              switch (currentCommand) {
                //コマンド（話す）
                case COM_TALK:
                  g.drawString("話す", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                //コマンド（調べる）
                case COM_CHK:
                  g.drawString("調べる", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                //コマンド（魔法）
                case COM_MGK:
                  g.drawString("魔法", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                //コマンド（アイテム）
                case COM_ITEM:
                  g.drawString("アイテム", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
                  break;
                //コマンド（強さ）
                case COM_STUS:
                  g.drawString("強さ", ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 60 / 100));
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
          //白枠の描写
          g.setColor(Graphics.getColorOfName(Graphics.WHITE));
          g.fillRect(0, ((int)DISP_HEIGHT * 50 / 100), ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 50 / 100));
          g.fillRect(((int)DISP_WIDTH * 20 / 100), ((int)DISP_HEIGHT * 50 / 100), ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 50 / 100));
          g.fillRect(((int)DISP_WIDTH * 99 / 100), ((int)DISP_HEIGHT * 50 / 100), ((int)DISP_WIDTH * 1 / 100), ((int)DISP_HEIGHT * 50 / 100));
          g.fillRect(0, ((int)DISP_HEIGHT * 50 / 100), DISP_WIDTH, ((int)DISP_HEIGHT * 1 / 100));
          g.fillRect(0, ((int)DISP_HEIGHT * 99 / 100), DISP_WIDTH, ((int)DISP_HEIGHT * 1 / 100));
          g.unlock(true);
          break;
        //ゲーム状態（ウエイト）
        case GAME_WAIT:
          g.lock();
          g.clearRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.BLACK));
          g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.LIME));
          g.drawString("データ読み込み中", ((int)DISP_WIDTH * 30 / 100), ((int)DISP_HEIGHT * 50 / 100));
          //データ読み込み
          readData();
          g.unlock(true);
          break;
        default:
          System.out.println("paint():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    /*************************************************************************/
    //  スレッド関数
    /*************************************************************************/
    public void run() {
      while(true) {
        try {
          //2度押しの制御
          isHit = true;
          //メインループ
          mainLoop();
          thDraponQuest.sleep(WAIT_MSEC);
        } catch (Throwable th) {
          System.out.println("run():SYSTEM ERROR: " + th.toString());
          break;
        }
      }
    }

    //メインループ
    public void mainLoop() {
      //ゲームステータスチェック
      chkGameStatus();
      //再描写
      repaint();
    }
  
    //フィールド移動関数
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
    
    //ゲームステータスチェック
    public void chkGameStatus() {
      switch (currentGameStatus) {
        //ゲーム状態（タイトル）
        case GAME_TITLE:
          break;
        //ゲーム状態（ウエイト）
        case GAME_WAIT:
          break;
        //ゲーム状態（オープン）
        case GAME_OPEN:
          switch (currentPlace) {
            //場所（フィールド）
            case PLACE_FIELD:
              //キャラクタアニメーション用
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

    //セレクトキー分岐関数
    public void hitKeySelect() {
      switch (currentGameStatus) {
        //ゲーム状態（タイトル）
        case GAME_TITLE:
          currentGameStatus = GAME_WAIT;
          break;
        //ゲーム状態（オープン）
        case GAME_OPEN:
          switch (currentMode) {
            //モード（移動）
            case MODE_MOVE:
              currentMode = MODE_COM;
              break;
            //モード（コマンド）
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

    //↑キー分岐関数
    public void hitUp() {
      switch (currentGameStatus) {
        //ゲーム状態（タイトル）
        case GAME_TITLE:
          break;
        //ゲーム状態（オープン）
        case GAME_OPEN:
          switch (currentMode) {
            //モード（移動）
            case MODE_MOVE:
              //フィールド移動関数
              moveFieldMap(Display.KEY_UP);
              //コマンドセレクト関数
              selectCommand(Display.KEY_UP);
              break;
            //モード（コマンド）
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

    //↓キー分岐関数
    public void hitDown() {
      switch (currentGameStatus) {
        //ゲーム状態（タイトル）
        case GAME_TITLE:
          break;
        //ゲーム状態（オープン）
        case GAME_OPEN:
          switch (currentMode) {
            //モード（移動）
            case MODE_MOVE:
              //フィールド移動関数
              moveFieldMap(Display.KEY_DOWN);
              break;
            //モード（コマンド）
            case MODE_COM:
              //コマンドセレクト関数
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

    //→キー分岐関数
    public void hitRight() {
      switch (currentGameStatus) {
        //ゲーム状態（タイトル）
        case GAME_TITLE:
          break;
        //ゲーム状態（オープン）
        case GAME_OPEN:
          switch (currentMode) {
            //モード（移動）
            case MODE_MOVE:
              //フィールド移動関数
              moveFieldMap(Display.KEY_RIGHT);
              break;
            //モード（コマンド）
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

    //←キー分岐関数
    public void hitLeft() {
      switch (currentGameStatus) {
        //ゲーム状態（タイトル）
        case GAME_TITLE:
          break;
        //ゲーム状態（オープン）
        case GAME_OPEN:
          switch (currentMode) {
            //モード（移動）
            case MODE_MOVE:
              //フィールド移動関数
              moveFieldMap(Display.KEY_LEFT);
              break;
            //モード（コマンド）
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

    //右ソフトキー分岐関数
    public void hitSoft2() {
      switch (currentGameStatus) {
        //ゲーム状態（タイトル）
        case GAME_TITLE:
          break;
        //ゲーム状態（オープン）
        case GAME_OPEN:
          switch (currentMode) {
            //モード（移動）
            case MODE_MOVE:
              break;
            //モード（コマンド）
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

    //コマンドセレクト関数
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

    //データ読み込み
    public void readData() {
      switch (currentPlace) {
        //場所（フィールド）
        case PLACE_FIELD:
          //画像読み込み
          miMe1 = MediaManager.getImage("resource:///me1.gif");
          miMe2 = MediaManager.getImage("resource:///me2.gif");
          miSea = MediaManager.getImage("resource:///sea.gif");
          miSnd = MediaManager.getImage("resource:///snd.gif");
          miStp = MediaManager.getImage("resource:///stp.gif");
          miFrst = MediaManager.getImage("resource:///wd.gif");
          
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
    
          //イメージオブジェクト格納
          imgMe1 = miMe1.getImage();
          imgMe2 = miMe2.getImage();
          imgSea = miSea.getImage();
          imgSnd = miSnd.getImage();
          imgsStp = miStp.getImage();
          imgFrst = miFrst.getImage();

          //マップ用
          imgFieldMap = new Image[fieldMapData.getMapLength()][fieldMapData.FIELD_MAP_WIDTH];
          //マップメモリー情報保存
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
          //ソフトキーの設定→クリア
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