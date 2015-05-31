package Dreamer;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.input.Keyboard.*;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import Dreamer.enums.Status;
import static Dreamer.enums.Status.*;
import Dreamer.interfaces.Performable;

abstract public class Keys {
	
	//-----------FIELDS
	
	static boolean initialized = false;
	
	static final char[] alphabetPlus = 
		(	
			"1234567890-=" + 
			"qwertyuiop[]" + 
			"asdfghjkl;'" + 
			"zxcvbnm,./" + 
			" "
		).toCharArray();
	
	static final int[] codes = 
		{ 
			2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // 1234567890-=
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, // qwertyuiop[]
			30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // asdfghjkl;'
			44, 45, 46, 47, 48, 49, 50, 51, 52, 53,// zxcvbnm,./
			Keyboard.KEY_SPACE 
		};
	
	static Map<String, Integer> keyMap = new HashMap<String, Integer>();
	
	static Map<Integer, Performable> startMap = new HashMap<>();
	static Map<Integer, Performable> stopMap = new HashMap<>();
	
	static Map<Integer, Performable> savedStartKeys = new HashMap<>();
	static Map<Integer, Performable> savedStopKeys = new HashMap<>();
	static String keyBuffer = "";
	
	//-----------METHODS

	public static void clearKeys() {
		
		startMap.clear();
		stopMap.clear();
	}

	public static void saveKeys() {
		
		savedStartKeys.clear();
		savedStartKeys.putAll(startMap);
		savedStopKeys.clear();
		savedStopKeys.putAll(stopMap);
	}

	public static void restoreKeys() {
		
		startMap.clear();
		startMap.putAll(savedStartKeys);
		stopMap.clear();
		stopMap.putAll(savedStopKeys);
	}

	public static boolean addKey(Integer i, Performable start, Performable stop) {
		
		startMap.put(i, start);
		stopMap.put(i, stop);
		return true;
	}

	public static boolean addKey(char c, Performable start, Performable stop) {
		return addKey(keyMap.get(c + ""), start, stop);
	}

	public static boolean addKey(Integer i, Performable p) {
		return addKey(i, p, () -> { /*no action*/ });
	}

	public static boolean addKey(char c, Performable p) {
		return addKey(keyMap.get(c + ""), p);
	}

	// handles all keyboard events in the game
	public static void getKeys() {
		
		while (Keyboard.next()) {
			
			Integer keyNum = Keyboard.getEventKey();
		
			if (Keyboard.getEventKeyState()) {
			
				try {
					startMap.get(keyNum).perform();
				} catch (NullPointerException e) {
					// not there, no bigs
				}
			} else {
				
				try {
					stopMap.get(keyNum).perform();
				} catch (NullPointerException e) {
					// not there, no bigs
				}
			}
		}
	}

	public static void init() {
	
		if (!initialized) {
		
			for (int i = 0; i < alphabetPlus.length; i++) {
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
		
		Keys.addKey(KEY_UP, () -> m.command("up") );
		Keys.addKey(KEY_DOWN, () -> m.command("down") );
		Keys.addKey(KEY_RETURN, () -> m.command("select") );
	}
}

class FunctionKeys extends Keys {
	
	void add() {
		
		addKey(KEY_TAB, () -> new Editor().start() );
		addKey('t', () -> Manager.debug = !Manager.debug );
		addKey('1', () -> new Dusk_1() );
		addKey('2', () -> new SimpleLevel() );
		addKey('3', () -> new BirdLevel() );
		addKey('4', () -> new ForestLevel() );
		addKey('m', () -> new MainMenu() );		
		addKey('c', () -> {
			
			if (Library.defaultFontColor == Color.black) {
				Library.defaultFontColor = Color.cyan;
				Library.messageFontColor = Color.red;
			} else {
				Library.defaultFontColor = Color.black;
				Library.messageFontColor = Color.blue;
			}
		});
		addKey('i', () -> {
			
			for (Player p : Player.list) {
				p.reset();
			}
			Camera.focus(new ClassFocus(200, Ninja.class));
		});
	}
}

class EditorKeys extends Keys {
	Editor editor;

	EditorKeys(Editor e) {
		editor = e;
	};

	void add() {
		for (int i = 0; i < alphabetPlus.length; i++) {
			char key = alphabetPlus[i];
			addKey(codes[i], () -> {
				Keys.keyBuffer = Keys.keyBuffer + key;
				editor.console.name = Keys.keyBuffer;
			});
		}
		addKey(KEY_RETURN, () -> {
			editor.command(Keys.keyBuffer);
			Keys.keyBuffer = "";
			editor.console.name = Keys.keyBuffer;
		});
		addKey(KEY_BACK,
				() -> {
					Keys.keyBuffer = Keys.keyBuffer.substring(0,
							Keys.keyBuffer.length() - 1);
					editor.console.name = Keys.keyBuffer;
				});
		// TODO switch cleanly between editor and game
		// addKey(KEY_TAB, ()-> { KeyHandler.openGameKeys(); });
	}
}

class ZoomKeys extends Keys {

	void add() {

		addKey('z', () -> Camera.zoom = !Camera.zoom);
		addKey(KEY_COMMA, () -> Camera.command("zoom_in"), Camera.stop);
		addKey(KEY_PERIOD, () -> Camera.command("zoom_out"), Camera.stop);
		addKey(KEY_UP, () -> Camera.command("up"), Camera.stop);
		addKey(KEY_DOWN, () -> Camera.command("down"), Camera.stop);
		addKey(KEY_LEFT, () -> Camera.command("left"), Camera.stop);
		addKey(KEY_RIGHT, () -> Camera.command("right"), Camera.stop);
	}
}

class WASDKeys extends Keys {

	Actor subject;

	public WASDKeys(Actor a) {
		super();
		subject = a;
	}

	void addActorKey(char c, Status s) {
		addKey(c, () -> subject.addStatus(s), () -> subject.removeStatus(s));
	}

	void add() {

		addActorKey(' ', TRYJUMP);
		addActorKey('d', TRYRIGHT);
		addActorKey('a', TRYLEFT);
		addActorKey('w', UP);
		addActorKey('s', DOWN);
		addActorKey('k', TRYATTACK);
		addActorKey((char)KEY_LSHIFT, TRYSPRINT);
		addActorKey('e', ACTING);
		addActorKey('l', BLOCKING);
	}
}
