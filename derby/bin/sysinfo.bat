@echo off
setlocal

call setEmbeddedCP.bat

java org.apache.derby.tools.sysinfo

endlocal
@echo on
