package Dreamer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

//TODO make setPosition etc throw an exception
public class Element implements Serializable {
	private static final long serialVersionUID = 1384182428126670525L;
	//the masterList contains all Elements that have had their .add() method called
	//they are removed from this list by calling their .remove() method
	protected static HashSet<Element> masterList 
		= new HashSet<Element>(1000);
	protected static PriorityQueue<Element> activeSet 
		= new PriorityQueue<Element>(1000, new zComparator());
	
	//each Collidable is placed in these maps according to it's x and y position
	//from now on, only Collidables need to be on this 
	//this list must have the current positions of the Collidable updated manually,
	//which entails .remove()ing it before modifying it's position and calling
	//.add() to place it back on this list, as it's critical for efficient collisions
	private static ElementMap<Float, HashSet<Element>> xRange 
		= new ElementMap<Float, HashSet<Element>>();
	private static ElementMap<Float, HashSet<Element>> yRange 
		= new ElementMap<Float, HashSet<Element>>();
	//for cleanup of empty containers (cleaning them up on-the fly introduces
	//concurrent errors)
	private static HashSet<Float> deathSet = new HashSet<Float>();
	//the set which all Elements implementing Updateable get added to with .add()
	static Set<Updateable> updateSet = new HashSet<Updateable>(100);
	//to avoid concurrency errors and such
	static Set<Updateable> updateLaterSet = new HashSet<Updateable>(100);
	static Set<Updateable> updateBirthSet = new HashSet<Updateable>();
	static Set<Updateable> updateDeathSet = new HashSet<Updateable>();
	private static Set<Element> xSet = new HashSet<Element>();
	private static Set<Element> ySet = new HashSet<Element>();
	//backgrounds are drawn before other elements and still use a variety of 
	//methods to render
	public static ArrayList<Element> background = new ArrayList<Element>();
	
	//each subclass's constructor should set x, y, width, height, and depth
	//TODO sort out positioning once and for all... confusing mix of variables
	Vector4f position = new Vector4f();
	private float width, height, depth = 0;
	protected boolean mutable = true;
	//set to false to turn off info
	public static boolean debug = false;

	protected Element() {}
	
	/**add: maps multiple keys to each element for fast retrieval
	 * 
	 * add this element's minimum, intermediate, and extreme x and y values
	 * as references to it, as well as adding a reference by id to the 
	 * 
	 * if an object is farther away(z < 1) it will be added to the background list
	 * objects on this list will always be active but do NOT get updated like
	 * everything else during normal game flow
	 * 
	 * WARNING: if you update add() remove() MUST be changed as well
	 * leaving references to objects on this list will cause massive
	 * memory leaks
	 */
	void add()
	{
		if(this instanceof Updateable)
			updateBirthSet.add((Updateable)this);
		if(Collidable.class.isAssignableFrom(getClass())) {
			mutable = false;
			xRange.add(getMinX(), this);
			yRange.add(getMinY(), this);
			for(float offset = getWidth(); offset >= 0; offset -= Constants.COLLISIONINTERVAL)
				xRange.add(getMinX() + offset, this);
			for(float offset = getHeight(); offset >= 0; offset -= Constants.COLLISIONINTERVAL)
				yRange.add(getMinY() + offset, this);
		} 
		masterList.add(this);
	}
	void remove()
	{
		if(this instanceof Updateable)
			updateDeathSet.add((Updateable)this);
		if(Collidable.class.isAssignableFrom(getClass())){
			xRange.remove(getMinX(), this);
			yRange.remove(getMinY(), this);
			for(float offset = getWidth(); offset >= 0; offset -= Constants.COLLISIONINTERVAL)
				xRange.remove(getMinX() + offset, this);
			for(float offset = getHeight(); offset >= 0; offset -= Constants.COLLISIONINTERVAL)
				yRange.remove(getMinY() + offset, this);
		}
		masterList.remove(this);
		mutable = true;
	}
	boolean isVisible() {
		//TODO make this effective at filtering out unneeded Elements
		return true;
	}
	@Override
	public String toString() {
		
		String s = getClass().toString()+"@";
		s = s.concat("("+(int)position.x);
		s = s.concat(", "+(int)position.y+") ");
		s = s.concat(" w "+(int)width);
		s = s.concat(" h "+(int)height);
		return s;
	}
	void drawCursor(String s, float x, float y, float z, Graphics g) {
		Vector3f v = Camera.translate(x, y, z);
		g.setColor(Library.defaultFontColor);
		g.setFont(Library.defaultFont);
		g.drawString(s, v.x, v.y);
		g.drawLine(
				v.x - Constants.MARKERSIZE, 
				v.y,
				v.x + Constants.MARKERSIZE, 
				v.y
		);
		g.drawLine(
				v.x, 
				v.y - Constants.MARKERSIZE,
				v.x, 
				v.y + Constants.MARKERSIZE
		);
	}
	
