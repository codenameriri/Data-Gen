// include for hashmaps
import java.util.Map;

/* vars */
// use a hashmap to pass in inputs. key = name of input (flex, pressure, brainwave, pulse). value = tick rate (how often you want that input to update it's data, in ms).
HashMap<String,Integer> inputSettings = new HashMap<String,Integer>();
DataGenerator exampleGenerator;


/* construct stage */
void setup() {
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

void draw(){
  // exampleGenerator.updateAllInputs();
  println("BRAINWAVE: " + exampleGenerator.getInput("brainwave"));
  println("PRESSURE: " +  exampleGenerator.getInput("pressure"));
  println("FLEX: " +  exampleGenerator.getInput("flex"));  
}