import java.util.ArrayList;
import java.util.List;
public class Game {
	String message = null; //message for Player (to output/display in the UI)
	Place currentPlace;
	
	Place previousPlace;
	
	//light value the player is carrying with artifacts used
	//loses value with drop or when lightActive of inve artifact becomes false
	int carryingLightValue = 0;
	
	Boolean Exit = false;
	Boolean Quit = false; 
	//contains all the artifacts the user is carrying
	List<Artifact> inventory = new ArrayList<Artifact>();
    
	/*
	 * execute token
	 * pre-condition:token is from Token.extractToken(), thus valid
	 * */
	void executeToken(Environment env, Token token){
		message = "";
		if(token.command.equalsIgnoreCase("HELP")){
			this.executeHelp();
		}else if(token.command.equalsIgnoreCase("QUIT")){
			this.executeQuit();
		}else if(token.command.equalsIgnoreCase("EXIT")){
			this.executeExit(env,token);
		}else if(token.command.equalsIgnoreCase("GO")){
			this.executeGo(env,token);
		}else if(token.command.equalsIgnoreCase("LOOK")){
			this.executeLook(env,token);
		}else if(token.command.equalsIgnoreCase("INVE") || token.command.equalsIgnoreCase("INVENTORY")){
			this.executeInventory();
		}else if(token.command.equalsIgnoreCase("DROP")){
			this.executeDrop(env, token);
		}else if(token.command.equalsIgnoreCase("GET")){
			this.executeGet(env, token);
		}else if(token.command.equalsIgnoreCase("USE")){
			this.executeUse(env, token);
		}
		else{
			message = "Invalid Token. Type correct commands or HELP for instructions.\n";
		}
	}
	
	/*execute the GO command*/
	void executeGo(Environment env, Token token){
		
		String dir = token.optional.toUpperCase();
		if(!currentPlace.isDirAvailable(dir) && checkLighting()){
			message = "Cannot go Direction "+dir+", nothing there."
					+" Try other directions.\n";
			return;
		}
		PlaceOut tempPO = currentPlace.findPlaceOutByDirection(dir);
		
		if(!checkLighting() && (tempPO == null || tempPO.outNeighborID != previousPlace.ID)){
			if((carryingLightValue + currentPlace.lightLevel + currentPlace.lightValueOnGround) < 15)
				message += "It is too dark here. ";
			else
				message += "It is too bright here. ";
			message += "Try adjusting the lights or returning to your previous room.\n";
			return;
		}
		if(tempPO.isLocked){
			if(tempPO.outNeighborID == 0){
				message = "Sorry, it's some WE-DON'T-KNOW-WHAT place to the " +dir+" but locked.\n";
			}else if(tempPO.outNeighborID==1){//tempPO.isExit
					message = "Sorry, it's EXIT to the " +dir+" but locked.\n";
			}else{
					message = "Sorry, it's "+Place.findPlaceByID(env.places, tempPO.outNeighborID).name
						+" to the " +dir+" but locked.\n";
			}
			return;
		}
		
		if(tempPO.isExit){
			message = "Congratualations! Game exited. Thanks for coming.";
			this.Exit = true;
			return;
		}
		
		previousPlace = currentPlace;
		this.currentPlace = Place.findPlaceByID(env.places, tempPO.outNeighborID);
		message = "*This is "+currentPlace.name+".\n";
		for(int i = 0; i<currentPlace.description.size();i++){
			message +=currentPlace.description.get(i)+"\n";
		}
	}
	
