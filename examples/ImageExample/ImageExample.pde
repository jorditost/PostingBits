import gab.opencv.*;
import StageDetector.*;

StageDetector stage;
PImage image;

void setup() {
  stage = new StageDetector(this, "data/kitchen.jpg"); 
  stage.detect();
  
  size(stage.width, stage.height);
}

void draw() {
  stage.drawBackground();
  stage.drawStageElements();
}
