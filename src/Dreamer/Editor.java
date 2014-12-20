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
	ShadowedMessage m = new ShadowedMessage("", 0, 0);
	
	Editor() {
		Level.clear();
		pointer = new MousePointer();
		pointer.add();
		origin = new Marker("origin", 0, 0);
		origin.add();
		write("test");
		Level.clear();
		read("test");
		new TestKeys().add();
		m.add();
		char[] alphabet = ("qwertyuiop" + "asdfghjkl" + "zxcvbnm").toCharArray();
		int[] codes = {
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, //qwertyuiop
				30, 31, 32, 33, 34, 35, 36, 37, 38, //asdfghjkl
				44, 45, 46, 47, 48, 49, 50 //zxcvbnm
				};
		for(int i = 0; i < alphabet.length; i++) {
			char key = alphabet[i];
			KeyHandler.actionMap.put(
				codes[i], 
				new Action() {
					void perform() {
						KeyHandler.keyBuffer = KeyHandler.keyBuffer + key;
						m.name = KeyHandler.keyBuffer;
					}
				}
			);
		}
		KeyHandler.actionMap.put(
			Keyboard.KEY_RETURN, 
			new Action() {
				void perform() {
					KeyHandler.keyBuffer = "";
					m.name = KeyHandler.keyBuffer;
				}
			}
		);
		KeyHandler.actionMap.put(
			Keyboard.KEY_SPACE, 
			new Action() {
				void perform() {
					KeyHandler.keyBuffer = KeyHandler.keyBuffer + " ";
					m.name = KeyHandler.keyBuffer;
				}
			}
		);
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
