import gab.opencv.*;
import StageDetector.*;

StageDetector stage;
PImage  before, after;

void setup() {
  before = loadImage("before.jpg");
  after = loadImage("after.jpg");
    
  stage = new StageDetector(this, before.width, before.height);
  stage.setDetectionMode(DetectionMode.BG_SUBTRACTION);
    
  //stage.diff(before, after);
  stage.loadBackgroundImage(before);
  stage.loadImage(after);
  stage.detect();
  
  size(stage.width, stage.height);
}

void draw() {
  stage.drawBackground();
  stage.drawStageElements();
}

