import processing.core.PApplet;
import processing.video.*;

import StageDetector.*;

/**
 * Simple detection with background detection
 */
public class BackgroundSubstractionExampleApp extends PApplet {

	StageDetector stage;
	Movie video;

	public void setup() {
		video = new Movie(this, "street.mov");
		
		stage = new StageDetector(this, 640, 480);
		stage.setDetectionMode(DetectionMode.BG_SUBSTRACTION);
		
		size(stage.width, stage.height);
		
		video.loop();
		video.play();
	}

	public void draw() {
		if (video.available()) {
		    video.read();
		    
		    stage.loadImage(video);
			stage.detect();
			
			//stage.drawDiff();
			stage.drawBackground();
			stage.drawStageElements();
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { BackgroundSubstractionExampleApp.class.getName() });
	}
}
