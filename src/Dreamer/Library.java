package Dreamer;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

class Library {
	private static HashMap<String, ImageTracker> images = new HashMap<String, ImageTracker>();
	static Texture tempTexture = null;	
    static Font font;// = new Font("Courier", Font.BOLD, 60);
    static TrueTypeFont messageFont;// = new TrueTypeFont(font, false);
    static Color messageFontColor = Color.black;
	static Color messageFontShadow = Color.gray;
	static TrueTypeFont defaultFont;
	static Color defaultFontColor = Color.black;
    static boolean messages = false;
    
	private static String[] imageNames = {
		"sunset",
		"space",
		"fastgrass",
		"sand",
		"sword",
		"katana",
		"battleaxe",
		"naginata",
		"arrow",
		"smallguy",
		"square", // Test for MeshMaker
		"squarecolormap",
		"maps/test",
		"maps/test_color",
		"maps/longmap",
		"maps/forest_elevation",
		"maps/forest_colour",
		"maps/flat_elevation",
		"maps/flat_colour",
		"fail",
		
		"block",
		"hitblock",
		"brick",
		"yellowblock",
		"grayblock",
		"treebark",
		"ground", 
		"arrow",
		"e_ninja_legs",
		"e_ninja_body",
		"e_ninja_head"
	};
	private static String[] bodyNames = {
		"e_ninja_",
		"e_ninjaalt_"
	};
	
	//main tests loading the current images 
	//TODO check all resources
	public static void test() {
		System.out.println("Initializing Library");
		Dreamer.init();
		Library.messages = true;
		Library.load();
	}
	static void load() {
		importFonts();
		importImages();
	}
	public static void addImage(String string, ImageTracker temp) {
		images.put(string, temp);
	}
	static Image getImage(String s, float f) {
		return images.get(s).scale(f);
	}
	static Image getImage(String s) {
		return images.get(s).original();
	}
	static Texture getTexture(String s) {
		return images.get(s).image.getTexture();//textures.get(s);
	}
	static void importImage() {
		for(String s: imageNames) {
			ImageTracker temp = new ImageTracker(s);
			images.put(s, temp);
		}
	}
	static void importImages() {
		for(String s: imageNames) {
			ImageTracker temp = new ImageTracker(s);
			images.put(s, temp);
		}
	}
	static void importFonts() {
		// load font from a .ttf file
		InputStream inputStream;

		Font chunk;
 		try {
			inputStream = ResourceLoader.getResourceAsStream("res/fonts/8-bit Madness.ttf");
			chunk = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			chunk = chunk.deriveFont(60f); // set font size
			messageFont = new TrueTypeFont(chunk, true);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 		/*
		Font raleway;
 		try {
			inputStream = ResourceLoader.getResourceAsStream("res/fonts/NewShape-Bold.ttf");
			raleway = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			raleway = raleway.deriveFont(62f); // set font size
			//messageFont = new TrueTypeFont(raleway, false);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		//init default truetypefont
	    font = new Font("Serif", Font.BOLD, 14);
	    defaultFont = new TrueTypeFont(font, true);
	}
}
/*ImageTracker: this keeps a scaled reference of an Image and updates this as necessary
 *TODO probably should switch all Images besides backgrounds and foregrounds to textures
 */
class ImageTracker {
	
	Image image;
	Image scaledImage;
	int scale = 100;
	
	ImageTracker() {}
	
	ImageTracker(String s) {
		try {
			image =  new Image(Constants.RESPATH+s+".png", true, Image.FILTER_NEAREST);
			scaledImage = image.copy();
			if(Library.messages) System.out.println("Image import of "+s+".png successful");
		} catch (RuntimeException e) {
			System.err.println("Image import of "+s+".png failed");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Image import of "+s+".png failed");
			e.printStackTrace();
		}
	}
	ImageTracker(String s, String part) {
		try {
			image =  new Image(Constants.RESPATH + s + part + ".png", true, Image.FILTER_NEAREST);
			scaledImage = image.copy();
			if(Library.messages) System.out.println("Image import of " + s + part + ".png successful");
		} catch (RuntimeException e) {
			System.err.println("Image import of " + s + part + ".png failed");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Image import of " + s + part + ".png failed");
			e.printStackTrace();
		}
	}
	Image scale(float f) {
		if((int)(f * 100) != scale)
		{
			scale = (int)(f * 100);
			scaledImage = image.getScaledCopy(f);
		}
		return scaledImage;
	}
	Image original() {return image;}
}
