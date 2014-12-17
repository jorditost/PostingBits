package StageDetector;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Basic transformation utility methods.
 */
public class TransformUtils {
	
	public static ArrayList<StageElement> scaleStageElementsArray(ArrayList<StageElement> array, float factor) {
		  
		// We have to clone the stage elements so as not to modify original values from StageDetector
		  
		ArrayList<StageElement> clonedArray = new ArrayList<StageElement>();
		  
		for (StageElement stageElement : array) {
		    
			StageElement clonedStageElement = (StageElement)(stageElement.clone());
		    
		    Rectangle clonedBoundingBox = clonedStageElement.getBoundingBox();
		    
		    clonedBoundingBox.x      *= factor;
		    clonedBoundingBox.y      *= factor;
		    clonedBoundingBox.width  *= factor;
		    clonedBoundingBox.height *= factor;
		    
		    clonedArray.add(clonedStageElement);
		}
		  
		return clonedArray;
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
