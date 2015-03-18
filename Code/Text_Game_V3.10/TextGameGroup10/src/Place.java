import java.util.ArrayList;
import java.util.List;


public class Place {
	int ID; //unique 32-bit signed positive int; 
	//0 for locked, and 1 for exit, both reserved
	
	String name; // long name with spaces
	List<String> description = new ArrayList<String>(); //in PDF file, >=1 lines, starting with an *
	List<PlaceOut> outGoing = new ArrayList<PlaceOut>();
	List<Artifact> roomArtifacts = new ArrayList<Artifact>();
	Actor actor = null; //at most 1 actor in each place
	
	int lightLevel; //0~100
	// Default light level of all places if not otherwise specified is 50 
	// Assign a light level to place 0 to change the overall default level 
	//by default (i==0 | i==50 |i==75 |i==100); light can add on this
	
	//sum of light level from artifacts on the ground
	//loses value from get and when lightActive of artifact on the ground becomes false
	int lightValueOnGround = 0;

	/*find PlaceOut in given direction*/
	PlaceOut findPlaceOutByDirection(String dir){
		dir = Token.abbreviateDir(dir);
		for(int i=0; i<this.outGoing.size();i++){
			PlaceOut tempPO = this.outGoing.get(i);
			if(tempPO.direction.equalsIgnoreCase(dir)){
				return tempPO;
			}
		}
		return null;
	}
	
	/*check if there is some place to the given direction
	 * Locked and Exit are "available";
	 * */
	Boolean isDirAvailable(String dir){
		dir = Token.abbreviateDir(dir);
		for(int i=0; i<this.outGoing.size();i++){
			PlaceOut tempPO = this.outGoing.get(i);
			if(tempPO.direction.equalsIgnoreCase(dir)){
				return true;
			}
		}
		return false;
	}
	
	/*look for a place's index with its id in given places list
	 *return the index, or -1;
	 * */
	static int findPlaceIndexByID(List<Place> places, int placeID){
		int index = -1;
		for(int i=0; i<places.size();i++){
			if(places.get(i).ID == placeID){
				index = i;
			}
		}
		return index;
	}
	
	/*look for a place with its id in given places list
	 * return the place, or NULL.
	 * */
	static Place findPlaceByID(List<Place> places, int placeID){
		int index = findPlaceIndexByID(places, placeID);
		if(index < 0) return null;
		return places.get(index);
	}
}
