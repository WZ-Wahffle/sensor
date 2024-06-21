import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.net.Socket

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
                && this.pulse.contains(pulse);
    }
    fun sendBmp()
    {
        val serverAddress = "192.168.2.112"
        val port = 42420
        val socket = Socket(serverAddress, port)
        println("Connected to server")


        val outputStream: OutputStream = socket.getOutputStream();

        println("$numberOfBitMaps bmps\n")
        val bytes = ByteArray(4)
        bytes[0] = (numberOfBitMaps shr 0).toByte()
        bytes[1] = (numberOfBitMaps shr 8).toByte()
        bytes[2] = (numberOfBitMaps shr 16).toByte()
        bytes[3] = (numberOfBitMaps shr 24).toByte()

        outputStream.write(bytes, 0 , 4);

        for(i in 0..<numberOfBitMaps)
        {
            println("File: $baseFilePath$i.bmp");
            val file = File("$baseFilePath$i.bmp");
            val fileInputStream = FileInputStream(file);

            val buffer = ByteArray(4096);
            var bytesRead: Int;

            while (fileInputStream.read(buffer).also { bytesRead = it } != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();
            println("file ${i + 1} out of ${numberOfBitMaps} send");
        }
        println("File sent")
        socket.close()
    }
}
