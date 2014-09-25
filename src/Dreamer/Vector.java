package Dreamer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.geom.Vector2f;

public class Vector {
	static Vector3f a = null;
	static Vector3f b = null;
	static Vector3f c = null;
	static Vector3f d = null;
	static float x, y, z;
	static Matrix4f mIndentity = new Matrix4f(); 
	static Matrix4f mRotation = new Matrix4f();
	
	static Vector3f getMinimum(Vector3f[] points) {
		for(Vector3f v: points) {
			if(a == null) {
				x = v.x;
				y = v.y;
				z = v.z;
			} else {
				x = Math.min(x, v.x);
				y = Math.min(y, v.y);
				z = Math.min(z, v.z);
			}
		}
		return new Vector3f(x, y, z);
	}
	static Vector3f getMaximum(Vector3f[] points) {
		for(Vector3f v: points) {
			if(a == null) {
				x = v.x;
				y = v.y;
				z = v.z;
			} else {
				x = Math.max(x, v.x);
				y = Math.max(y, v.y);
				z = Math.max(z, v.z);
			}
		}
		return new Vector3f(x, y, z);
	}
	static Vector2f getMinimum(Vector2f[] points) {
		for(Vector2f v: points) {
			if(a == null) {
				x = v.x;
				y = v.y;
			} else {
				x = Math.min(x, v.x);
				y = Math.min(y, v.y);
			}
		}
		return new Vector2f(x, y);
	}
	static Vector2f getMaximum(Vector2f[] points) {
		for(Vector2f v: points) {
			if(a == null) {
				x = v.x;
				y = v.y;
			} else {
				x = Math.max(x, v.x);
				y = Math.max(y, v.y);
			}
		}
		return new Vector2f(x, y);
	}
	static Vector3f copy(Vector3f v) {
		return new Vector3f(v.x, v.y, v.z);
	}
	public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (float)Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
	}
	public static float getDistance(Vector4f v1, Vector4f v2) {
		return getDistance(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	public static float getDistance(Vector4f v1, Vector3f v2) {
		return getDistance(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	public static float getDistance(Vector3f v1, Vector3f v2) {
		return getDistance(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	public static float getManhattanDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (float)(Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2));
	}
	public static float getManhattanDistance(Vector4f v1, Vector4f v2) {
		return getManhattanDistance(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	public static float getManhattanDistance(Vector4f v1, Vector3f v2) {
		return getManhattanDistance(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	public static float getManhattanDistance(Vector3f v1, Vector3f v2) {
		return getManhattanDistance(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	public static Vector4f crossNormalized(Vector4f v1, Vector4f v2) {
		return new Vector4f(
				(v1.y * v2.z) - (v1.z * v2.y),
				- (v1.x * v2.z) + (v1.z * v2.x),
				(v1.x * v2.y) - (v1.y * v2.x),
				0
				).normalise(null);
	}
	public static Vector4f rotate(Vector3f rotationAxis, Vector4f v, float angle) {
		mIndentity.rotate(angle, rotationAxis, mRotation);
		return	Matrix4f.transform(mRotation, v, null);
	}
	public static Vector4f rotate(float x, float y, float z, Vector4f v, float angle) {
		return	rotate(new Vector3f(x, y, z), v, angle);
	}
}
