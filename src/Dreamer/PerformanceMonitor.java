package Dreamer;

import java.util.ArrayList;

public final class PerformanceMonitor implements Updateable {
	
	
	private static PerformanceMonitor global = new PerformanceMonitor("global");
	private static ArrayList<PerformanceMonitor> children = new ArrayList<>();
	
	private long start, delta;
	private String label = "";
	
	public PerformanceMonitor(String s) {
		label = s;
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
	
	static public PerformanceMonitor getGlobal() {
		return global;
	}
}
