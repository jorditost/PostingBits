import processing.core.PApplet;
import processing.video.*;

import StageDetector.*;

/**
 * Simple detection with background detection using the camera
 */
public class BackgroundSubtractionCaptureExampleApp extends PApplet {

	StageDetector stage;
	Capture video;

	public void setup() {
		video = new Capture(this, 640, 480);
		video.start();
		
		stage = new StageDetector(this, 640, 480);
		
		// Set background subtraction
		stage.setDetectionMode(DetectionMode.BG_SUBTRACTION);
		
		size(stage.width, stage.height);
	}

	public void draw() {
		if (video.available()) {
		    video.read();
		    
		    // Detect
		    stage.loadImage(video);
			stage.detect();
			
			stage.drawDiff();
			//stage.drawBackground();
			stage.drawStageElements();
		}
	}
	
	public void keyPressed() {
		
		if (key == ENTER) {
			stage.loadBackgroundImage(video);
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { BackgroundSubtractionCaptureExampleApp.class.getName() });
	}
}
