package Dreamer;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import Dreamer.Camera.Function;

abstract public class KeyHandler {
	Actor focus;
	Map<String, Integer>keyMap;
	java.util.HashSet<String> wasPressed = new java.util.HashSet<String>();
	boolean[] keys = new boolean[127];
	
	public KeyHandler() {
		keyMap = new HashMap<String, Integer>();
	}
	
	public void getKeys() {
		if(!focus.checkStatus("dead")) {
			//if the key is freshly pressed
			float scaleVel = (focus.checkStatus("blocking"))?Constants.VEL*0.6f:Constants.VEL;
			if(Keyboard.isKeyDown(keyMap.get("jumpKey"))) {
				if(!wasPressed.contains("jumpKey")) {
					wasPressed.add("jumpKey");
					if(!focus.checkStatus("jumping"))
						if(focus.checkStatus("grounded")) {
							focus.addStatus("jumping");
							focus.adjustVel(0, Constants.PLAYERJUMPVEL);
							focus.removeStatus("grounded");
							focus.motion = null;
						}
				}
			} else {
				wasPressed.remove("jumpKey");
			}
			
			// Sideways movement!
			float scaleJump = 1;
			if (focus.checkStatus("jumping")) 
				scaleJump = 0.4f;
			if(Keyboard.isKeyDown(keyMap.get("rightKey"))) {
				focus.removeStatus("left");
				focus.addStatus("right");
				if (focus.xVel < scaleVel)
					focus.xVel+=(focus.xVel < 5)? 2 * scaleJump: Constants.ACTORACCELERATION * scaleJump;
				else 
					focus.setXVel(scaleVel);
			}
			if(Keyboard.isKeyDown(keyMap.get("leftKey"))) {
				focus.removeStatus("right");
				focus.addStatus("left");
				if (focus.xVel > -scaleVel)
					focus.xVel-=(focus.xVel > -5)? 2 * scaleJump: Constants.ACTORACCELERATION * scaleJump;
				else 
					focus.setXVel(-scaleVel);
			}  
			if(Keyboard.isKeyDown(keyMap.get("upKey"))) {
				focus.removeStatus("down");
				focus.addStatus("up");
			}else if(Keyboard.isKeyDown(keyMap.get("downKey"))) {
				focus.removeStatus("up");
				focus.addStatus("down");					
			} else {
				focus.removeStatus("up");
				focus.removeStatus("down");
			}
			if(Keyboard.isKeyDown(keyMap.get("attackKey"))) {
				focus.addStatus("attacking");
			} 	else
				focus.removeStatus("attacking");
			
			if(Keyboard.isKeyDown(keyMap.get("actionKey"))) {
				if(keys[keyMap.get("actionKey")] != true) {
					focus.addStatus("acting");	
				} else
					focus.removeStatus("acting");
				keys[keyMap.get("actionKey")] = true;
			} else {
				keys[keyMap.get("actionKey")] = false;
				focus.removeStatus("acting");
			}
			
			if(Keyboard.isKeyDown(keyMap.get("blockKey"))) {
				focus.addStatus("blocking");
			} 	else
				focus.removeStatus("blocking");
			

			if(Keyboard.isKeyDown(Keyboard.KEY_H)) {	
				if(keys[Keyboard.KEY_H] != true) {
					Block3d p;
					if(focus.checkStatus("left"))
						p = new Block3d(Color.blue, focus.getX() - 100, focus.getMinY() + 50, focus.getZ(), 100, 20, 100);
					else
						p = new Block3d(Color.red, focus.getX() + 100, focus.getMinY() + 50, focus.getZ(), 100, 20, 100);
					p.generateCollidable();
					p.add();
				} 
				keys[Keyboard.KEY_H] = true;
			}else {
				keys[Keyboard.KEY_H] = false;
			}
		}
	}
	void add() {
		Level.keys.add(this);
	}
	void remove() {
		Level.keys.remove(this);
	}
}
class FunctionKeys extends KeyHandler {
	@Override 
	public void getKeys() {
		if(Keyboard.isKeyDown(Keyboard.KEY_P)) {
			if(keys[Keyboard.KEY_P] != true) {
				
				Element.printActive();
				/*
				Element.printAll();
				
				System.out.println(Level.current.getClass());
				System.out.println("UPDATE SET START");
				for(Updateable u: Element.updateSet)
					System.out.println(u.toString());
				System.out.println("END");
				*/
			}
			keys[Keyboard.KEY_P] = true;
		} else {
			keys[Keyboard.KEY_P] = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_T)) {
			if(keys[Keyboard.KEY_T] != true)
				Element.debug = !Element.debug;
			keys[Keyboard.KEY_T] = true;
		} else {
			keys[Keyboard.KEY_T] = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			//starts sampling a function
			Dreamer.sampled = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_1)) {
			new TestLevel();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_2)) {
			new SimpleLevel();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_3)) {
			new BirdLevel();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_4)) {
			new ForestLevel();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_M)) {
			new MainMenu();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_I)){
			for(Player p: Player.list) {
				p.reset();
				new ForestLevel();
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
			if(keys[Keyboard.KEY_C] != true)
				if(Library.defaultFontColor == Color.black) {
					Library.defaultFontColor = Color.cyan;
					Library.messageFontColor = Color.red;
				} else {
					Library.defaultFontColor = Color.black;
					Library.messageFontColor = Color.blue;
				}
			keys[Keyboard.KEY_C] = true;
		}	
		else {
			keys[Keyboard.KEY_C] = false;
		}
	}
}
class ZoomKeys extends KeyHandler {
	protected boolean z = false, j = false;
	private int velocity = 0;
	@Override 
	public void getKeys() {
		if(Keyboard.isKeyDown(Keyboard.KEY_J)) {
			if(j != true)
				if(Camera.mode == Function.NEW)
					Camera.mode = Function.ORIGINAL;
				else
					Camera.mode = Function.NEW;
			j = true;
		}	
		else {
			j = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			if(z != true)
				Camera.zoom = !Camera.zoom;
			z = true;
		}	
		else {
			z = false;
		}
		if(Camera.zoom) {
			if(Keyboard.isKeyDown(Keyboard.KEY_COMMA)) {
				Camera.nudge(0, 0, -velocity);
				Camera.zoomLength--;
				velocity++;
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
				Camera.nudge(0, 0, velocity);
				Camera.zoomLength++;
				velocity++;
			}	
			else if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				Camera.nudge(0, velocity, 0);
				velocity++;
			}	
			else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				Camera.nudge(0, -velocity, 0);
				velocity++;
			}	
			else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				Camera.nudge(-velocity, 0, 0);
				velocity++;
			}	
			else if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				Camera.nudge(velocity, 0, 0);
				velocity++;
			}	
			else {
				velocity = 0;
			}
		}
	}
}
class ArrowKeys extends KeyHandler {

	public ArrowKeys(Actor subject) {
		focus = subject;
		keyMap = new HashMap<String, Integer>();
		keyMap.put("jumpKey", Keyboard.KEY_SPACE);
		keyMap.put("leftKey", Keyboard.KEY_LEFT);
		keyMap.put("rightKey", Keyboard.KEY_RIGHT);
		keyMap.put("downKey", Keyboard.KEY_S);
		keyMap.put("attackKey", Keyboard.KEY_F);
		keyMap.put("actionKey", Keyboard.KEY_E);
	}
}
class WASDKeys extends KeyHandler {
	public WASDKeys(Actor subject) {
		focus = subject;	
		keyMap = new HashMap<String, Integer>();
		keyMap.put("jumpKey", Keyboard.KEY_SPACE);
		keyMap.put("upKey", Keyboard.KEY_W);
		keyMap.put("leftKey", Keyboard.KEY_A);
		keyMap.put("rightKey", Keyboard.KEY_D);
		keyMap.put("downKey", Keyboard.KEY_S);
		keyMap.put("actionKey", Keyboard.KEY_E);
		keyMap.put("attackKey", Keyboard.KEY_K);
		keyMap.put("blockKey", Keyboard.KEY_L);
	}
}

