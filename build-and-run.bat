@echo off
echo ========================================
echo DraponQuest JavaFX - Build and Run
echo ========================================
echo.

REM Compile first
call compile.bat

REM If compilation successful, run the game
if %ERRORLEVEL% EQU 0 (
    echo.
    echo Starting game...
    call run.bat
) else (
    echo Build failed, cannot run game.
    pause
) 