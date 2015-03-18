

public class Path {
	int ID;
	int source, destination; // Place ID
	String direction; 
	// source and destination correspond to Place IDs 
	// A negative destination indicates a locked door;
		// Destination 1 exits the program; 
		// 0 leads ¡°nowhere¡±,implying the door is locked and must stay locked.
//	String description[];
	
	void dsplay(){
		// TODO Auto-generated method stub
	}
	
//	/*look for a path's index with its id in given paths list
//	 * return the index
//	 * */
//	int findPathIndexByID(List<Path> paths, int pathID){
//		int index = -1;
//		for(int i=0; i<paths.size();i++){
//			if(paths.get(i).ID == pathID){
//				index = i;
//			}
//		}
//		return index;
//	}
//	
//	/*look for a path with its id in given paths list
//	 * return the path
//	 * */
//	Path findPathByID(List<Path> paths, int pathID){
//		return paths.get(findPathIndexByID(paths, pathID));
//	}
	
}
