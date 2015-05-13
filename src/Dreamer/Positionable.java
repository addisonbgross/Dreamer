package Dreamer;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.geom.Vector2f;

public class Positionable extends Element {
	
	private static final long serialVersionUID = -7005269360412558578L;
	// each subclass's constructor should set x, y, width, height, and depth
	// TODO sort out positioning once and for all... confusing mix of variables
	Vector3f position = new Vector3f();
	private float width, height, depth = 0;
	protected boolean mutable = true;
	
	@Override
	public String toString() {

		String s = getClass().toString() + "@";
		s = s.concat("(" + (int) position.x);
		s = s.concat(", " + (int) position.y + ") ");
		s = s.concat(" w " + (int) getWidth());
		s = s.concat(" h " + (int) getHeight());
		return s;
	}
	
	// getters and printing
		float getMinX() {return position.x;}
		float getMaxX() {return position.x + width;}
		float getMinY() {return position.y;}
		float getMaxY() {return position.y + height;}
		float getMinZ() {return position.z - depth / 2;}
		float getMaxZ() {return position.z + depth / 2;}

		float getX() {return position.x + (width / 2);}
		float getY() {return position.y + (height / 2);}
		float getZ() {return position.z;}
		float getWidth() {return width;}
		float getHeight() {return height;}
		float getDepth() {return depth;}
		
		float findDistanceTo(Positionable e) {
			return findDistanceTo(e.getX(), e.getY(), e.getZ());
		}

		float findDistanceTo(float x, float y, float z) {
			float dX = this.getX() - x;
			float dY = this.getY() - y;
			float dZ = this.getZ() - z;
			return (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)
					+ Math.pow(dZ, 2));
		}

		Vector3f getCenterBottom() {
			return new Vector3f(getMinX() + getWidth() / 2, getMinY(), getMinZ());
		}
		
		Vector3f getPosition3f() {
			return new Vector3f(getX(), getY(), getZ());
		}
		
		void setDimensions(float w, float h, float d) {
			width = w;
			height = h;
			depth = d;
		}

		public void setPosition(Vector3f v) {
			setPosition(v.x, v.y, v.z);
		}

		public void setPosition(float x, float y, float z) {
			this.position.set(x, y, z);
		}
		
		void setCenterBottom(float x, float y) {
			setMinX(x - getWidth() / 2);
			setMinY(y);
		}
		
		void setCenterBottom(Vector2f v) {
			setCenterBottom(v.x, v.y);
		}

		void setCenter(float x, float y) {
			this.position.x = x - width / 2;
			this.position.y = y - height / 2;
		}

		void setCenterX(float x) {
			this.position.x = x - width / 2;
		}

		void setCenterY(float y) {
			this.position.y = y - width / 2;
		}

		void setMinX(float x) {
			this.position.x = x;
		}

		void setMinY(float y) {
			this.position.y = y;
		}

		void setMaxX(float x) {
			this.width = x - getMinX();
		}

		void setMaxY(float y) {
			this.height = y - getMinY();
		}

		void setZ(float z) {
			this.position.z = z;
		}

		void setHeight(float height) {
			this.height = height;
		}

		void setWidth(float width) {
			this.width = width;
		}

		void setDepth(float depth) {
			this.depth = depth;
		}
}

class Marker extends Positionable {
	
	private static final long serialVersionUID = 6813143029134144770L;
	
	String name;

	Marker(String s, float x, float y) {
		setPosition(x, y, 0);
		name = s;
	}

	@Override
	boolean isVisible() {
		return Element.debug || Element.trackview;
	}
	
	@Override
	void draw() {
		if (isVisible()) {
			Drawer.drawCursor(name + "@(" + (int) getMinX() + ", " + (int) getMinY()
					+ ")", getX(), getY(), getZ());
		}
	}
}
