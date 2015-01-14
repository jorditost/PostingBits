/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package BitStage;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import processing.core.*;
import gab.opencv.*;

/**
 * A stage detector. It detects the elements attached to the stage (a wall, for example).
 * 
 * It uses the OpenCV library for Processing by Greg Borenstein
 * https://github.com/atduskgreg/opencv-processing
 */

public class BitStage {
		
	PApplet parent;
	
	public int width;
	public int height;
	
	private OpenCV opencv;
	  
	private PImage inputImage;
	private PImage backgroundImage;
	private PImage outputImage;
  
	private ArrayList<Contour> contours;
	private ArrayList<StageElement> stageElements;
	
	// List of the new parsed contours (every frame) 
	ArrayList<Contour> parsedContours;
	
	// Number of stage elements detected over all time. Used to set IDs.
	int blobCount = 0;
	
	// List of the new parsed stage elements (every frame)
	//ArrayList<StageElement> newStageElements;
  
	// Detection parameters
	private DetectionMode detectionMode = DetectionMode.CHANNEL_S;
	private float contrast = 1;
	private int brightness = 0;
	private int threshold = 75; // 40: Natural light
	private boolean useAdaptiveThreshold = false; // use basic thresholding
	private int thresholdBlockSize = 500; //489;
	private int thresholdConstant = -20; //45;
	private boolean dilate = false;
	private boolean erode = true;
	private int blurSize = 1; 
	private boolean useThresholdAfterBlur = false;
	private int thresholdAfterBlur = 75;
	private int minBlobSize = 20;
	private int maxBlobSize = 400;
	  
	private boolean useColorTracking;
	  
	// Color tracking parameters
	private int redH       = -1; //166;  //167;
	private int greenH     = -1; //45;   //37;
	private int blueH      = -1; //104;  //104;
	private int rangeWidth = 10;
	
	
	public final static String VERSION = "##library.prettyVersion##";
	
	
	/**
     * Initialize BitStage with a width and height.
     * You will need to load an image in before processing.
     * Good when working with video.
     * 
     * @param parent
     * 			A PApplet representing the user sketch, i.e "this"
     * @param width
     * @param height
     */
	public BitStage(PApplet parent, int width, int height) {
	    this.parent = parent;
	    useColorTracking = false;
	    init(width, height);
	}
	
	/**
     * Initialize BitStage with the path to an image.
     * The image will be loaded and prepared for processing.
     * 
     * @param parent 
     * 			A PApplet representing the user sketch, i.e "this"
     * @param pathToImg
     * 			A String with a path to the image to be loaded
     */
	public BitStage(PApplet parent, String pathToImg) {
	    this.parent = parent;
	    useColorTracking = false;
	    initFromString(pathToImg);
	}
	
	/**
     * Initialize BitStage with the path to an image.
     * The image will be loaded and prepared for processing.
     * 
     * @param parent
     * 			A PApplet representing the user sketch, i.e "this"
     * @param pathToImg
     * 			A String with a path to the image to be loaded
     * @param useColorTracking 
     * 			(Optional) Set to true if you want to perform color tracking
     */
	public BitStage(PApplet parent, String pathToImg, boolean useColorTracking) {
	    this.parent = parent;
	    this.useColorTracking = useColorTracking;
	    initFromString(pathToImg);
	}
	
	/**
     * Initialize BitStage with an image.
     * The image will be loaded and prepared for processing.
     * 
     * @param parent 
     * 			A PApplet representing the user sketch, i.e "this"
     * @param img
     * 			A PImage to be loaded
     */
	public BitStage(PApplet parent, PImage img) {
	    this.parent = parent;
	    useColorTracking = false;
	    initFromPImage(img);
	}
	
	/**
     * Loads an image and initializes the OpenCV object with it.
     * Used when initializing the detector with an image.
     * 
     * @param pathToImg
     * 			A String with a path to the image to be loaded
     */
	private void initFromString(String pathToImg) {
	    PImage imageToLoad = parent.loadImage(pathToImg);
	    init(imageToLoad.width, imageToLoad.height);
	    loadImage(imageToLoad);
	}
	
