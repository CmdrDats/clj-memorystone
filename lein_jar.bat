@rem needs this lein.bat https://github.com/technomancy/leiningen/blob/master/bin/lein.bat
	@rem put it in PATH or near this .bat file

@echo off
set JAVA_CMD="c:\program files\java\jdk1.6.0_31\bin\java.exe"
@rem note that this won't be enough(use uberjar instead) if your project deps have anything newer than what cljminecraft project has FIXME:
call lein jar

rem built.jar is a folder link to your running server's plugin folder
rem you make it by running this cmd: mklink /d built.jar c:\craftbukkit\plugins\
if EXIST "built.jar" ( move target\memorystone-*SNAPSHOT.jar built.jar )

call lein install

if NOT "%1" == "nopause" @pause
