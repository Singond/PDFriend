@echo off
set appdir=%~dp0
java -jar %appdir%\pdfriend-standalone.jar %*
