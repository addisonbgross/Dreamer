package Dreamer;

import org.newdawn.slick.Color;

public class Constants {

	static int screenWidth = 800, screenHeight = 600;
	public static int ladderWidth = 50;
	public static String slash 
		= System.getProperty("os.name").startsWith("W") ? "\\" : "/";

	static final String 
		RESPATH = "res" + slash,
		LEVELPATH = RESPATH + "levels" + slash,
		SHAPE3DPATH = RESPATH + "shape3d" + slash,
		MAPPATH = "maps" + slash,
		FONTSPATH = "fonts" + slash,
		SOUNDSPATH = "sounds" + slash,
		MODELPATH = "models" + slash,
		BACKGROUNDPATH = RESPATH + "backgrounds" + slash
		;

	static final float ACTORACCELERATION = 1.7f;
	static final int ACTORLOOKX = 500, ACTORLOOKY = 200;
	static final int DEFAULTPATROLRANGE = 500;
	// resolution of collision detection
	// TODO switch collision methods and deprecate the shit out of this
	static final int ADJUSTSTEPS = 20;
	static final float AIRFRICTION = 0.02f, TERRAINFRICTION = 0.25f,
			GROUNDFRICTION = 0.4f, STATICFRICTION = 0.8f;
	// buffer for detecting collisions and fidelity of collision detection
	static final int COLLISIONINTERVAL = 30;
	static final int ENEMYMOTIONBUFFER = 80;
	static final int ENEMYJUMPRANGEX = 200;
	static final int ENEMYATTACKRANGE = 80;
	public static final int ENEMYATTACKWAIT = 30;
	static final int EXISTENCEBUFFER = 500;
	static final float GRAVITY = 2f;
	static final int JUMP = 5;
	static final int JUMPBUFFER = 10;
	public static final float LIGHTDISTANCE = 600;
	static final int MARKERSIZE = 20;
	// limit for testing collisions, after that adjusts velocity to 1 in current
	// direction
	static final int MAXTEST = 20;
	static final float PLAYERJUMPVEL = 35, PLAYERMAXVEL = 35;
	static final int DAMAGESTUN = 35;
	static final float SHADOWOFFSET = 4;
	static final int SNOWHEIGHT = 2000;
	static final int STARTX = -200, STARTY = 1;
	static final float VEL = 18;
	static final float SPRINTVEL = 25;
	static final float SPRINTSTAMINA = 0.1f;
	static final int VIEWMARGIN = 100;
	static final int FALLRESET = -10000;
	static final float STARTINGHEALTH = 100;
	static final float STARTINGSTAMINA = 100;
	static final float STAMINAREGEN = 0.20f;
	public static final int NUMFRAMES = 4;
	public static final Color COLLISIONCOLOUR = Color.yellow;
	public static final Color HIGHLIGHTCOLOR = Color.red;
	// models
	static int DEFAULTMODELSCALE = 100;
}
