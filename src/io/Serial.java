package io;

import jssc.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

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

enum STATUS {
		
	WAITING_GET,
	WAITING_SET,
	IDLING 
}

public final class Serial {   
	
	static boolean echo = false;
    static List<String> ports;
    static SerialPort serialPort;
    public static SerialData serialData = new SerialData(); 
    static ConcurrentLinkedQueue<Attribute> attributeQueue = new ConcurrentLinkedQueue<>();

    public static void begin(int speed) {

    	ports = Arrays.asList(SerialPortList.getPortNames());
    	
    	if(ports.size() > 0) {
    	
    		print(ports);
    	
    		serialPort = new SerialPort(ports.get(0)); 
    		boolean opened = false;
    		
    		while(!opened)
		    	try {
		    		opened = serialPort.openPort();
		            serialPort.setParams(speed, 8, 1, 0);
		            int mask = SerialPort.MASK_RXCHAR;
		            serialPort.setEventsMask(mask);
		            serialPort.addEventListener(new SerialPortReader());
		            // serialPort.writeString("BLARGH");
		        
		    	} catch (SerialPortException spe) { 
		    		System.err.println(spe); 
		    	}
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
    	
    	if(Serial.available() >= size) {
    		
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
    	}
    		
    	return false;
    }
    
    /*
     * returns -1 for error 
     */
    public static int available() {
    	
    	try {
			return serialPort.getInputBufferBytesCount();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return -1;
    }
    
    /*
     * returns -1 for error 
     */
    public static int read() {
    	try {
			return (int)(serialPort.readBytes(1)[0]);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return -1;
    }
    
    public static int readBytes(byte[] buffer, int length) {
    	try {
			buffer = serialPort.readBytes(length);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return buffer.length;
    }
    
    public static int tryNextInt() throws Exception {
    
    	Attribute attribute = new Attribute(0, "TEST", MESSAGE_TYPE.UINT32_T);
    	loadData(serialData, attribute);
    	return attribute.data.getInt();
    }

    public static void get(Attribute a) {
    	
    	attributeQueue.add(a);
    	
    	try {
			serialPort.writeByte((byte)'g');
			serialPort.writeByte((byte)a.reference);
			// System.out.println("getting " + (byte)a.reference);
		} catch (SerialPortException e) {
			
			e.printStackTrace();
		}
	}
    
    static boolean loadData(SerialData s, Attribute a) throws Exception {
		
    	int size = a.messageType.size();
		if(s.queue.size() >= size) {
			
			byte[] temp = new byte[size];
			
			for(int i = 1; i <= size; i++) {				
			
				temp[size - i] = s.queue.poll().byteValue();
			}

			a.data = ByteBuffer.wrap(temp);
			a.newDataAvailable(true);
			return true;
		}

	    return false;
    }
    
    static final class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
        	
        	if(event.isRXCHAR()) {
        		
        		while(Serial.available() > 0) {
        			
        			byte b = (byte)Serial.read();
        			if(echo) System.out.print((char)b);
        			else
        				serialData.queue.add(b);
        		}
        		
        		if(!attributeQueue.isEmpty()) {
        			
        			Attribute attribute = attributeQueue.peek();
        			
        			try {
        				if(loadData(serialData, attribute)) {
        					attributeQueue.poll();
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
            }
        }
    }
    
    public static void set(Attribute a) {
    	
    	write((byte)a.reference);
    	
    	for(int i = 0; i < a.messageType.size(); i++) {
    		write(a.data.array()[i]);
    	}
    }
	
    public static void print(java.util.Collection<?> c) {
    	
    	c.stream().forEach( (o)-> System.out.println(o.toString()) );
    }

	public static void write(byte b) {
		try {
			serialPort.writeByte(b);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void write(String s) {
		try {
			serialPort.writeString(s);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}