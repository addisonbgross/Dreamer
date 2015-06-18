package Dreamer;

import interfaces.Updateable;

import java.util.ArrayList;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import enums.Status;

public class Animation2 extends Shape3d implements Updateable {
	
	private static final long serialVersionUID = -863958778224192478L;
	Face f;
	float sheetWidth, sheetHeight;
	int columns, rows, width, height;
	float speed;
	long frameCounter;
	boolean running = false, looping = true, pingpong = false, reverse = false;
	boolean firstUpdate = true;
	static final int FORWARDS = 1, BACKWARDS = -1;
	static final int LEFT = 1, RIGHT = -1;
	int direction = LEFT;
	ArrayList<Vector2f> indices = new ArrayList<Vector2f>();
	public int currentIndex = 0;
	long lastTime;
	{
		//vertices that define the corners of the animation
		for(int i = 0; i < 4; i++)
			vertices.add(new Vector3f());
	}
	
	Animation2(String s, int columns, int rows, int speed) {
		this(s, columns, rows, speed, 64, 64);
	}
	
	Animation2(String s, int columns, int rows, int speed, int w, int h) {
		
		super(0, 0, 0);
		width = w;
		height = h;
		this.speed = speed;
		this.columns = columns;
		this.rows = rows;
		firstUpdate = true;
		f = new Face(s, new Color(1.0f, 1.0f, 1.0f, 1.0f), 0, 1, 2, 3);
		f.setTexturePoints(0, 0, 1, 1);
		sheetWidth = textureStretch(Library.getImage(s).getWidth());
		sheetHeight = textureStretch(Library.getImage(s).getHeight());
		
		vertices.get(0).set(-width/2, -height/2, 0);
		vertices.get(1).set(-width/2, height/2, 0);
		vertices.get(2).set(width/2, height/2, 0);
		vertices.get(3).set(width/2, -height/2, 0);		
		
		selectRow(0);
		addFace(f);
	}
	
	public void update() {		
		
		try {

			if (running) {
				long now = getTime();
				long delta = now - lastTime;
				if (firstUpdate) {
					delta = 0;
					firstUpdate = false;
				}
				lastTime = now;
			  
				currentIndex += nextFrame(delta);
				currentIndex = currentIndex % indices.size();
			
				selectFrame(indices.get(currentIndex).x, indices.get(currentIndex).y);
		   }  else
			    selectFrame(indices.get(currentIndex).x, indices.get(currentIndex).y);
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	void start() { running = true; }
	
	void stop() { running = false; }
	
	void reset() {
		
		currentIndex = 0;
		firstUpdate = true;
	}
	
	void setDirection(Status dir) { direction = (dir == Status.LEFT)? 1 : -1; }
	
	void setLooping(boolean value) { looping = value; }
	
	void setReverse(boolean value) { reverse = value; }
	
	void setPingPong(boolean value) { pingpong = value; }
	
	void setSpeed(float rate) {
	
		if (rate >= 0)
			speed = rate;
	}
	
	void selectRow(int r) {
		
		indices.clear();
		
		for (int i = 0; i < columns; ++i)
			indices.add(new Vector2f(i, r));
	}
	
	int framesWide() { return columns; }
	
	Animation2 getRow() { return this; }
	
	void selectFrame(float x, float y) {
	
		if (direction == LEFT) {
			f.setTexturePoints(
					sheetWidth * x / columns, 
					sheetHeight * y / rows, 
					sheetWidth * (x + 1) / columns, 
					sheetHeight * (y + 1) / rows
					);
		} else {
			x++;
			f.setTexturePoints(
					sheetWidth * x  / columns, 
					sheetHeight * y / rows, 
					sheetWidth * (x - 1) / columns, 
					sheetHeight * (y + 1) / rows
					);
		}
	}
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	/**
	 * Check if we need to move to the next frame
	 * 
	 * @param delta The amount of time thats passed since last update
	 */
	int nextFrame(long delta) {
		
		int numFrames = 0;
		
		if (!running) {
			frameCounter = 0;
			return 0;
		}
		
		frameCounter += delta;
	
		if(frameCounter >= speed) {
			
			numFrames = (int) (frameCounter / speed);
			frameCounter = (int)(frameCounter % speed);
		
			if (!looping) {
			
				if (currentIndex + numFrames > 5) {
					numFrames = 0;
					reset();
				}
			}
		}
		
		return numFrames;
	}
}
