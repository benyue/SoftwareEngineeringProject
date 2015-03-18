import java.util.List;

/*for place, define the structure of its outgoing paths and neighbor places*/
public class PlaceOut {
	int ID;
	String direction;
	int outNeighborID;
	//+ the ID of the neighbor place; 1 EXIT; 0 locked; - locked neighbors;
	int lockPattern = 0;//32-bit int indicating the pattern/combination of lock on this path 
	// lockPattern = 0 indicates no key
	
	//important note: only absolute value of the int are stored here
	
	Boolean isLocked = false, isExit = false;

	static int findPlaceOutByID(List<PlaceOut> paths, int placeOutID){
		int index = -1;
		for(int i=0; i<paths.size();i++){
			if(paths.get(i).ID == placeOutID){
				index = i;
			}
		}
		return index;
	}
}