	//only method subclasses must implement, even if just for debugging
	void draw(Graphics g) {}
	
	void drawShape(Shape s, Color c, Graphics g, boolean filled) {
		if(Line.class.equals(s.getClass())) {
			Line l = (Line)s;
			l = new Line(	
					Camera.translate(l.getX1(), l.getY1(), 0).x,
					Camera.translate(l.getX1(), l.getY1(), 0).y,
					Camera.translate(l.getX2(), l.getY2(), 0).x,
					Camera.translate(l.getX2(), l.getY2(), 0).y
					);
			g.setColor(c);
			g.draw(l);
		} else {
			Polygon p = new Polygon();
			int i = s.getPointCount() - 1;
			while(i >= 0) {
				p.addPoint(
						Camera.translate(s.getPoint(i)[0], s.getPoint(i)[1], position.z).x,
						Camera.translate(s.getPoint(i)[0], s.getPoint(i)[1], position.z).y
						);
				i--;
			}
			g.setColor(c);
			if(filled)
				g.fill(p);
			else
				g.draw(p);
		}
	}
	void drawShape(Shape s, Color c, Graphics g) {
		drawShape(s, c, g, true);
	}

	//getters and printing
	float getMinX() {return position.x;}
	float getMaxX() {return position.x + width;}
	float getMinY() {return position.y;}
	float getMaxY() {return position.y + height;}
	float getMinZ() {return position.z - depth / 2;}
	float getMaxZ() {return position.z + depth / 2;}
	float getZ() {return position.z;}
	Vector2f getCenterBottom() {
		return new Vector2f(getMinX() + getWidth() / 2, getMinY());
	}
	float getX() {return position.x + (width / 2);}
	float getY() {return position.y + (height / 2);}
	float getWidth() {return width;}
	float getHeight() {return height;}
	float getDepth() {return depth;}
	void print() {System.out.println(this.toString());}
	
