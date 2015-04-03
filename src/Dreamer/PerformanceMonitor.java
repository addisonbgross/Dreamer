package Dreamer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

public final class PerformanceMonitor {	

	private class Record {
		Record(String s, Long l) { content = s; time = l; };
		String content;
		Long time;
		
		@Override public String toString() {
			return time + " : " + content;
		}
	}
	
	private static PerformanceMonitor global = new PerformanceMonitor("global");
	private static ArrayList<PerformanceMonitor> children = new ArrayList<>();
	static int numberOfCollisions = 0;
	static BufferedWriter logWriter = null;
	
	private long delta;
	private ArrayList<Record> history = new ArrayList<>();
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
	
		return java.lang.System.nanoTime();
	}
	
	public static void addMonitor(PerformanceMonitor... pms) {
	
		for(PerformanceMonitor pm: pms) {
			children.add(pm);
		}
	}
	
	static public void printAll() {
		
		global.stop();
		global.start();
		/*
			System.out.println(
					"frequency: " +
					(double)(1000000000 / global.delta) +
					" Hz"
					);
		*/
		for(PerformanceMonitor pm: children) {
			pm.print();
		}
	}
	
	public PerformanceMonitor start() { 
		history.add(new Record("start", getTime())); 
		return this;
	}
	
	public PerformanceMonitor mark(String s) { 
		history.add(new Record(s, getTime())); 
		return this;
	}
	
	public PerformanceMonitor stop() { 
		delta = getTime() - history.get(0).time; 
		return this;
	}
	
	public PerformanceMonitor print() { 
		try {
			
			logWriter.write(
					label + 
					": " +
					String.format("%.1f", (double) 100 * delta / global.delta)
					)
					;
			
			for(Record r: history) {logWriter.write(r.toString()); }
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return this;
	}
	
	static int numberTotal() {return Element.masterList.size();}

	static int numberYRangeSets() {return Collidable.yRange.size();}

	static int numberXRangeSets() {return Collidable.xRange.size();}

	static int numberActive() {return Element.activeSet.size();}

	static public PerformanceMonitor getGlobal() { return global; }
	
	static void displayInfo() {
		
		Drawer.setColor(Library.defaultFontColor);
		Drawer.drawString(
				PerformanceMonitor.numberActive()+" ACTIVE ELEMENTS, "
				+PerformanceMonitor.numberXRangeSets()+" SETS IN X, "
				+PerformanceMonitor.numberYRangeSets()+" SETS IN Y",
				20, 
				20
		);
		Drawer.drawString(PerformanceMonitor.numberTotal()+" TOTAL ELEMENTS", 20, 40);
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
