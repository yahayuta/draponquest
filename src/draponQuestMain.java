import com.nttdocomo.ui.*;

/*************************************************************************/
//  DraponQuest
//  programmed by Yakkun
//  Loop processing
/*************************************************************************/
public class draponQuestMain extends IApplication {
  public void start() {
    // Backlight ON
    PhoneSystem.setAttribute(PhoneSystem.DEV_BACKLIGHT, PhoneSystem.ATTR_BACKLIGHT_ON);
    Display.setCurrent(new canvas());
  }

  /*************************************************************************/
  // Canvas Class
  /*************************************************************************/
  class canvas extends Canvas implements Runnable {
    // Constant definitions
    // Wait speed
    final int WAIT_MSEC = 100;
    // Display width
    final int DISP_WIDTH = 256;
    // Display height
    final int DISP_HEIGHT = 256;
    // Game status (title)
    final int GAME_TITLE = 0;
    // Game status (open)
    final int GAME_OPEN = 1;
    // Game status (wait)
    final int GAME_WAIT = 2;
    // Game status (continue)
    final int GAME_CONT = 3;
    // Mode (move)
    final int MODE_MOVE = 0;
    // Mode (command)
    final int MODE_COM = 1;
    // Mode (battle)
    final int MODE_BATTLE = 2;
    // Mode (event)
    final int MODE_EVENT = 3;
    // Mode (status)
    final int MODE_STATUS = 4;
    // Place (field)
    final int PLACE_FIELD = 0;
    // Place (building)
    final int PLACE_BLDNG = 1;
    // Place (cave)
    final int PLACE_CAVE = 2;
    // Command (talk)
    final int COM_TALK = 1;
    // Command (check)
    final int COM_CHK = 2;
    // Command (magic)
    final int COM_MGK = 3;
    // Command (item)
    final int COM_ITEM = 4;
    // Command (status)
    final int COM_STUS = 5;
    // Battle command (attack)
    final int BCOM_ATK = 1;
    // Battle command (magic)
    final int BCOM_MGK = 2;
    // Battle command (item)
    final int BCOM_ITEM = 3;
    // Battle command (run)
    final int BCOM_RUN = 4;
    // Object type variable definition
    // Thread
    Thread thDraponQuest = null;
    // Image
    Image imgMe1 = null;
    Image imgMe2 = null;
    Image imgFieldMap[][] = null;
    Image imgSea = null;
    Image imgSnd = null;
    Image imgsStp = null;
    Image imgFrst = null;
    // Media Image
    MediaImage miMe1 = null;
    MediaImage miMe2 = null;
    MediaImage miSea = null;
    MediaImage miSnd = null;
    MediaImage miStp = null;
    MediaImage miFrst = null;
    // Primitive variables
    // Button pressed detection variable
    boolean isHit = false;
    // Current game status
    int currentGameStatus = GAME_TITLE;
    // Current mode
    int currentMode = MODE_MOVE;
    // Current place
    int currentPlace = PLACE_FIELD;
    // Current command
    int currentCommand = COM_TALK;
    // Character animation
    int flip = 0;
    // For map
    int fieldMapEndWidth = 0;
    int fieldMapEndHeight = 0;
    int mapX = 0;
    int mapY = 0;
    // Script variables
    StringBuffer scriptBuffer[] = new StringBuffer[10];
    String currentChar = null;
    int scriptID = 0;
    int scriptLine = 0;
    int scriptNum = 0;
    int scriptheight = 0;
    // Player Status
    String playerName = "Drapon";
    int playerLevel = 1;
    int playerHP = 20;
    int playerMaxHP = 20;
    int playerMP = 0;
    int playerMaxMP = 0;
    int playerGold = 0;
    int playerExp = 0;
    // Detailed Stats
    int playerStr = 4;
    int playerAgi = 4;
    int playerAtk = 4;
    int playerDef = 4;
    String playerWeapon = "None";
    String playerArmor = "None";
    String playerShield = "None";
    // Command cursor
    int commandCursorX = 0;
    int commandCursorY = 0;

    /*************************************************************************/
    // Constructor
    // Variable initialization and image data acquisition
    /*************************************************************************/
    public canvas() {
      // Script buffer initialization
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
      // Thread initialization
      thDraponQuest = new Thread(this);
      thDraponQuest.start();
    }

