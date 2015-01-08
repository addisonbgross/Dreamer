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
	ShadowedMessage console = new ShadowedMessage("", 0, 0);
	
	Editor() {}
	
	void start() {
		Level.clear();
		
		Theme mono = new Theme();
		mono.addColor("light", 200, 200, 200);
		mono.addColor("dark", 25, 25, 25);
		mono.addColor("font", 225, 225, 225);
		
		Theme.current = mono;
		pointer = new MousePointer();
		pointer.add();
		origin = new Marker("origin", 0, 0);
		origin.add();
		write("test");
		Level.clear();
		read("test");
		console.add();
		ShadowedMessage test = new ShadowedMessage("tets", 0, 100);
		test.add();
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