	/**
     * Loads an image and initializes the OpenCV object with it.
     * Used when initializing the detector with an image.
     * 
     * @param img
     * 			A PImage to be loaded
     */
	private void initFromPImage(PImage img) {
	    init(img.width, img.height);
	    loadImage(img);
	}
	
	/**
     * Initializes all intern variables
     * 
     * @param w - width
     * @param h - height
     */
	private void init(int w, int h) {
		width = w;
		height = h;
		welcome();
		
		// Init OpenCV
		opencv = new OpenCV(parent, width, height);
		
		opencv.useColor(PApplet.HSB);
		
		/*if (useColorTracking) {
			opencv.useColor(PApplet.RGB);
    	} else {
    		opencv.useColor(PApplet.HSB);
    	}*/
		
		contours = new ArrayList<Contour>();
		stageElements = new ArrayList<StageElement>();
	}
	
	/**
	 * Load a background image from a path (for background substraction)
	 * 
	 * @param imgPath
	 * 			String with the path to the image
	 */
	public void loadImage(String imgPath) {
		loadImage(parent.loadImage(imgPath));
	}
	
	/**
	 * Load an image into OpenCV
	 * 
	 * @param img
	 */
	public void loadImage(PImage img) {
	    inputImage = img;
	    
	    // If background subtraction mode, then use this image as stage image
	    if (detectionMode == DetectionMode.BG_SUBTRACTION) {
	    	if (backgroundImage == null) {
	    		PApplet.println("dale");
	    		backgroundImage = inputImage.get(); // use get to avoid to get video object
	    	}
	    	opencv.loadImage(backgroundImage);
	    	opencv.diff(inputImage);
	    } else {
	    	opencv.loadImage(inputImage);
	    }
	}
	
	/**
	 * Load a background image from a path.
	 * 
	 * @param imgPath
	 * 			String with the path to the background image
	 */
	public void loadBackgroundImage(String imgPath) {
		loadBackgroundImage(parent.loadImage(imgPath));
	}
	
	/**
	 * Load a background image into OpenCV
	 * 
	 * @param img
	 */
	public void loadBackgroundImage(PImage img) {
	    backgroundImage = img.get();
	}
	
	/**
	 * Calculate the difference between the background image previously
	 * loaded into OpenCV and a second image. The result is stored
	 * in the loaded image in OpenCV. Works on both color and grayscale
	 * images.
	 * 
	 * @param img
	 * 		A PImage to diff against.
	 */
	/*public void diff(PImage img) {
		inputImage = img;
	    opencv.loadImage(backgroundImage);
	    opencv.diff(inputImage);
	}*/
	
	/**
	 * Performs filtering algorithms over source image
	 */
	private void filterImage() {
		
		///////////////////////////////
		// <1> PRE-PROCESS IMAGE
		// - Detection channel 
		// - Brightness / Contrast
		///////////////////////////////
		
		// Detection channel
		if (detectionMode == DetectionMode.CHANNEL_S) {
			opencv.useColor(PApplet.HSB);
			opencv.setGray(opencv.getS().clone());
		} else {
			opencv.gray();
		}
		
		// Contrast
		//opencv.brightness(brightness);
		if (contrast > 1) {
			opencv.contrast(contrast);
		}
		
		///////////////////////////////
		// <2> PROCESS IMAGE
		// - Threshold
		// - Noise Supression
		///////////////////////////////
		
		// Adaptive threshold - Good when non-uniform illumination
		if (useAdaptiveThreshold) {
		
		// Block size must be odd and greater than 3
		if (thresholdBlockSize%2 == 0) thresholdBlockSize++;
		if (thresholdBlockSize < 3) thresholdBlockSize = 3;
		
		opencv.adaptiveThreshold(thresholdBlockSize, thresholdConstant);
		
		// Basic threshold - range [0, 255]
		} else {
			opencv.threshold(threshold);
		}
		
		// Invert (black bg, white blobs)
		if (detectionMode == DetectionMode.CHANNEL_GRAY) {
			opencv.invert();
		}
		
		// Reduce noise - Dilate and erode to close holes
		if (dilate) opencv.dilate();
		if (erode)  opencv.erode();
		
		// Blur
		if (blurSize > 1) {
			opencv.blur(blurSize);
		}
		
		if (useThresholdAfterBlur) {
			opencv.threshold(thresholdAfterBlur);
		}
		
		// Save snapshot for display
		outputImage = opencv.getSnapshot();
	}
	
