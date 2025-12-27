@echo off
REM Generate all DraponQuest sound and music files

pushd %~dp0
python generate_sounds.py

REM Move all generated .wav files to the resource directory
move *.wav ..\src\main\resources\sounds\

REM Double-check: delete any .wav files left in soundgen (should be none)
del /Q *.wav

echo All sound and music files have been generated and moved to the resource directory!
popd
