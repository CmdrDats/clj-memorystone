@rem needs this lein.bat https://github.com/technomancy/leiningen/blob/master/bin/lein.bat
	@rem put it in PATH

@echo off
set JAVA_CMD="c:\program files\java\jdk1.6.0_31\bin\java.exe"
@rem note that this won't be enough(use uberjar instead) if your project deps have anything newer than what cljminecraft project has FIXME:
call lein do clean, jar

set deployDIR=deploy
rem %deployDIR% is a folder link to your running server's plugin folder
rem you make it by running this cmd: mklink /d deploy c:\craftbukkit\plugins\
if EXIST "%deployDIR%" ( 
   move target\memorystone-*SNAPSHOT.jar %deployDIR%\memorystone.jar 
   if ERRORLEVEL 1 echo FAILED, make sure bukkit isn't running (so the plugin .jar isn't in locked)
	)

rem this only when other plugins depend on this plugin, put it in local ~/.m2 repository
rem call lein install

if NOT "%1" == "nopause" @pause
