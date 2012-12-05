@echo off
set cwd=%~dp0
cd ..\clj-minecraft
call on_changed.bat nopause
cd %cwd%

if NOT "%1" == "nopause" @pause
