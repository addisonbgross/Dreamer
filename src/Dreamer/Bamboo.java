package Dreamer;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;

public class Bamboo extends Element {
	Polygon bs, ds, ring;
	ArrayList<Polygon> rings;
	Color brightColor, darkColor, ringColor;
	
	Bamboo(float x, float y, float z, int size) {
		// filter input parameters
		if (size > 600)
			size = 600;
		else if (size < 100)
			size = 100;
		
		setPosition(x, y, z);
		setWidth(size / 100);
		setHeight(size);
		bs = new Polygon();
		ds = new Polygon();
		rings = new ArrayList<Polygon>();
		brightColor = new Color(180, 240, 190);
		darkColor = new Color(120, 200, 140);
		ringColor = new Color(20, 80, 60);
		
		// bright bamboo
		bs.addPoint(x, y);
		bs.addPoint(x,  size);
		bs.addPoint(x + size / 100, size);
		bs.addPoint(x + size / 100, y);		
		
		// dark bamboo
		ds.addPoint(x + size / 130, y);
		ds.addPoint(x + size / 130, size);
		ds.addPoint(x + size / 100, size);
		ds.addPoint(x + size / 100, y);
	
		// dark rings
		for (int i = 1; i < size - (i*i); ++i) {
			ring = new Polygon();
			ring.addPoint(x, size - (i*i));
			ring.addPoint(x, size - (i*i) + 1);
			ring.addPoint(x + size / 100, size - (i*i) + 1);
			ring.addPoint(x + size / 100, size - (i*i));
			rings.add(ring);
			// add leaves
			if (i % 3 == 0)
				rings.add(makeLeaf(size - (i*i), size));
		}
	}
	Polygon makeLeaf(int height, int size) {
		Polygon leaf = new Polygon();
		Random r = new Random();
		
		if (r.nextFloat() > 0.5f) {
			leaf.addPoint(getMinX(), height);
			leaf.addPoint(getMinX() - 8, height + 4);
			leaf.addPoint(getMinX() - 10, height + 2);
			leaf.addPoint(getMinX(), height + 1);
		} else {
			leaf.addPoint(getMinX() + getWidth(), height);
			leaf.addPoint(getMinX() + size / 100 + 8, height + 4);
			leaf.addPoint(getMinX() + size / 100 + 10, height + 2);
			leaf.addPoint(getMinX() + size / 100, height + 1);
		}
		return leaf;
	}
	void draw(Graphics g) {
		g.setAntiAlias(true);
		drawShape(bs, brightColor, g);
		drawShape(ds, darkColor, g);
		for (Polygon r: rings)
			drawShape(r, ringColor, g);
	}
}
