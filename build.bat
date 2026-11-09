@echo off
rem Builds spider_ludo.jar (Java 17+). Run from the project folder: build.bat
if exist out rmdir /s /q out
mkdir out\classes
dir /s /b src\*.java > out\sources.txt
javac --release 17 -encoding UTF-8 -d out\classes @out\sources.txt || exit /b 1
echo Main-Class: Main> out\manifest.txt
jar cfm spider_ludo.jar out\manifest.txt -C out\classes .
echo built spider_ludo.jar, run it with: java -jar spider_ludo.jar