	/**
     * Find contours by color
     * 
     * @param hueValue
     * 			Hue value of the color to filter
     * @return ArrayList<Contour>
     * 			An array of the contours of this color
	 */
	private ArrayList<Contour> findContoursByColor(int hueValue) {
		
	    // Load input image again
		opencv.loadImage(inputImage);
	    
	    opencv.useColor(PApplet.HSB);
	    
	    // Copy the Hue channel of our image into 
	    // the gray channel, which we process.
	    opencv.setGray(opencv.getH().clone());
	    
	    // Filterimage based on the range of hue values  
	    // that match the object we want to track.
	    opencv.inRange(hueValue-rangeWidth/2, hueValue+rangeWidth/2);
	    
	    //opencv.dilate();
	    opencv.erode();
	    
	    return opencv.findContours(true,true);
	}
	
	/**
	 * Detects the stage elements.
	 * An image must have been previously loaded with loadImage() or diff()
	 * 
	 * @returns ArrayList<StageElement>
	 */
	public ArrayList<StageElement> detect() {
	    
		// Clear old contours
	    contours.clear();
	    //stageElements.clear();
	    
	    // Filter image
	    filterImage();
	    
	    // Find new contours
	    contours = opencv.findContours(true, true);
	    
	    parsedContours = parseContours(contours);
	    
	    // Get stage elements from contours
	    //newStageElements = parseStageElementsFromContours(contours, TrackingColor.NONE);
	    //stageElements.addAll(newStageElements);
	     
	    blobPersistence();
	    
	    // Color tracking
	    if (useColorTracking) {
	      
	    	// Find RED Contours
	    	if (redH > -1) {
	    		ArrayList<Contour> redContours = parseContours(findContoursByColor(redH));
	    		redContours = getFirstContours(redContours, 3);
		    	blobPersistenceByColor(redContours, TrackingColor.RED);
		    	//contours.addAll(redContours);
		    	//ArrayList<StageElement> redStageElements = parseStageElementsFromContours(redContours, TrackingColor.RED);
	    	}
		  
	    	// Find GREEN Contours
	    	if (greenH > -1) {
		    	ArrayList<Contour> greenContours = parseContours(findContoursByColor(greenH));
		    	greenContours = getFirstContours(greenContours, 3);
		    	
		    	for (Contour c : greenContours) {
					c.draw();
				}
		    	
		    	PApplet.println("num green contours: " + greenContours.size());
		    	
		    	blobPersistenceByColor(greenContours, TrackingColor.GREEN);
		    	//contours.addAll(greenContours);
		    	//ArrayList<StageElement> greenStageElements = parseStageElementsFromContours(greenContours, TrackingColor.GREEN);
	    	}
		  
	    	// Find BLUE Contours
	    	if (blueH > -1) {
		    	ArrayList<Contour> blueContours = parseContours(findContoursByColor(blueH));
		    	blueContours = getFirstContours(blueContours, 3);
		    	blobPersistenceByColor(blueContours, TrackingColor.BLUE);
		    	//contours.addAll(blueContours);
		    	//ArrayList<StageElement> blueStageElements = parseStageElementsFromContours(blueContours, TrackingColor.BLUE);
	    	}
	    	
	    	// Check repeated elements before adding them
	    	//checkAddedElements(redStageElements, TrackingColor.RED);
	    	//checkAddedElements(greenStageElements, TrackingColor.GREEN);
	    	//checkAddedElements(blueStageElements, TrackingColor.BLUE);
		  
	    	/*if (method == HYBRID) {
		    	checkAddedElements(redStageElements, RED);
		    	checkAddedElements(greenStageElements, GREEN);
		    	checkAddedElements(blueStageElements, BLUE);
		  	} else {
		    	stageElements.addAll(redStageElements);
		    	stageElements.addAll(greenStageElements);
		    	stageElements.addAll(blueStageElements);
		  	}*/
	    }
	    
	    //PApplet.println("Found " + stageElements.size() + " stage elements");
	    return stageElements;
	}
	
