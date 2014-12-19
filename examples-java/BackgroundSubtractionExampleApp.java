import processing.core.PApplet;
import processing.video.*;
import StageDetector.*;

/**
 * Simple detection with background detection
 */
public class BackgroundSubtractionExampleApp extends PApplet {

	StageDetector stage;
	Movie video;

	public void setup() {
		video = new Movie(this, "map_isight_med.mov");
		
		stage = new StageDetector(this, 640, 480);
		
		// Set background subtraction
		stage.setDetectionMode(DetectionMode.BG_SUBTRACTION);
		
		// Filter image
		stage.useAdaptiveThreshold(true); // seems to work better
		stage.setMinBlobSize(40);
		
		size(stage.width, stage.height);
		
		video.loop();
		video.play();
	}

	public void draw() {
		if (video.available()) {
		    video.read();
		    
		    // Detect
		    stage.loadImage(video);
			stage.detect();
			
			stage.drawDiff();
			//stage.drawBackground();
			//stage.drawBackgroundImage();
			//stage.drawOutputImage();
			stage.drawStageElements();
		}
	}
	
	public void keyPressed() {
		
		if (key == ENTER) {
			stage.loadBackgroundImage(video);
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { BackgroundSubtractionExampleApp.class.getName() });
	}
}
