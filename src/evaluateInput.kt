/**
 * Mappingprogramm, welches Messwerte als Argumente nimmt und zu Emotion korrespondierende Emoji-UTF8-Codepoints ausgibt.
 * Mit Unix-Philosophie konzipiert, als Bestandteil des Datenstroms gedacht
 * @author Marc Vauderwange, mvauderw@stud.hs-offenburg.de
 */

fun main(args: Array<String>)
{
    val excited = state(    (1.5..4.0),
                            (120.0..160.0),
                            (80.0..100.0),
                            (90.0..140.0),
                            6,
                            "./BitMaps/excited/excited")

    val happy = state(      (1.0..3.5),
                            (90.0..120.0),
                            (60.0..80.0),
                            (50.0..90.0),
                            8,
                            "./BitMaps/happy/happy")

    val normal = state(     (0.5..3.0),
                            (90.0..120.0),
                            (60.0..80.0),
                            (60.0..100.0),
                            8,
                            "./BitMaps/normal/normal")

    val sad = state(        (0.5..1.5),
                            (100.0..130.0),
                            (70.0..90.0),
                            (70.0..110.0),
                            8,
                            "./BitMaps/sad/sad")

    val stressed = state(   (1.0..3.5),
                            (120.0..160.0),
                            (80.0..100.0),
                            (90.0..140.0),
                            7,
                            "./BitMaps/stressed/stressed")

    val states = arrayOf(excited, happy, normal, sad, stressed);
    if (args.isEmpty())
    {
        return
    }

    val (acceleration, bloodPressureSystolic, bloodPressureDiastolic, pulse) = args.map { it.toDouble() }

    // Messwerte werden nach Intervallen geprüft, welche mit dem Analyseprogramm festgestellt wurden
    for(currentState in states)
    {
        if(currentState.isState(acceleration, bloodPressureSystolic, bloodPressureDiastolic, pulse))
        {
            currentState.sendBmp()
            break
        }
    }

}