    /*************************************************************************/
    // Window Drawing Utility
    /*************************************************************************/
    public void drawWindow(Graphics g, int x, int y, int w, int h) {
      g.setColor(Graphics.getColorOfName(Graphics.BLACK));
      g.fillRect(x, y, w, h);
      g.setColor(Graphics.getColorOfName(Graphics.WHITE));
      // Basic NES border (single white block border)
      g.drawRect(x, y, w, h);
      g.drawRect(x + 2, y + 2, w - 4, h - 4);
    }

    /*************************************************************************/
    // Function to get events
    /*************************************************************************/
    public void processEvent(int type, int param) {
      if (type == Display.KEY_PRESSED_EVENT) {
        // Check for double press
        if (isHit) {
          // Ignore double press
          isHit = false;
          switch (param) {
            case Display.KEY_SELECT:
              // Select key pressed function
              hitKeySelect();
              break;
            case Display.KEY_UP:
              // Up key pressed function
              hitUp();
              break;
            case Display.KEY_DOWN:
              // Down key pressed function
              hitDown();
              break;
            case Display.KEY_RIGHT:
              // Right key pressed function
              hitRight();
              break;
            case Display.KEY_LEFT:
              // Left key pressed function
              hitLeft();
              break;
            case Display.KEY_SOFT2:
              // Soft2 key pressed function
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
    // Drawing function
    /*************************************************************************/
    public void paint(Graphics g) {
      switch (currentGameStatus) {
        // Game status (title)
        case GAME_TITLE:
          g.lock();
          g.clearRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.BLACK));
          g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.LIME));
          g.drawString("DRAPON QUEST", ((int) DISP_WIDTH * 30 / 100), ((int) DISP_HEIGHT * 30 / 100));
          g.drawString("PRESS ENTER", ((int) DISP_WIDTH * 30 / 100), ((int) DISP_HEIGHT * 50 / 100));
          g.drawString("(c)2005", ((int) DISP_WIDTH * 35 / 100), ((int) DISP_HEIGHT * 80 / 100));
          g.drawString("Yakkun", ((int) DISP_WIDTH * 35 / 100), ((int) DISP_HEIGHT * 90 / 100));
          g.unlock(true);
          break;
        // Game status (open)
        case GAME_OPEN:
          g.lock();
          g.clearRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.GRAY));
          g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          switch (currentPlace) {
            // Place (field)
            case PLACE_FIELD:
              // Map drawing
              for (int i = 0; i < 8; i++) {
                for (int ii = 0; ii < 16; ii++) {
                  g.drawImage(imgFieldMap[i + fieldMapEndHeight][ii + fieldMapEndWidth], mapX, mapY);
                  mapX += 16;
                  if (mapX == DISP_WIDTH) {
                    mapX = 0;
                  }
                }
                mapY += 16;
                if (mapY == DISP_HEIGHT / 2) {
                  mapY = 0;
                }
              }
              // Animation flip
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

          // --- NES DQ1 Status Window (Top-Left) ---
          if (currentMode == MODE_MOVE || currentMode == MODE_COM || currentMode == MODE_BATTLE) {
            drawWindow(g, 8, 8, 80, 80);
            g.setColor(Graphics.getColorOfName(Graphics.WHITE));
            g.drawString(playerName, 14, 22);
            g.drawString("LV " + playerLevel, 14, 34);
            g.drawString("HP " + playerHP, 14, 46);
            g.drawString("MP " + playerMP, 14, 58);
            g.drawString("G  " + playerGold, 14, 70);
            g.drawString("E  " + playerExp, 14, 82);
          }

          g.setColor(Graphics.getColorOfName(Graphics.BLACK));
          g.fillRect(0, DISP_HEIGHT / 2, DISP_WIDTH, DISP_HEIGHT / 2);
          switch (currentMode) {
            // Mode (move)
            case MODE_MOVE:
              g.setColor(Graphics.getColorOfName(Graphics.LIME));
              g.drawString("Move", ((int) DISP_WIDTH * 1 / 100), ((int) DISP_HEIGHT * 55 / 100));
              // Script processing test_START
              currentChar = scriptData.returnTestScript(scriptID, scriptNum);
              if ("@".equals(currentChar)) {
                scriptLine++;
              } else if ("E".equals(currentChar)) {
                scriptLine = 0;
                scriptheight = 0;
              } else {
                scriptBuffer[scriptLine].append(currentChar);
              }
              for (int i0 = 0; i0 <= scriptLine; i0++) {
                g.drawString(scriptBuffer[i0].toString(), ((int) DISP_WIDTH * 21 / 100),
                    ((int) DISP_HEIGHT * (55 + scriptheight) / 100));
                scriptheight = +5;
              }
              scriptheight = 0;
              if (scriptNum < scriptData.returnTestScriptLength(scriptID) - 1) {
                scriptNum++;
              } else {
                scriptNum = 0;
              }
              // Script processing test_END
              break;

            // Mode (command)
            case MODE_COM:
              drawWindow(g, (int) (DISP_WIDTH * 0.4), (int) (DISP_HEIGHT * 0.1), 120, 100);
              g.setColor(Graphics.getColorOfName(Graphics.WHITE));
              // Column 1
              g.drawString("TALK", (int) (DISP_WIDTH * 0.45), (int) (DISP_HEIGHT * 0.22));
              g.drawString("STATUS", (int) (DISP_WIDTH * 0.45), (int) (DISP_HEIGHT * 0.34));
              g.drawString("STAIRS", (int) (DISP_WIDTH * 0.45), (int) (DISP_HEIGHT * 0.46));
              g.drawString("SEARCH", (int) (DISP_WIDTH * 0.45), (int) (DISP_HEIGHT * 0.58));
              // Column 2
              g.drawString("SPELL", (int) (DISP_WIDTH * 0.7), (int) (DISP_HEIGHT * 0.22));
              g.drawString("ITEM", (int) (DISP_WIDTH * 0.7), (int) (DISP_HEIGHT * 0.34));
              g.drawString("DOOR", (int) (DISP_WIDTH * 0.7), (int) (DISP_HEIGHT * 0.46));
              g.drawString("TAKE", (int) (DISP_WIDTH * 0.7), (int) (DISP_HEIGHT * 0.58));

              // Menu Cursor (indicator)
              int curX = (int) (DISP_WIDTH * (0.42 + (commandCursorX * 0.25)));
              int curY = (int) (DISP_HEIGHT * (0.22 + (commandCursorY * 0.12)));
              g.drawString(">", curX, curY);
              break;

            // Mode (battle)
            case MODE_BATTLE:
              // Enemy display in the center
              drawWindow(g, (int) (DISP_WIDTH * 0.1), (int) (DISP_HEIGHT * 0.1), (int) (DISP_WIDTH * 0.8),
                  (int) (DISP_HEIGHT * 0.4));
              g.setColor(Graphics.getColorOfName(Graphics.WHITE));
              g.drawString("SLIME APPEARS!", (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.25));

              // Battle Commands Window
              drawWindow(g, (int) (DISP_WIDTH * 0.4), (int) (DISP_HEIGHT * 0.55), 120, 60);
              g.drawString("FIGHT", (int) (DISP_WIDTH * 0.45), (int) (DISP_HEIGHT * 0.65));
              g.drawString("RUN", (int) (DISP_WIDTH * 0.45), (int) (DISP_HEIGHT * 0.75));
              g.drawString("SPELL", (int) (DISP_WIDTH * 0.72), (int) (DISP_HEIGHT * 0.65));
              g.drawString("ITEM", (int) (DISP_WIDTH * 0.72), (int) (DISP_HEIGHT * 0.75));

              // Battle Cursor
              int bcurX = (int) (DISP_WIDTH * (0.42 + (commandCursorX * 0.27)));
              int bcurY = (int) (DISP_HEIGHT * (0.65 + (commandCursorY * 0.10)));
              g.drawString(">", bcurX, bcurY);
              break;

            // Mode (status)
            case MODE_STATUS:
              drawWindow(g, (int) (DISP_WIDTH * 0.1), (int) (DISP_HEIGHT * 0.1), (int) (DISP_WIDTH * 0.8),
                  (int) (DISP_HEIGHT * 0.8));
              g.setColor(Graphics.getColorOfName(Graphics.WHITE));
              g.drawString(playerName, (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.20));
              g.drawString("LV " + playerLevel, (int) (DISP_WIDTH * 0.55), (int) (DISP_HEIGHT * 0.20));

              g.drawString("HP " + playerHP + "/" + playerMaxHP, (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.30));
              g.drawString("MP " + playerMP + "/" + playerMaxMP, (int) (DISP_WIDTH * 0.55), (int) (DISP_HEIGHT * 0.30));

              g.drawString("STR: " + playerStr, (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.40));
              g.drawString("AGI: " + playerAgi, (int) (DISP_WIDTH * 0.55), (int) (DISP_HEIGHT * 0.40));
              g.drawString("ATK: " + playerAtk, (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.48));
              g.drawString("DEF: " + playerDef, (int) (DISP_WIDTH * 0.55), (int) (DISP_HEIGHT * 0.48));

              g.drawString("WEP: " + playerWeapon, (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.60));
              g.drawString("ARM: " + playerArmor, (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.68));
              g.drawString("SHLD:" + playerShield, (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.76));

              g.drawString("EXP: " + playerExp, (int) (DISP_WIDTH * 0.15), (int) (DISP_HEIGHT * 0.86));
              g.drawString("GOLD:" + playerGold, (int) (DISP_WIDTH * 0.55), (int) (DISP_HEIGHT * 0.86));
              break;

            default:
              System.out.println("paint():ERROR UNEXPECTED VALUE:currentMode = " + currentMode);
              break;
          }
          g.unlock(true);
          break;
        // Game status (wait)
        case GAME_WAIT:
          g.lock();
          g.clearRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.BLACK));
          g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
          g.setColor(Graphics.getColorOfName(Graphics.LIME));
          g.drawString("Loading Data", ((int) DISP_WIDTH * 30 / 100), ((int) DISP_HEIGHT * 50 / 100));
          // Read data
          readData();
          g.unlock(true);
          break;
        default:
          System.out.println("paint():ERROR UNEXPECTED VALUE:currentGameStatus = " + currentGameStatus);
          break;
      }
    }

    /*************************************************************************/
    // Thread function
    /*************************************************************************/
    public void run() {
      while (true) {
        try {
          // Prevent double press
          isHit = true;
          // Main loop
          mainLoop();
          thDraponQuest.sleep(WAIT_MSEC);
        } catch (Throwable th) {
          System.out.println("run():SYSTEM ERROR: " + th.toString());
          break;
        }
      }
    }

    // Main loop
    public void mainLoop() {
      // Check game status
      chkGameStatus();
      // Redraw
      repaint();
    }

    // Field map movement function
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

    // Check game status
    public void chkGameStatus() {
      switch (currentGameStatus) {
        // Game status (title)
        case GAME_TITLE:
          break;
        // Game status (wait)
        case GAME_WAIT:
          break;
        // Game status (open)
        case GAME_OPEN:
          switch (currentPlace) {
            // Place (field)
            case PLACE_FIELD:
              // For character animation
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

    // Select key pressed function
    public void hitKeySelect() {
      switch (currentGameStatus) {
        // Game status (title)
        case GAME_TITLE:
          currentGameStatus = GAME_WAIT;
          break;
        // Game status (open)
        case GAME_OPEN:
          switch (currentMode) {
            // Mode (move)
            case MODE_MOVE:
              currentMode = MODE_COM;
              break;
            // Mode (command)
            case MODE_COM:
              // Command (status) is at X=0, Y=1
              if (commandCursorX == 0 && commandCursorY == 1) {
                currentMode = MODE_STATUS;
              }
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

    // Up key pressed function
    public void hitUp() {
      switch (currentGameStatus) {
        // Game status (title)
        case GAME_TITLE:
          break;
        // Game status (open)
        case GAME_OPEN:
          switch (currentMode) {
            // Mode (move)
            case MODE_MOVE:
              // Field map movement function
              moveFieldMap(Display.KEY_UP);
              // Command select function
              selectCommand(Display.KEY_UP);
              break;
            // Mode (command)
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

    // Down key pressed function
    public void hitDown() {
      switch (currentGameStatus) {
        // Game status (title)
        case GAME_TITLE:
          break;
        // Game status (open)
        case GAME_OPEN:
          switch (currentMode) {
            // Mode (move)
            case MODE_MOVE:
              // Field map movement function
              moveFieldMap(Display.KEY_DOWN);
              break;
            // Mode (command)
            case MODE_COM:
              // Command select function
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

    // Right key pressed function
    public void hitRight() {
      switch (currentGameStatus) {
        // Game status (title)
        case GAME_TITLE:
          break;
        // Game status (open)
        case GAME_OPEN:
          switch (currentMode) {
            // Mode (move)
            case MODE_MOVE:
              // Field map movement function
              moveFieldMap(Display.KEY_RIGHT);
              break;
            // Mode (command)
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

    // Left key pressed function
    public void hitLeft() {
      switch (currentGameStatus) {
        // Game status (title)
        case GAME_TITLE:
          break;
        // Game status (open)
        case GAME_OPEN:
          switch (currentMode) {
            // Mode (move)
            case MODE_MOVE:
              // Field map movement function
              moveFieldMap(Display.KEY_LEFT);
              break;
            // Mode (command)
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

    // Soft2 key pressed function
    public void hitSoft2() {
      switch (currentGameStatus) {
        // Game status (title)
        case GAME_TITLE:
          break;
        // Game status (open)
        case GAME_OPEN:
          switch (currentMode) {
            // Mode (move)
            case MODE_MOVE:
              break;
            // Mode (command)
            case MODE_COM:
              selectCommand(Display.KEY_SOFT2);
              currentMode = MODE_MOVE;
              break;
            // Mode (status)
            case MODE_STATUS:
              currentMode = MODE_COM;
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

    // Command select function
    public void selectCommand(int keyName) {
      if (currentMode == MODE_COM || currentMode == MODE_BATTLE) {
        switch (keyName) {
          case Display.KEY_UP:
            commandCursorY--;
            if (commandCursorY < 0) {
              commandCursorY = (currentMode == MODE_COM) ? 3 : 1;
            }
            break;
          case Display.KEY_DOWN:
            commandCursorY++;
            if (commandCursorY > ((currentMode == MODE_COM) ? 3 : 1)) {
              commandCursorY = 0;
            }
            break;
          case Display.KEY_LEFT:
            commandCursorX--;
            if (commandCursorX < 0)
              commandCursorX = 1;
            break;
          case Display.KEY_RIGHT:
            commandCursorX++;
            if (commandCursorX > 1)
              commandCursorX = 0;
            break;
          case Display.KEY_SOFT2:
            commandCursorX = 0;
            commandCursorY = 0;
            break;
          default:
            System.out.println("selectCommand():ERROR UNEXPECTED VALUE:keyName = " + keyName);
            break;
        }
      }
    }

    // Read data
    public void readData() {
      switch (currentPlace) {
        // Place (field)
        case PLACE_FIELD:
          // Load image
          miMe1 = MediaManager.getImage("resource:///images/me1.gif");
          miMe2 = MediaManager.getImage("resource:///images/me2.gif");
          miSea = MediaManager.getImage("resource:///images/sea.gif");
          miSnd = MediaManager.getImage("resource:///images/snd.gif");
          miStp = MediaManager.getImage("resource:///images/stp.gif");
          miFrst = MediaManager.getImage("resource:///images/wd.gif");

          try {
            miMe1.use();
            miMe2.use();
            miSea.use();
            miSnd.use();
            miStp.use();
            miFrst.use();
          } catch (Throwable th) {
            System.out.println("canvas():SYSTEM ERROR: " + th.toString());
          }

          // Image object initialization
          imgMe1 = miMe1.getImage();
          imgMe2 = miMe2.getImage();
          imgSea = miSea.getImage();
          imgSnd = miSnd.getImage();
          imgsStp = miStp.getImage();
          imgFrst = miFrst.getImage();

          // For map
          imgFieldMap = new Image[fieldMapData.getMapLength()][fieldMapData.FIELD_MAP_WIDTH];
          // Map data storage
          for (int i = 0; i < fieldMapData.getMapLength(); i++) {
            for (int ii = 0; ii < fieldMapData.FIELD_MAP_WIDTH; ii++) {
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
                  System.out.println("readData():ERROR UNEXPECTED VALUE:fieldMapData.mapDataReturnField(i, ii) = "
                      + fieldMapData.mapDataReturnField(i, ii));
                  break;
              }
            }
          }
          // Soft key 2 setting clear
          // The string literal "CLEAR" in setSoftLabel is an external API call and should
          // not be translated here.
          // setSoftLabel(SOFT_KEY_2, "CLEAR"); // Commented out as setSoftLabel is not
          // defined and likely part of old DoJa API
          currentGameStatus = GAME_OPEN;
          break;
        default:
          System.out.println("readData():ERROR UNEXPECTED VALUE:currentPlace = " + currentPlace);
          break;
      }
    }
  }
}
