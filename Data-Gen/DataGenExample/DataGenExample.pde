/**
 * @author    Thomas Conroy <tdc5536@rit.edu>
 * @version   1.0
 * @since     2014-03-10
 */
 
/********************
  IMPORTS
********************/
import java.util.Map;

/********************
  PROPERTIES
********************/
// use a hashmap to pass in inputs. 
//    key   = name of input (flex, pressure, brainwave, pulse). 
//    value = tick rate (how often you want that input to update it's data, in ms).
HashMap<String,Integer> inputSettings = new HashMap<String,Integer>();
DataGenerator exampleGenerator;
StringBuilder stringBuilder;

/********************
  CONSTRUCTOR
********************/
void setup() {
  background(0);
  frameRate(60); // dont know if needed, but I set the framerate to a stable 60
  size(600,250);
   
   // ignore this -- simply for displaying demo output text
   stringBuilder = new StringBuilder();

  /* define our settings in the inputSettings HashMap
     like so: inputSettings.put("type", tickRate); */
  inputSettings.put("pressure", 200);
  inputSettings.put("flex", 200);
  inputSettings.put("brainwave", 5000);
  inputSettings.put("pulse", 500);
  
  // create generator, passing it our settings.
  exampleGenerator = new DataGenerator( inputSettings );
}

/********************
  METHODS
********************/
void draw(){
  background(0);
  text( ("brainwave: " + exampleGenerator.getInput("brainwave")), 10, 20,  width, height );
  text( ("Pressure:  " + exampleGenerator.getInput("pressure")),  10, 60,  width, height );
  text( ("Flex:      " + exampleGenerator.getInput("flex")),      10, 100, width, height );
  text( ("Pulse:     " + exampleGenerator.getInput("pulse")),     10, 140, width, height );
}