@echo off
REM Generate all DraponQuest sound and music files

python generate_missing_sounds.py
python generate_ff_sounds.py
python generate_ff_authentic.py
python generate_ff_exact.py
python generate_battle_music.py
python generate_ff_victory.py
python generate_ff_battle.py
python generate_town_music.py

REM Move all generated .wav files to the resource directory
move *.wav ..\src\main\resources\sounds\

REM Double-check: delete any .wav files left in soundgen (should be none)
del /Q *.wav

echo All sound and music files have been generated and moved to the resource directory! 