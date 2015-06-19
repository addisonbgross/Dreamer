package serial;

import jssc.*;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

final class SerialData {
    
    byte[] buffer;
    ConcurrentLinkedQueue<Byte> queue = new ConcurrentLinkedQueue<>();
    int 
    	lastInt, bytesReceived, bytesSent;
}

public final class RX {   
	
    static List<String> ports;
    static SerialPort serialPort;
    static SerialData serialData = new SerialData(); 

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
    
    public static int tryNextInt() throws Exception {
    	
    	return getNextInt(serialData);
    }
    
    private static int getNextInt(SerialData s) throws Exception {

    	Exception e = new Exception() {
    		
			private static final long serialVersionUID = 3758617126125859927L;

			public String toString() {
    			return "RX.java NOT IMPLEMENTED";
    		}
		};
    	
		String temp = "", two = "";
		boolean yes = false;
	
		while( !s.queue.poll().equals((byte)'#') && !s.queue.isEmpty() );
		
		/*
		while(!s.queue.isEmpty()) {
			System.out.println(s.queue.poll());
		}
		*/
		//System.out.println("GOOD? " + new Byte((byte) 255).byteValue());
		
		if(s.queue.size() >= 4) {
			
			int output = 0;
			int v = 0;
			System.out.println("-OP-");
			
			v += 0x1 *((int)(s.queue.poll().byteValue()) & 0xff);
			v += 0x100 * ((int)(s.queue.poll().byteValue()) & 0xff);
			v += 0x10000 * ((int)(s.queue.poll().byteValue()) & 0xff);
			v += 0x1000000 * ((int)(s.queue.poll().byteValue()) & 0xff);
			
			System.out.println(v);
			
			/*
			output += v;
			System.out.println(output);
			v = (int)s.queue.poll().byteValue() & 0xff;
			output += v * 0x100;
			System.out.println(output);
			v = (int)s.queue.poll().byteValue() & 0xff;
			output += v * 0x10000;
			System.out.println(output);
			v = (int)s.queue.poll().byteValue() & 0xff;
			output += v * 0x1000000;
			System.out.println(output);
			return output;
			*/	
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