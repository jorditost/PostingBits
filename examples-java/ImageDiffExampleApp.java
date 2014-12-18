import processing.core.PApplet;
import processing.core.PImage;

import StageDetector.*;

/**
 * Simple detection from the difference between two images (background substraction)
 */
public class ImageDiffExampleApp extends PApplet {

	StageDetector stage;
	PImage before, after;

	public void setup() {
		before = loadImage("before.jpg");
		after = loadImage("after.jpg");
		
		stage = new StageDetector(this, before.width, before.height);
		stage.setDetectionMode(DetectionMode.BG_SUBSTRACTION);
		
		//stage.diff(before, after);
		stage.loadBackgroundImage(before);
		stage.loadImage(after);
		stage.detect();
		
		size(stage.width, stage.height);
	}

	public void draw() {
		stage.drawBackground();
		stage.drawStageElements();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { ImageDiffExampleApp.class.getName() });
	}
}
