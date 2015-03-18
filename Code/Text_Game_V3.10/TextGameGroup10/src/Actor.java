import java.util.ArrayList;
import java.util.List;

public class Actor {
	ArrayList<String> question = new ArrayList<String>();
	String name;
	String answer;
	int ID;
	int placeID;
	
	static int findActorIndexByID(List<Actor> actors, int actorID){
		int index = -1;
		for(int i = 0; i < actors.size(); i++){
			if(actors.get(i).ID == actorID)
				index = i;
		}
		return index;
	}
}
