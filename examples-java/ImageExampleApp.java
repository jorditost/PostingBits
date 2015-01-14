import processing.core.PApplet;
import processing.core.PImage;
import BitStage.*;

/**
 * Performs a basic detection from an image.
 */
public class ImageExampleApp extends PApplet {

	BitStage stage;

	public void setup() {
		stage = new BitStage(this, "kitchen.jpg");
		
		stage.useColorTracking(true);
		stage.setRedHue(166);
		//stage.setGreenHue(44);
		stage.setBlueHue(104);
		
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
