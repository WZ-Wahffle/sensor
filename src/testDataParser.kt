/**
 * Analyseprogramm für bereitgestellte Test-Sensordaten
 * @author Marc Vauderwange, mvauderw@stud.hs-offenburg.de
 *
 * NOTIZ: Die zu überprüfenden Programme müssen sich in einem Verzeichnis "sensing_data" befinden,
 * welcher sich im Working Directory von diesem Programm befindet!
 */

import java.io.File

/**
 * Funktionaler Mapper, welcher aus zwei Listen alle möglichen Paare aus Elementen
 * jener Listen bildet
 * @receiver Die erste Liste
 * @param other Die zweite Liste
 * @return Liste von allen möglichen Kombinationen von {$(Element aus l1), $(Element aus l2)}
 */
fun <S, T> List<S>.doublePermute(other: List<T>) : List<Pair<S, T>> =
    this.flatMap { s -> List(other.size) {s}.zip(other) }

fun main()
{
    val sensors = listOf("acceleration", "blutdruck", "pulse", "temperature")
    val states = listOf("excited", "happy", "normal", "sad", "stressed")

    // Bilde alle Kombinationen aus Emotion und Sensorwert und lese dessen Datei aus
    states.doublePermute(sensors).forEach{ it1 ->
        val f = File("./sensing_data/" + it1.second + "_" + it1.first + ".txt").bufferedReader()

        // Separierung zwischen Blutdruck und den restlichen Werten, da Blutdruck ein anderes Format vorweist
        when(it1.second)
        {
            "acceleration", "pulse", "temperature" ->
            {
                var min = Double.MAX_VALUE
                var max = 0.0
                var avg = 0.0
                var total = 0
                f.forEachLine { it2 ->
                    // Breche Suche in Messwertdatei ab, sobald ein String gefunden wurde, der nicht mit einem Zeitstempel beginnt
                    // (Erste 5 Buchstaben bestehen nicht aus zwei Zahlen, einem Doppelpunkt und zwei Zahlen)
                    if(!it2.take(5).matches(Regex("\\d\\d:\\d\\d"))) return@forEachLine
                    val inc = it2.takeLastWhile { it3 -> it3 != ' ' }.toDouble()
                    avg += inc
                    if(inc > max) max = inc
                    if(inc < min) min = inc

                    total++
                }
                // Gebe für jeden Messwert Minimum, Maximum und Mittelwert (letzteres größtenteils nutzlos) aus
                println(String.format("%-40s %f | %f | %f", "${it1.second} ${it1.first} min|max|avg:", min, max, avg / total))
            }

            "blutdruck" ->
            {
                // Äquivalent zu anderen Messwerten, nur mit zwei Werten pro Zeile
                var minSystolic = Double.MAX_VALUE
                var maxSystolic = 0.0
                var avgSystolic = 0.0
                var minDiastolic = Double.MAX_VALUE
                var maxDiastolic = 0.0
                var avgDiastolic = 0.0
                var total = 0
                f.forEachLine { it2 ->
                    if(!it2.take(5).matches(Regex("\\d\\d:\\d\\d"))) return@forEachLine
                    val incSystolic = it2.split(":")[2].split(", ")[0].split("=")[1].toDouble()
                    val incDiastolic = it2.split(":")[2].split(", ")[1].split("=")[1].toDouble()
                    avgSystolic += incSystolic
                    if(maxSystolic < incSystolic) maxSystolic = incSystolic
                    if(minSystolic > incSystolic) minSystolic = incSystolic
                    avgDiastolic += incDiastolic
                    if(maxDiastolic < incDiastolic) maxDiastolic = incDiastolic
                    if(minDiastolic > incDiastolic) minDiastolic = incDiastolic
                    total++
                }
                println(String.format("%-40s %f/%f | %f/%f | %f/%f", "${it1.second} ${it1.first} min|max|avg:", minSystolic, minDiastolic, maxSystolic, maxDiastolic, avgSystolic / total, avgDiastolic / total))
            }
        }
    }
}