package Dreamer;

class Action {
	
	String command = "";
	Object object;
	
	Action() {
		this(null, "none");
	}
	Action(String s){
		this(null, s);
	}
	Action(Object o, String s){
		object = o;
		command = s;
	}
	
	void perform() {}
	void stop() {}
	void perform(Actor a) {}
	
	static void lambda(Performable a, Stoppable s) {
		a.perform();
		s.stop();
	}
	
	interface Performable {
		void perform();
	}
	interface Stoppable {
		void stop();
	}
	
	static void test() {
		lambda(
				() -> { System.out.println("YES"); },
				() -> { System.out.println("YES2"); }
				);
	}
}

class KeyedActorAction extends Action {
	KeyedActorAction(Actor a, String s) {
		super(a, s);
	}
	void perform() {((Actor)object).addStatus(command);}
	void stop() {((Actor)object).removeStatus(command);}
}
class MenuAction extends Action {
	MenuAction(Menu m, String s) {
		super(m, s);
	}
	void perform() {((Menu)object).command(command);}
}
class CameraAction extends Action {
	CameraAction(String s) {
		super(s);
	}
	void perform() {Camera.command(command);}
	void stop() {Camera.command("stop");}
}
class MouseAction extends Action {
	MouseAction(MousePointer mp, String s) {
		super(mp, s);
	}
}