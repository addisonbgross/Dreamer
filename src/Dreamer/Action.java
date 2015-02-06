package Dreamer;

class Action {
	String command = "";
	Object object;
	
	Action() {
		this(null, "none");
	}
	Action(Object o){
		this(o, "none");
	}
	Action(Object o, String s){
		object = o;
		command = s;
	}
	
	void perform() {}
	void stop() {}
	void perform(Actor a) {}
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
	void perform() {Camera.move(command);}
	void stop() {Camera.move("stop");}
}