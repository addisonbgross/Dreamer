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

import apple.laf.JRSUIConstants.Focused;

public class Editor {
	Class<?> c;
	//considering how to automate this
	MousePointer pointer;
	Marker origin;
	BufferedWriter bw;
	String path = Constants.RESPATH + Constants.LEVELPATH;
	ShadowedMessage console = new ShadowedMessage("", 0, 0);
	Menu editorMenu = new Menu(Justification.LEFT, -Constants.screenWidth / 2, 200);
	Menu creationMenu = new Menu(Justification.LEFT, -Constants.screenWidth / 2, 200);
	Shape3d focus = null;
	
	Editor() {
		creationMenu.addOption(
				"MAKE", 
				new Action() {
					void perform() {
						if(focus != null) {
							focus.remove();	
						}
						focus = ShapeMaker.make("block");
						focus.add();
					}
				}
				);
		creationMenu.addOption(
				"TRANSLATE", 
				new Action() {
					void perform() {
						pointer.onMove = new Action() {
							void perform() {
								focus.setPosition(pointer.getX(), pointer.getMaxY(), 0);
							}
						};
					}
				}
				);
		creationMenu.addOption("ROTATE", new Action() {
			void perform() {
				// focus.rotate(1, 0, 0, pointer.getX() / 1000);
				focus.remove();
				focus.makeDynamic()
					.clearTransformers()
					.addTransformer(new Rotator(1, 0, 0, 0.1f))
					.add();
			}
		});
		creationMenu.addOption(
				"SCALE", 
				new Action() {
					void perform() {
						focus.scale(2);
					}
				}
				);
		creationMenu.addOption(
				"RANDOM", 
				new Action() {
					void perform() {
						focus.randomize(0.5f);
					}
				}
				);
		creationMenu.addOption(
				"TEST SHAPEMAKER",
				new Action() {
					void perform() {
						ShapeMaker
							.make("block")
							.scale(3, 2, 3)
							.setColor(new Color(Shape3d.r.nextInt()))
							.randomize(0.8f)
							.rotate(0, 1, 1, 45)
							.makeDynamic()
							.addTransformer(new Rotator(0, 1, 3, 0.3f))
							.add()
							; 
					}
				}
				);
		creationMenu.addOption(
				"EXIT MENU",
				new MenuAction(creationMenu, "exit")
				);
		
		editorMenu.addOption(
				"OPEN CREATION MENU",
				new Action() {
					void perform() {
						editorMenu.command("exit");
						creationMenu.open();
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
		
		Theme.current = Theme.mono;
		
		pointer = new MousePointer();
		origin = new Marker("origin", 0, 0);
		origin.add();
		write("test");
		Level.clear();
		read("test");
		
		console.add();
		pointer.add();
		
		editorMenu.open();
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
	
	void write(String s) {
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
