import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import BitStage.*;

/**
 * Performs a basic detection from an image.
 */
public class BlobPersistenceImageExampleApp extends PApplet {

	BitStage stage;
	PImage src;
	ArrayList<StageElement> stageElements;
	ArrayList<StageElement> tempStageElements;
	
	public void setup() {
		src = loadImage("kitchen.jpg");
		stage = new BitStage(this, src.width, src.height);
		
		stage.useColorTracking(true);
		stage.detectRed(166);
		stage.detectGreen(44);
		stage.detectBlue(104);
		
		size(stage.width, stage.height);
	}

	public void draw() {
		stage.loadImage(src);
		//stageElements = stage.detect();
		
		tempStageElements = stage.detect();
		stageElements = TransformUtils.scaleStageElementsArray(tempStageElements, 1.0f);
		
		stage.drawBackground();
		stage.drawStageElements();
		
		for (StageElement stageElement : stageElements) {
			
			TrackingColor trackingColor = stageElement.getTrackingColor();
			
			// It's new: do something and set it as initialized
			if (stageElement.isNew()) {
				PApplet.println("new " + trackingColor.displayName() + " stage element! ID: " + stageElement.id);
				stageElement.initialized();
			}
			
			// Do something always
			if (trackingColor == TrackingColor.RED) {
				this.fill(255,0,0);
				this.stroke(255,0,0);
				this.ellipse(stageElement.getBoundingBox().x + 30, stageElement.getBoundingBox().y - 20, 10, 10);
			} else if (trackingColor == TrackingColor.GREEN) {
				this.fill(0,255,0);
				this.stroke(0,255,0);
				this.ellipse(stageElement.getBoundingBox().x + 30, stageElement.getBoundingBox().y - 20, 10, 10);
			} else if (trackingColor == TrackingColor.BLUE) {
				this.fill(0,0,255);
				this.stroke(0,0,255);
				this.ellipse(stageElement.getBoundingBox().x + 30, stageElement.getBoundingBox().y - 20, 10, 10);
			}
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { BlobPersistenceImageExampleApp.class.getName() });
	}
}
