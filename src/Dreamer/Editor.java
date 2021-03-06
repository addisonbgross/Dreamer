package Dreamer;

import interfaces.Performable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.geom.Rectangle;

import enums.FontType;
import enums.Justification;

public class Editor {

	enum Mode {
		NUMERIC, COMMAND
	};

	MousePointer pointer = new MousePointer();
	Text prompt = new Text("", 0, 100);
	Text console = new Text("", 0, 0);
	Menu editorMenu = new Menu(Justification.LEFT, -Constants.screenWidth / 2,
			200);
	EditorKeys editorKeys = new EditorKeys(this);
	Mode mode = Mode.COMMAND;
	Performable currentAction;
	ArrayList<Float> numericInput = new ArrayList<Float>();
	String lastCommand = "";
	static {
		ShapeMaker.init();
	}

	Editor() {
		init();
	}

	void init() {

		editorMenu.addOption("LIST ACTIVE OBJECTS", () -> {

			Menu objectMenu = new Menu(Justification.CENTER, 0, 0);
			objectMenu.setParent(editorMenu);
			
			Manager.activeDrawingSet.stream().forEach(
				(d) -> {
					try {
						Positionable p = (Positionable)d;
						objectMenu.addOption( p.toString(), 
							()-> Camera.focus(p) );
						
					} catch(ClassCastException cce) {
						
						System.out.println("Editor.java 52: Class not Positionable");	
					}
				}
			);
			
			objectMenu.addExitOption();
			objectMenu.setFont(FontType.DEFAULT);
			objectMenu.open();
		});
		
		Performable selectNear = ()-> {
		
			Manager.activeDrawingSet.stream().forEach( (d)-> {
				
				if(d.getClass().isAssignableFrom(Shape3d.class)) { 
				
					Shape3d s = (Shape3d)d;
					
					boolean xNear = Math.abs(pointer.getX() - s.getX()) < 10;
					boolean yNear = Math.abs(pointer.getY() - s.getY()) < 10;
					
					if(xNear && yNear)
						ShapeMaker.focus = s;
				}
			});
			
			pointer.onLeftClick = ()-> {

				ShapeMaker.addFocus();
			};
		};

		editorMenu.addOption("MAKE SHAPE3D", ()-> {

			ShapeMaker.menu.setParent(editorMenu);
			ShapeMaker.menu.open();

			pointer.onLeftClick = ()-> {

				ShapeMaker.addFocus();
				pointer.onLeftClick = selectNear;
			};
			
			pointer.onMove = ()-> {

				ShapeMaker.focus.setPosition(pointer.getX(),
						pointer.getY(), pointer.getZ());
			};
		});

		editorMenu.addOption("MAKE MODEL", ()-> {

			Menu modelMenu = new Menu(Justification.CENTER, 0, 150);
			modelMenu.setParent(editorMenu);

			for (File file : new File(Constants.RESPATH
					+ Constants.MODELPATH).listFiles()) {

				modelMenu.addOption(file.getName(), () -> {

					String f = file.getName().replace(".obj", "");
					Background.background.clear();
					Model m = new Model(f, 200, 0, 0, 0);
					m.add();
					ShapeMaker.focus = m.getFirst();
					
					pointer.onMove = () -> {

						ShapeMaker.focus.setPosition(pointer.getX(),
								pointer.getY(), pointer.getZ());
					};
				});
			}

			modelMenu.addExitOption();
			modelMenu.open();
		});

		editorMenu.addOption("EDIT TRACKS", () -> {

			new TrackEditor(editorMenu);
		});

		editorMenu.addOption("CHANGE Z", () -> {

			pointer.onMove = () -> {
				pointer.setZ(pointer.getZ() + pointer.lastXVel);
				ShapeMaker.focus.setPosition(ShapeMaker.focus.getX(),
						ShapeMaker.focus.getY(), pointer.getZ());
			};
		});

		editorMenu.addOption("ROTATE", () -> {

			pointer.onMove = () -> {
				Vector3f v = new Vector3f(pointer.getX(), pointer
						.getY(), 1);
				Vector3f n = new Vector3f(pointer.lastClickedX,
						pointer.lastClickedY, 1);
				v = Vector3f.cross(v, n, v);
				v.normalise();
				ShapeMaker.focus.rotate(v.x, v.y, v.z,
						0.01f * (pointer.lastXVel + pointer.lastYVel));
			};
		});

		editorMenu.addOption("SCALE", () -> {

			mode = Mode.NUMERIC;
			editorKeys.add();

			currentAction = () -> {
				try {
					ShapeMaker.focus.scale(numericInput.get(0));
				} catch (Exception e) {
					// bad input
				}
				editorMenu.open();
			};
		});

		editorMenu.addOption("RANDOM", () -> {
			ShapeMaker.focus.randomize(0.5f);
		});

		editorMenu.addOption("PLAY", () -> {

			Manager.debug = false;
			Player.getFirst().add();
			Keys.openGameKeys();
			Camera.focus(new ClassFocus(150, Ninja.class));
			Foreground.foreground.clear();
			pointer.remove();
		});

		editorMenu.addOption("CHANGE BACKGROUND", () -> {

			Menu backgroundMenu = new Menu(Justification.CENTER, 0, 150);
			backgroundMenu.setParent(editorMenu);

			for (File file : new File(Constants.BACKGROUNDPATH).listFiles()) {
				backgroundMenu.addOption(file.getName(), () -> {
					Background.background.clear();
					new Background(file.getName().replace(".png", "")).add();
					;
				});
			}

			backgroundMenu.addExitOption();
			backgroundMenu.open();
		});

		editorMenu.addOption("SAVE", () -> {

			editorMenu.exit();

			prompt("Enter level name", () -> {
				pointer.remove();
				Foreground.foreground.clear();
				Level.write(lastCommand);
				editorMenu.open();
				pointer.add();
			});
		});

		editorMenu.addExitOption();
	}