	/*execute the LOOK command, may or may not have direction/object*/
	//valid input: LOOK [Direction,"HERE",object]
	void executeLook(Environment env, Token token){
		// if lightLevel<15 or >= 100 user can not see
		if((carryingLightValue + currentPlace.lightValueOnGround + currentPlace.lightLevel) < 15  ||
				(carryingLightValue + currentPlace.lightValueOnGround + currentPlace.lightLevel) >= 100){
			message = "Too ";
			if((carryingLightValue + currentPlace.lightValueOnGround + currentPlace.lightLevel) < 15)
				message += "dark";
			else
				message += "bright";
			message += " to see things here. Try adjusting the lights or going to your previous room.\n";
			return;
		}
			
		
		String opt = token.optional;
		if(opt == null){ //LOOK
			message = "*This is "+this.currentPlace.name+".\n";
			for(int i=0;i<this.currentPlace.description.size();i++){
				message += this.currentPlace.description.get(i)+"\n";
			}
			message += "*Tips on surroundings: \n";
			//describe all the surroundings
			for(int i=0; i<currentPlace.outGoing.size();i++){
				PlaceOut tempPO = currentPlace.outGoing.get(i);
				String tempDir = Token.abbreviateDir(tempPO.direction);
				if(tempPO.isLocked){
					if(tempPO.outNeighborID == 0){
						message += "It's some WE-DON'T-KNOW-WHAT place "
								+"to the " +tempDir+" but locked.\n";
					}else if(tempPO.outNeighborID == 1){
						message += "It's EXIT to the "+tempDir+" but locked.\n";
					}else{
						message += "It's "+Place.findPlaceByID(env.places, tempPO.outNeighborID).name
							+" to the "+tempDir+ " but locked.\n";
					}
				}else if(tempPO.isExit){
					message += "An Exit is to the "+tempDir+".\n";
				}else{
					message +="You can go "+tempDir+","+
							" it's "+Place.findPlaceByID(env.places, tempPO.outNeighborID).name+".\n";
				}
			}
			message += "Other Directions are not available.\n";
			return;
		}else if(Token.isValidDirection(opt)){ //LOOK Direction
			opt = Token.abbreviateDir(opt); //upper case for output
			PlaceOut tempPO = currentPlace.findPlaceOutByDirection(opt);
			if(tempPO != null){
				//describe things in that direction
				if(tempPO.isLocked){
					if(tempPO.outNeighborID == 0){
						message += "It's some WE-DON'T-KNOW-WHAT place "
								+"to the " +opt+" but locked.\n";
					}else if(tempPO.outNeighborID == 1){
						message += "It's EXIT to the "+opt+" but locked.\n";
					}else{
						message += "It's "+Place.findPlaceByID(env.places, tempPO.outNeighborID).name
							+" to the "+opt+ " but locked.\n";
					}
				}else if(tempPO.isExit){
					message += "An Exit is to the "+opt+".\n";
				}else{
					message ="You can go "+opt+", "+
							"it's "+Place.findPlaceByID(env.places, tempPO.outNeighborID).name+".\n";
				}
			}else{
				message = "Nothing is to the "+opt+". Try other directions.\n";
			}
			return;
		}
        //action of LOOK HERE command
		if(opt.equalsIgnoreCase("HERE")){
			if(this.currentPlace.roomArtifacts.isEmpty()){
				message = "There is no artifact in this room.\n";
			}else{
				message = "*Artifacts in this room:\n";
				for(int i=0;i<this.currentPlace.roomArtifacts.size();i++){
					message += this.currentPlace.roomArtifacts.get(i).name+"\n";
				}
			}
			return;
		}
		
		//action of LOOK[Object Name]command
		for(int i=0;i<this.currentPlace.roomArtifacts.size();i++){//look at item in the room
			if(opt.equalsIgnoreCase(this.currentPlace.roomArtifacts.get(i).name)){
				for(int j=0; j<this.currentPlace.roomArtifacts.get(i).description.size();j++){
					message = currentPlace.roomArtifacts.get(i).description.get(j)+"\n";
				}
				return;
			}
		}
		for(int i=0;i<this.inventory.size();i++){//look at item in the inve
			if(opt.equalsIgnoreCase(this.inventory.get(i).name)){
				for(int j=0; j<this.inventory.get(i).description.size();j++){
					message = inventory.get(i).description.get(j)+"\n";
				}
				return;
			}
		}
		//no such item
		message = "No "+opt+" in the room or your inventory. Try other artifacts.\n";
	}
	
