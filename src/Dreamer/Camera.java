package Dreamer;

import java.util.Stack;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Camera {
	
	private static Rectangle prescaledScene  = new Rectangle(
			-Constants.screenWidth / 2, 
			-Constants.screenHeight / 2, 
			Constants.screenWidth, 
			Constants.screenHeight);
	private static Rectangle scene = prescaledScene;
	private static Vector3f position = new Vector3f(0, 0, 2000);
	private static float scale = 1, focalLength = 2000;
	private static Positionable target;
	static boolean zoom = false;
	private static String nextMovement = "stop";
	private static int velocity = 0;
	private static Stack<Vector3f> positionStack = new Stack<>();
	
	static Vector4f rotated = new Vector4f();
	static Vector3f tempV3f = new Vector3f();
	static Vector3f translated = new Vector3f();
	
	static void draw(Graphics g){	
		
		try {
			//print out the camera info and focus box
			g.setColor(Library.defaultFontColor);
			scene.setCenterX(Constants.screenWidth / 2);
			scene.setCenterY(Constants.screenHeight / 2);
			g.draw(scene);
			scene.setCenterX(0);
			scene.setCenterY(0);
			g.setColor(Library.defaultFontColor);
			g.drawString("CAMTARGET "+target.toString(), 20, 180);
			g.drawString("CAMCENTER@("+(int)getCenterX()+", "+(int)getCenterY()+", "+scale+")", 20, 200);
			g.drawString("CAMSCENE ("+(int)getMinX()+", "+(int)getMinY()+", "+(int)getMaxX()+", "+(int)getMaxY()+")", 20, 220);
			g.setColor(Library.defaultFontColor);
		} catch(NullPointerException e) {
			//camera unfocused
		}
	}
	
	static void update() {
		
		switch (nextMovement) {
		
	        case	"zoom_in":
	    		nudge(0, 0, velocity);
	    		velocity++;
				break;
				
	        case	"zoom_out":
	    		nudge(0, 0, -velocity);
	    		velocity++;
				break;
				
	        case	"up":
				nudge(0, velocity, 0);
				velocity++;
				break;
				
	        case	"down":
				nudge(0, -velocity, 0);
				velocity++;
				break;
				
	        case	"left":
				nudge(-velocity, 0 ,0);
				velocity++;
				break;
				
	        case	"right":
				nudge(velocity, 0, 0);
				velocity++;
				break;
				
	        case "stop":
	        	velocity = 0;
	        	break;
		}
		
		if(target  !=  null) {
			focus(target);
			// TODO sort this out, some things are updated twice I imagine
			if(target instanceof Updateable)
				((Updateable) target).update();
		}
	}
	/**focuses the camera on a specific element
	 * 
	 * that element is casted to a ClassFocus with initial maxDistance 0
	 * if the cast is appropriate the maxDistance will be the maximum
	 * distance between the farthest apart objects of those classes and
	 * will be used to scale the camera
	 * 
	 * @param p element to focus on
	 * @return this camera(why? no good reason)
	 */
	static void focus(Positionable p) {	
		
		if(!zoom) {
			
			try {
				ClassFocus cf = (ClassFocus)p;
				if ((cf.maxDistance + Constants.VIEWMARGIN) > Constants.screenHeight)
					scale = Constants.screenHeight / (cf.maxDistance + Constants.VIEWMARGIN);
				else 
					scale = 1;
			} catch(ClassCastException cce) {
				//not a valid classFocus, normal scaling in effect
				scale = 1;
			}
			
			try {
					target = p;
					position.x = p.getX();
					position.y = p.getY();
			} catch(NullPointerException n) {
				System.err.println("Camera not focused before updating!");
			}
		}
	}
	static void focus(float x, float y, float z) {
		// target = null;
		position.set(x, y, z);
	}
	static void reset() {
		
		target = null;
		position.x = 0;
		position.y = 0;
		position.z = 2000;
	}
	//nudge to adjust camera position
	private static void nudge(float x, float y, float z) {
		position.x += x;
		position.y += y;
		position.z += z;
	}
	//getters
	static float getMinX() {return (scene.getMinX() / scale) + position.x;}
	static float getMaxX() {return (scene.getMaxX() / scale) + position.x;}
	//TODO document what is going on here
	//since the screen is drawn flipped in the y-dir all kinds of things do not make logical sense
	//maybe the draw methods could be made more transparent?
	static float getMinY() {return (scene.getMinY() / scale) + position.y;}
	static float getMaxY() {return (scene.getMaxY() / scale) + position.y;}
	static float getWidth() {return scene.getWidth() / scale;}
	static float getHeight() {return scene.getHeight() / scale;}

	static float getScale() {return scale;}
	
	static float getCenterX() {return position.x;}
	static float getCenterY() {return position.y;}
	static float getCenterZ() {return position.z;}

	static boolean isPointVisible(float x, float y, float z) {
		translate(x, y, z, tempV3f);
		if(tempV3f.x > 0)
			if(tempV3f.x < Constants.screenWidth)
				if(tempV3f.y > 0)
					if(tempV3f.y < Constants.screenHeight)
						return true;
		return false;
	}
	
	static Vector3f translateMouse(float x, float y, float f) {
		return new Vector3f(
				((float)x / Constants.screenWidth * getWidth()) + getMinX(),
				((float)y / Constants.screenHeight * getHeight()) + getMinY(),
				f
				);
	}
	//this is the master translate method, this vector should be used ASAP after returning
	static Vector3f translate(float x, float y, float z, Vector3f result) {
		
		z = Math.abs(focalLength / (z - getCenterZ()));
		x = (x - getCenterX()) * z;
		y = (y - getCenterY()) * z;
		
		result.set(			
				x + Constants.screenWidth / 2,
				-y + Constants.screenHeight / 2,
				Math.min(1, Math.max(1 - z / 10000, 0))
				);
		
		return result;
	}
	
	static Vector3f translate(float x, float y, float z) {
			return translate(x, y, z, translated);
	}
	
	static Vector3f translate(Vector3f vector3f, Vector3f result) {
		return translate(vector3f.x, vector3f.y, vector3f.z, result);
	}
	
	public static void print() {
		String s = "camera: ";
		s = s.concat("minX: "+getMinX());
		s = s.concat(" minY: "+getMinY());
		s = s.concat(" maxX: "+getMaxX());
		s = s.concat(" maxY: "+getMaxY());
		System.out.println(s);
	}
	
	public static void command(String s) {
		nextMovement = s;
	}
	public static void pushPosition() {
		positionStack.push(new Vector3f(position));
	}
	public static void popPosition() {
		position.set(positionStack.pop());
	}
}