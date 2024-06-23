# Kotlin-Teil
## Ausführen
Abhängigkeiten:
- kotlin
- kotlinc
### Methode 1: IntelliJ
Das GitHub-Repo kann ohne weiteres in IntelliJ geklont und die Dateien ausgeführt werden. Es sei zu erwähnen, dass evaluateInput von Kommandozeilenargumenten abhängig ist.

### Methode 2: CLI
Das Kotlin-CLI kann auch ohne weiteres verwendet werden, um die beiden Dateien zu bauen. Dazu muss nur Kotlin an sich installiert sein, welches in allen gängigen Paket-Managern erhältlich ist. Alternativ können Releases auch direkt über [das offizielle Repository](https://github.com/JetBrains/kotlin/releases) erhalten werden.

Sobald heruntergeladen, können die Programme folgendermaßen gebaut und ausgeführt werden (mit Beispieldaten für evaluateInput):
```
kotlinc evaluateInput.kt testDataParser.kt
kotlin TestDataParserKt
kotlin EvaluateInputKt 2 130 90 100
```
# C-Teil
Für den RaspberryPi 3 Model B+ zur verwendung mit [diesem Display](https://www.waveshare.com/wiki/1.28inch_LCD_Module) geschrieben. Sollte auch auf anderen Modellen Funktionieren. 
## Ausführen
Die Anleitung zum Installieren der API ist [hier](https://www.waveshare.com/wiki/1.28inch_LCD_Module) zu finden, die BCM2835-Bibliothek wird verwendet.
Das Kompilieren erfolg mit der von der API mitgelieferten API und die [main.c](./main.c) ersetzt die gleichnamige Datei in "LCD_Module_RPI_code/RaspberryPi/c/examples" der API.
Das Ausführen des C-Programms erfolgt mit:
```
sudo ./main
```
## Erweiterung
Bei Erweiterung des C-Programms ist darauf zu achten, dass diese Zeile nicht gelöscht wird:
```
DEV_GPIO_Mode(18, BCM2835_GPIO_FSEL_OUTP);
```
Die API enthält einen Fehler in der Initialisierung des Backlight-Pins, diese Zeile behebt diesen.