package io;

public class Test implements Runnable {
	
	static boolean flag = true;
	static int numberOfThreads = 0;
	int id = 0;
	Thread thread;
	
	public Test() {
		id = numberOfThreads;
		numberOfThreads++;
	}
	
	public void start() {
		
		thread = new Thread(this);
		thread.start();
	}
	
	protected void finalize() throws Throwable {
		numberOfThreads--;
	};

	@Override
	public void run() {
		
		Serial.begin();
		
		while(true) {
			// System.out.println("RUNNIN THRED " + id);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
