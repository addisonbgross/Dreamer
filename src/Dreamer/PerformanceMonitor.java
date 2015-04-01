package Dreamer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;

public final class PerformanceMonitor implements Updateable {
	
	
	private static PerformanceMonitor global = new PerformanceMonitor("global");
	private static ArrayList<PerformanceMonitor> children = new ArrayList<>();
	static int numberOfCollisions = 0;
	static BufferedWriter logWriter = null;
	
	private long start, delta;
	private String label = "";
	
	public PerformanceMonitor(String s) {
	
		label = s;
		
		try {
			logWriter = new BufferedWriter(new FileWriter("log.txt"));
			logWriter.write("debugging log\r\n");
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public static long getTime() {
	
		return java.lang.System.nanoTime();// / Sys.getTimerResolution();
	}
	
	public static void addMonitor(PerformanceMonitor... pms) {
	
		for(PerformanceMonitor pm: pms) {
			children.add(pm);
		}
	}
	
	static public void printAll() {
		
		global.stop();
		global.start();
		System.out.println(
				"frequency: " +
				(double)(1000000000 / global.delta) +
				" Hz"
				);
		
		for(PerformanceMonitor pm: children) {
			pm.print();
		}
	}
	
	public void start() { start = getTime(); }
	
	public void stop() { delta = getTime() - start; }
	
	public void print() { 
		
		System.out.println(
				label + 
				": " +
				String.format("%.1f", (double) 100 * delta / global.delta)
				)
				; 
	}
	
	static public PerformanceMonitor getGlobal() { return global; }
	
	static void displayInfo() {
		
		Drawer.setColor(Library.defaultFontColor);
		Drawer.drawString(
				Element.numberActive()+" ACTIVE ELEMENTS, "
				+Element.numberXRangeSets()+" SETS IN X, "
				+Element.numberYRangeSets()+" SETS IN Y",
				20, 
				20
		);
		Drawer.drawString(Element.numberTotal()+" TOTAL ELEMENTS", 20, 40);
		Drawer.drawString(numberOfCollisions + " COLLISIONS CHECKED", 20, 60);
	}
	
	static void displayMem() {
		
		Drawer.setColor(Library.defaultFontColor);
		Runtime runtime = Runtime.getRuntime();
		NumberFormat format = NumberFormat.getInstance();  
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();
	    Drawer.drawString("free memory: " + format.format(freeMemory / 1024), 20, 80);
	    Drawer.drawString("allocated memory: " + format.format(allocatedMemory / 1024), 20, 100);
	    Drawer.drawString("max memory: " + format.format(maxMemory / 1024),20, 120);
	    Drawer.drawString("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024), 20, 140);
	}
}