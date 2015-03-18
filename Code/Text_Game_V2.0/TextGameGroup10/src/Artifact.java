import java.util.ArrayList;
import java.util.List;

public class Artifact{
	int ID; //ID of the Artifact
	int placeID; //ID of the room it is originally in
				//Cindy:<0, place 0 exists.
	int value; //score points
	int movability; //0 immovable, non-zero for movable
	String name; //name of artifact
	int type; //type 0 for no use
				//type = 1 for key
				//type = 2 for light
	
	List<String> description = new ArrayList<String>();
	boolean usedEver = false;//if the player ever get/use the item, this value is true
	
	/*look for an artifact's index with its id in given artifacts list
	 *return the index, or -1;
	 * */
	static int findArtifactIndexByID(List<Artifact> artifacts, int aID){
		int index = -1;
		for(int i=0; i<artifacts.size();i++){
			if(artifacts.get(i).ID == aID){
				index = i;
			}
		}
		return index;
	}
	
	/*look for an artifact with its id in given list
	 * return the artifact, or NULL.
	 * */
	static Artifact findArtifactByID(List<Artifact> art, int aID){
		int index = findArtifactIndexByID(art, aID);
		if(index < 0) return null;
		return art.get(index);
	}
	
	/*mark "this" artifact as picked up, by modifying its placeID value*/
	void pickUp(){
		this.placeID = -1;
	}

}