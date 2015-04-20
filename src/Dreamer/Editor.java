package Dreamer;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class Editor {

	enum Mode {
		NUMERIC, COMMAND
	};

	MousePointer pointer = new MousePointer();;
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
		editorMenu.addOption("TEST SHAPEMAKER", ()-> {
			ShapeMaker.make("block").scale(3, 2, 3)
				.setColor(new Color(Shape3d.r.nextInt()))
				.randomize(0.8f).rotate(0, 1, 1, 45).makeDynamic()
				.addTransformer(new Rotator(0, 1, 3, 0.3f)).add();
		});
		editorMenu.addOption("PLAY", ()-> {
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
		editorMenu.addOption("OPEN", ()-> { Level.openMenu(editorMenu); });
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