	private void blobPersistenceByColor(ArrayList<Contour> colorContours, TrackingColor trackingColor) {
		
		// Match color contour with a stage element object
		for (int i = 0; i < colorContours.size(); i++) {
			// Find stage elements closest to the new color contour
			// set available to false
			float record = 50000;
			int index = -1;
			
			for (int j = 0; j < stageElements.size(); j++) {
				StageElement b = stageElements.get(j);
				// Check distance
				float d = PApplet.dist(colorContours.get(i).getBoundingBox().x, colorContours.get(i).getBoundingBox().y, b.getBoundingBox().x, b.getBoundingBox().y);
				if (d < record && b.isAvailable()) {
					record = d;
					index = j;
				} 
			}
			// Update color information
			StageElement b = stageElements.get(index);
			b.setTrackingColor(trackingColor);
	    }
	}
	
	/**
	 * Blob persistence algorithm
	 * The contours should be filtered before
	 * 
	 * @returns TrackingColor	blob color
	 */
	private void blobPersistence() {
		
		// SCENARIO 1 
		// stageElements is empty
		if (stageElements.isEmpty()) {
			// Just make a StageElement object for every Contour
			for (int i = 0; i < parsedContours.size(); i++) {
				//println("+++ New blob detected with ID: " + blobCount);
				stageElements.add(new StageElement(parent, blobCount, parsedContours.get(i)));
				blobCount++;
			}
		  
		// SCENARIO 2 
		// StageElements <= Contours: We have fewer StageElement objects than Contours detected by OpenCV in this frame
		} else if (stageElements.size() <= parsedContours.size()) {
			
			boolean[] used = new boolean[parsedContours.size()];
			
			// Match existing Stage Elements objects with new Contours
			for (StageElement b : stageElements) {
				// Find the new blob newStageElements.get(index) that is closest to blob b
				// set used[index] to true so that it can't be used twice
				float record = 50000;
				int index = -1;
				for (int i = 0; i < parsedContours.size(); i++) {
					float d = PApplet.dist(parsedContours.get(i).getBoundingBox().x, parsedContours.get(i).getBoundingBox().y, b.getBoundingBox().x, b.getBoundingBox().y);
					if (d < record && !used[i]) {
						record = d;
						index = i;
					} 
				}
				// Update StageElement object contour
				used[index] = true;
				b.update(parsedContours.get(index));
			}
			// Add any unused stage elements
			for (int i = 0; i < parsedContours.size(); i++) {
				if (!used[i]) {
					//println("+++ New blob detected with ID: " + blobCount);
					stageElements.add(new StageElement(parent, blobCount, parsedContours.get(i)));
					blobCount++;
				}
			}
		  
		// SCENARIO 3 
		// StageElements > Contours: We have more StageElement objects than Contours detected by OpenCV in this frame
		} else {
			
			// All Blob objects start out as available
			for (StageElement b : stageElements) {
				b.setAvailable(true);
			} 
			// Match contour with a stage element object
			for (int i = 0; i < parsedContours.size(); i++) {
				// Find blob object closest to the newStageElements.get(i) Contour
				// set available to false
				float record = 50000;
				int index = -1;
				for (int j = 0; j < stageElements.size(); j++) {
					StageElement b = stageElements.get(j);
					float d = PApplet.dist(parsedContours.get(i).getBoundingBox().x, parsedContours.get(i).getBoundingBox().y, b.getBoundingBox().x, b.getBoundingBox().y);
					if (d < record && b.isAvailable()) {
						record = d;
						index = j;
					} 
				}
				// Update stage element contour
				StageElement b = stageElements.get(index);
				b.setAvailable(false);
				b.update(parsedContours.get(i));
		    }
		    // Start to kill any left over stage elements
		    for (StageElement b : stageElements) {
		    	if (b.isAvailable()) {
		    		b.countDown();
		    		if (b.isDead()) {
		    			b.delete();
		    		} 
		    	}
		    } 
		}
	}
	
