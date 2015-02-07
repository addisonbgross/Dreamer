package Dreamer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

public class Editor {
	Class<?> c;
	//considering how to automate this
	MousePointer pointer;
	Marker origin;
	BufferedWriter bw;
	String path = Constants.RESPATH + Constants.LEVELPATH;
	ShadowedMessage instructions 
		= new ShadowedMessage("Type 'options' or 'menu'", 0, 200);
	ShadowedMessage console = new ShadowedMessage("", 0, 0);
	Menu editorMenu = new Menu();
	
	Editor() {
		editorMenu.addOption(
				"ADD BLOCK",
				new Action() {
					void perform() {
						Block3d b = new Block3d(Color.red, -200, 0, 0, 200, 200, 200);
						b.generateMotionTracks();
						b.add();
					}
				}
				);
		editorMenu.addOption(
				"ADD OTHER BLOCK",
				new Action() {
					void perform() {
						Block3d b = new Block3d(Color.blue, 200, 0, 0, 200, 200, 200);
						b.generateMotionTracks();
						b.add();
					}
				}
				);
		editorMenu.addOption(
				"ADD SUN",
				new Action() {
					void perform() {
						new Sun().add();
					}
				}
				);
		editorMenu.addOption(
				"PLAY",
				new Action() {
					void perform() {
						Player.getFirst().add();
						KeyHandler.openGameKeys();
						Camera.focus(Player.getFirst());
						Element.foreground.clear();
					}
				}
				);
		editorMenu.addOption(
				"EXIT MENU",
				new MenuAction(editorMenu, "exit")
				);
	}
	
	void start() {
		Level.clear();
		KeyHandler.openEditorKeys(this);
		
		Theme mono = new Theme();
		mono.addColor("light", 200, 200, 200);
		mono.addColor("dark", 25, 25, 25);
		mono.addColor("font", 225, 225, 225);
		Theme.current = mono;
		
		pointer = new MousePointer();
		origin = new Marker("origin", 0, 0);
		origin.add();
		write("test");
		Level.clear();
		read("test");
		
		console.add();
		instructions.add();
		pointer.add();
	}
	
	void command(String s) {
		switch(s) {
			
			case "menu":
				new MainMenu();
				break;
				
			case "options":
				editorMenu.open();
				break;
		}
	}
	
	void write(String s, Element... e) {
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(path + s + ".level"));
			out.writeObject(Element.masterList);
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	void read(String s) {
		try{ 
			FileInputStream door = new FileInputStream(path + s + ".level"); 
			ObjectInputStream reader = new ObjectInputStream(door); 
			HashSet<Element> x;
			x = (HashSet<Element>) reader.readObject();
			for(Element e: x)
				e.add();
			reader.close();
		} catch (IOException e){ 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
