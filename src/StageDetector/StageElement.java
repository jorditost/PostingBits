package StageDetector;

import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PGraphics;
import gab.opencv.Contour;

/**
 * StageElement
 * A detected stage element. Contains its contours, bounding box and color.
 */
public class StageElement {
  
	PApplet parent;
	
	private Contour contour;
	private Rectangle boundingBox;
  
	private TrackingColor trackingColor;
	
	
	/**
     * Initialize StageElement
     * 
     * @param theParent
     * 			A PApplet representing the user sketch, i.e "this"
     */
	public StageElement(PApplet parent) {
		this.parent = parent;
		this.boundingBox = new Rectangle();
		this.trackingColor = TrackingColor.NONE;
	}
	
	/**
     * Initialize StageElement with a bounding box and a tracking color
     * 
     * @param theParent
     * 			A PApplet representing the user sketch, i.e "this"
     * @param boundingBox
     * @param trackingColor
     */
	public StageElement(PApplet parent, Rectangle boundingBox, TrackingColor trackingColor) {
		this.parent = parent;
		this.boundingBox = boundingBox;
		this.trackingColor = trackingColor;
	}

	
	/**
	 * Get the bounding box for the Contour.
	 * 
	 * @return A java.awt.Rectangle
	 */
	public Rectangle getBoundingBox(){
		return boundingBox;
	}
	
	/**
	 * Get the tracking color
	 * 
	 * @return TrackingColor
	 */
	public TrackingColor getTrackingColor() {
		return trackingColor;
	}
	
	/**
	 * Set the tracking color
	 * 
	 * @param trackingColor
	 */
	public void setTrackingColor(TrackingColor trackingColor) {
		this.trackingColor = trackingColor;
	}
	
	/**
	 * Clone the given stage element
	 * 
	 * @return StageElement
	 */
	public Object clone() { 
		StageElement tmp = new StageElement(parent); 
		tmp.trackingColor = trackingColor;
		tmp.boundingBox = boundingBox;
		//tmp.rect = (Rectangle)(rect.clone());
		return tmp; 
	}
	
	
	/**
     * Display the StageElement
     */
	public void draw() {
		
	    //Rectangle r = contour.getBoundingBox();
		switch (trackingColor) {
        case RED:
        	parent.stroke(255, 0, 0);
        	parent.fill(255, 0, 0, 150);
            break;
        case GREEN:
        	parent.stroke(0, 255, 0);
        	parent.fill(0, 255, 0, 100);
            break;
        case BLUE:
        	parent.stroke(0, 0, 255);
        	parent.fill(0, 0, 255, 100);
            break;  
        default:
        	parent.stroke(255, 255, 0);
        	parent.fill(255, 255, 0, 150);
            break;
		}
      
		parent.strokeWeight(1);
		parent.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
	}
}
