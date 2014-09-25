package Dreamer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Camera {
	
	public enum Function {
		ORIGINAL, NEW
	}
	public static Function mode = Function.ORIGINAL;
	private static float angle = 0f;
	private static Rectangle prescaledScene  = new Rectangle(
			-Constants.screenWidth / 2, 
			-Constants.screenHeight / 2, 
			Constants.screenWidth, 
			Constants.screenHeight);
	private static Rectangle scene = prescaledScene;
	private static float centerX, centerY, centerZ = 2000;
	private static float scale = 1;
	static float focalLength = 2000;
	private static Element target;
	static boolean zoom = false;
	Matrix4f projectionMatrix = new Matrix4f();
	
	static void draw(Graphics g)
	{
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
	}
	static void update() {
		if(target != null) {
			focus(target);
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
	 * @param e element to focus on
	 * @return this camera(why? no good reason)
	 */
	static void focus(Element e) {	
		if(!zoom) {
			try {
				ClassFocus cf = (ClassFocus)e;
				if ((cf.maxDistance + Constants.VIEWMARGIN) > Constants.screenHeight)
					setScale(Constants.screenHeight / (cf.maxDistance + Constants.VIEWMARGIN));
				else 
					setScale(1);
			} catch(ClassCastException cce) {
				//not a valid classfocus, normal scaling in effect
				setScale(1);
			}
			try {
				switch(mode) {
				case NEW:
					target = e;
					angle = -(float)Math.atan2(centerX - e.getX(), centerZ - e.getZ());
					centerY = e.getY();
					break; 
				default: 
					target = e;
					centerX = e.getX();
					centerY = e.getY();
					break;
				}
			} catch(NullPointerException n) {
				System.err.println("Camera not focused before updating!");
			}
		}
	}
	//nudge to adjust camera position
	static void nudge(float x, float y, float z) {
		centerX += x;
		centerY += y;
		centerZ += z;
	}
	//getters
	static float getMinX() {return (scene.getMinX() / scale) + centerX;}
	static float getMaxX() {return (scene.getMaxX() / scale) + centerX;}
	//TODO document what is going on here
	//since the screen is drawn flipped in the y-dir all kinds of things do not make logical sense
	//maybe the draw methods could be made more transparent?
	static float getMinY() {return (scene.getMinY() / scale) + centerY;}
	static float getMaxY() {return (scene.getMaxY() / scale) + centerY;}
	static float getWidth() {return scene.getWidth() / scale;}
	static float getHeight() {return scene.getHeight() / scale;}
	static Vector2f getPositionVector() {return new Vector2f(centerX, centerY);}
	static float getCenterX() {return centerX;}
	static float getCenterY() {return centerY;}
	static float getCenterZ() {return centerZ;}
	static float getScale() {return scale;}
	//setters
	static void setScale(float s) {if(s != scale) {scale = s;}}
	static Vector4f translateMouse(int x, int y) {
		return new Vector4f(
				((float)x / Constants.screenWidth * getWidth()) + getMinX(),
				((float)y / Constants.screenHeight * getHeight()) + getMinY(),
				0,
				1
				);
	}
	//this is the master translate method
	static Vector3f translate(float x, float y, float z) {
		Vector4f v = new Vector4f(x - getCenterX(), y - getCenterY(), z - getCenterZ(), 1);
		float distance = v.length();
		switch(mode) {
			case NEW:
				if(Keyboard.isKeyDown(Keyboard.KEY_RBRACKET))
					angle += 0.000002f;
				if(Keyboard.isKeyDown(Keyboard.KEY_LBRACKET))
					angle -= 0.000002f;
				angle = angle % (float)(2 * Math.PI);
				Vector4f rotated = Vector.rotate(0, 1, 0, v, angle);
				z = Math.abs(focalLength / rotated.z);
				x = rotated.x * z;
				y = rotated.y * z;
				break; 
			default: 
				z = Math.abs(focalLength / v.z);
				x = v.x * z;
				y = v.y * z;
				break;
		}
		return new Vector3f(			
			x + Constants.screenWidth / 2,
			-y + Constants.screenHeight / 2,
			Math.min(1, Math.max(1 - z / 10000, 0))
			);
	}
static Vector3f translate(Vector3f v) {
		return translate(v.x, v.y, v.z);
	}
	static Vector3f translate(Vector4f v) {
		return translate(v.x, v.y, v.z);
	}
	static float zFunction(float z) {
		return  (float)Math.pow(2, -z / focalLength);
		//return 1 - z / getFocalLength();
	}
	public static void print() {
		String s = "camera: ";
		s = s.concat("minX: "+getMinX());
		s = s.concat(" minY: "+getMinY());
		s = s.concat(" maxX: "+getMaxX());
		s = s.concat(" maxY: "+getMaxY());
		System.out.println(s);
	}

}