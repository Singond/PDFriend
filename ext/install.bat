@echo off
set target=%cd%/pdfriend-standalone.jar
if not exist %target% (
	echo Please run this script in the directory where the pdfriend-standalone.jar file is located
	pause
	exit
) 
set launcher=%cd%/pdfriend.bat
echo @echo off > %launcher%
echo java -jar %target% >> %launcher%
echo PDFriend launcher script has been created in %launcher%.
echo Copy it somewhere to your PATH in order to make it executable by typing "pdfriend"
pause