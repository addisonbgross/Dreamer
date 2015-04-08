package Dreamer;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

class Background extends Positionable {

	private static final long serialVersionUID = -5311718550637469560L;
	static float BACKGROUNDMOTION = 10;
	static float FOREGROUNDMOTION = 100;
	static final float ANGULARVELOCITY = 0.5f;
	String imageName;
	float relativeMotion = BACKGROUNDMOTION;
	public static ArrayList<Element> background = new ArrayList<Element>();

	Background() {
		imageName = "";
	}

	Background(String s) {
		imageName = s;
		setWidth(Library.getImage(s).getWidth());
		setHeight(Library.getImage(s).getHeight());
	}

	@Override
	void add() {
		Background.background.add(this);
	}

	@Override
	void remove() {
		Background.background.remove(this);
	}

	@Override
	void draw() {
		if (imageName != "") {
			float x;
			float y;
			float marginX = (getWidth() - Constants.screenWidth) / 2;
			float marginY = (getHeight() - Constants.screenHeight) / 2;
			x = Camera.getCenterX();
			y = Camera.getCenterY();
			x = (float) (marginX * Math.signum(x) * (1 - Math.sqrt(Math
					.exp(-relativeMotion / 10000 * Math.abs(x)))));
			y = (float) (marginY * Math.signum(y) * (1 - Math.sqrt(Math
					.exp(-relativeMotion / 10000 * Math.abs(y)))));
			Drawer.drawImage(Library.getImage(imageName), -x - marginX, y
					- marginY);
		}
	}
}

class RovingGround extends Background implements Updateable {

	private static final long serialVersionUID = -3409596590781397942L;
	GradientFill gradient;
	MotionTrack m;
	float groundHeight;

	RovingGround(Color c, int y, float z) {
		this(c, c, y, z);
	}

	RovingGround(Color top, Color bottom, int y, float z) {
		gradient = new GradientFill(0, 0, top, 0, 1, bottom, true);
		groundHeight = y;
		setMinX(Camera.getMinX());
		setMinY(Camera.getMinY());
		setWidth(Camera.getWidth());
		setWidth(Camera.getHeight());
		setZ(z);
	}

	float tempY;

	@Override
	void draw() {
		update();
		// if(Camera.getMY() < groundHeight)
		{
			tempY = Camera.translate(0, groundHeight, getZ()).y;
			Polygon p = new Polygon();
			p.addPoint(0, tempY);
			p.addPoint(0, Constants.screenHeight);
			p.addPoint(Constants.screenWidth, Constants.screenHeight);
			p.addPoint(Constants.screenWidth, tempY);
			Drawer.graphics.fill(p, gradient);
		}
	}

	public void update() {
		if (m != null)
			m.remove();
		m = new MotionTrack(Camera.getMinX() - Constants.EXISTENCEBUFFER,
				groundHeight, Camera.getMaxX() + Constants.EXISTENCEBUFFER,
				groundHeight);
		m.add();
	}
}

class SolidBackground extends Background {

	private static final long serialVersionUID = 883437971065634321L;
	Color color;

	SolidBackground(Color c) {
		color = c;
	}

	@Override
	void draw() {
		Drawer.setBackground(color);
	}
}

class GradientBackground extends Background {

	private static final long serialVersionUID = 2718003017663189562L;
	GradientFill gradient;

	GradientBackground(Color t, Color b) {
		gradient = new GradientFill(0, 0, t, 0, Constants.screenHeight, b);
	}

	@Override
	void draw() {
		Drawer.graphics.fill(new Rectangle(0, 0, Constants.screenWidth,
				Constants.screenHeight), gradient);
	}
}

class Mountain extends Background {

	private static final long serialVersionUID = -5998631704992314615L;
	Polygon shape = new Polygon();
	Polygon shadow = new Polygon();
	Polygon snow = new Polygon();
	Color mountainColor = new Color(127, 127, 127);
	Color shadowColor = new Color(63, 63, 63);
	Color snowColor = new Color(200, 220, 255, 191);

	Mountain(float x, float y, float z, int size) {
		setPosition(x, y, z);

		shape.addPoint(x, y);
		shape.addPoint(x + size, y + size);
		shape.addPoint(x + (2 * size), y);

		shadow.addPoint(x + size, y + size);
		shadow.addPoint(x + (1.5f * size), y);
		shadow.addPoint(x + (2 * size), y);

		if (size > Constants.SNOWHEIGHT) {
			snow.addPoint(x + Constants.SNOWHEIGHT, y + Constants.SNOWHEIGHT);
			snow.addPoint(x + size, y + size);
			snow.addPoint(x + (2 * size) - Constants.SNOWHEIGHT, y
					+ Constants.SNOWHEIGHT);
		}

		setWidth(shape.getWidth());
		setHeight(shape.getHeight());
	}

	void draw() {
		Drawer.drawShape(shape, mountainColor);
		Drawer.drawShape(shadow, shadowColor);
		Drawer.drawShape(snow, snowColor);
	}
}
