@rem this is used to clean then compile then export as .jar  (2 jar files in 'target' folder) 
@rem needs this lein.bat https://github.com/technomancy/leiningen/blob/master/bin/lein.bat
	@rem put it in PATH or near this .bat file

@echo off

	
set JAVA_CMD="c:\program files\java\jdk1.6.0_31\bin\java.exe"
rem the project should be closed in eclipse+ccw if you're running the cmd on this line: call lein.bat clean
call lein.bat uberjar
pause
