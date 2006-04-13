@echo off
setlocal

call setEmbeddedCP.bat

java -Dij.protocol=jdbc:derby: %CONNECTION% org.apache.derby.tools.ij

endlocal
@echo on
