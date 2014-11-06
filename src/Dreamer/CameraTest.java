package Dreamer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class CameraTest {
	TestLevel level;
	@Before
	public void before() throws Exception {
		Dreamer.init();
		try {
			Library.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		level = new TestLevel();
	}
	@After
	public void after() {
		Display.destroy();
		//level.
	}

	@Test
	public void isOriginVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		assertEquals(true, Camera.isPointVisible(0, 0, 0));
	}
	@Test
	public void areQuadrantEdgesVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		int w = Constants.screenWidth / 2 - 1;
		int h = Constants.screenHeight / 2 - 1;
		assertEquals(true, Camera.isPointVisible(-w, h, 0));
		assertEquals(true, Camera.isPointVisible(w, h, 0));
		assertEquals(true, Camera.isPointVisible(w, -h, 0));
		assertEquals(true, Camera.isPointVisible(-w, -h, 0));
	} 
	@Test
	public void areQuadrantEdgesNotVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		int w = Constants.screenWidth / 2 + 1;
		int h = Constants.screenHeight / 2 + 1;
		assertEquals(false, Camera.isPointVisible(-w, h, 0));
		assertEquals(false, Camera.isPointVisible(w, h, 0));
		assertEquals(false, Camera.isPointVisible(w, -h, 0));
		assertEquals(false, Camera.isPointVisible(-w, -h, 0));
	} 
	@Test
	public void isDeepPointVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		int w = Constants.screenWidth - 1;
		int h = Constants.screenHeight - 1;
		assertEquals(true, Camera.isPointVisible(-w, h, -2000));
		assertEquals(true, Camera.isPointVisible(w, h, -2000));
		assertEquals(true, Camera.isPointVisible(w, -h, -2000));
		assertEquals(true, Camera.isPointVisible(-w, -h, -2000));
	} 
	@Test
	public void isDeepPointNotVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		int w = Constants.screenWidth + 1;
		int h = Constants.screenHeight + 1;
		assertEquals(false, Camera.isPointVisible(-w, h, -2000));
		assertEquals(false, Camera.isPointVisible(w, h, -2000));
		assertEquals(false, Camera.isPointVisible(w, -h, -2000));
		assertEquals(false, Camera.isPointVisible(-w, -h, -2000));
	}
	@Test
	public void isBlock3dVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		int sw = Constants.screenWidth / 2 + 1;
		int sh = Constants.screenHeight / 2 + 1;
		int w = 100;
		int h = 100;
		int d = 100;
		Block3d blockTop    = new Block3d(Color.magenta, 0, sh, 0, w, h, d);
		Block3d blockBottom = new Block3d(Color.magenta, 0, -sh, 0, w, h, d);
		Block3d blockRight  = new Block3d(Color.magenta, sw, 0, 0, w, h, d);
		Block3d blockLeft   = new Block3d(Color.magenta, -sw, 0, 0, w, h, d);
		
//		System.out.println("Top Block's x:" + blockTop.getX() + " y:" + blockTop.getY() + " z:" + blockTop.getZ());
//		System.out.println("Top Block's width:" + blockTop.getWidth() + " height:" + blockTop.getHeight() + " depth:" + blockTop.getDepth());
		
		assertEquals(true, blockTop.isVisible());
		assertEquals(true, blockBottom.isVisible());
		
		System.out.println("GETX: " + blockRight.getX() + " GETY: " + blockRight.getY() + " GETZ: " + blockRight.getZ());
		System.out.println("GETW: " + blockRight.getWidth() + " GETH: " + blockRight.getHeight() + " GETD: " + blockRight.getDepth());
		System.out.println("CAMERA CENTRE: " + Camera.getCenterX() + ", " + Camera.getCenterY());
		
		assertEquals(true, blockRight.isVisible());
		assertEquals(true, blockLeft.isVisible());
	}
	@Test
	public void isBlock3dNotVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		int sw = Constants.screenWidth / 2 + 1;
		int sh = Constants.screenHeight / 2 + 1;
		int w = 100;
		int h = 100;
		int d = 100;
		Block3d blockTop    = new Block3d(Color.magenta, 0, sh + h / 2, d / 2, w, h, d);
		Block3d blockBottom = new Block3d(Color.magenta, 0, -sh - h / 2, d / 2, w, h, d);
		Block3d blockRight  = new Block3d(Color.magenta, sw + w / 2, 0, d / 2, w, h, d);
		Block3d blockLeft   = new Block3d(Color.magenta, -sw - w / 2, 0, d / 2, w, h, d);
		
//		System.out.println("Top Block's x:" + blockTop.getX() + " y:" + blockTop.getY() + " z:" + blockTop.getZ());
//		System.out.println("Top Block's width:" + blockTop.getWidth() + " height:" + blockTop.getHeight() + " depth:" + blockTop.getDepth());
		
		assertEquals(false, blockTop.isVisible());
		assertEquals(false, blockBottom.isVisible());
		assertEquals(false, blockRight.isVisible());
		assertEquals(false, blockLeft.isVisible());
	}
	@Test
	public void isBlock3dPushedBackVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		int sw = Constants.screenWidth + 1;
		int sh = Constants.screenHeight + 1;
		int w = 100;
		int h = 100;
		int d = 100;
		Block3d blockTop    = new Block3d(Color.magenta, 0, sh, -2000, w, h, d);
		Block3d blockBottom = new Block3d(Color.magenta, 0, -sh, -2000, w, h, d);
		Block3d blockRight  = new Block3d(Color.magenta, sw, 0, -2000, w, h, d);
		Block3d blockLeft   = new Block3d(Color.magenta, -sw, 0, -2000, w, h, d);
		
//		System.out.println("Top Block's x:" + blockTop.getX() + " y:" + blockTop.getY() + " z:" + blockTop.getZ());
//		System.out.println("Top Block's width:" + blockTop.getWidth() + " height:" + blockTop.getHeight() + " depth:" + blockTop.getDepth());
		
		assertEquals(true, blockTop.isVisible());
		assertEquals(true, blockBottom.isVisible());
		assertEquals(true, blockRight.isVisible());
		assertEquals(true, blockLeft.isVisible());
	}
	@Test
	public void isBigBlock3dPushedBackNotVisible() {
		Camera.focus(new Marker("Origin", 0, 0));
		int sw = Constants.screenWidth + 1;
		int sh = Constants.screenHeight + 1;
		int w = 1000;
		int h = 1000;
		int d = 1000;
		Block3d blockTop    = new Block3d(Color.magenta, 0, sh + h / 2, d / 2 - 2000, w, h, d);
		Block3d blockBottom = new Block3d(Color.magenta, 0, -sh - h / 2, d / 2 - 2000, w, h, d);
		Block3d blockRight  = new Block3d(Color.magenta, sw + w / 2, 0, d / 2 - 2000, w, h, d);
		Block3d blockLeft   = new Block3d(Color.magenta, -sw - w / 2, 0, d / 2 - 2000, w, h, d);
		
//		System.out.println("Top Block's x:" + blockTop.getX() + " y:" + blockTop.getY() + " z:" + blockTop.getZ());
//		System.out.println("Top Block's width:" + blockTop.getWidth() + " height:" + blockTop.getHeight() + " depth:" + blockTop.getDepth());
		
		assertEquals(false, blockTop.isVisible());
		assertEquals(false, blockBottom.isVisible());
		assertEquals(false, blockRight.isVisible());
		assertEquals(false, blockLeft.isVisible());
	}
}