	/*execute the Exit command*/
	void executeExit(Environment env, Token token) {
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
	void executeQuit() {
		message = "Game quit. Thanks for coming.";
		this.currentPlace = null;
		this.Quit = true;
	}

	/*execute the HELP command*/
	void executeHelp() {
		message = "Valid inputs: \"HELP\", \"QUIT\", \"EXIT\", " +
				"\"GO\" Direction, \"LOOK\" [Direction/\"HERE\"/Object], \n" +
				"\"INVE(NTORY)\", \"GET\"/\"DROP\"/\"USE\" [Object].\n"
				+"Valid Directions: N,NORTH, S,SOUTH, E,EAST, W,WEST, U,UP, D,DOWN,\n" +
				" NE,NORTHEAST, NW,NORTHWEST, SE,SOUTHEAST, SW,SOUTHWEST,\n" +
				" NNE,NORTHNORTHEAST, NNW,NORTHNORTHWEST,\n" +
				" SSE,SOUTHSOUTHEAST, SSW,SOUTHSOUTHWEST,\n" +
				" ENE,EASTNORTHEAST, ESE,EASTSOUTHEAST,\n" +
				" WNW,WESTNORTHWEST, WSW,WESTSOUTHWEST.\n";
	}
	
	/*execute the INVE(NTORY) command
	 * list the objects the user is carrying*/
	void executeInventory() {
		if(inventory.size() == 0)
			message = "*No artifacts in your inventory.\n";
		else{
			message = "*Artifacts in your inventory:\n";
			for(int i = 0; i < inventory.size(); i++){
				message += inventory.get(i).name + "\n";
			}
		}			
	}
	
	/*execute the DROP command*/
	void executeDrop(Environment env, Token token){
		if(inventory.size() != 0){
			if(token.optional == null){//drop with no artifact name
				message += "Artifacts to drop on the ground: \n";
				for(int i = 0; i < inventory.size(); i++){
					message += inventory.get(i).name + "\n";
				}
			}
			else{//there is an artifact name
				boolean found = false;
				for(int i = 0; i < inventory.size(); i++){//find it in your inventory
					if(token.optional.equalsIgnoreCase(inventory.get(i).name)){
						inventory.get(i).placeID = currentPlace.ID;
						if(inventory.get(i) instanceof Light && ((Light)inventory.get(i)).lightActive){
							carryingLightValue -= ((Light)inventory.get(i)).lightLevel;
							currentPlace.lightValueOnGround += ((Light)inventory.get(i)).lightLevel;
						}
						currentPlace.roomArtifacts.add(inventory.get(i));
						inventory.remove(i);
						found = true;
						message += "Dropped " + token.optional + " on the ground," +
								" will stay in " + currentPlace.name + ".\n";
						break;
					}
				}
				if(found == false)
					message += token.optional + " is not in your inventory," +
							" type INVENTORY/INVE to see what is in your inventory.\n";
			}
		}
		else
			message += "There is nothing in your inventory to drop.\n";
	}
	
	/*execute the GET command*/
	void executeGet(Environment env, Token token){
		if((carryingLightValue + currentPlace.lightLevel + currentPlace.lightValueOnGround) < 15){
			message += "Too dark to see what is on the ground. Try turning on a light.\n";
			return;
		}
		if((carryingLightValue + currentPlace.lightLevel + currentPlace.lightValueOnGround) >= 100){
			message += "Too bright here to see what is on the ground. Try turning off a light.\n";
			return;
		}
		if(currentPlace.roomArtifacts.size() > 0){
			if(token.optional == null){//get with no artifact name
				message = "*Artifacts in the room:\n";
				for(int i = 0; i < currentPlace.roomArtifacts.size(); i++){
					message += currentPlace.roomArtifacts.get(i).name + "\n";
				}
			}
			else{//there is an artifact name
				boolean found = false;
				for(int i = 0; i < currentPlace.roomArtifacts.size(); i++){
					if(currentPlace.roomArtifacts.get(i).name.equalsIgnoreCase(token.optional)){
						found = true;
						if(currentPlace.roomArtifacts.get(i).movability > 0){
							currentPlace.roomArtifacts.get(i).usedEver = true;
							currentPlace.roomArtifacts.get(i).pickUp();//set placeID = -1
							inventory.add(currentPlace.roomArtifacts.get(i));
							currentPlace.roomArtifacts.remove(i);
							message += "Picked " + token.optional + " up, it is now in your inventory.\n";
							if(inventory.get(inventory.size() - 1) instanceof Light){
								if(((Light)inventory.get(inventory.size() - 1)).lightActive){
									carryingLightValue += ((Light)inventory.get(inventory.size() - 1)).lightLevel;
									currentPlace.lightValueOnGround -= ((Light)inventory.get(inventory.size() - 1)).lightLevel;
								}
							}
						}
						else
							message += token.optional + " is not movable.\n";
						break;
					}
				}
				if(found == false)
					message += "No "+token.optional+" in this place," +
							" type GET or LOOK HERE to see what is in your current location.\n";
			}
		}
		else
			message += "There is nothing here.\n";
	}
	
	// execute the USE command 
	//for artifact in the inventory or room
	void executeUse(Environment env, Token token){
		if(token.optional == null){//no artifact name specified
			message = "Please choose one artifact in your inventory or at this place:\n";
			this.executeInventory();
			
			if(!checkLighting()){
				if((carryingLightValue + currentPlace.lightLevel + currentPlace.lightValueOnGround) < 15)
					message = "It is too dark to see artifacts on the ground. Try turning a light on.\n";
				else
					message = "It is too bright to see artifacts on the ground. Try turning a light off.\n";
				return;
			}
			
			if(currentPlace.roomArtifacts.size() > 0){
				//if(!this.checkLighting()){
				//	message += "*Artifacts in the room you ever gor or used:\n";
				//	boolean found = false;
				//	for(int i = 0; i < currentPlace.roomArtifacts.size(); i++){
				//		if(currentPlace.roomArtifacts.get(i).usedEver){
				//			found = true;
				//			message += currentPlace.roomArtifacts.get(i).name + "\n";
				//		}
				//	}
				//	if(!found)
				//		message += "*Nothing. Too light or too dark to see other items in the room.\n";
				//	return;
				//}
				
				message += "*Artifacts in the room:\n";
				for(int i = 0; i < currentPlace.roomArtifacts.size(); i++){
					message += currentPlace.roomArtifacts.get(i).name + "\n";
				}
			}else message += "*No artifacts in the room.\n";
				
			return;
		}
		
		///USE artifact name
		//use artifact in the inventory; such artifacts must be usedEver since they were "got"
		for(int i=0; i<this.inventory.size(); i++){
			if(token.optional.equalsIgnoreCase(this.inventory.get(i).name)){//in his inve
				message = token.optional + " is in your inventory.\n";
				//action of using key
				if(this.inventory.get(i) instanceof Key){
					if((carryingLightValue + currentPlace.lightLevel + currentPlace.lightValueOnGround) < 15){
						message += "It is too dark to find a door for " + token.optional + ". Try turning a light on.\n";
						return;
					}
					else if((carryingLightValue + currentPlace.lightLevel + currentPlace.lightValueOnGround) >= 100){
						message += "It is too bright to find a door for " + token.optional + ". Try turning a light off.\n";
						return;
					}
					this.useKey((Key)this.inventory.get(i),env);
					return;
				}
				//action of using light
				else if(this.inventory.get(i) instanceof Light){
					this.useLight((Light)this.inventory.get(i), true);
					return;
				}
				//action of using other artifacts
				message += "Now it's in use.";
				return;
			}
		}
		if((carryingLightValue 
				+ currentPlace.lightLevel + currentPlace.lightValueOnGround) < 15){
			message += "It is too dark to use any artifacts on the ground." +
					" Try turning a light on.\n";
			return;
		}else if((carryingLightValue 
				+ currentPlace.lightLevel + currentPlace.lightValueOnGround) >= 100){
			message += "It is too bright to use any artifacts on the ground." +
					" Try turning a light off.\n";
			return;
		}
		//use artifact in the place	
		for(int i = 0; i < currentPlace.roomArtifacts.size(); i++){
			if(token.optional.equalsIgnoreCase(currentPlace.roomArtifacts.get(i).name)){//in the room
				//allow the use of used artifacts even without lighting supporting
				//if(currentPlace.roomArtifacts.get(i).usedEver){
				//	this.useArtifactOnGround(currentPlace.roomArtifacts.get(i),env);
				//	return;
				//}
				//check lighting in the room before using unused artifacts
				
				message = token.optional + " is in this place.\n";
				if(currentPlace.roomArtifacts.get(i) instanceof Key)
					useKey((Key)currentPlace.roomArtifacts.get(i), env);
				else if(currentPlace.roomArtifacts.get(i) instanceof Light)
					useLight((Light)currentPlace.roomArtifacts.get(i), false);
				else
					;//to be later defined with other artifacts
				
				//this.useArtifactOnGround(currentPlace.roomArtifacts.get(i),env);
//				currentPlace.roomArtifacts.get(i).usedEver = true;//duplicate, realized in useArtifact..
				return;
			}
		}
		//item not found
		message = "Sorry, no "+token.optional+" found in your inventory or in this place.\n";
	}
	
	void useArtifactOnGround(Artifact art,Environment env){
		art.usedEver = true;
		
		message = art.name+ " is in this place.\n";
		if(art instanceof Key){
			this.useKey((Key)art,env);
		}else if(art instanceof Light){
			this.useLight((Light)art, false);
		}else{//action of using other artifacts
			message += "Now it's in use.";
		}
	}
	
	//execute USE Light at current Place
	//if the light is ON, turn it off; if it's OFF, turn it on
	void useLight(Light light, boolean inInventory){
		if(light.lightActive){
			if(inInventory)
				carryingLightValue -= light.lightLevel;
			else
				currentPlace.lightValueOnGround -= light.lightLevel;
			light.lightActive = false;
			message += "It is now off.\n";
		}
		else{
			if(inInventory)
				carryingLightValue += light.lightLevel;
			else
				currentPlace.lightValueOnGround += light.lightLevel;
			light.lightActive = true;		
			message += "It is now on.\n";
		}
	}
	
	//execute USE Key at current Place
	//unlock or lock all matching paths
	void useKey(Key key,Environment env){
		boolean foundLock = false;
		for(int j=0; j<this.currentPlace.outGoing.size(); j++){
			if(this.checkKeyAgainstLock(key.keyPattern, 
				key.masterCode,
				this.currentPlace.outGoing.get(j).lockPattern)){
				foundLock = true;
				if(this.currentPlace.outGoing.get(j).isLocked == false){
					this.currentPlace.outGoing.get(j).isLocked = true;
					if(currentPlace.outGoing.get(j).outNeighborID==1)
						message += " Path in the direction "+currentPlace.outGoing.get(j).direction+" to "+ 
								"EXIT" 
								+ " gets locked.\n";
					else
						message += " Path in the direction "+currentPlace.outGoing.get(j).direction+" to "+ 
							Place.findPlaceByID(env.places,currentPlace.outGoing.get(j).outNeighborID).name 
							+ " gets locked.\n";
				}else{
					this.currentPlace.outGoing.get(j).isLocked = false;
					if(currentPlace.outGoing.get(j).outNeighborID==1)
						message += " Path in the direction "+currentPlace.outGoing.get(j).direction+" to "+ 
								"EXIT" 
								+ " gets unlocked.\n";
					else
						message += " Path in the direction "+currentPlace.outGoing.get(j).direction+" to "+ 
							Place.findPlaceByID(env.places,currentPlace.outGoing.get(j).outNeighborID).name 
							+ " gets unlocked.\n";
				}
//				message += "The key unlocked every locked door and locked every unlocked door.\n";
			}
		}
		if(!foundLock)
			message += "No lock matched that key pattern.\n";
	}
	
	/*check if a keyPattern matches a lockPattern*/
	boolean checkKeyAgainstLock(int keyPattern, int mastercode, int lockPattern){
		if(lockPattern == 0){ //no key can change the status of this lock
			return false;
		}
		if(lockPattern/((int)Math.pow(10,mastercode)) == keyPattern){
			return true;
		}
		return false;
		
	}
	
	//return true if visible
	boolean checkLighting(){
		if((carryingLightValue + currentPlace.lightLevel + currentPlace.lightValueOnGround) < 15 || 
				(carryingLightValue + currentPlace.lightLevel + currentPlace.lightValueOnGround) >= 100)
			return false;
		return true;
	}
}
