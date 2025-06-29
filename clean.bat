@echo off
echo Cleaning DraponQuest JavaFX project...

REM Remove compiled classes
if exist "target\classes" (
    rmdir /s /q "target\classes"
    echo Removed compiled classes.
)

REM Remove save file
if exist "draponquest_save.dat" (
    del "draponquest_save.dat"
    echo Removed save file.
)

echo Clean complete!
pause 