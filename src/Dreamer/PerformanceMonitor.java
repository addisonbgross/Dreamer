package Dreamer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;

public final class PerformanceMonitor {

	private class Record implements Comparable<Record> {

		Record(String s, Long l, Long d) {
			content = s;
			time = l;
			delta = d;
		};

		String content;
		Long time, delta;

		@Override
		public String toString() {
			return (float)100 * delta / global.delta + "," + time + "," + content + "\n";
		}

		@Override
		public int compareTo(Record o) {
			return Double.compare(o.delta, delta);
		}
	}

	private static PerformanceMonitor global = new PerformanceMonitor("global");
	private static ArrayList<PerformanceMonitor> children = new ArrayList<>();
	static int numberOfCollisions = 0;
	static BufferedWriter logWriter = null;
	{
		try {
			logWriter = new BufferedWriter(new FileWriter("dreamerLog.txt",
					false));
			logWriter.write("debugging log\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long delta;
	private ArrayList<Record> history = new ArrayList<>();
	@SuppressWarnings("unused")
	private String label = "";

	public PerformanceMonitor(String s) {
		label = s;
	}

	public static long getTime() {

		return java.lang.System.nanoTime();
	}

	public static void addMonitor(PerformanceMonitor... pms) {

		for (PerformanceMonitor pm : pms) {
			children.add(pm);
		}
	}

	public void start() {
		history.add(new Record("start", getTime(), 0L));		
	}

	public void mark(String s) {
		delta = getTime() - history.get(history.size() - 1).time;
		history.add(new Record(s, getTime(), delta));		
	}
	
	public void clear() {
		history.clear();
	}

	public void stop() {
		delta = getTime() - history.get(0).time;		
	}

	public void log() {
		for (Record r : history) {
			log(r.toString());
		}	
	}
	
	public void log(String s) {
		try {
			logWriter.write(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static int numberTotal() {
		return Element.masterList.size();
	}

	static int numberYRangeSets() {
		return Collidable.yRange.size();
	}

	static int numberXRangeSets() {
		return Collidable.xRange.size();
	}

	static int numberActive() {
		return Element.activeSet.size();
	}

	static public PerformanceMonitor getGlobal() {
		return global;
	}

	static void displayInfo() {

		Drawer.setColor(Library.defaultFontColor);
		Drawer.drawString(numberActive()
				+ " ACTIVE ELEMENTS, " + numberXRangeSets()
				+ " SETS IN X, " + numberYRangeSets()
				+ " SETS IN Y", 20, 20);
		Drawer.drawString(numberTotal() + " TOTAL ELEMENTS",
				20, 40);
		Drawer.drawString(numberOfCollisions + " COLLISIONS CHECKED", 20, 60);
	}

	static void displayMem() {

		Drawer.setColor(Library.defaultFontColor);
		Runtime runtime = Runtime.getRuntime();
		NumberFormat format = NumberFormat.getInstance();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		Drawer.drawString("free memory: " + format.format(freeMemory / 1024),
				20, 80);
		Drawer.drawString(
				"allocated memory: " + format.format(allocatedMemory / 1024),
				20, 100);
		Drawer.drawString("max memory: " + format.format(maxMemory / 1024), 20,
				120);
		Drawer.drawString(
				"total free memory: "
						+ format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024),
				20, 140);
	}

	public void sort() {
		java.util.Collections.sort(history);
	}
}