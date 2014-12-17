import processing.video.*;

import gab.opencv.*;
import StageDetector.*;

StageDetector stage;
Capture video;

void setup() {
  video = new Capture(this, 640, 480);
  video.start();
      
  stage = new StageDetector(this, 640, 480);
  stage.setDetectionMode(DetectionMode.CHANNEL_GRAY);
      
  stage.listFilterValues();
      
  size(stage.width, stage.height);
}

void draw() {
  if (video.available()) {
    video.read();
      
    stage.loadImage(video);
    stage.detect();
          
    stage.drawBackground();
    stage.drawStageElements();
  }
}