	void prompt(String s, Performable p) {

		mode = Mode.COMMAND;
		prompt.name = s;
		prompt.add();
		currentAction = p;
		editorKeys.add();
	}

	void start() {

		Keys.clearKeys();
		Keys.openEditorKeys(this);
		Camera.reset();
		Theme.current = Theme.mono;
		console.add();
		pointer.add();
		editorMenu.open();
	}

	void command(String s) {

		if (mode == Mode.NUMERIC) {

			numericInput.clear();

			for (String st : s.split("[, ]")) {
				
				try {
					numericInput.add(Float.parseFloat(st + "f"));
				
				} catch (NumberFormatException nfe) {
					
					System.err.println("Incorrect numeric input");
				}
			}

			currentAction.perform();

		} else if (mode == Mode.COMMAND) {

			lastCommand = s;

			switch (s) {

			case "menu":
				new MainMenu();
				break;

			case "exit":
				editorMenu.open();
				break;

			default:
				System.err.println("Invalid command");
				break;
			}

			currentAction.perform();
		}
	}
}

class TrackEditor {

	MousePointer pointer;
	ArrayList<Marker> pointList = new ArrayList<>();
	ArrayList<MotionTrack> trackList = new ArrayList<>();
	ArrayList<MotionTrack> deleteList = new ArrayList<>();
	Rectangle selectionRectangle = null;

	TrackEditor(Menu callback) {

		Manager.trackview = true;
		pointer = MainMenu.editor.pointer;

		Menu trackMenu = new Menu(Justification.LEFT,
				-Constants.screenWidth / 2, Constants.screenHeight / 2 - 80);

		trackMenu.setParent(callback);

		trackMenu.addOption("MAKE TRACK POINT", () -> {

			Vector3f v = pointer.getPosition3f();
			Marker m = new Marker(pointList.size() + "", v.x, v.y);
			m.add();
			pointList.add(m);
		});

		trackMenu.addOption("MAKE TRACKS", () -> {

			for (int i = 1; i < pointList.size(); i++) {

				Marker start = pointList.get(i - 1);
				Marker end = pointList.get(i);
				MotionTrack mt = new MotionTrack(start.getX(), start.getY(),
						end.getX(), end.getY());
				mt.add();
				trackList.add(mt);
				end.remove();
				start.remove();
			}

			pointList.clear();
		});

		trackMenu.addOption("DELETE", () -> {

			for (MotionTrack mt : deleteList) {
				trackList.remove(mt);
				mt.remove();
			}
			deleteList.clear();
		});

		trackMenu.addOption("EXIT", () -> {
			trackMenu.exit();
			Manager.trackview = false;
		});

		trackMenu.open();

		for (Serializable s : Manager.masterList) {

			if ((MotionTrack.class).isAssignableFrom(s.getClass())) {
				trackList.add((MotionTrack) s);
			}
		}

		pointer.setPosition(Camera.getCenterX(), Camera.getCenterY(), 0);

		pointer.onLeftClick = () -> pointer.startSelection();

		pointer.onMove = () -> pointer.updateSelection();

		pointer.onLeftClickRelease = () -> {

			for (MotionTrack mt : deleteList)
				mt.highlighted = false;

			deleteList.clear();

			for (Positionable p : pointer.getSelection()) {

				if (MotionTrack.class.isAssignableFrom(p.getClass())) {

					deleteList.add((MotionTrack) p);
					((MotionTrack) p).highlighted = true;
				}
			}

			pointer.resetSelection();
		};

		pointer.add();
	}
}