	float findDistanceTo(Element e) {
		return findDistanceTo(e.getX(), e.getY(), e.getZ());
	}
	float findDistanceTo(float x, float y, float z) {
		float dX = this.getX() - x;
		float dY = this.getY() - y;
		float dZ = this.getZ() - z;
		return (float)Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)  + Math.pow(dZ, 2));
	}
	float findBottomDistanceTo(Element e) {
		return findDistanceTo(e.getX(), e.getMinY(), e.getZ());
	}
	//setters and mutators
	//all these require mutable = true
	//Elements should be removed before modification
	Vector3f getPosition3f() {
		return new Vector3f(getX(), getY(), getZ());
	}
	Vector4f getPosition4f() {
		return new Vector4f(getX(), getY(), getZ(), 1);
	}
	
	public boolean contains(String... strings) {
		for (String s: strings)
			if(!getClass().toString().toLowerCase().contains(s.toLowerCase())) 
					return false;
		return true;
	}
	void setPosition(Vector4f v)
	{
		setPosition(v.x, v.y, v.z);
	}
	void setDimensions(float w, float h, float d) {
		width = w; height = h; depth = d;
	}
	public void setPosition(float x, float y, float z)
	{
		if(!mutable) throw new NotMutableException();
		this.position.x = x; 
		this.position.y = y;
		this.position.z = z;
	}
	void setCenterBottom(Vector2f v) {
		setCenterBottom(v.x, v.y);
	}
	//pretty sure this routine is causing a java.util.ConcurrentModificationException exception when called from Ninja.class
	void setCenterBottom(float x, float y) {
		remove();
		setMinX(x - getWidth() / 2);
		setMinY(y);
		add();
	}
	void setCenter(float x, float y) 
	{
		if(!mutable) throw new NotMutableException();
		this.position.x = x - width / 2; 
		this.position.y = y - height / 2;
	}
	void setCenterX(float x) 
	{
		if(!mutable) throw new NotMutableException();
		this.position.x = x - width / 2;
	}
	void setCenterY(float y) 
	{
		if(!mutable) throw new NotMutableException();
		this.position.y = y - width / 2; 
	}
	void setMinX(float x) {
		if(!mutable) throw new NotMutableException();
		this.position.x = x;
	}
	void setMinY(float y) {
		if(!mutable) throw new NotMutableException();
		this.position.y = y;
	}
	void setMaxX(float x) {
		if(!mutable) throw new NotMutableException();
		this.width = x - getMinX();
	}
	void setMaxY(float y) {
		if(!mutable) throw new NotMutableException();
		this.height = y - getMinY();
	}
	void setZ(float z) {
		if(!mutable) throw new NotMutableException();
		this.position.z = z;
	}
	void setHeight(float height) {
		if(!mutable) throw new NotMutableException();
		this.height = height;
	}
	void setWidth(float width) {
		if(!mutable) throw new NotMutableException();
		this.width = width;
	}
	void setDepth(float depth) {
		if(!mutable) throw new NotMutableException();
		this.depth = depth;
	}
	/*
	 *this function adds Elements to be drawn to the activeSet 
	 *each element checks whether it isVisible() to determine if it is drawn 
	 * MODIFIES: static activeSet
	 */
	static void activateVisible()
	{	
		for (Element e : masterList)
			if (e.isVisible())
				activeSet.add(e);
	}
	/**getActiveWithin: returns the active set bound within a rectangle of Shape width and height
	 * @param <T>
	 * 
	 * @param s Shape x and y extremes
	 * @return Set of elements within a given bounds
	 */
	static Set<Element> getActiveWithin(Shape s)
	{	
		Set<Map.Entry<Float, HashSet<Element>>> temp 
			= new HashSet<Map.Entry<Float, HashSet<Element>>>();
		Set<Element> tempActive = new HashSet<Element>();
		
		//take all set of elements in the camera scene x range
		temp.addAll(xRange.subMap(s.getMinX(), true, s.getMaxX(), true).entrySet());
		
		//add each set together in xSet
		for(Map.Entry<Float, HashSet<Element>> entry : temp) 
		{
			xSet.addAll(entry.getValue());
		}
		
		temp.clear();
		
		//take all the elements in scene y range
		temp.addAll(yRange.subMap(s.getMinY(), true, s.getMaxY(), true).entrySet());
		
		//add them together in ySet
		for(Map.Entry<Float, HashSet<Element>> entry : temp) 
		{
			ySet.addAll(entry.getValue());
		}
		
		//if an element is in both x and y sets then draw it, make it active,
		//this has the effect of rendering and activating only the elements
		//that are within the camera scene boundaries
		for(Element o: xSet) 
		{
			if(ySet.contains(o))
			{
				tempActive.add(o);
			}
		}
		return tempActive;
	}
	static HashSet<Element> getMasterList() {
		return masterList;
	}
	static ElementMap<Float, HashSet<Element>> getXRange() {
		return xRange;
	}
	static ElementMap<Float, HashSet<Element>> getYRange() {
		return yRange;
	}
	public static ArrayList<Element> getBackground() {
		return background;
	}	
	public static void addBackground(Element e) {
		background.add(e);
	}
	
	static void printAll() 
	{	
		System.out.println("ALL ELEMENTS");
		System.out.println("BACKGROUND");
		for(Element e: background) e.print();
		printMasterList();
	}
	static void printMasterList() {
		System.out.println("MASTERLIST");
		for (Element e : masterList)
			e.print();
	}
	static void printActive() {	
		System.out.println("ACTIVE ELEMENTS");
		for(Element e: activeSet) 
		{
			e.print();
		}
	}
	static void drawAll(Graphics g){
		for(Element e: background) e.draw(g);
		for (Element e : masterList)
			e.draw(g);
	}
	static void drawActive(Graphics g){
		for(Element e: background) 
			e.draw(g);
		for(Element o: activeSet)
			o.draw(g);
		Face.drawFaces();
	}
	
	static int numberActive() {
		return activeSet.size();
	}	
	static int numberXRangeSets() {
		return xRange.size();
	}	
	static int numberYRangeSets() {
		return yRange.size();
	}	
	static int numberTotal() {
		return masterList.size();
	}	
	
	static void clearAll() {	
		updateSet.clear();
		updateBirthSet.clear();
		updateDeathSet.clear();
		masterList.clear();
		xRange.clear();
		yRange.clear();
		activeSet.clear();
		xSet.clear();
		ySet.clear();
		background.clear();
		Light.clearAll();
	}
	static void clearActive() {
		activeSet.clear();
		xSet.clear();
		ySet.clear();
	}
	/**
	 * goes through a set of all Elements implementing Updateable interface
	 * also cleans up the active lists of Elements 
	 */
	public static void updateAll() {

		
		updateSet.addAll(updateBirthSet);
		updateBirthSet.clear();
		
		Updateable z = null;
		try {
			for(Updateable e: updateSet) {
				if(Actor.class.isAssignableFrom(e.getClass()))
					e.update();
				else
					updateLaterSet.add(e);
				z = e;
			}
		} catch(java.util.ConcurrentModificationException e) {
			//TODO fix this exception
			//this is caused by Dreamer.Ninja adding and removing something,
			//probably a call to .remove() or .add() in update
			if(z != null) System.out.println(z.getClass().toString());
			e.printStackTrace();
		}
		for(Updateable e: updateLaterSet) {
			e.update();
		}
		updateSet.removeAll(updateDeathSet);
		updateDeathSet.clear();
		
		updateLaterSet.clear();
		
		deathSet.clear();
		
		for(Map.Entry<Float, HashSet<Element>> entry : xRange.entrySet()) {
			if(entry.getValue().isEmpty())
				deathSet.add(entry.getKey());
		}
		for(Float f: deathSet)
			xRange.remove(f);
		deathSet.clear();
		for(Map.Entry<Float, HashSet<Element>> entry : yRange.entrySet()) {
			if(entry.getValue().isEmpty())
				deathSet.add(entry.getKey());
		}
		for(Float f: deathSet)
			yRange.remove(f);
	}
}
/**ElementMap: allows a key to refer to a set of Element objects
 * and transparently allows addition and removal operations
 * 
 * @author Maxim
 *
 * @param <K> can be any key
 * @param <V> must be a HashSet<Element>
 */
