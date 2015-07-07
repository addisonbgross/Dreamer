package io;
import static io.MESSAGE_TYPE.*;
import static io.STATUS.*;

import java.nio.ByteBuffer;

import javax.xml.crypto.Data;

import org.lwjgl.util.vector.Vector3f;

import Dreamer.Drawer;

public class Test implements Runnable {
	
	static int shared = 0;
	static int numberOfThreads = 0;
	int id = 0;
	Thread thread;
	
	public static void main(String[] argv) {
		
		Serial.begin(115200);
		Serial.echo = false;
		Attribute x = new Attribute(1, "x axis", UINT32_T);
		Attribute y = new Attribute(2, "y axis", UINT32_T);
		Attribute z = new Attribute(3, "z axis", UINT32_T);
		Attribute a = new Attribute(4, "acceleration", FLOAT32_T);
		
		boolean b = true;
		try {
			Thread.sleep(2000); // Arduino programming delay
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Dreamer.Dreamer.go();
		Vector3f v = new Vector3f();
		Vector3f u = new Vector3f();
		
		while(b == true) {
			
			/*
			serialize(0.5f, test2.data);
			Serial.set(test2);
			*/
			
			Serial.get(x);
			Serial.get(y);
			Serial.get(z);
			
			float average = 0f;
			float remainder = 1 - average;
			v.set(average * v.x + remainder * x.asInt, 
					average * v.y + remainder * y.asInt, 
					average * v.z + remainder * z.asInt);
			u = v.normalise(u);
			float gConvert = 9.8f/69;
			String s = String.format("%6.2f %6.2f %6.2f %6.2f", u.x, u.y, u.z, v.length() * gConvert );
			System.out.println(s);
			// System.out.println(getString(x) + getString(y) + getString(z) + getString(a));
	
			try {
				Thread.sleep(2);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		};
	}
	
	private static String getString(Attribute a) {
		String s = "";
		if(a.messageType == UINT32_T) {
			s = String.format("%7d", a.asInteger());
		} else if(a.messageType == FLOAT32_T) {
			s = String.format("%7.2f", a.asFloating());
		} a.newDataAvailable(false);

		return s;
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