	/**
     * Parse contours depending on size, etc.
     * 
     * @param contoursArray
     * 			Array of the contours to parse
     * @param maxContours
     * 			max number of contours
     */
	private ArrayList<Contour> parseContours(ArrayList<Contour> contoursArray) {
	    
		
		ArrayList<Contour> parsedContours = new ArrayList<Contour>();
	    
		for (Contour contour : contoursArray) {
	      
			Rectangle r = contour.getBoundingBox();
	      
			if (//(float(r.width)/float(displayWidth) > 0.3 || float(r.height)/float(displayWidth) > 0.3) ||
			   (r.width > maxBlobSize || r.height > maxBlobSize) ||
			   (r.width < minBlobSize && r.height < minBlobSize))
				continue;
	      
			parsedContours.add(contour);
	    }
	    
	    return parsedContours;
	}
	
	/**
     * Parse stage elements from contours depending on size, etc.
     * 
     * @param contoursArray
     * 			Array of the contours
     * @param colorId
     * 			The color id
     * @return ArrayList<StageElement>
     * 			Cloned array to manipulate outside BitStage
     */
	/*private ArrayList<StageElement> parseStageElementsFromContours(ArrayList<Contour> contoursArray, TrackingColor colorId) {
	    
		ArrayList<StageElement> tempStageElements = new ArrayList<StageElement>();
	    
		for (Contour contour : contoursArray) {
	      
			Rectangle r = contour.getBoundingBox();
	      
			if (//(float(r.width)/float(displayWidth) > 0.3 || float(r.height)/float(displayWidth) > 0.3) ||
			   (r.width > maxBlobSize || r.height > maxBlobSize) ||
			   (r.width < minBlobSize && r.height < minBlobSize))
				continue;
	      
			StageElement stageElement = new StageElement(parent, 0, contour, colorId);
			tempStageElements.add(stageElement);
	    }
	    
	    return tempStageElements;
	}*/
	
	/**
     * Check if two bounding boxes are the same.
     * TO DO: Check the class ContourComparator instead.
     * 
     * @param s1
     * 			First StageElement
     * @param s2
     * 			Second StageElement
	 */
	private boolean stageElementsAreTheSame(StageElement s1, StageElement s2) {
	    
		Rectangle rect1 = s1.getBoundingBox();
		Rectangle rect2 = s2.getBoundingBox();
 		
		return (Math.abs(rect1.x - rect2.x) < 8 && 
				Math.abs(rect1.y - rect2.y) < 8 && 
				Math.abs(rect1.width - rect2.width) < 8 && 
				Math.abs(rect1.height - rect2.height) < 8);
	}
	
	/**
     * Check the color of new detected elements on stage. Useful when detecting color.
     * It looks if the detected elements by color were already added in the main detection. 
     * 
     * @param newStageElements
     * 			The recently detected stage elements
     * @param trackingColor
     * 			A given color to track
     * @return ArrayList<Contour>
     * 			An array of the contours of this color
	 */
	private void checkAddedElements(ArrayList<StageElement> newStageElements, TrackingColor trackingColor) {
	    
		for (StageElement newStageElement : newStageElements) {
	      
			boolean isAdded = false;
			for (int i=0; i < stageElements.size(); i++) {
				StageElement stageElement = stageElements.get(i);
	        
				// Check if they are the same
				if (stageElementsAreTheSame(stageElement, newStageElement)) {
					stageElement.setTrackingColor(trackingColor);
					isAdded = true;
					break;
				}
			}
	      
			// If it wasn't added, add new
			if (!isAdded) {
				stageElements.add(newStageElement);
			}
	    }
	}
	
	
	/**
	 * Set methods
	 */
	public void setDetectionMode(DetectionMode detectionMode) {
	    this.detectionMode = detectionMode;
	}
	  
	public void setContrast(float contrast) {
	    this.contrast = contrast;
	}
	  
	public void setThreshold(int threshold) {
	    this.threshold = threshold;
	}
	  
	public void useAdaptiveThreshold(boolean flag) {
	    this.useAdaptiveThreshold = flag;
	}
	  
	public void setThresholdBlockSize(int value) {
	    this.thresholdBlockSize = value;
	}
	  
	public void setThresholdConstant(int value) {
	    this.thresholdConstant = value;
	}
	  
