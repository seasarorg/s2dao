@echo off
setlocal

call setEmbeddedCP.bat

java org.apache.derby.tools.dblook -d %URL% %*

endlocal
@echo on
