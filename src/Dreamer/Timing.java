package Dreamer;

import org.lwjgl.Sys;

public class Timing 
{
	// time at last frame
	static long lastFrame = 0;
	// frames per second
	static int fps = 0;
	static int delta = 0;
	static int time = 0;
	// last fps time
	static long lastFPS = 0;
	
	static public void updateTimer(int del)
	{
		time += del;
		if(time > 4000)
			time = 0;	
	}
	// calculate how many milliseconds have passed since last frame
	static public void setDelta()
	{
		long time = getTime();
		delta = (int)(time - lastFrame);
		lastFrame = time;
	}
	static public void setLastFPS()
	{
		lastFPS = (Sys.getTime() * 1000 / Sys.getTimerResolution());
	}
	static public int getDelta()
	{
		return delta;
	}
	// get accurate system time
	static private long getTime()
	{
		return (Sys.getTime() * 1000 / Sys.getTimerResolution());
	}
	// calculate the FPS and set it in the title bar
	static public void updateFPS()
	{
		if (getTime() - lastFPS > 1000)
		{
			fps = 0;
			lastFPS += 1000;
		}
		++fps;
	}
	static public int getFPS()
	{
		return fps;
	}
}
