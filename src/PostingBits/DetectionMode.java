/**
 * DetectionMode
 * Used to select the channel in which the contour detection will be performed
 * 
 * Taken from the ofxCv addon for openFrameworks by Kyle McDonald
 * enum TrackingColorMode {GRAY, S} //{TRACK_COLOR_RGB, TRACK_COLOR_HSV, TRACK_COLOR_H, TRACK_COLOR_HS};
 */

package PostingBits;
	
public enum DetectionMode { 
	CHANNEL_GRAY, 		// Gray channel. More stable with video source
	CHANNEL_S, 			// Saturation channel
	BG_SUBTRACTION;		// Background Subtraction
	
	
	public String displayName() {
        switch(this) {
            case CHANNEL_GRAY: 		return "Gray Channel";
            case CHANNEL_S: 		return "Saturation Channel";
            case BG_SUBTRACTION: 	return "Background Subtraction";
        }
        return null;
    }
}
