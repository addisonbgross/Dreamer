package Dreamer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import quicktime.streaming.EditEntry;

import com.sun.j3d.utils.scenegraph.io.state.javax.media.j3d.ModelClipState;
import com.sun.org.apache.xml.internal.security.Init;

import apple.laf.JRSUIConstants.Focused;

public class Editor {
	
	enum Mode {NUMERIC, COMMAND};
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
	EditorKeys editorKeys = new EditorKeys(this);
	Mode mode = Mode.COMMAND;
	Action currentAction = new Action();
	ArrayList<Float> numericInput = new ArrayList<Float>();
	
	Editor() { 
		init();
	}
	
	void init() {
		editorMenu.addOption(
				"COMMAND",
				new Action() {
					void perform() {
						mode = Mode.COMMAND;
						editorKeys.add();
					}
				}
				);
		editorMenu.addOption(
				"NUMERIC",
				new Action() {
					void perform() {
						mode = Mode.NUMERIC;
						editorKeys.add();
					}
				}
				);
		editorMenu.addOption(
				"MAKE", 
				new Action() {
					void perform() {
						if(focus != null) {
							focus.remove();	
						}
						focus = ShapeMaker.make("block");
						focus.add();
						
						pointer.onLeftClick = new Action() {
							void perform() {
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
								pointer.onMove = new Action();
							}
						};
					}
				}
				);
		editorMenu.addOption(
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
		editorMenu.addOption("ROTATE", new Action() {
			void perform() {
				pointer.onMove = new Action() {
					void perform() {
						Vector3f v = new Vector3f(
								pointer.getX(),
								pointer.getY(),
								1
								);
						Vector3f n = new Vector3f(
								pointer.lastX,
								pointer.lastY,
								1
								);
						v = Vector3f.cross(v, n, v);
						v.normalise();
						focus.rotate(
								v.x, 
								v.y, 
								v.z, 
								0.01f * (pointer.lastXVel + pointer.lastYVel)
								);
					}
				};
				
				/*
				focus.remove();
				focus = focus
					.makeDynamic()
					.clearTransformers()
					.addTransformer(new Rotator(1, 0, 0, 0.1f))
					;
				focus.add();
				*/
			}
		});
		editorMenu.addOption(
				"SCALE", 
				new Action() {
					void perform() {
						mode = Mode.NUMERIC;
						editorKeys.add();
						
						currentAction = new Action() {
							void perform() {
								try {
									focus.scale(numericInput.get(0));
								} catch (Exception e) {
									// bad input
								}
							}
						};
					}
				}
				);
		editorMenu.addOption(
				"RANDOM", 
				new Action() {
					void perform() {
						focus.randomize(0.5f);
					}
				}
				);
		editorMenu.addOption(
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
	
		new Sun().add();
		
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

		if(mode == Mode.NUMERIC) {
			numericInput.clear();
			for(String st: s.split("[, ]")) {
				try {
					numericInput.add(Float.parseFloat(st + "f"));
				} catch(NumberFormatException nfe) {
					// bad number input
				}
			}
			currentAction.perform();
		} else {
			switch(s) {
				case "menu":
					new MainMenu();
					break;
					
				case "exit":
					editorMenu.open();
					break;
					
				default:
					System.out.println("invalid command");
					break;
			}
		}
		
		KeyHandler.clearKeys();
		editorMenu.open();
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
