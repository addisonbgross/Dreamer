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
}

class KeyedActorAction extends Action {
	Status status = null;
	KeyedActorAction(Actor a, Status s) {
		object = a;
		status = s;
	}
	void perform() {((Actor)object).addStatus(status);}
	void stop() {((Actor)object).removeStatus(status);}
}
class MouseAction extends Action {
	MouseAction(MousePointer mp, String s) {
		super(mp, s);
	}
}