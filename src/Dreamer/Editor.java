package Dreamer;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Editor {

	enum Mode {
		NUMERIC, COMMAND
	};

	MousePointer pointer = new MousePointer();
	ShadowedMessage prompt = new ShadowedMessage("", 0, 100);
	ShadowedMessage console = new ShadowedMessage("", 0, 0);
	Menu editorMenu = new Menu(Justification.LEFT, -Constants.screenWidth / 2, 200);
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
		editorMenu.addOption("MAKE", () -> {
			
			ShapeMaker.menu.setParent(editorMenu);
			ShapeMaker.menu.open();

			pointer.onLeftClick = ()-> {
				ShapeMaker.addFocus();
				pointer.onMove = ()-> {};
			};
		});
		editorMenu.addOption("EDIT TRACKS", ()-> { new TrackEditor(); });
		editorMenu.addOption("TRANSLATE", ()-> {
			pointer.onMove = ()-> {
				ShapeMaker.focus.setPosition(pointer.getX(),
						pointer.getMaxY(), pointer.getZ());
			};
		});
		editorMenu.addOption("CHANGE Z", ()-> {
			pointer.onMove = ()-> {
				pointer.setZ(pointer.getZ() + pointer.lastXVel);
				ShapeMaker.focus.setPosition(ShapeMaker.focus.getX(),
						ShapeMaker.focus.getY(), pointer.getZ());
			};
		});
		editorMenu.addOption("ROTATE", ()-> {
			pointer.onMove = ()-> {
				Vector3f v = new Vector3f(pointer.getX(), pointer
						.getY(), 1);
				Vector3f n = new Vector3f(pointer.lastX, pointer.lastY,
						1);
				v = Vector3f.cross(v, n, v);
				v.normalise();
				ShapeMaker.focus.rotate(v.x, v.y, v.z,
						0.01f * (pointer.lastXVel + pointer.lastYVel));
			};
		});
		editorMenu.addOption("SCALE", ()-> {
			mode = Mode.NUMERIC;
			editorKeys.add();

			currentAction = ()-> {
					try {
						ShapeMaker.focus.scale(numericInput.get(0));
					} catch (Exception e) {
						// bad input
					}
					editorMenu.open();
			};
		});
		editorMenu.addOption("RANDOM", ()-> { ShapeMaker.focus.randomize(0.5f); });
		editorMenu.addOption("PLAY", ()-> {
			Element.debug = false;
			Player.getFirst().add();
			KeyHandler.openGameKeys();
			Camera.focus(new ClassFocus(150, Ninja.class));
			Foreground.foreground.clear();
			pointer.remove();
		});
		editorMenu.addOption("CHANGE BACKGROUND", ()-> {
			Menu backgroundMenu = new Menu(Justification.CENTER, 0, 150);
			backgroundMenu.setParent(editorMenu);
			
			for (File file : new File(Constants.BACKGROUNDPATH).listFiles()) {
				backgroundMenu.addOption(file.getName(), ()-> {
					Background.background.clear();
					new Background(file.getName().replace(".png", "")).add();;
				});
			}
			
			backgroundMenu.addExitOption();
			backgroundMenu.open();
		});
		editorMenu.addOption("SAVE", ()-> {
			
			editorMenu.exit();

			prompt("Enter level name", ()-> {
					pointer.remove();
					Foreground.foreground.clear();
					Level.write(lastCommand);
					editorMenu.open();
					pointer.add();
				}
			);
		});
		editorMenu.addOption("OPEN", ()-> { Level.openSelectionMenu(editorMenu); });
		editorMenu.addOption("EXIT", ()-> { new MainMenu(); });
	}

	void prompt(String s, Performable p) {
		mode = Mode.COMMAND;
		prompt.name = s;
		prompt.add();
		currentAction = p;
		editorKeys.add();
	}
	
	void start() {
		KeyHandler.openEditorKeys(this);
		Camera.focus(0, 0, 2000);
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
					// bad number input
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
	
	MousePointer pointer = new MousePointer();
	ArrayList<Marker> pointList = new ArrayList<>();
	
	TrackEditor() {
		
		Element.debug = true;
		
		pointer.setPosition(0, 0, 0);
		pointer.add();
		pointer.onLeftClick = ()-> {
			Vector3f v = pointer.getPosition3f();
			Marker m = new Marker(pointList.size() + "", v.x, v.y);
			m.add();
			pointList.add(m);
		};
		pointer.onRightClick = ()-> {
			
			for(int i = 1; i < pointList.size(); i++) {
				Marker start = pointList.get(i - 1);
				Marker end = pointList.get(i);
				new MotionTrack(start.getX(), start.getY(), end.getX(), end.getY()).add();
				end.remove();
				start.remove();
			}
			pointList.clear();
		};
	}
}
