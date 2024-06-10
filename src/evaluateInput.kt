/**
 * Mappingprogramm, welches Messwerte als Argumente nimmt und zu Emotion korrespondierende Emoji-UTF8-Codepoints ausgibt.
 * Mit Unix-Philosophie konzipiert, als Bestandteil des Datenstroms gedacht
 * @author Marc Vauderwange, mvauderw@stud.hs-offenburg.de
 */

fun main(args: Array<String>)
{
    if (args.isEmpty())
    {
        return
    }

    val (acceleration, bloodPressureSystolic, bloodPressureDiastolic, pulse) = args.map { it.toDouble() }

    // Messwerte werden nach Intervallen geprüft, welche mit dem Analyseprogramm festgestellt wurden
    if ((1.5..4.0).contains(acceleration) &&
        (120.0..160.0).contains(bloodPressureSystolic) &&
        (80.0..100.0).contains(bloodPressureDiastolic) &&
        (90.0..140.0).contains(pulse))
    {
        // excited
        println("\uD83E\uDD29")
    }

    else if ((1.0..3.5).contains(acceleration) &&
        (90.0..120.0).contains(bloodPressureSystolic) &&
        (60.0..80.0).contains(bloodPressureDiastolic) &&
        (50.0..90.0).contains(pulse))
    {
        // happy
        println("\uD83D\uDE00")
    }

    else if ((0.5..3.0).contains(acceleration) &&
        (90.0..120.0).contains(bloodPressureSystolic) &&
        (60.0..80.0).contains(bloodPressureDiastolic) &&
        (60.0..100.0).contains(pulse))
    {
        // normal
        println("\uD83D\uDE36")
    }

    else if ((0.5..1.5).contains(acceleration) &&
        (100.0..130.0).contains(bloodPressureSystolic) &&
        (70.0..90.0).contains(bloodPressureDiastolic) &&
        (70.0..110.0).contains(pulse))
    {
        // sad
        println("\uD83D\uDE25")
    }

    else if ((1.0..3.5).contains(acceleration) &&
        (120.0..160.0).contains(bloodPressureSystolic) &&
        (80.0..100.0).contains(bloodPressureDiastolic) &&
        (90.0..140.0).contains(pulse))
    {
        // stressed
        println("\uD83D\uDE27")
    }

    else println("❓")
}