package BitStage;

import gab.opencv.Contour;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Utils for Contours
 */
public class ContourUtils {
	
	/**
     * Returns the first contours of an array 
     * (normally the biggest ones, since it is configurable in getContours()) 
     * 
     * @return ArrayList<Contour>
     * 			An array of the contours
     * @param maxContours
     * 			Maximum contours to return
	 */
	public static ArrayList<Contour> getFirstContours(ArrayList<Contour> contoursArray, int maxContours) {
		
		if (contoursArray.size() <= maxContours) {
			return contoursArray;
		}
		
		return new ArrayList<Contour>(contoursArray.subList(0, maxContours-1));
	}
	
	/**
     * Check if two contours are the same.
     * 
     * @param c1
     * 			First Contour
     * @param c2
     * 			Second Contour
	 */
	public static boolean contoursAreTheSame(Contour c1, Contour c2) {
	    
		Rectangle rect1 = c1.getBoundingBox();
		Rectangle rect2 = c2.getBoundingBox();
 		
		return (Math.abs(rect1.x - rect2.x) < 8 && 
				Math.abs(rect1.y - rect2.y) < 8 && 
				Math.abs(rect1.width - rect2.width) < 8 && 
				Math.abs(rect1.height - rect2.height) < 8);
	}
	
}
