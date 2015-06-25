package io;

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

public final class Serial {   
	
    static List<String> ports;
    static SerialPort serialPort;
    public static SerialData serialData = new SerialData(); 

    public static void begin(int speed) {

    	ports = Arrays.asList(SerialPortList.getPortNames());
    	
    	if(ports.size() > 0) {
    	
    		print(ports);
    	
    		serialPort = new SerialPort(ports.get(0)); 
        
	    	try {
	    		
	            serialPort.openPort();
	            serialPort.setParams(speed, 8, 1, 0);
	            int mask = SerialPort.MASK_RXCHAR;
	            serialPort.setEventsMask(mask);
	            serialPort.addEventListener(new SerialPortReader());
	            // serialPort.writeString("BLARGH");
	        
	    	} catch (SerialPortException spe) { System.err.println(spe); }
    	} else {
    		System.out.println("NO SERIAL PORTS AVAILABLE");;
    	}
    }
    
    public static void end() {
    	
    	try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static boolean find(String target) {
    	
    	/*
    	 * TODO Test this shit
    	 */
    	
    	char[] targetArray = target.toCharArray();
    	int size = targetArray.length;
    	int charsFound = 0;
    	
    	if(Serial.available() >= size)
    		
    		while(charsFound < size && Serial.available() != 0) {
    	
    			try {
					byte b[] = serialPort.readBytes(1);
					if((char)b[0] == targetArray[charsFound])
						charsFound++;
				} catch (SerialPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
    		}
    	
    		if(charsFound == size)
    			return true;
    		
    	return false;
    }
    
    // fulfilling Arduino Serial API
    public static int available() {
    	
    	try {
			return serialPort.getInputBufferBytesCount();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return -1;
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