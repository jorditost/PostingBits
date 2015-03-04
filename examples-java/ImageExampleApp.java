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
		//stage = new PostingBits(this, "stage-640x480.jpg");
		
		stage.useColorTracking(true);
		//stage.detectRed(166);
		//stage.detectGreen(44);
		//stage.detectBlue(104);
		
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
