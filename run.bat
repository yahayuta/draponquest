@echo off
echo Running DraponQuest JavaFX...

REM Set JavaFX SDK path
set JAVAFX_SDK=C:\java\javafx-sdk-25.0.1

REM Set JDK path
set PATH=C:\java\jdk-25.0.1\bin;%PATH%

REM Check if classes exist
if not exist "target\classes\com\draponquest\DraponQuestFX.class" (
    echo Error: Game not compiled. Run compile.bat first!
    pause
    exit /b 1
)

REM Copy resources to target directory
if not exist "target\classes\images" mkdir "target\classes\images"
copy "src\main\resources\images\*" "target\classes\images\" >nul 2>&1

if not exist "target\classes\sounds" mkdir "target\classes\sounds"
copy "src\main\resources\sounds\*" "target\classes\sounds\" >nul 2>&1

REM Run the game
java --module-path "%JAVAFX_SDK%\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -cp target\classes com.draponquest.DraponQuestFX

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Game exited with error code: %ERRORLEVEL%
    pause
) 