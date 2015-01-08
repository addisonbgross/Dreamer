package Dreamer;

class Action {
	void perform() {}
	void stop() {}
	void perform(Actor a) {}
}

class KeyedActorAction extends Action {
	String command = "";
	Actor actor;
	
	KeyedActorAction(Actor a, String s) {
		command = s;
		actor = a;
	}
	
	void perform() {actor.addStatus(command);}
	void stop() {actor.removeStatus(command);}
}

class CameraAction extends Action {
	String command = "";
	
	CameraAction(String s) {command = s;}
	
	void perform() {Camera.move(command);}
	void stop() {Camera.move("stop");}
}