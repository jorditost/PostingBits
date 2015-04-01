import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import gab.opencv.Contour;
import PostingBits.*;

/**
 * Performs a basic detection from an image.
 */
public class PolygonApproxImageExampleApp extends PApplet {

	PostingBits stage;
	ArrayList<StageElement> stageElements;

	public void setup() {
		stage = new PostingBits(this, "kitchen.jpg");
		//stage = new PostingBits(this, "stage-640x480.jpg");
		
		stage.setThreshold(115);
		//stage.useColorTracking(true);
		//stage.detectRed(166);
		//stage.detectGreen(44);
		//stage.detectBlue(104);
		
		// List all filter values
		stage.listFilterValues();
		
		stageElements = new ArrayList<StageElement>();
		stageElements = stage.detect();
		
		size(stage.width, stage.height);
	}

	public void draw() {
		stage.drawBackground();
		
		stroke(255, 255, 0);
    	fill(255, 255, 0, 150);
		
		for (StageElement stageElement : stageElements) {
			//stageElement.draw();
			
			// Get Contour
			Contour c = stageElement.getContour();
			
			// Get Polygon Approximation
			//println("approx: " + c.getPolygonApproximationFactor());
			//c.setPolygonApproximationFactor(3.0);
			Contour cApprox = c.getPolygonApproximation();
			
			// Get Polygon Approximation's points
			ArrayList<PVector> points = cApprox.getPoints();
			
			println("stage element #" + stageElement.id + ", num points: " + points.size(), "approx factor: " + c.getPolygonApproximationFactor());
			
			beginShape();
			for (PVector p : points) {
				vertex(p.x, p.y);
			}
			endShape(PConstants.CLOSE);
			
//			for (int i=0; i<points.size(); i++) {
//				if (i<points.size()-1) {
//					line(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
//				} else {
//					line(points.get(i).x, points.get(i).y, points.get(0).x, points.get(0).y);
//				}
//			}
			
			//c.draw();
		}
		
		println("  ");
		//stage.drawStageElements();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { PolygonApproxImageExampleApp.class.getName() });
	}
}