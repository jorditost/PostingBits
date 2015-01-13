package BitStage;

/**
 * TrackingColor
 * Possible colors to detect when performing color tracking.
 */
public enum TrackingColor {
	NONE,
	RED,
	GREEN,
	BLUE,
	BLACK;
	
	public String displayName() {
        switch(this) {
            case NONE: 	return "None";
            case RED: 	return "Red";
            case GREEN: return "Green";
            case BLUE: 	return "Blue";
            case BLACK: return "Black";
            
        }
        return null;
    }
}
