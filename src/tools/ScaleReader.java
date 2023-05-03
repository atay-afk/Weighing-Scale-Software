/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tools;

/**
 *
 * @author User
 **/
import com.fazecast.jSerialComm.*;

public class ScaleReader {
    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPort("COM1"); // or whatever port name you're using
        port.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);

        if (port.openPort()) {
            System.out.println("Port opened successfully");
        } else {
            System.err.println("Failed to open port");
            return;
        }

        byte[] buffer = new byte[1024];
        int numRead;

        while (true) {
            numRead = port.readBytes(buffer, buffer.length);
            System.out.println("Read " + numRead + " bytes");

            // Do something with the data here, e.g.:
            String example = "Data read: " + new String(buffer, 0, numRead);
            example=example.substring(example.lastIndexOf("S") + 1);            
            String[] split = example.split("k");
            String value = split[0];
            System.out.println(value);
            
        }

    }
}

