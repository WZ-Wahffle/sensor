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


