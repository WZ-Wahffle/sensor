import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.net.Socket

/**
 * State-Klasse, welche einen Gemütszustand abbildet.
 * @param acceleration Bereich des Beschleunigungssensors, der zum Gemüt passt.
 * @param bloodPressureSystolic Bereich des systolischen Blutdrucksensors, der zum Gemüt passt.
 * @param bloodPressureDiastolic Bereich des diastolischen Blutdrucksensors, der zum Gemüt passt.
 * @param pulse Bereich des Herzschlagsensors, der zum Gemüt passt.
 * @param numberOfBitMaps Anzahl der zur Animation gehörenden Bitmaps
 * @param baseFilePath Pfad der Basis der Bitmaps. Bitmaps haben dem Schema zu Folgen: "nameX.bmp" (X = 0 bis numberOfBitMaps - 1). Der Pfad wäre hier: "name". "X" und ".bmp" sind nicht Teil des Pfades. X muss mit 0 beginnen. Auflösung hat 240x240 zu sein!
 * @author Valentin Werner, vwerner1@stud.hs-offenburg.de
 */
class state(val acceleration: ClosedFloatingPointRange<Double>,
            val bloodPressureSystolic: ClosedFloatingPointRange<Double>,
            val bloodPressureDiastolic: ClosedFloatingPointRange<Double>,
            val pulse: ClosedFloatingPointRange<Double>,
            val numberOfBitMaps: Int,
            val baseFilePath: String)
{
    fun isState(acceleration: Double,
                bloodPressureSystolic: Double,
                bloodPressureDiastolic: Double,
                pulse: Double)
                : Boolean
    {
        return  this.acceleration.contains(acceleration)
                && this.bloodPressureSystolic.contains(bloodPressureSystolic)
                && this.bloodPressureDiastolic.contains(bloodPressureDiastolic)
                && this.pulse.contains(pulse)
    }

    /**
     * Sendet die BitMaps and den RaspberryPi. Dieser gibt diese dann in einer Schleife aus.
     * @author Valentin Werner, vwerner1@stud.hs-offenburg.de
     */
    fun sendBmp()
    {
        val serverAddress = "192.168.2.112"
        val port = 42420
        val socket = Socket(serverAddress, port)
        println("Connected to server")


        val outputStream: OutputStream = socket.getOutputStream()

        println("$numberOfBitMaps bmps\n")
        //this has to be done. I think it might have to do with little/big endian
        val bytes = ByteArray(4)
        bytes[0] = (numberOfBitMaps shr 0).toByte()
        bytes[1] = (numberOfBitMaps shr 8).toByte()
        bytes[2] = (numberOfBitMaps shr 16).toByte()
        bytes[3] = (numberOfBitMaps shr 24).toByte()

        outputStream.write(bytes, 0 , 4)

        for(i in 0..<numberOfBitMaps)
        {
            println("File: $baseFilePath$i.bmp")
            val file = File("$baseFilePath$i.bmp")
            val fileInputStream = FileInputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (fileInputStream.read(buffer).also { bytesRead = it } != -1)
            {
                outputStream.write(buffer, 0, bytesRead)
            }
            fileInputStream.close()
            println("file ${i + 1} out of ${numberOfBitMaps} send")
        }
        println("File sent")
        socket.close()
    }
}
