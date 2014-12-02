package Dreamer;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

class Library {
	private static HashMap<String, ImageTracker> images = new HashMap<String, ImageTracker>();
	private static HashMap<String, Shape3d> models = new HashMap<String, Shape3d>();
	static Texture tempTexture = null;	
    static Font font;// = new Font("Courier", Font.BOLD, 60);
    static TrueTypeFont messageFont;// = new TrueTypeFont(font, false);
    static Color messageFontColor = Color.black;
	static Color messageFontShadow = Color.gray;
	static TrueTypeFont defaultFont;
	static Color defaultFontColor = Color.black;
    static boolean messages = false;
	
	//main tests loading the current images 
	public static void test() throws IOException {
		System.out.println("Initializing Library");
		Dreamer.init();
		Library.messages = true;
		Library.load();
	}
	static void load() throws IOException {
		importFonts();
		importArt();
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
	static Shape3d getModel(String s) {
		Shape3d m = new Shape3d();
		m = models.get(s);
		return m;
	}
	static Texture getTexture(String s) {
		return images.get(s).image.getTexture();//textures.get(s);
	}
	static void importArt() throws IOException {
		Files.walk(Paths.get(Constants.RESPATH)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				if (!filePath.toString().contains(Constants.FONTSPATH) && !filePath.toString().contains(Constants.LEGACYPATH) && !filePath.toString().contains(Constants.SOUNDSPATH)) {
					// load obj file model
					if (filePath.toString().contains(".obj")) {
						String tempName = filePath.toString().substring(0, filePath.toString().length());
						tempName = tempName.replace(Constants.RESPATH, "");
						try {
							String modelName = tempName.replace(Constants.MODELPATH, "").replace(".obj", "");
	                        models.put(modelName, ObjLoader.loadModel(new File(Constants.RESPATH + tempName)));
                        } catch (Exception e) {
	                        System.out.println("Failure to load the model: " + tempName);
	                        e.printStackTrace();
                        }
					// load standard png image
					} else if (!filePath.toString().contains("models")) {
						String tempName = filePath.toString().substring(0, filePath.toString().lastIndexOf("."));
				    	tempName = tempName.replace(Constants.RESPATH, "");
						
						ImageTracker tempImage = new ImageTracker(tempName);
				    	images.put(tempName, tempImage);
					}
				} 
		    }
		});
	}
	static void importModels() throws IOException {
		Files.walk(Paths.get(Constants.RESPATH)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				if (!filePath.toString().contains(Constants.FONTSPATH) && !filePath.toString().contains(Constants.LEGACYPATH) && !filePath.toString().contains(Constants.SOUNDSPATH)) {
					String tempName = filePath.toString().substring(0, filePath.toString().lastIndexOf("."));
			    	tempName = tempName.replace(Constants.RESPATH, "");
					
					ImageTracker tempImage = new ImageTracker(tempName);
			    	images.put(tempName, tempImage);
				}
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
