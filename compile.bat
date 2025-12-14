@echo off
setlocal enabledelayedexpansion
echo Compiling DraponQuest JavaFX...

REM Set JavaFX SDK path
set JAVAFX_SDK=C:\java\javafx-sdk-25.0.1

REM Set JDK path
set PATH=C:\java\jdk-25.0.1\bin;%PATH%

REM Create output directory
if not exist "target\classes" mkdir "target\classes"

REM Compile Java files with JavaFX modules
set ALL_JAVA_FILES=
for /R src\main\java\com\draponquest %%f in (*.java) do (
    set ALL_JAVA_FILES=!ALL_JAVA_FILES! "%%f"
)
javac -encoding UTF-8 --module-path "%JAVAFX_SDK%\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -d target\classes %ALL_JAVA_FILES%

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo To run the game, use: run.bat
) else (
    echo Compilation failed!
    pause
) 