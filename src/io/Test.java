package io;

public class Test implements Runnable {
	
	static int shared = 0;
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
		try {
			Thread.sleep(1000);
			//thread.join();
			//Thread.currentThread().interrupt();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		while(attempts < 500) {
			
			shared = id;
			System.out.println("RUNNIN THRED " + id + " ATTEMPT " + attempts);
			try {
				Thread.sleep(100);
				attempts++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Serial.available() > 0);
		}
		
		System.exit(0);
		
		return;
	}
}
