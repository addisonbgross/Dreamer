package Dreamer;

import org.newdawn.slick.Color;

public class ShapeMaker {

	static Shape3d focus = new Shape3d(); 
	static java.util.Random r = new java.util.Random();

	static Menu menu = new Menu(Justification.LEFT, -Constants.screenWidth / 2, 0);
	
	static void init() {
		add(menu, "block");
		add(menu, "island");
		add(menu, "weird");		
	}
	
	static Shape3d make(String name) {
		
		if(name.equals("block")) {
			focus = new Block3d(new Color(r.nextInt()), 0, 0, 0, 100, 100, 100);
			focus.print();
			return focus;
		}
		
		if(name.equals("island")) {
			return focus = new Island(0, 0, 0);
		}
		
		if(name.equals("weird")) {
			return focus = new Weird(0, 0, 0, 100);
		}
		
		return null;
	}
	
	static Menu add(Menu m, String s) {
		m.addOption(s, 
				new Action() {
					void perform() {
						make(s).add();
						menu.exit();
					}
				}
			);
		return m;
	}

	static void addFocus() {
		if(focus != null) {
			Shape3d s;
			try {
				s = ((DynamicShape3d)focus).makeStatic();
				s.add();
				focus.remove();
			} catch(ClassCastException cce) {
				s = focus.getCopy();
				s.add();
				focus.remove();
			}
		}
	}
}
