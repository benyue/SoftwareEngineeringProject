/*for place, define the structure of its outgoing paths and neighbor places*/
public class PlaceOut {
	int outPathID;
	String direction;
	int outNeighborID; 
	//+ the ID of the neighbor place; 1 EXIT; 0 locked; - locked neighbors;
	//only absolute value here
	
	Boolean isLocked = false;
	Boolean isExit = false;
	
}
