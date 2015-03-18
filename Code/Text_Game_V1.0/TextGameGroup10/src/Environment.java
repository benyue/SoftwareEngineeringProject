import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Environment {
	static int nPlace = 1;//at least 1 place is required
	List<Place> places = new ArrayList<Place>();
	
	static int nPath = 1;//at least 1 path is required
	List<Path> paths = new ArrayList<Path>();
	
	static UserInterface UI;
	String message = null; //message for Player (to output/display in the UI)
	
	Place currentPlace;
	Boolean Exit = false;
	Boolean Quit = false; 

	void display(){
		// TODO Auto-generated method stub
	}
	
	static void setUI(UserInterface UI){
		// TODO Auto-generated method stub
	}
	
	/*
	 * execute token
	 * pre-condition:token is from Token.extractToken(), thus valid
	 * */
	void executeToken(Token token){
		message = "";
		if(token.command.equalsIgnoreCase("HELP")){
			this.executeHelp();
		}else if(token.command.equalsIgnoreCase("QUIT")){
			this.executeQuit();
		}else if(token.command.equalsIgnoreCase("EXIT")){
			this.executeExit(token);
		}else if(token.command.equalsIgnoreCase("GO")){
			this.executeGo(token);
		}else if(token.command.equalsIgnoreCase("LOOK")){
			this.executeLook(token);
		}else{
			message = "Invalid Token. Type correct commands or HELP for instructions.\n";
		}
	}
	
	/*execute the GO command*/
	void executeGo(Token token){
		String dir = token.direction.toUpperCase();
		if(!currentPlace.isDirAvailable(dir)){
			message = "Cannot go Direction "+dir+", nothing there."
					+" Try other directions.\n";
			return;
		}
		PlaceOut tempPO = currentPlace.findPlaceOutByDirection(dir);
		if(tempPO.isLocked){
			if(tempPO.outNeighborID == 0){
				message = "It's some WE-DON'T-KNOW-WHAT place to the " +dir+" but locked." +
								" Try other directions.\n";
			}else{
				message = "It's "+Place.findPlaceByID(places, tempPO.outNeighborID).name
						+" to the " +dir+" but locked." +
						" Try other directions.\n";
			}
			return;
		}

		if(tempPO.isExit){
			message = "Congratualations! Game exited. Thanks for coming.";
			this.Exit = true;
			return;
		}
		this.currentPlace = Place.findPlaceByID(this.places, tempPO.outNeighborID);
		message = "**This is the "+currentPlace.name+"!\n";
		for(int i = 0; i<currentPlace.description.size();i++){
			message +=currentPlace.description.get(i)+"\n";
		}
	}
	
	/*execute the LOOK command, may or may not have direction*/
	void executeLook(Token token){
		//first describe current place
		message = "**This is the "+this.currentPlace.name+".\n";
		for(int i=0;i<this.currentPlace.description.size();i++){
			message += this.currentPlace.description.get(i)+"\n";
		}
		
		message += "*Tips on surroundings: \n";
		String dir = token.direction;
		//The Token comes without Direction
		if(dir == null){
			//describe all the surroundings
			for(int i=0; i<currentPlace.outGoing.size();i++){
				PlaceOut tempPO = currentPlace.outGoing.get(i);
				String tempDir = Token.abbreviateDir(tempPO.direction);
				if(tempPO.isExit){
					message += "An Exit is to the "+tempDir+".\n";
				}else if(tempPO.isLocked){
					if(tempPO.outNeighborID == 0){
						message += "It's some WE-DON'T-KNOW-WHAT place "
								+"to the " +tempDir+" but locked.\n";
					}else{
						message += "It's "+Place.findPlaceByID(places, tempPO.outNeighborID).name
							+" to the "+tempDir+ " but locked.\n";
					}
				}else{
					message +="You can go "+tempDir+","+
							" it's "+Place.findPlaceByID(places, tempPO.outNeighborID).name+".\n";
				}
			}
			message += "Other Directions are not available.\n";
			return;
		}
		//the Token comes with Direction
		dir = Token.abbreviateDir(dir); //upper case is for output
		PlaceOut tempPO = currentPlace.findPlaceOutByDirection(dir);
		if(tempPO != null){ 
			//describe things in that direction
			if(tempPO.isExit){
				message = "An Exit is to the "+dir+".\n";
			}else if(tempPO.isLocked){
				if(tempPO.outNeighborID == 0){
					message = "It's some WE-DON'T-KNOW-WHAT place to the " +dir+" but locked.\n";
				}else{
					message = "It's "+Place.findPlaceByID(places, tempPO.outNeighborID).name
						+" to the "+dir+ " but locked.\n";
				}
			}else{
				message ="You can go "+dir+", "+
						"it's "+Place.findPlaceByID(places, tempPO.outNeighborID).name+".\n";
			}
		}else{
			message = "Nothing is to the "+dir+". Try other directions.\n";
		}
	}
	
	/*execute the Exit command*/
	void executeExit(Token token) {
		if(currentPlace.ID == 1){ //exit here
			message = "Congratualations! Game exited. Thanks for coming.";
			this.Exit = true;
			this.currentPlace = null;
		}else{
			message = "Sorry, it's not an exit here.\n";
//			message = "Game quit. Thanks for coming.";
//			this.Quit = true;
//			this.currentPlace = null;
		}
	}
	
	/*execute the Quit command*/
	void executeQuit() {//TODO: need confirmation?
		message = "Game quit. Thanks for coming.";
		this.currentPlace = null;
		this.Quit = true;
	}

	/*execute the HELP command*/
	void executeHelp() {
		message = "Valid inputs: HELP, QUIT, EXIT, LOOK [Direction], GO Direction.\n"
				+"Valid Directions: N,NORTH, S,SOUTH, E,EAST, W,WEST, U,UP, D,DOWN,\n" +
				" NE,NORTHEAST, NW,NORTHWEST, SE,SOUTHEAST, SW,SOUTHWEST,\n" +
				" NNE,NORTHNORTHEAST, NNW,NORTHNORTHWEST,\n" +
				" SSE,SOUTHSOUTHEAST, SSW,SOUTHSOUTHWEST,\n" +
				" ENE,EASTNORTHEAST, ESE,EASTSOUTHEAST,\n" +
				" WNW,WESTNORTHWEST, WSW,WESTSOUTHWEST.\n";
		
	}
	
	@SuppressWarnings("resource")
	/*constructor: build the environment from the input file*/
	Environment(String filename){
			File file = new File(filename);
			BufferedReader brf = null;
			try {
				brf = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e1) {
				message = "ERROR: No environment file found!";
				return;
			}
			
			String line = "";
			boolean pathDone = false;
			boolean placeDone = false;
			while(true){
				if(pathDone) break;//no longer read lines
				if(placeDone){
					placeDone = false;
				}else{
					try {
						line = brf.readLine().split("//")[0].trim();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if(line.isEmpty()) continue;
				if(line.startsWith("GDF")){
					this.message = "Game environment: "+line+"\n";
					continue;
				}
				if(line.startsWith("PLACES")){
					int p = nPlace = parseIntegers(line).get(0);
					//read all places. suppose places come right after the "PLACES" line
					while(p>=0){
						try {
							line = brf.readLine().split("//")[0].trim();
						} catch (Exception e) {
							message += "ERROR: Wrong map - fewer valid places than announced!";
							return;
						}
						if(line.isEmpty()) continue;
						if(line.startsWith("*")){ //description to the latest place
							//get index, remove *. suppose only one * at the start
							places.get(places.size()-1) //get index
								.description.add(line.replace("*","")); 
							continue;
						}
						if(p==0){//finish reading places. this line is the number of PATHS
							this.currentPlace = this.places.get(0); //root place
							placeDone = true;
							break;
						}
						
						if(line.startsWith("PATHS")){
							message += "ERROR: Wrong map - not so many places as announced!";
							return;
						}
						
						List<Integer> nums = parseIntegers(line);
						if(nums.size()>0){
							Place tempPlace = new Place();
							tempPlace.ID = nums.get(0);
							tempPlace.name = line.replaceFirst("[0-9]+", "").trim();
							if(tempPlace.name.isEmpty()){
								message +="ERROR: Wrong map - place "+tempPlace.ID+" has no name!";
								return;
							}
							places.add(tempPlace);
							p--;
						}
						
					}
					continue;
				} // end if(line.startsWith("PLACES"))
				if(line.startsWith("PATHS")){
					placeDone = false;
					int p2 = nPath = parseIntegers(line).get(0);
					//read all paths. suppose paths come right after the "PATHES" line
					while(p2>0){
						try {
							line = brf.readLine().split("//")[0].trim();
						} catch (Exception e) {
							message += "ERROR: Wrong map - fewer valid paths than announced!";
							return;
						}
						if(line.isEmpty()) continue;
						List<Integer> nums = parseIntegers(line);
						if(nums.size() == 3){ //a path in this line
							Path tempPath = new Path();
							tempPath.ID = nums.get(0);
							tempPath.source = nums.get(1); // source place ID: +
							tempPath.destination = nums.get(2);//non-negative only
							String dir = line.replaceAll("[0-9]+","").replaceAll("-","").trim();
							if(Token.isValidDirection(dir)){
								tempPath.direction = Token.abbreviateDir(dir);
							}else{
								message += "ERROR: Wrong map - " +
										"check the direction of Path ID "+tempPath.ID+".";
								return;
							}
							//TODO ATTENTION: source and destination are not checked!!!*/
							paths.add(tempPath);
							p2--;
							
							//add outgoing info for source place
							int index = Place.findPlaceIndexByID(places, tempPath.source);
							if(index == -1){
								message += "ERROR: Wrong map - " +
										"check the source of Path ID "+tempPath.ID+".";
								return;
							}else{
								PlaceOut po = new PlaceOut();
								po.outPathID = tempPath.ID;
								po.direction = Token.abbreviateDir(dir);
								po.outNeighborID = tempPath.destination;
								if(tempPath.destination == 1){
									po.isExit = true;
								}
								if(line.contains("-") || tempPath.destination == 0){
									po.isLocked = true;
								}
								places.get(index).outGoing.add(po);
							}
							
							//check destination place
							if(tempPath.destination != 0 && tempPath.destination != 1){
								index = Place.findPlaceIndexByID(places, tempPath.destination);
								if(index == -1){
									message += "ERROR: Wrong map - " +
											"check the destination of Path ID "+tempPath.ID+".";
									return;
								}
							}	

						}
					}
					if(p2==0) pathDone = true;
				}// end if(line.startsWith("PATHS"))
				
				if(paths.size()==nPath && places.size()==nPlace){
					//if the loop cannot break, it implies abnormal GDF file
					message += "Input commands to wander around and find the exit.\n" +
							"Type HELP for instructions at any time.\n" +
							"Let's begin!";
					try {
						brf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			}

	}

	/*Read numbers from one line of GDF file*/
	List<Integer> parseIntegers(String s){
		List<Integer> nums = new ArrayList<Integer>();
		Pattern p = Pattern.compile("[0-9]+"); //get non-negative integer only
		Matcher m = p.matcher(s);
		while (m.find()) {
			int temp = Integer.parseInt(m.group());
			nums.add(temp);
		}
		return nums;
	}
}
