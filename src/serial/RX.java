package serial;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author scream3r
 */
public class RX {

    /**
     * @param args the command line arguments
     */
    public static void go() {
    	
        SerialPort serialPort = new SerialPort("COM4");
        try {
        	// serialPort.closePort();//Close serial port
            serialPort.openPort();//Open serial port
            serialPort.setParams(115200, 8, 1, 0);//Set params.
            while(serialPort.isOpened()) {
            	serialPort.writeByte((byte)0x01);
	            byte[] buffer = serialPort.readBytes(4);//Read 10 bytes from serial port
	            
	            for (int i = 0; i < buffer.length; i++) {
	            	 System.out.println(buffer[i] );
				}
				
	            // System.out.println(buffer[3] * 0x1000000 + buffer[2] * 0x10000 + buffer[1] * 0x100 + buffer[0]);
            }
            serialPort.closePort();//Close serial port
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
}