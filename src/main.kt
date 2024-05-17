import java.io.File
import kotlin.math.max

fun <S, T> List<S>.doublePermute(other: List<T>) : List<Pair<S, T>> =
    this.flatMap { s -> List(other.size) {s}.zip(other) }

fun main()
{
    val sensors = listOf("acceleration", "blutdruck", "pulse", "temperature")
    val states = listOf("excited", "happy", "normal", "sad", "stressed")

    sensors.doublePermute(states).forEach{ it1 ->
        val f = File("./sensing_data/sensing_data/" + it1.first + "_" + it1.second + ".txt").bufferedReader()
        when(it1.first)
        {
            "acceleration", "pulse", "temperature" ->
            {
                var min = Double.MAX_VALUE
                var max = 0.0
                var avg = 0.0
                var total = 0
                f.forEachLine { it2 ->
                    if(!it2.take(5).matches(Regex("\\d\\d:\\d\\d"))) return@forEachLine
                    val inc = it2.takeLastWhile { it3 -> it3 != ' ' }.toDouble()
                    avg += inc
                    if(inc > max) max = inc
                    if(inc < min) min = inc

                    total++
                }
                println("${it1.first} ${it1.second} min|max|avg: $min | $max | ${avg / total}")
            }

            "blutdruck" ->
            {
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

                println("${it1.first} ${it1.second} min|max|avg: $minSystolic/$minDiastolic | $maxSystolic/$maxDiastolic | ${avgSystolic / total}/${avgDiastolic / total}")
            }
        }
    }
}