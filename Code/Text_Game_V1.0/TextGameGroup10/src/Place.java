import java.util.ArrayList;
import java.util.List;


public class Place {
	int ID; //unique 32-bit signed positive int; 
	//0 for locked, and 1 for exit, both reserved
	
	String name; // long name with spaces
	List<String> description = new ArrayList<String>(); //in PDF file, >=1 lines, starting with an *
	List<PlaceOut> outGoing = new ArrayList<PlaceOut>();
	
	
	/*display description?*/
	void display(){
		// TODO Auto-generated method stub
	}
	
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
	 *return the index
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
	 * return the place
	 * */
	static Place findPlaceByID(List<Place> places, int placeID){
		return places.get(findPlaceIndexByID(places, placeID));
	}
}
