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
  void updateAllInputs(){
    // loop through all the input objects, and execute their update method.
    for(Map.Entry inp : inputMap.entrySet()){
      inputMap.get(inp.getKey()).updateData();
    }
  }
  
  // return specified input
  String getInput(String desiredInput){
    if( desiredInput != null && inputMap.get(desiredInput) != null ){
      String datastr = new String();
      datastr = inputMap.get(desiredInput).getData();
      return datastr;
    }else{
      return null;
    }
  }
  
  // returns the data for all of the active input objects.
  void returnAllData(){
    
  }
  
}