	public void setDilate(boolean flag) {
	    this.dilate = flag;
	}
	  
	public void setErode(boolean flag) {
	    this.erode = flag;
	}
	  
	public void setBlurSize(int blurSize) {
	    this.blurSize = blurSize;
	}
	  
	public void setUseThresholdAfterBlur(boolean flag) {
	    this.useThresholdAfterBlur = flag;
	}
	  
	public void setThresholdAfterBlur(int value) {
	    this.thresholdAfterBlur = value;
	}
	  
	public void setMinBlobSize(int minBlobSize) {
	    this.minBlobSize = minBlobSize;
	}
	  
	public void setMaxBlobSize(int maxBlobSize) {
	    this.maxBlobSize = maxBlobSize;
	}
	  
	public void useColorTracking() {
	    useColorTracking = true;
	}
	  
	public void useColorTracking(boolean value) {
	    useColorTracking = value;
	}
	
	public void setRedHue(int hue) {
		redH = hue;
	}
	
	public void setGreenHue(int hue) {
		greenH = hue;
	}
	
	public void setBlueHue(int hue) {
		blueH = hue;
	}
	
	public void setRangeWidthHue(int rw) {
		rangeWidth = rw;
	}
	
	/**
	 * Display functions
	 */
	public void drawBackground() {
		parent.image(inputImage, 0, 0);
	}
	
	public void drawBackgroundImage() {
		parent.image(backgroundImage, 0, 0);
	}
	
	public void drawOutputImage() {
		parent.image(outputImage, 0, 0);
		//parent.image(outputImage, 3*width/4, 3*height/4, width/4, height/4);
	}
	
	public void drawDiff() {
		opencv.loadImage(backgroundImage);
		opencv.diff(inputImage);
		parent.image(opencv.getSnapshot(), 0, 0);
	}
	
	public void drawStageElements() {
		for (StageElement stageElement : stageElements) {
			stageElement.draw();
		}
	}
	
	/**
     * Returns the first contours of an array 
     * (normally the biggest ones, since it is configurable in getContours()) 
     * 
     * @return ArrayList<Contour>
     * 			An array of the contours
     * @param maxContours
     * 			Maximum contours to return
	 */
	private static ArrayList<Contour> getFirstContours(ArrayList<Contour> contoursArray, int maxContours) {
		
		if (contoursArray.size() <= maxContours) {
			return contoursArray;
		}
		
		return new ArrayList<Contour>(contoursArray.subList(0, maxContours-1));
	}
	
	
	/**
	 * Prints a list with the filter values
	 */
	public void listFilterValues() {
		PApplet.println(" ");
		PApplet.println("BitStage filter values");
		PApplet.println("==============================");
		PApplet.println("- Detection Mode:          " + detectionMode.displayName());
		PApplet.println("- Contrast:                " + contrast);
	    //System.out.println("- Brightness:            " + brightness);
	    
		if (useAdaptiveThreshold) {
			PApplet.println("- Adaptive Threshold");
			PApplet.println("    Block Size:            " + thresholdBlockSize);
			PApplet.println("    Constant:              " + thresholdConstant);
	    } else {
	    	PApplet.println("- Threshold:               " + threshold);
	    }
		
		PApplet.println("- Blur Size:               " + blurSize);
	    
	    if (useThresholdAfterBlur) {
	    	PApplet.println("- Threshold (after blur):  " + thresholdAfterBlur);
	    }
	    
	    PApplet.println("- Min. Blob Size:          " + minBlobSize);
	    PApplet.println("- Max. Blob Size:          " + maxBlobSize);
	    PApplet.println(" ");
	    
	    if (useColorTracking) {
	    	PApplet.println("- Color Tracking:  	YES");
	    	PApplet.println("	- redH:  			" + redH);
	    	PApplet.println("	- greenH:  			" + greenH);
	    	PApplet.println("	- blueH:  			" + blueH);
	    } else {
	    	PApplet.println("- Color Tracking:  NO");
	    }
	}
	
	/**
	 * Prints a welcome text
	 */
	private void welcome() {
		System.out.println("##library.name## ##library.prettyVersion## by ##author##");
	}
	
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}
}

