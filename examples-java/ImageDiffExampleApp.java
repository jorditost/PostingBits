import processing.core.PApplet;
import processing.core.PImage;

import StageDetector.*;

/**
 * Simple detection from the difference between two images (background subtraction)
 */
public class ImageDiffExampleApp extends PApplet {

	StageDetector stage;
	PImage before, after;

	public void setup() {
		before = loadImage("map_before.jpg");
		after = loadImage("map_after.jpg");
		
		stage = new StageDetector(this, before.width, before.height);
		
		// Set background substraction
		stage.setDetectionMode(DetectionMode.BG_SUBTRACTION);
		
		// Filter image
		stage.useAdaptiveThreshold(true); // seems to work better
		stage.setMinBlobSize(40);
		
		// Detect
		stage.loadBackgroundImage(before);
		stage.loadImage(after);
		stage.detect();
		
		size(stage.width, stage.height);
	}

	public void draw() {
		//stage.drawDiff();
		stage.drawBackground();
		//stage.drawOutputImage();
		//stage.drawBackgroundImage();
		stage.drawStageElements();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { ImageDiffExampleApp.class.getName() });
	}
}
