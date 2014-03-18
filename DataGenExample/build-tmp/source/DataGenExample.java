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

public class DataGenExample extends PApplet {

// include for hashmaps


/* vars */
// use a hashmap to pass in inputs. key = name of input (flex, pressure, brainwave, pulse). value = tick rate (how often you want that input to update it's data, in ms).
HashMap<String,Integer> inputSettings = new HashMap<String,Integer>();
DataGenerator exampleGenerator;


/* construct stage */
public void setup() {
  background(0);
  frameRate(60);
  size(100,100);
   
  // define our settings in the inputSettings HashMap
  inputSettings.put("pressure", 1000);
  inputSettings.put("flex", 1000);
  // inputSettings.put("pulse", 1000);
  inputSettings.put("brainwave", 1000);
  
  // create generator, passing it our settings.
  exampleGenerator = new DataGenerator( inputSettings );
}

public void draw(){
  // exampleGenerator.updateAllInputs();
  println("BRAINWAVE: " + exampleGenerator.getInput("brainwave"));
  println("PRESSURE: " +  exampleGenerator.getInput("pressure"));
  println("FLEX: " +  exampleGenerator.getInput("flex"));  
}
class DataGenerator{

  /* vars */
  HashMap<String, InputObject> inputMap;
  
  /* constructor */
  DataGenerator(HashMap<String,Integer> inputSettings) {
    
    // instantiate the inputMap (where we store the input objects)
    inputMap = new HashMap<String, InputObject>();
    
    // create the input objects using data from inputSettings, saving them to the inputMap for mater reference
    for( Map.Entry input : inputSettings.entrySet() ){
      InputObject tmpObj = new InputObject( (String)input.getKey(), (Integer)input.getValue() );
      inputMap.put((String)input.getKey(), tmpObj);
    }
          
  }
  
  /* methods */
  public void updateAllInputs(){
    // loop through all the input objects, and execute their update method.
    for(Map.Entry inp : inputMap.entrySet()){
      inputMap.get(inp.getKey()).updateData();
    }
  }
  
  // return specified input
  public String getInput(String desiredInput){
    if( desiredInput != null && inputMap.get(desiredInput) != null ){
      String datastr = new String();
      datastr = inputMap.get(desiredInput).getData();
      return datastr;
    }else{
      return null;
    }
  }
  
  // returns the data for all of the active input objects.
  public void returnAllData(){
    
  }
  
}
class InputObject{

  /* vars */
  // global to inputs
  String type;          // input type
  int tickRate;         // the rate to tick at
  int elapsedTime;      // time since last tick
  String lastTickValue; // the value returned in the last tick 

  // flex / pressure
  int seedValue;
  int xSig;
  int fp_currentVal;
  // pulse
  
  // brainwave
  StringList bw_refinedLines = new StringList();
  int bw_currentLine = 0;

  /* constructor */
  InputObject(String type, int tickRate){

    // setup properties
    this.type        = type;
    this.tickRate    = tickRate;
    this.elapsedTime = millis();
    
    if( this.type == "flex" || this.type == "pressure" ){
      seedValue = round(random(0,100)); // set random starting point 
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
      // TODO
    }

    // update the data
    updateData();
  }
  
  /* methods */
 // updates data on tick 
 public void updateData(){

    // check if tickRate has ellapsed since last update
    if( millis() - elapsedTime >= tickRate ){ 
      // generate data for this tick.
      if( this.type == "flex" || this.type == "pressure" ){
        fp_currentVal = this.randomWalk();
      }
      else if( this.type == "brainwave" ){
        bw_currentLine += 1;
      }
      else if( this.type == "pulse" ){
        // TODO generate pulse data
      }
      
      // reset the stored time for the next tick.
      elapsedTime = millis(); 
    } else{
      try {
        Thread.sleep(10);
        updateData();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
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
      // TODO - create return string for pulse
    }
    
    return returnStr;
  }






  // generates a trended value between 0 and 100 
  // ( for flex and pressure sensors )
  private int randomWalk(){
    xSig = seedValue +  (int)random(-10,10);
    if(xSig < 0)        {xSig = 0;}
    else if(xSig > 100) {xSig = 100;}
    else                {seedValue = xSig;}
    return xSig;
  }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "DataGenExample" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
