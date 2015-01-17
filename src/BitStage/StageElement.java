package BitStage;

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
	//private Rectangle boundingBox;
  
	private TrackingColor trackingColor;
	
	// Am I available to be matched?
	private boolean available;
	  
	// Should I be deleted?
	private boolean delete;
	  
	// How long should I live if I have disappeared?
	private int persistence = 5; //127;
	private int timer;
	  
	// Unique ID for each stage element
	public int id;
	
	
	/**
     * Initialize StageElement
     * 
     * @param theParent
     * 			A PApplet representing the user sketch, i.e "this"
     * @param id
     */
	public StageElement(PApplet parent, int id) {
		this.parent = parent;
		this.id = id;
		
		//this.boundingBox = new Rectangle();
		this.trackingColor = TrackingColor.NONE;
		
		this.available = true;
	    this.delete = false;
	    this.timer = persistence;
	}
	
	/**
     * Initialize StageElement with a bounding box and a tracking color
     * 
     * @param theParent
     * 			A PApplet representing the user sketch, i.e "this"
     * @param id
     * @param contour
     * @param trackingColor
     */
	public StageElement(PApplet parent, int id, Contour c) {
		this.parent = parent;
		this.id = id;
		
		this.contour = new Contour(parent, c.pointMat);
		this.trackingColor = TrackingColor.NONE;
		
		this.available = true;
	    this.delete = false;
	    this.timer = persistence;
	}
	
	/**
     * Initialize StageElement with a bounding box and a tracking color
     * 
     * @param theParent
     * 			A PApplet representing the user sketch, i.e "this"
     * @param boundingBox
     * @param trackingColor
     */
	/*public StageElement(PApplet parent, Rectangle boundingBox, TrackingColor trackingColor) {
		this.parent = parent;
		this.id = id;
		
		this.boundingBox = boundingBox;
		this.trackingColor = trackingColor;
		
		this.available = true;
	    this.delete = false;
	    this.timer = persistence;
	}*/
	
	/**
	 * Get the contour.
	 * 
	 * @return Contour
	 */
	public Contour getContour(){
		return contour;
	}
	
	/**
	 * Get the bounding box for the Contour.
	 * 
	 * @return A java.awt.Rectangle
	 */
	public Rectangle getBoundingBox(){
		return contour.getBoundingBox();
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
		StageElement tmp = new StageElement(parent, id); 
		tmp.trackingColor = trackingColor;
		tmp.contour = new Contour(parent, contour.pointMat);
		//tmp.boundingBox = boundingBox;
		//tmp.rect = (Rectangle)(rect.clone());
		
		tmp.available = available;
		tmp.delete = delete;
		tmp.timer = timer;
		
		return tmp; 
	}
	
	
	/**
     * Display StageElement
     */
	public void draw() {
		
	    Rectangle r = contour.getBoundingBox();
		
		float opacity = PApplet.map(timer, 0, persistence, 0, 150);
		
		switch (trackingColor) {
        case RED:
        	parent.stroke(255, 0, 0);
        	parent.fill(255, 0, 0, opacity);
            break;
        case GREEN:
        	parent.stroke(0, 255, 0);
        	parent.fill(0, 255, 0, opacity);
            break;
        case BLUE:
        	parent.stroke(0, 0, 255);
        	parent.fill(0, 0, 255, opacity);
            break;  
        default:
        	parent.stroke(255, 255, 0);
        	parent.fill(255, 255, 0, opacity);
            break;
		}
      
		parent.strokeWeight(1);
		parent.rect(r.x, r.y, r.width, r.height);
		
		// Draw ID
		parent.fill(255, 2*opacity);
		parent.textSize(22);
	    parent.text(""+id, r.x+5, r.y+22);
	}
	
	// Give me a new contour for this blob (shape, points, location, size)
	// Oooh, it would be nice to lerp here!
	public void update(Contour newC) {
	    
	  contour = new Contour(parent, newC.pointMat);
	    
	  // Is there a way to update the contour's points without creating a new one?
	  /*ArrayList<PVector> newPoints = newC.getPoints();
	  Point[] inputPoints = new Point[newPoints.size()];
	    
	  for(int i = 0; i < newPoints.size(); i++){
	    inputPoints[i] = new Point(newPoints.get(i).x, newPoints.get(i).y);
	  }
	  contour.loadPoints(inputPoints);*/
	    
	  timer = persistence;
	}

	// Count me down, I am gone
	public void countDown() {    
	  timer--;
	}

	// I am deed, delete me
	public boolean isDead() {
	  if (timer < 0) return true;
	  return false;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean value) {
		this.available = value;
	}
	
	public void delete() {
		this.delete = true;
	}
	
	public void delete(boolean value) {
		this.delete = value;
	}
}