class ElementMap<K, V> extends TreeMap<K, HashSet<Element>> {
	private static final long serialVersionUID = 186057469873355492L;
	
	boolean add(K key, Element value) 
	{	
		if(super.containsKey(key)) 
		{
			super.get(key).add(value);
		} 
		else 
		{
			HashSet<Element> a = new HashSet<Element>();
			a.add(value);
			super.put(key, a);
		}			
		return true;
	}
	boolean remove(K key, Element value) 
	{
		try 
		{
			return super.get(key).remove(value);
		}
		catch(Exception e) 
		{
			return false;
		}
	}
}

class Marker extends Element {
	String name;
	Marker(String s, float x, float y) 
	{
		setPosition(x, y, 0);
		name = s;	
	}
	@Override
	void draw(Graphics g) 
	{
		if(Element.debug)
		{
			drawCursor(name+"@("+(int)getMinX()+", "+(int)getMinY()+")", getX(), getY(), getZ(), g);
		}
	}
}
class PermanentLine extends Element {
	Line l;
	PermanentLine(Line l) {
		this.l = l;
		setMinX(l.getMinX());
		setMinY(l.getMinY());
		setWidth(l.getWidth());
		setWidth(l.getHeight());
	}
	@Override
	void draw(Graphics g) {
		if(Element.debug)
			drawShape(l, Library.defaultFontColor, g);
		else
			remove();
	}
}

class zComparator implements Comparator<Element> {
    @Override
    public int compare(Element a, Element b) {
    	return a.getZ() < b.getZ() ? 1 : (a.getZ() == b.getZ() ? 0 : -1);
    }
}
















