package Dreamer;

import java.util.ArrayList;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;

public class Animation2 extends Shape3d implements Updateable {
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
	/**
	 * Animation2 preamble
	 */
	{
		//vertices that define the corners of the animation
		for(int i = 0; i < 4; i++)
			vertices.add(new Vector4f());
	}
	
	/**
	 * Create a new Animation2 slide using default dimensions of 64 x 64
	 * @param s  		name of texture sprite sheet
	 * @param columns 	num columns in sprite sheet
	 * @param rows		num rows in sprite sheet
	 * @param speed		framerate of animation
	 */
	Animation2(String s, int columns, int rows, int speed) {
		this(s, columns, rows, speed, 64, 64);
	}
	/**
	 * Create a new Animation2 slide
	 * @param s			name of texutre sprite sheet
	 * @param columns	num columns in sprite sheet
	 * @param rows		num rows in sprite sheet
	 * @param speed		frame rate of animation
	 * @param w			width of animation slide
	 * @param h			height of animation slide
	 */
	Animation2(String s, int columns, int rows, int speed, int w, int h) {
		super(0, 0, 0);
		width = w;
		height = h;
		this.speed = speed;
		this.columns = columns;
		this.rows = rows;
		firstUpdate = true;
		f = new Face(Library.getTexture(s), new Color(1.0f, 1.0f, 1.0f, 1.0f), 0, 1, 2, 3);
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
	/**
	 * Update this object per game iteration
	 */
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
	/**
	 * Start this animation
	 */
	void start() {
		running = true;
	}
	/**
	 * Stop this animation
	 */
	void stop() {
		running = false;
	}
	/**
	 * Reset this animation to its first frame
	 */
	void reset() {
		currentIndex = 0;
		firstUpdate = true;
	}
	/**
	 * Set this animation to be facing left or right
	 * @param dir	direction of animation	
	 */
	void setDirection(int dir) {
		direction = (dir == LEFT)?LEFT:RIGHT;
	}
	/**
	 * Set this animation to cycle
	 * @param value
	 */
	void setLooping(boolean value) {
		looping = value;
	}
	/**
	 * Make this animation iterate through its sprite sheet in reverse
	 * @param value
	 */
	void setReverse(boolean value) {
		reverse = value;
	}
	/**
	 * Make this animation reverse once it reaches its last frame
	 * @param value	
	 */
	void setPingPong(boolean value) {
		pingpong = value;
	}
	/**
	 * Set the frame rate of this animation
	 * @param rate	frame rate
	 */
	void setSpeed(float rate) {
		if (speed >= 0)
			speed = rate;
	}
	/**
	 * Set this animation to the specified row and reset it
	 * @param r	sprite sheet row
	 */
	void selectRow(int r) {
		indices.clear();
		for (int i = 0; i < columns; ++i)
			indices.add(new Vector2f(i, r));
	}
	/**
	 * Set this animation to a specific frame within its sprite sheet
	 * @param x	 horizontal position of frame within the sprite sheet
	 * @param y	 vertical position of frame within the sprite sheet
	 */
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
	 * @return The system time in milliseconds
	 */
	private long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	/**
	 * Check if we need to move to the next frame
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
			frameCounter = (int)(frameCounter % speed); // Gotta have an int frame 
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
