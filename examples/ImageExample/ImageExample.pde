import gab.opencv.*;
import BitStage.*;

BitStage stage;
PImage image;

void setup() {
  stage = new BitStage(this, "data/kitchen.jpg"); 
  stage.detect();
  
  size(stage.width, stage.height);
}

void draw() {
  stage.drawBackground();
  stage.drawStageElements();
}
