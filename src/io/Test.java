package io;
import static io.MESSAGE_TYPE.*;
import static io.STATUS.*;

import java.nio.ByteBuffer;

public class Test implements Runnable {
	
	static int shared = 0;
	static int numberOfThreads = 0;
	int id = 0;
	Thread thread;
	
	public static void main(String[] argv) {
		
		Serial.begin(115200);
		Serial.echo = false;
		Attribute test = new Attribute(1, "some data", UINT32_T);
		Attribute test2 = new Attribute(2, "thing to set", FLOAT32_T);
		Attribute test3 = new Attribute(2, "float reading", FLOAT32_T);
		
		boolean b = true;
		try {
			Thread.sleep(2000); // Arduino programming delay
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(b == true) {
			
			/*
			serialize(0.5f, test2.data);
			Serial.set(test2);
			*/
			
			Serial.get(test);
			Serial.get(test3);
			
			for(int i = 0; i < 10; i++) {
				
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				if(test.newDataAvailable())
					try {
						System.out.println(test.name + " " +  test.data.getInt());
						test.newDataAvailable(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
	
				if(test3.newDataAvailable())
					try {
						System.out.println(test3.name + " " + test3.data.getFloat());
						test3.newDataAvailable(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		};
	}
	
	private static void serialize(float f, ByteBuffer data) {
		data.clear();
		data.putFloat(f);
	}

	public Test() {
		id = numberOfThreads;
		numberOfThreads++;
	}
	
	
	public void start() {
		
		thread = new Thread(this);
		thread.start();
		try {
			Thread.sleep(1000);
			//thread.join();
			//Thread.currentThread().interrupt();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread.interrupt();
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		/*
		 * Does this ever get called ever?
		 */
		
		numberOfThreads--;
		System.out.println(numberOfThreads + " THREADS LEFT RUNNING");
	};

	@Override
	public void run() {
		
		int attempts = 0;
		Serial.begin(115200);
		
		while(attempts++ < 1) {
			
			shared = id;
			System.out.println("RUNNIN THRED " + id + " ATTEMPT " + attempts);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Serial.available() > 0) {
				int i = Serial.read();
				System.out.println(i);
			}
		}
		
		return;
	}
}
