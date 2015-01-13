import processing.core.PApplet;
import processing.video.*;
import BitStage.*;

/**
 * Performs a basic detection from a camera.
 */
public class CaptureExampleApp extends PApplet {

	BitStage stage;
	Capture video;

	public void setup() {
		video = new Capture(this, 640, 480);
	    video.start();
	    
	    stage = new BitStage(this, 640, 480);
	    //stage.setDetectionMode(DetectionMode.CHANNEL_GRAY);
	    
	    stage.listFilterValues();
	    
	    size(stage.width, stage.height);
	}

	public void draw() {
		
		if (video.available()) {
			video.read();
			
			stage.loadImage(video);
			stage.detect();
			    
			stage.drawBackground();
			stage.drawStageElements();
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { CaptureExampleApp.class.getName() });
	}
}
