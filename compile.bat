@echo off
echo Compiling DraponQuest JavaFX...

REM Set JavaFX SDK path
set JAVAFX_SDK=C:\javafx-sdk-24.0.1

REM Create output directory
if not exist "target\classes" mkdir "target\classes"

REM Compile Java files with JavaFX modules
javac --module-path "%JAVAFX_SDK%\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -d target\classes src\main\java\com\draponquest\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo To run the game, use: run.bat
) else (
    echo Compilation failed!
    pause
) 