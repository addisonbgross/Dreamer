package serial;

import jssc.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Arrays;

enum MESSAGE_TYPE {
	
	UINT32_T(4), 
    FLOAT32_T(4), 
    CHAR(1);

    private int enumVal;

    MESSAGE_TYPE(int value) {
        this.enumVal = value;
    }

    public int size() {
        return enumVal;
    }
}

final class Attribute {
	
	Attribute(int handle, String attributeName, MESSAGE_TYPE type) {
		
		name = attributeName;
		reference = handle;
		messageType = type;
	}
	
	String name;
	MESSAGE_TYPE messageType;
	int reference;
	ByteBuffer data;
}

public final class RX {   
	
    static List<String> ports;
    static SerialPort serialPort;
    public static SerialData serialData = new SerialData(); 

    public static void go() {

    	ports = Arrays.asList(SerialPortList.getPortNames());
    	
    	if(ports.size() > 0) {
    	
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
    
    	Attribute attribute = new Attribute(0, "TEST", MESSAGE_TYPE.UINT32_T);
    	loadData(serialData, attribute);
    	return attribute.data.getInt();
    }
    
    public static Attribute nextAttribute;
    
    public static void get(Attribute a) {
    	
    	// receiveQueue.add(a);
    	nextAttribute = a;
    	
    	try {
			serialPort.writeString('g' + "" + (char)a.reference);
		} catch (SerialPortException e) {
			
			e.printStackTrace();
		}
	}
    
    private static boolean loadData(SerialData s, Attribute a) throws Exception {
		
    	int size = a.messageType.size();
		if(s.queue.size() >= size) {
			
			byte[] temp = new byte[size];
			
			for(int i = 1; i <= size; i++) {				
			
				temp[size - i] = s.queue.poll().byteValue();
			}

			a.data = java.nio.ByteBuffer.wrap(temp);
			return true;
		}
		
	    return false;
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