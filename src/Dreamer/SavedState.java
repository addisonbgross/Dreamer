package Dreamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

//a saved game state
public class SavedState implements Serializable{
	private static final long serialVersionUID = -2248376617115341248L;
	protected String name;
	private HashSet<Element> saved;
	private ArrayList<Element> background;
	
	public SavedState() {
		saved = new HashSet<Element>();
		background = new ArrayList<Element>();
	}
	public SavedState(String s) {
		name = s;
		saved = new HashSet<Element>();
		background = new ArrayList<Element>();
		for(HashSet<Element> entry : Element.getMasterList().values()) 
		{
			for(Element e: entry)
			{
				saved.add(e);
			}
		}
		for(Element e: Element.getBackground())
			background.add(e);
	}
	void restore() {
		Element.clearAll();
		for(Element e: saved)
			e.add();
		for(Element e: background)
			e.add();
	}
	boolean saveToFile(String s) {
		File newSavedGame = new File("res/"+s+".dsf");
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(newSavedGame);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	boolean restoreFromFile(String s) {
		try {
			FileInputStream fileIn = new FileInputStream("res/"+s+".dsf");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			((SavedState) in.readObject()).restore();;
			in.close();
			fileIn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}