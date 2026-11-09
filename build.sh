#!/bin/sh
# Builds spider_ludo.jar (Java 17+). Run from the project folder: ./build.sh
set -e
rm -rf out
mkdir -p out/classes
javac --release 17 -encoding UTF-8 -d out/classes $(find src -name '*.java')
printf 'Main-Class: Main\n' > out/manifest.txt
jar cfm spider_ludo.jar out/manifest.txt -C out/classes .
echo "built spider_ludo.jar, run it with: java -jar spider_ludo.jar"
