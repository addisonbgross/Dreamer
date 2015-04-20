package Dreamer;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.input.Keyboard.*;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

abstract public class KeyHandler {
	static boolean initialized =  false;
	//these next two fields cannot be changed without drastic consequences
	static final char[] alphabetPlus = (
			"1234567890-=" + 
			"qwertyuiop[]" + 
			"asdfghjkl;'" + 
			"zxcvbnm,./" + 
			" "
			).toCharArray();
	static final int[] codes = {
			2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // 1234567890-=
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, // qwertyuiop[]
			30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // asdfghjkl;'
			44, 45, 46, 47, 48, 49, 50, 51, 52, 53,// zxcvbnm,./
			Keyboard.KEY_SPACE
			};
	static Map<String, Integer>keyMap = new HashMap<String, Integer>();
	static Map<Integer, Action>actionMap = new HashMap<Integer, Action>();
	static Map<Integer, Action>savedKeys = new HashMap<Integer, Action>();
	static String keyBuffer = "";

	public KeyHandler() {}
	
	public static boolean test(Integer keyCode) {
		return Keyboard.isKeyDown(keyCode);
	}
	public static boolean test(String s) {
		return Keyboard.isKeyDown(keyMap.get(s));
	}
	public static void clearKeys() {
		actionMap.clear();
	}
	public static void saveKeys() {
		savedKeys.clear();
		savedKeys.putAll(actionMap);
	}
	public static void restoreKeys() {
		actionMap.clear();
		actionMap.putAll(savedKeys);
	}
	public static boolean addKey(Integer i, Performable p) {
		actionMap.put(i, new Action() {
			void perform() {
				p.perform();
			}
		});
		return true;
	}
	public static boolean addKey(char c, Performable p) {
		return addKey(keyMap.get(c + ""), p);
	}
	//handles all keyboard events in the game
	public static void getKeys() {
		while (Keyboard.next()) {
			Integer keyNum = Keyboard.getEventKey();
			if(Keyboard.getEventKeyState()) {
				try {
					actionMap.get(keyNum).perform();
				} catch(NullPointerException e) {
					//not there, no bigs
				}
			} else {
				try {
					actionMap.get(keyNum).stop();
				} catch(NullPointerException e) {
					//not there, no bigs
				}
			}
		}
	}
	public static void init() {
		if(!initialized) {
			for(int i = 0; i < alphabetPlus.length; i++) {
				keyMap.put(alphabetPlus[i] + "", codes[i]);
			}
			initialized = true;
		}
	}
	public static void openGameKeys() {
		clearKeys();
		new ZoomKeys().add();
		new FunctionKeys().add();
		new WASDKeys(Player.getFirst()).add();
	}
	public static void openEditorKeys(Editor e) {
		clearKeys();
		new EditorKeys(e).add();
	}
	public static void openMenuKeys(Menu m) {
		clearKeys();
		KeyHandler.addKey(KEY_UP, ()-> { m.command("up"); });
		KeyHandler.addKey(KEY_DOWN, ()-> { m.command("down"); });
		KeyHandler.addKey(KEY_RETURN, ()-> { m.command("select"); });
	}
}
class FunctionKeys extends  KeyHandler {
	void add() {
		addKey('p', ()-> { Element.printActive(); });
		addKey('t', ()-> { Element.debug = !Element.debug; });
		addKey('c', ()-> {
			if(Library.defaultFontColor == Color.black) {
				Library.defaultFontColor = Color.cyan;
				Library.messageFontColor = Color.red;
			} else {
				Library.defaultFontColor = Color.black;
				Library.messageFontColor = Color.blue;
			}
		});
		addKey('1', ()-> { new TestLevel(); } );
		addKey('2', ()-> { new SimpleLevel(); } );
		addKey('3', ()-> { new BirdLevel(); } );
		addKey('4', ()-> { new ForestLevel(); } );
		addKey('m', ()-> { new MainMenu(); } );
		addKey('i', ()-> {
			for(Player p: Player.list) {
				p.reset();
			}
			new ForestLevel();
		});
	}
}
class EditorKeys extends KeyHandler {
	Editor editor;
	
	EditorKeys(Editor e){
		editor = e;
	};
	
	void add() {
		for(int i = 0; i < alphabetPlus.length; i++) {
			char key = alphabetPlus[i];
			addKey(codes[i], ()-> {
				KeyHandler.keyBuffer = KeyHandler.keyBuffer + key;
				editor.console.name = KeyHandler.keyBuffer;
			});
		}
		addKey(KEY_RETURN, ()-> {
			editor.command(KeyHandler.keyBuffer);
			KeyHandler.keyBuffer = "";
			editor.console.name = KeyHandler.keyBuffer;
		});
		addKey(KEY_TAB, ()-> { KeyHandler.openGameKeys(); });
	}
}
class ZoomKeys extends KeyHandler {
	void add() {
		addKey('z', ()-> { Camera.zoom = !Camera.zoom; });
		addKey(KEY_COMMA, ()-> { Camera.command("zoom_in"); });
		addKey(KEY_PERIOD, ()-> { Camera.command("zoom_out"); });
		addKey(KEY_UP, ()-> { Camera.command("up"); });
		addKey(KEY_DOWN, ()-> { Camera.command("down"); });
		addKey(KEY_LEFT, ()-> { Camera.command("left"); });
		addKey(KEY_RIGHT, ()-> { Camera.command("right"); });
	}
}
class WASDKeys extends KeyHandler {
	Actor subject;
	
	public WASDKeys(Actor a) {
		super();
		subject = a;
	}
	
	void add() {
		addKey(' ', ()-> { subject.addStatus(Status.TRYJUMP); });
		addKey('d', ()-> { subject.addStatus(Status.TRYRIGHT); });
		addKey('a', ()-> { subject.addStatus(Status.TRYLEFT); });
		addKey('w', ()-> { subject.addStatus(Status.UP); });
		addKey('s', ()-> { subject.addStatus(Status.DOWN); });
		addKey('k', ()-> { subject.addStatus(Status.TRYATTACK); });
		addKey(KEY_LSHIFT, ()-> { subject.addStatus(Status.TRYSPRINT); });
		addKey('e', ()-> { subject.addStatus(Status.ACTING); });
		addKey('l', ()-> { subject.addStatus(Status.BLOCKING); });
	}
}

