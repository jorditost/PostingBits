import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import PostingBits.*;

/**
 * Performs a basic detection from an image.
 */
public class ImageExampleApp extends PApplet {

	PostingBits stage;

	public void setup() {
		stage = new PostingBits(this, "kitchen.jpg");
		
		stage.setThreshold(115);
		//stage.useColorTracking(true);
		//stage.detectRed(166);
		//stage.detectGreen(44);
		//stage.detectBlue(104);
		
		// List all filter values
		stage.listFilterValues();
		
		stage.detect();
		
		size(stage.width, stage.height);
	}

	public void draw() {
		stage.drawBackground();
		stage.drawStageElements();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { ImageExampleApp.class.getName() });
	}
}
