Data-Gen
==============
a Data Generator for Project Riri to simulate hardware inputs.


###Classes:
####DataGenerator_Class
main "generator", manages the InputObject classes. You interact with the InputObjects through the generator. 



####InputObject_Class
class to replicate data based on what input type it is set to replicate. Takes in an input type ("pressure", "flex", "brainwave", "pulse")
and a tickRate (in milliseconds). Tickrate indicates interval to return data at (default 1 second). 



#####Brainwave:
 * requires Data/brainwaveData.txt (CSV txtfile of brainwave data). Removes junk data and then parses through the CSV, one line at a time, returning one line per tick.
 * return type = string

#####pressure / flex
 * uses a random walk to generate random trended values between 0 and 100.
 * starts at a randomly defined "seed" value, then proceedes to walk randomly from there
 * return type = string ( note: should probably be reconverted / read in as an int )

##### pulse
 * similar to Pressure / flex, but returns a pulse BPM between 60-80.
