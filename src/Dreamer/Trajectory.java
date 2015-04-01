package Dreamer;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.geom.Line;

public final class Trajectory implements Updateable {
	
	private Vector3f lastPosition = new Vector3f(), position = new Vector3f();
	private Actor actor;
	
	Trajectory(Actor a) {
		actor = a;
	}
	
	public void update() {
		//for debugging the trajectory
		position = actor.getPosition3f();
		if(lastPosition == null)
			lastPosition.set(actor.getPosition3f());
		else if(
				!(Math.abs(lastPosition.x - position.x) < 0.05f)
				||
				!(Math.abs(lastPosition.y - position.x) < 0.05f)
				||
				!(Math.abs(lastPosition.z - position.x) < 0.05f)
				) {
			//adds trajectory lines for debugging
			new PermanentLine(lastPosition, position).add();
		} 
		
		lastPosition.set(position);
	}
	
	private final class PermanentLine extends Positionable {
		
		private static final long serialVersionUID = 1829625227950634231L;
		
		Line l;
		
		PermanentLine(float ax, float ay, float az, float bx, float by, float bz) {
			setMinX(Math.min(ax, bx));
			setMinY(Math.min(ay, by));
			setWidth(Math.abs(ax - bx));
			setWidth(Math.abs(ay - by));
			l = new Line(getMinX(), getMinY(), getWidth(), getHeight());
		}
		PermanentLine(Vector3f lastPosition, Vector3f position) {
			this(
				lastPosition.x, lastPosition.y, lastPosition.z, 
				position.x, position.y, position.z
				);
		}
		
		@Override
		void draw() {
			if(Element.debug)
				Drawer.drawShape(l, Library.defaultFontColor);
			else
				remove();
		}
	}
}
