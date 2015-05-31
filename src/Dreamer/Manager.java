package Dreamer;

import java.util.HashSet;
import java.io.Serializable;

import Dreamer.interfaces.*;

public class Manager {
	
	protected static HashSet<Serializable> masterList = new HashSet<>(2000);
	protected static HashSet<Drawable> activeDrawingSet = new HashSet<>(2000);
	
	static PerformanceMonitor performance = new PerformanceMonitor("drawActive");
	static boolean debug = false, drawing = false, trackview = false;
	static int count = 0;
	static java.util.Collection<Manageable> emptyList = new java.util.ArrayList<>();
	
	public static void add(Object o) {
	
		Updater.tryAdd(o);
		Collidable.tryAdd(o);
		masterList.add((Serializable)o);
	}
	
	public static void remove(Object o) {
	
		Updater.tryRemove(o);
		Collidable.tryRemove(o);
		masterList.remove((Serializable)o);
	}
	
	static void activateVisible() {
		
		for (Object o: masterList)
			
			if(o instanceof Drawable)
			
				if (((Drawable)o).isVisible())
					activeDrawingSet.add((Drawable)o);
	}

	static void printActive() {
		
		System.out.println("ACTIVE ELEMENTS");
		
		activeDrawingSet
			.stream()
			.forEach( (e)-> System.out.println(e.toString()) );
	}

	static void drawActive() {

		drawing = true;
		count = 0;
		
		performance.clear();
		performance.start();
		
		for (Drawable d : Background.background) {
			
			count++;
			Light.light(d);
			d.draw();
			performance.mark(count + "," + d.toString());
		}

		for (Drawable d : activeDrawingSet) {
			
			count++;
			Light.light(d);
			d.draw();
			performance.mark(count + "," + d.toString());
		}

		Face.drawFaces();
		performance.mark("Faces");

		for (Drawable d : Foreground.foreground) {
			
			count++;
			d.draw();
			performance.mark(count + "," + d.toString());
		}

		performance.sort();
		drawing = false;
	}

	static void clearAll() {

		masterList.clear();
		activeDrawingSet.clear();
		Updater.clear();
		Collidable.clear();
		Background.background.clear();
		Foreground.foreground.clear();
		Light.clearAll();
	}

	static void clearActive() {

		activeDrawingSet.clear();
	}
}
