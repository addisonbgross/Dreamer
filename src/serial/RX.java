package serial;

import jssc.*;

import java.util.List;
import java.util.Arrays;

public final class RX {   
	
    static List<String> ports;
    static SerialPort serialPort;
    public static SerialData serialData = new SerialData(); 

    public static void go() {

    	ports = Arrays.asList(SerialPortList.getPortNames());
    	
    	if(ports != null) {
    	
    		print(ports);
    	
    		serialPort = new SerialPort(ports.get(0)); 
        
	    	try {
	    		
	            serialPort.openPort();
	            serialPort.setParams(115200, 8, 1, 0);
	            int mask = SerialPort.MASK_RXCHAR;
	            serialPort.setEventsMask(mask);
	            serialPort.addEventListener(new SerialPortReader());
	            // serialPort.writeString("BLARGH");
	        
	    	} catch (SerialPortException spe) { System.err.println(spe); }
    	}
    }
    
    public static boolean tryNextInt() throws Exception {
    	
    	return getNextInt(serialData);
    }
    
    private static boolean getNextInt(SerialData s) throws Exception {

    	Exception e = new Exception() {
    		
			private static final long serialVersionUID = 3758617126125859927L;

			public String toString() {
    			return "RX.java NOT IMPLEMENTED";
    		}
		};
	
		Byte head = 0;
		
		do {
			head = s.queue.poll();
			
		} while(
				
			!head.equals((byte)'#') 
			&& !head.equals((byte)'$') 
			&& !s.queue.isEmpty()
		);
		
		if(s.queue.size() >= 8) {
			System.out.println("2 bytes in buffer");
			
			byte[] a = new byte[4];
			byte[] b = new byte[4]; 
			// System.out.println("-OP-");
			
			for(int i = 1; i <= 4; i++) {				
			
				a[4 - i] = s.queue.poll().byteValue();
				// System.out.println((int)v[i] & 0xff);
			}
			
			for(int i = 1; i <= 4; i++) {				
			
				b[4 - i] = s.queue.poll().byteValue();
				// System.out.println((int)v[i] & 0xff);
			}
			
			s.a = java.nio.ByteBuffer.wrap(a).getInt();
			s.b = java.nio.ByteBuffer.wrap(b).getInt();

			return true;
		}
		
	    throw e;
    }
    
    private static String peekString(java.util.Queue<Byte> q) {
    	return "size: " + q.size() + " char: " + (char)q.peek().intValue();
    }
    
    static final class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
        	
        	if(event.isRXCHAR()) {
            
        		try {
        			serialData.buffer = serialPort.readBytes();
        			
        			for(int i = 0; i < serialData.buffer.length; i++) {
        				serialData.queue.add(serialData.buffer[i]);
        				serialData.bytesReceived++;
                		// System.out.println(i + ": " + ((int)serialData.buffer[i] & 0xff) + " "  + (char)serialData.buffer[i]);
        			};
        		
        		} catch (SerialPortException spe) {
        			System.err.println(spe); 
        		}
            }
        }
    }
	
    public static void print(java.util.Collection<?> c) {
    	
    	c.stream().forEach( (o)-> System.out.println(o.toString()) );
    }
}