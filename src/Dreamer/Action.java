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
	
	void start() {}
	void stop() {}
}

class KeyedActorAction extends Action {
	Status status = null;
	KeyedActorAction(Actor a, Status s) {
		object = a;
		status = s;
	}
	void start() {((Actor)object).addStatus(status);}
	void stop() {((Actor)object).removeStatus(status);}
}
class MouseAction extends Action {
	MouseAction(MousePointer mp, String s) {
		super(mp, s);
	}
}