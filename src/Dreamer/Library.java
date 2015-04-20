package Dreamer;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

class Library {
	private static HashMap<String, ImageTracker> images = new HashMap<String, ImageTracker>();
	private static HashMap<String, File> models = new HashMap<String, File>();
	static Texture tempTexture = null;	
    static Font font;// = new Font("Courier", Font.BOLD, 60);
    static TrueTypeFont messageFont;// = new TrueTypeFont(font, false);
    static Color messageFontColor = Color.black;
	static Color messageFontShadow = Color.gray;
	static TrueTypeFont defaultFont;
	static Color defaultFontColor = Color.black;
    static boolean messages = false;
	
    static Object get(String s, FileType ft) {
    	if(ft == FileType.IMG) {
    		return getImage(s);
    		/*
    		static void loadImage(String file) {
				String referenceName = file.substring(0, file.toString().lastIndexOf("."));
	    		referenceName = referenceName.replace(Constants.RESPATH, "");
	
				ImageTracker tempImage = new ImageTracker(referenceName);
	    		images.put(referenceName, tempImage);
			}
    		*/
    	}
    	return null;
    }
    
	static void load() {
		try {
			importFonts();
			importArt();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * Create new Model object composed of Shape3d objects
	 * 
	 * @param s name of Obj file
	 * @param x x origin of model
	 * @param y y origin of model
	 * @param z z origin of model
	 * @return ArrayList of Shape3d objects that compose model
	 */
	static ArrayList<Shape3d> getModel(String s, int x, int y, int z) {
		return getModel(s, Constants.DEFAULTMODELSCALE, x, y, z);
	}
	static ArrayList<Shape3d> getModel(String s, int scale, int x, int y, int z) {
		ArrayList<Shape3d> modelList = null;
		try {
			modelList = ObjLoader.loadModel(models.get(s), scale);
			for (Shape3d m : modelList) {
				m.setPosition(x, y, z);
				m.generateMotionTracks();
			}
		} catch (FileNotFoundException e) { //model not found
			System.out.println("Model [" + s + "] not found!");
			e.printStackTrace();
		} catch (IOException e) {//model not found in a different way
			System.out.println("IOException while loading model [" + s + "]");
			e.printStackTrace();
		}
		return modelList;
	}
	
	/**
	 * Gather a list of points within the specified Obj file where new light objects
	 * should be placed
	 * 
	 * @param s name of Obj file
	 * @param x x origin of model
	 * @param y y origin of model
	 * @param z z origin of model
	 * @return ArrayList of points where light objects will be created
	 */
	static ArrayList<Vector3f> getModelLights(String s, int x, int y, int z) {
		return getModelLights(s, Constants.DEFAULTMODELSCALE, x, y, z);
	}
	static ArrayList<Vector3f> getModelLights(String s, int scale, int x, int y, int z) {
		ArrayList<Vector3f> lightPointList = null;
		try {
			lightPointList = ObjLoader.loadLights(models.get(s), scale, x, y, z);
		} catch (FileNotFoundException e) { //model not found
			System.out.println("Model [" + s + "] not found!");
			e.printStackTrace();
		} catch (IOException e) {//model not found in a different way
			System.out.println("IOException while loading model [" + s + "]");
			e.printStackTrace();
		}
		return lightPointList;
	}
	static Texture getTexture(String s) {
		try {
			return images.get(s).image.getTexture();
		} catch(Exception e) {
			return images.get("fail").image.getTexture();
		}
	}
	static private void loadModel(String file) {
		String referenceName = file.substring(0, file.length());
		referenceName = referenceName.replace(Constants.RESPATH, "");
		try {
			String modelName = referenceName.replace(Constants.MODELPATH, "").replace(".obj", "");
            models.put(modelName, new File(Constants.RESPATH + referenceName));
        } catch (Exception e) {
            System.out.println("Failure to load the model file: " + referenceName);
            e.printStackTrace();
        }
	}
	static void loadImage(String file) {
		String referenceName = file.substring(0, file.toString().lastIndexOf("."));

		ImageTracker tempImage = new ImageTracker(referenceName + ".png");
		referenceName = referenceName.replace("backgrounds\\", "").replace("backgrounds/", "");
		referenceName = referenceName.replace("res\\", "").replace("res/", "");
    	images.put(referenceName, tempImage);
	}
	static void importArt() throws IOException {
		Files.walk(Paths.get(Constants.RESPATH)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				if (filePath.toString().contains(".obj"))
					loadModel(filePath.toString());
				else if (filePath.toString().contains(".png"))
					loadImage(filePath.toString()); 
			}
		});
	}
	static void importFonts() {
		// load font from a .ttf file
		InputStream inputStream;

		Font chunk;
 		try {
			inputStream = ResourceLoader.getResourceAsStream(Constants.RESPATH + Constants.FONTSPATH + "8-bit Madness.ttf");
			chunk = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			chunk = chunk.deriveFont(60f); // set font size
			messageFont = new TrueTypeFont(chunk, true);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			image =  new Image(s, true, Image.FILTER_NEAREST);
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
	Image scale(float f) {
		if((int)(f * 100) != scale)
		{
			scale = (int)(f * 100);
			scaledImage = image.getScaledCopy(f);
		}
		return scaledImage;
	}
	Image original() { return image; }
}
