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
 void updateData(){

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
        // sleep for 10ms then recall the function
        Thread.sleep(10); 
        updateData();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  // retrieve current data
  String getData(){ 

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