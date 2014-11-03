package Dreamer;

import org.newdawn.slick.Color;

//sorted alphabetically
public class Constants {
	static int screenWidth = 1367, screenHeight = 768;

	public static int ladderWidth = 50;
	
	public static final String RESPATH = "res/";
	//these define a box within which vision is active
	static final float ACTORACCELERATION = 1.7f;
	static final int ACTORLOOKX = 800, ACTORLOOKY = 200;
	//resolution of collision detection
	//TODO switch collision methods and deprecate the shit out of this
	static final int ADJUSTSTEPS = 20;
	static final float AIRFRICTION = 0.02f,
			TERRAINFRICTION = 0.25f,
			GROUNDFRICTION = 0.4f, 
			STATICFRICTION = 0.8f;
	//buffer for detecting collisions and fidelity of collision detection
	static final int COLLISIONINTERVAL = 30;
	static final boolean FASTCAMERA = true;
	static final int ENEMYMOTIONBUFFER = 80;
	static final int ENEMYJUMPRANGEX = 200;
	static final int ENEMYATTACKRANGE = 180;
	public static final int ENEMYATTACKWAIT = 30;
	static final int EXISTENCEBUFFER = 500;
	static final float GRAVITY = 2F;
	static final int JUMP = 5;
	static final int JUMPBUFFER = 10;
	static final float LEAFTHRESHHOLD = 0.3f;
	public static final float LIGHTDISTANCE = 600;
	static final int MARKERSIZE = 20;
	//limit for testing collisions, after that adjusts velocity to 1 in current direction
	static final int MAXTEST = 20;
	static final int MEMORYLENGTH = 6;
	static final float OPENGLZ = 1000;
	static final int PERPENDICULARSCALE = 4;
	static final float PERSPECTIVE = 1.5f, PERSPECTIVESQUASH = 4f;
	static final float PLAYERJUMPVEL = 35, PLAYERMAXVEL = 35;
	static final float SHADOWOFFSET = 4;
	static final int SNOWHEIGHT = 2000;
	static final int STARTX = -200, STARTY = 1;
	static final int TOP = 1, RIGHT = 2, BOTTOM = 3, LEFT = 0;
	static final float VEL = 18;
	static final int VIEWMARGIN = 100;
	static final int FALLRESET = -2000;
	static final int STARTINGHEALTH = 100;
	public static final int NUMFRAMES = 4;
	public static final Color COLLISIONCOLOUR = Color.yellow;
	public static final Color TRAILCOLOR = Color.black;
	public static final float TRANSPARENCYRANGE = 400;	
}
