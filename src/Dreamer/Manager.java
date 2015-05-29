package Dreamer;

import java.util.HashSet;

public class Manager {
	
	protected static HashSet<Element> masterList = new HashSet<>(2000);
	protected static HashSet<Element> activeSet = new HashSet<>(2000);
	
	static PerformanceMonitor performance = new PerformanceMonitor("drawActive");
	static boolean debug = false, drawing = false, trackview = false;
	static int count = 0;
	
	public static void add(Object o) {
	
		Updater.tryAdd(o);
		Collidable.tryAdd(o);
		masterList.add((Element)o);
	}
	
	public static void remove(Object o) {
	
		Updater.tryRemove(o);
		Collidable.tryAdd(o);
		masterList.remove((Element)o);
	}
	
	static void activateVisible() {
		
		for (Element e : masterList)
			if (e.isVisible())
				activeSet.add(e);
	}

	static void printActive() {
		
		System.out.println("ACTIVE ELEMENTS");
		
		for (Element e : activeSet) {
			e.print();
		}
	}

	static void drawActive() {

		drawing = true;
		count = 0;
		
		performance.clear();
		performance.start();
		
		for (Element e : Background.background) {
			
			count++;
			Light.light(e);
			e.draw();
			performance.mark(count + "," + e.toString());
		}

		for (Element o : activeSet) {
			
			count++;
			Light.light(o);
			o.draw();
			performance.mark(count + "," + o.toString());
		}

		Face.drawFaces();
		performance.mark("Faces");

		for (Element e : Foreground.foreground) {
			
			count++;
			e.draw();
			performance.mark(count + "," + e.toString());
		}

		performance.sort();
		drawing = false;
	}

	static void clearAll() {

		masterList.clear();
		activeSet.clear();
		Updater.clear();
		Collidable.clear();
		Background.background.clear();
		Foreground.foreground.clear();
		Light.clearAll();
	}

	static void clearActive() {

		activeSet.clear();
	}

	static void printAll() {
		for(Element e: masterList) {
			e.print();
		}
	}
}
