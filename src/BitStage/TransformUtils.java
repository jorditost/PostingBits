package BitStage;

import java.awt.Rectangle;
import java.util.ArrayList;

import processing.core.PApplet;

/**
 * Basic transformation utility methods.
 */
public class TransformUtils {
	
	public static ArrayList<StageElement> scaleStageElementsArray(ArrayList<StageElement> array, float factor) {
		  
		// We have to clone the stage elements so as not to modify original values from BitStage
		  
		ArrayList<StageElement> clonedArray = new ArrayList<StageElement>();
		  
		for (StageElement stageElement : array) {
			
			if (!stageElement.isNew()) {
				PApplet.println("element with id " + stageElement.id + " was already initialized!");
			}
		    
			StageElement clonedStageElement = stageElement.clone();
		    
		    Rectangle clonedBoundingBox = clonedStageElement.getBoundingBox();
		    
		    clonedBoundingBox.x      *= factor;
		    clonedBoundingBox.y      *= factor;
		    clonedBoundingBox.width  *= factor;
		    clonedBoundingBox.height *= factor;
		    
		    clonedArray.add(clonedStageElement);
		}
		  
		return clonedArray;
	}
	
	public static Rectangle scaleBoundingBox(Rectangle r, float factor) {
		
		Rectangle rScaled = new Rectangle();
		rScaled.x      = r.x;
		rScaled.y      = r.y;
		rScaled.width  = r.width;
		rScaled.height = r.height;
		
		// Scale
		rScaled.x      *= factor;
		rScaled.y      *= factor;
		rScaled.width  *= factor;
		rScaled.height *= factor;
		
		return rScaled;
	}

	public static ArrayList<Rectangle> scaleRectanglesArray(ArrayList<Rectangle> array, float factor) {
		for (Rectangle r : array) {
		    r.x      *= factor;
		    r.y      *= factor;
		    r.width  *= factor;
		    r.height *= factor;
		}
		return array;
	}
}
