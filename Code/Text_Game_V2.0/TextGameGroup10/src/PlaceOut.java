/*for place, define the structure of its outgoing paths and neighbor places*/
public class PlaceOut {
	String direction;
	int outNeighborID; 
	//+ the ID of the neighbor place; 1 EXIT; 0 locked; - locked neighbors;
	int lockPattern;//32-bit int indicating the pattern/combination of lock on this path 
	// lockPattern = 0 indicates no key
	
	//important note: only absolute value of the int are stored here
	
	Boolean isLocked = false, isExit = false;
	
}
