package Dreamer;

public class StatCard {
	String prefix;
	int width, height, collisionWidth, collisionHeight;
	//int[] attackAngles = {220, 0, 30, 45, 60, 90};
	//int[] attackOffsetX = {15, 0, 1, 2, 3, 4, 5};
	//int[] attackOffsetY = {15, 20, 16, 14, 7, 1};
	
	StatCard(String name, int w, int h) {
		prefix = name;
		width = w;
		height = h;
	}
}
