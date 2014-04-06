import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Map; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FlexDiskExample extends PApplet {


// for the Data Generator ////////////////////
 
HashMap<String,Integer> inputSettings = new HashMap<String,Integer>();
DataGenerator exampleGenerator;
/////////////////////////////////////////////
float flexValue;
int numPts        = 64;
float minRadius   = 100;
float radiusRange = 75;

PVector[] points = new PVector[numPts]; // Stores the circle's points
float angle = TWO_PI/numPts;


public void setup(){
  size(600, 600);
  frameRate(60);

  // for the Data Generator //////////
  inputSettings.put("flex", 100);
  exampleGenerator = new DataGenerator( inputSettings );
  ////////////////////////////////////
}

public void draw(){
  translate(width/2, height/2);
  background(170, 121, 240);
  drawDisk(); 
  
  // for the Data Generator ////////// 
  flexValue = Float.parseFloat(exampleGenerator.getInput("flex"));  
  ////////////////////////////////////

  noFill();
  strokeWeight(1);
  beginShape();
  
  // For each numPts assign a coordinate point, store it in a PVector array
  // From the start every point will have the same radius
  for(int n = 0; n < numPts; n++){
    float xPt = cos(angle * n);
    float yPt = sin(angle * n);
    points[n] = new PVector(xPt,yPt);
      
    // The flex values(fValue) are proportional to the radiusRange  
    float radius = minRadius + (( radiusRange * flexValue )/100); 
    //println("Radius: " + radius);
    
    //changes the raidus value depending on the flexValue range
    if(flexValue > 0 && flexValue < 50.0f){
       radius += random(5);
     }
     else if(flexValue > 50.0f && flexValue < 100){
       radius  += random(15, 25);
     }
     else if(flexValue == 100){
       radius  += random(25, 45);
     }
     
     points[n].mult(radius);
          
     // The first control point and the start point of the curve use the same point
     // therefore we need to repeat the curveVertex() using the same point
     if(n == 0 ){
       curveVertex(points[n].x, points[n].y);
     }
    
     curveVertex(points[n].x, points[n].y);
     
     // The last point of curve and the last control point use the same point
     if(n == numPts-1){
       // In this case, the last point will be the same as the starting point b/c its a circle
       curveVertex(points[0].x, points[0].y);
       curveVertex(points[0].x, points[0].y);
     }
     
    }// END for
    endShape(CLOSE);

text(("Flex Value(from generator): " + flexValue), 0, 100, width, height );
}//END draw()

// Draws the disk in the background
public void drawDisk(){
  strokeWeight(5);
  stroke(17,224,245);
  // Disk
  fill(0);
  ellipse(0, 0, 500, 500);
  
  // Inner hole
  strokeWeight(2);
  fill(170, 121, 240); // Has to be the same color as the background
  ellipse(0, 0, 65, 65);
}
/**
 * @author    Thomas Conroy <tdc5536@rit.edu>
 * @version   1.0
 * @since     2014-03-10
 */
class DataGenerator{
  /********************
    PROPERTIES
  ********************/
  HashMap<String, InputObject> inputMap;
  
  /********************
    CONSTRUCTOR
  ********************/
  DataGenerator(HashMap<String,Integer> inputSettings) {
    
    // instantiate the inputMap (where we store the input objects)
    inputMap = new HashMap<String, InputObject>();
    
    // create the input objects using data from inputSettings, saving them to the inputMap for mater reference
    for( Map.Entry input : inputSettings.entrySet() ){
      InputObject tmpObj = new InputObject( (String)input.getKey(), (Integer)input.getValue() );
      inputMap.put((String)input.getKey(), tmpObj);
    }

    // then, start all the input threads
    toggleAll("on");
    
  }
  
  /*********************
    CUSTOM METHODS
  *********************/
  /**
   * calls update method on all input threads the generator controls.
   *
   * @param     none
   * @return    void
   */    
  public void updateAllInputs(){
    // loop through all the input objects, and execute their update method.
    for(Map.Entry inp : inputMap.entrySet()){
      inputMap.get(inp.getKey()).updateData();
    }
  }

  /**
   * starts or stops all input threads.
   *
   * @param     String setting : "start", "stop"
   * @return    void
   */   
  public void toggleAll(String setting){
    for( Map.Entry inp : inputMap.entrySet() ){
      if( setting == "on" ){
        inputMap.get(inp.getKey()).start();
      }
      if( setting == "off" ){
        inputMap.get(inp.getKey()).quit();
      }
    }
  }

  /**
   * starts or stops specific input thread.
   *
   * @param     String    type    : "flex", "pressure", "brainwave", "pulse"
   * @param     String    setting : "start" or "stop"
   * @return    void
   */    
  public void toggleSpecific(String type, String setting){
    for( Map.Entry inp : inputMap.entrySet() ){
      // only act on the specified inputMaker
      if( inputMap.get(inp.getKey()).type == type ){
        if( setting == "start" ){
          inputMap.get(inp.getKey()).start();
        }
        else if( setting == "stop" ){
          inputMap.get(inp.getKey()).quit();
        }
      }
    }
  }

  /**
   * returns current tick value of specified input. 
   *
   * @param     String    type    : "flex", "pressure", "brainwave", "pulse"
   * @return    String    datastr : current value of tick (in string form, may need to convert to int/float)
   */    
  public String getInput(String type){
    if( type != null && inputMap.get(type) != null ){
      
      String datastr = new String();
      datastr        = inputMap.get(type).getData();
      
      return datastr;
    }else{
      return null;
    }
  }
  
}
/**
 * @author    Thomas Conroy <tdc5536@rit.edu>
 * @version   1.0
 * @since     2014-03-10
 */
public class InputObject extends Thread {
  /********************
    PROPERTIES
  ********************/
  // gvars
  String type;          // input type / thread name
  int tickRate;         // the rate to tick at
  int elapsedTime;      // time since last tick
  String lastTickValue; // the value returned in the last tick 

  // thread vars
  boolean running;      // == true if thread is running 

  // flex / pressure
  int fp_seedValue;
  int xSig;
  int fp_currentVal;
  
  // pulse
  int pu_currentVal;
  int pu_seedValue;

  // brainwave
  StringList bw_refinedLines = new StringList();
  int bw_currentLine = 0;

  /********************
    CONSTRUCTOR
  ********************/
  InputObject(String type, int tickRate){
    
    // setup properties
    this.running     = false; // disable thread running by default
    this.type        = type;
    this.tickRate    = tickRate;
    this.elapsedTime = millis();
    
    if( this.type == "flex" || this.type == "pressure" ){
      fp_seedValue = round(random(0,100)); // set random starting point 
    } 
    
    if(this.type == "brainwave"){
      // load brainwave data
      String[] allLines = loadStrings("brainwaveData.txt");
      // remove dead data
      for(int i=0; i<allLines.length; i++){
        if( !(allLines[i].length() < 20) && (allLines[i].indexOf("ERROR:") == -1) ){
          bw_refinedLines.append(allLines[i]);
        }
      }
    }
    
    if( this.type == "pulse" ){
      // set seed value for pulserate (starting pulse value)
      pu_seedValue = round(random(75,80));

    }

  }


  /***********************
    THREAD METHODS
   **********************/
  // override start method
  public void start(){
    // enable running bool
    running = true;

    // start the thread
    super.start();
  }

  // run method, triggered by start()
  public void run(){
    while(running){
      // do whatever we want to do this tick
      if( this.type == "flex" || this.type == "pressure" ){
        fp_currentVal = this.randomWalk(fp_currentVal, "fp");
      }
      
      if( this.type == "brainwave" ){
        bw_currentLine += 1;
        if( bw_currentLine >= bw_refinedLines.size() ){
            // reset
            bw_currentLine = 0;
        }else{
          bw_currentLine += 1;
        }
      }
      
      if( this.type == "pulse" ){
        pu_currentVal = this.randomWalk(pu_currentVal, "pu");
      }

      // wait for next tick
      try {
        sleep((long)(tickRate));
      } catch (Exception e) {
        
      }
    }
    println(this.type + " Thread is done.");
  }

  // method to quit the thread
  public void quit() {
    println("Quitting thread..");
    this.running = false; // terminate the loop
    interrupt();
  }
  
 /*********************
    CUSTOM METHODS
 *********************/
 // updates data on tick 
 public void updateData(){
    //
  }
  
  // retrieve current data
  public String getData(){ 

    // update the data first
    this.updateData();

    // the string we're returning
    String returnStr = "";
    
    if( this.type == "pressure" || this.type == "flex" ){
      returnStr = Integer.toString(fp_currentVal);
    }
    
    if( this.type == "brainwave" ){
      returnStr = bw_refinedLines.get(bw_currentLine);
    }
    
    if( this.type == "pulse" ){
      returnStr = Integer.toString(pu_currentVal);
    }
    
    return returnStr;
  }






  // generates a trended value between 0 and 100 
  // ( for flex and pressure sensors )
  private int randomWalk(int seedVal, String sensorType ){
    
    if( sensorType == "pu" ){
      xSig = seedVal +  (int)random(-2,2);
      if(xSig < 60)       {xSig = 60;}
      else if(xSig > 80) {xSig = 80;}
      else               {seedVal = xSig;}
    }

    if( sensorType == "fp" ){
      xSig = seedVal +  (int)random(-10,10);
      if(xSig < 0)        {xSig = 0;}
      else if(xSig > 100) {xSig = 100;}
      else                {seedVal = xSig;}
    }


    return xSig;
  }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FlexDiskExample" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
