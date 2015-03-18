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
	List<Place> places = new ArrayList<Place>();
	List<Artifact> artifacts = new ArrayList<Artifact>();
	List<Actor> actors = new ArrayList<Actor>();
	List<PlaceOut> paths = new ArrayList<PlaceOut>();
	List<Integer> lighting = new ArrayList<Integer>();

	String message = "";
	int defaultLighting = 50;
//	TestParser testing = new TestParser();
	int[] version = new int[2];
	
	/*build the environment from the input file*/
	void initEnvironment(String filename){
//		set default lightLevel according to time

		//UNPLAYABLE at night time in our current implementation, may be revamped
		//java.util.Date date= new java.util.Date();
		//if(date.getHours()<6 | date.getHours()>20) this.defaultLighting = 0;
		//else if(date.getHours()<7 | date.getHours()>18 ) this.defaultLighting = 15;
		//else this.defaultLighting = 50;
		
		File file = new File(filename);
		BufferedReader brf = null;
		try {
			brf = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			message = "ERROR: No environment file found!";
			return;
		}
		String line = "";
		String tempLine = "";

		while(true){
			if(tempLine.isEmpty()){
				try {
					line = brf.readLine().split("//")[0].replaceAll("\t", " ").trim();
				} catch (Exception e1) { //end of file
					if(message.isEmpty()){ //no "GDF ..." in the map file
						message += "ERROR: No magic word, version number or environment name!/n";
					}
					if(message.contains("ERROR") == false){
						if(places.size() > 0){ //fine enough to allow game to start
							message += "Input commands to wander around and find the exit.\n" +
									"Type HELP for instructions at any time.\n" +
									"Let's begin!";
						}
						else
							message += "ERROR: Incomplete map - Places are required!\n";
					}
					try {
						brf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			}else{
				//tempLine is a starting line of new section, or ""
				line = tempLine;
				tempLine = ""; //isEmpty()
			}

			if(line.isEmpty()) continue;
			if(!message.isEmpty() && message.contains("ERROR")) return;
			
			String lineHead =  line.replaceAll("\t", " ").trim().split(" ",2)[0];
			int secCounter = 0;
			try{
				secCounter = this.parseIntegers(line).get(0); //number of items in this section
			}
			catch(Exception e){
				message += "ERROR: could not parse integer next to " + lineHead + "!\n";
				return;
			}
			switch (lineHead){
				case "GDF":
					message = "Game environment: "+line+"\n";
					//check version number
					try{
						version = this.parseVersionNumber(line);
					}
					catch(Exception e){
						message += "ERROR: could not parse version number!\n";
						continue;
					}
					if(version == null){
						message +="ERROR: check the version number in the game file\n";
						continue;
					}
					
					if(version[0]>3 || (version[0]==3 && version[1]!=10)){
						message +="ERROR: we currently accept only map version 1.*, 2.* and 3.10\n";
						continue;
					}
					
					continue;//continue to the outer while loop
				case "PLACES": 
					tempLine = this.addAllPlaces(brf,line);
					//even in called function, brf updates location globally, great!
//					testing.testPlaces(this);
					if(message.contains("ERROR") == false){
						if(secCounter != this.places.size())
							message += "ERROR: inconsistency between announced Places number and the real number!\n";
						else if(this.places.get(this.places.size() - 1).description.size() == 0)
							message += "ERROR: place ID" + this.places.get(this.places.size() - 1).ID + " has no description!\n";
					}
					continue;
				case "PATHS": 
					tempLine = this.addAllPathsAsPlaceOuts(brf,line);
					if(secCounter != paths.size() && message.contains("ERROR") == false)
						message += "ERROR: inconsistency between announced Paths number and the real number!\n";
//					testing.testPaths(this);
					continue;
				case "LIGHTING": 
					if(version[0] < 2){
						message += "ERROR: map version under 2.0 doesn't allow Lighting entries!\n";
						continue;
					}
					tempLine = this.addAllLightingsAsLightLevels(brf,line);
					if(secCounter != lighting.size() && message.contains("ERROR") == false)
						message += "ERROR: inconsistency between announced Lightings number and the real number!\n";
					continue;
				case "ARTIFACTS":
//					if(version[0] < 2){
//						message += "ERROR: invalid map version for artifact entries!\n";
//						continue;
//					}
					int artCounter = artifacts.size();
					tempLine = this.addAllArtifacts(brf,line);
					if(message.contains("ERROR") == false){
						if(secCounter != (this.artifacts.size() - artCounter))
							message += "ERROR: inconsistency between announced Artifacts number and the real number!\n";
						else if(this.artifacts.get(this.artifacts.size() - 1).description.size() == 0)
							message += "ERROR: artifact ID " + this.artifacts.get(this.artifacts.size() - 1).ID + " has no description!\n";
					}
					//testing.testArtifacts(this);
					continue;
				case "KEYS":
//					if(version[0] < 2){
//						message += "ERROR: invalid map version for key entries!\n";
//						continue;
//					}
					int keyCounter = artifacts.size();
					tempLine = this.addAllKeys(brf,line);
					if(message.contains("ERROR") == false){
						if(secCounter != (this.artifacts.size() - keyCounter))
							message += "ERROR: inconsistency between announced Keys number and the real number!\n";
						else if(this.artifacts.get(this.artifacts.size() - 1).description.size() == 0)
							message += "ERROR: Key ID " + this.artifacts.get(this.artifacts.size() - 1).ID + " has no description!\n";
					}
					continue;
				case "LIGHTS":
//					if(version[0] < 2){
//						message += "ERROR: invalid map version for light entries!\n";
//						continue;
//					}
					int lightCounter = artifacts.size();
					tempLine = this.addAllLights(brf,line);
					if(message.contains("ERROR") == false){
						if(secCounter != (this.artifacts.size() - lightCounter))
							message += "ERROR: inconsistency between announced Lights number and the real number!\n";
						else if(this.artifacts.get(this.artifacts.size() - 1).description.size() == 0)
							message += "ERROR: Light ID " + this.artifacts.get(this.artifacts.size() - 1).ID + " has no description!\n";
					}
					continue;
				case "ACTORS":
					if(version[0] < 3){
						message += "ERROR: invalid map version for actor entries (should be 3.10)!\n";
						continue;
					}
					tempLine = this.addAllActors(brf,  line);
					if(message.contains("ERROR") == false){
						if(secCounter != actors.size())
							message += "ERROR: inconsistency between announced Actors number and the real number!\n";
						else if(this.actors.get(this.actors.size() - 1).question.size() == 0)
							message += "ERROR: Actor ID " + this.actors.get(this.actors.size() - 1).ID + " has no description!\n";
					}
					continue;
				default: break;
			}

		}

	}
	
	/*add all Places to the map, ignoring nPlace,
	 *  thus "more/fewer Places than announced" error tolerated
	 *input: variable line is the staring line of this section*/
	String addAllPlaces(BufferedReader brf, String line){
		//read new lines and add new places
		while(true){
			try {
				line = brf.readLine().split("//")[0].replaceAll("\t", " ").trim();
			} catch (Exception e) {
//				message += "ERROR: Incomplete map, detected while reading PLACES!";
				return "";
			}
			if(line.isEmpty()) continue;
			
			if(line.startsWith("*")){ //description to the latest place
				//get index, remove *. suppose only one * at the start
				if(places.isEmpty()){
					message += "ERROR - added a description for places when no places parsed.\n";
					return line;
				}
				places.get(places.size()-1).description.add(line.replace("*","")); 
				continue;
			}
			
			if(this.isSectionStarter(line)) return line;
			
			List<Integer> nums;
			try{
				nums = parseIntegers(line);
			}
			catch(Exception e){
				message += "ERROR: could not parse integers in Place "+line+" !\n";
				return line;
			}
			if(nums.size()>0){
				if(Character.isDigit(line.charAt(0)) == false){
					message += "ERROR - ID is not first in this line.\n";
					return line;
				}
				Place tempPlace = new Place();
				if(nums.get(0)==0 | nums.get(0)==1){
					message +="ERROR: Wrong map with place ID "+nums.get(0)+", ID 0 and 1 are reserved!";
					return line;
				}else if(Place.findPlaceIndexByID(places, nums.get(0)) != -1){//duplicate ID
					message +="ERROR: Wrong map - duplicate place ID "+nums.get(0)+"!";
					return line;
				}else{
					tempPlace.ID = nums.get(0);
				}
				tempPlace.name = line.replaceFirst("[0-9]+", "").trim();
				if(tempPlace.name.isEmpty()){
					message +="ERROR: Wrong map - place "+tempPlace.ID+" has no name!";
					return line;
				}
				tempPlace.lightLevel = this.defaultLighting;
				
				if(places.isEmpty() == false && places.get(places.size() - 1).description.size() == 0){
					message += "ERROR - no description for " + places.get(places.size() - 1).name + "\n";
					return line;
				}
				
				places.add(tempPlace);
			}
			
		}
	}
	
	/*read PATHS and add outgoing PlaceOut for Places, ignoring nPath
	 *input: variable line is the staring line of this section*/
	String addAllPathsAsPlaceOuts(BufferedReader brf, String line){
		//read lines, add paths and PlaceOuts
		while(true){
			try {
				line = brf.readLine().split("//")[0].replaceAll("\t", " ").trim();
			} catch (Exception e) {
//				message += "ERROR: Incomplete map, detected while reading PATHS!";
				return "";
			}
			if(line.isEmpty()) continue;
			if(this.isSectionStarter(line)) return line;
			
			List<Integer> nums;
			try{
				nums = this.parseIntegers(line);
			}
			catch(Exception e){
				message += "ERROR: could not parse integers in Path "+line+" !\n";
				return line;
			}
			if(nums.size()< 3 || nums.size() >=5) continue;//invalid path
			//add outgoing info for source place
			int index = Place.findPlaceIndexByID(places, nums.get(1));
			if(index < 0){//the source Place does not exist
				message += "ERROR: Wrong map - " +
						"check the source of Path ID "+nums.get(0)+".";
				return line;
			}
			
			PlaceOut po = new PlaceOut();
			
			if(PlaceOut.findPlaceOutByID(paths, nums.get(0)) != -1){
				message += "ERROR - duplicate path ID's: " + nums.get(0) + "\n";
				return line;
			}
			po.ID = nums.get(0);
			if(!this.isValidPlaceID(Math.abs(nums.get(2)))){
				message += "ERROR: Wrong map - " +
						"check the destination of Path ID "+nums.get(0)+".";
				return line;
			}else{
				po.outNeighborID = Math.abs(nums.get(2));//non-negative only
			}
			if(nums.size() == 4){
				if(version[0] >= 2)
					po.lockPattern = nums.get(3);
				else{ //v1.*
					message += "ERROR: map V" + version[0] +"."+version[1]+ 
							" does not allow this many integers for path " + po.ID + "!\n";
					return line;
				}
			}
			String dir = line.replaceAll("[0-9]+","").replaceAll("-","").trim();
			if(line.indexOf(dir) <= (String.valueOf(po.ID).length() + String.valueOf(nums.get(1)).length())){
				message += "ERROR - direction is out of order for Path ID " + nums.get(0);
				return line;
			}
			if(Token.isValidDirection(dir)){
				po.direction = Token.abbreviateDir(dir);
			}else{
				message += "ERROR: Wrong map - " +
						"check the direction of Path ID "+nums.get(0)+".";
				return line;
			}

			if(po.outNeighborID == 1){
				po.isExit = true;
			}
			if(nums.get(2)<0 || po.outNeighborID == 0){
				po.isLocked = true;
			}
			places.get(index).outGoing.add(po);
			paths.add(po);
		}
	}
	
//	/*Return the opposite direction of a given direction*/
//	String getOppositeDir(String dir) {
//		switch (dir.toUpperCase()) {
//			case "UP": case "U": return "D";//break;
//			case "DOWN": case "D": return "U";
//	        case "NORTH": case "N": return "S";
//	        case "SOUTH": case "S": return "N";
//	        case "EAST": case "E": return "W";
//	        case "WEST": case "W": return "E";
//	        case "NORTHEAST": case "NE": return "SW"; 
//	        case "NORTHWEST": case "NW": return "SE"; 
//	        case "SOUTHEAST": case "SE": return "NW"; 
//	        case "SOUTHWEST": case "SW": return "NE"; 
//	        case "NORTHNORTHEAST": case "NNE": return "SSW"; 
//	        case "NORTHNORTHWEST": case "NNW": return "SSE"; 
//	        case "SOUTHSOUTHEAST": case "SSE": return "NNW"; 
//	        case "SOUTHSOUTHWEST": case "SSW": return "NNE"; 
//	        case "EASTNORTHEAST": case "ENE": return "WSW"; 
//	        case "EASTSOUTHEAST": case "ESE": return "WNW"; 
//	        case "WESTNORTHWEST": case "WNW": return "ESE"; 
//	        case "WESTSOUTHWEST": case "WSW": return "ENE";
//		}
//		return null;
//	}
	
	
	
	/*read LIGHTING and add lightLevel directly to Places, ignoring nEntries
	 *input: variable line is the staring line of this section
	 *Pre-condition: PLACES added.*/
	String addAllLightingsAsLightLevels(BufferedReader brf, String line){
		boolean changeDefaultLighting = false;
		int newDefaultLighting = -1;
		//read new lines and add lightLevel to Places
		while(true){
			try {
				line = brf.readLine().split("//")[0].replaceAll("\t", " ").trim();
			} catch (Exception e) {
//				message += "ERROR: Incomplete map, detected while reading LIGHTING!";
				if(changeDefaultLighting){
					this.changeDefaultPlaceLightLevel(newDefaultLighting);
				}
				return "";
			}
			if(line.isEmpty()) continue;

			if(this.isSectionStarter(line)){
				//fewer LIGHTING than announced is tolerated
//				message += "ERROR: Incomplete map detected while reading LIGHTING!";
				if(changeDefaultLighting){
					this.changeDefaultPlaceLightLevel(newDefaultLighting);
				}
				return line;
			}
			
			List<Integer> nums;
			try{
				nums = parseIntegers(line);
			}
			catch(Exception e){
				message += "ERROR: could not parse integers in lighting entries!\n";
				return line;
			}
			if(nums.size()==3){
				for(int i = 0; i < lighting.size(); i++){
					if(nums.get(0) == lighting.get(i).intValue()){
						message += "ERROR: duplicate lighting IDs: " + nums.get(0) + "\n";
						return line;
					}
				}
				int placeID = nums.get(1);
				if(nums.get(2)<0 || nums.get(2)>100){
					//error tolerated, this line will be ignored
					message += "ERROR: wrong lightLevel in LIGHTING "+nums.get(0)+"!\n";
					return line;
				}
				if(placeID==0){
					changeDefaultLighting = true;
					newDefaultLighting = nums.get(2);
				}else if(this.isInPlaces(placeID)){
					Place.findPlaceByID(places, placeID).lightLevel = nums.get(2);
				}
				//invalid-PlaceID-error tolerated and ignored
				lighting.add(new Integer(nums.get(0)));
			}
			
		}
	}
	
	/*add Artifacts to the map, ignoring nArtifacts
	 * *input: variable line is the staring line of this section*/
	String addAllArtifacts(BufferedReader brf, String line){		
		//read lines and add Artifacts
		while(true){
			try {
				line = brf.readLine().split("//")[0].replaceAll("\t", " ").trim();
			} catch (Exception e) {
//				message += "ERROR: Incomplete map, detected while reading ARTIFACTS!";
				return "";
			}
			if(line.isEmpty()) continue;
			
			if(line.startsWith("*")){ //description to the latest-added artifact
				if(artifacts.isEmpty()){
					message += "ERROR - added a description to artifacts when no artifacts parsed.\n";
					return line;
				}
				artifacts.get(artifacts.size()-1).description.add(line.replace("*","")); 
				continue;
			}
			
			if(this.isSectionStarter(line)) return line;
			
			List<Integer> nums;
			try{
				nums = parseIntegers(line);
			}
			catch(Exception e){
				message += "ERROR: could not parse integers in Artifact "+line+" !\n";
				return line;
			}
			if(nums.size()>=4){
				Artifact art = new Artifact();
				art.type = 0;
				if(Artifact.findArtifactIndexByID(artifacts,nums.get(0)) != -1){//duplicate ID
					message +="ERROR: Wrong map - duplicate Artifact ID "+nums.get(0)+"!";
					return line;
				}else{
					art.ID = nums.get(0);
				}
				if(this.isInPlaces(nums.get(1))){
					art.placeID = nums.get(1);
				}else{
					message += "ERROR: Wrong map - " +
							"check the location Place ID of Artifact "+art.ID+".";
					return line;
				}
				art.value = nums.get(2);
				if(nums.get(3) > 1000 || nums.get(3) < 0){
					message += "ERROR: movability of " + art.ID + " must be between 0 and 1000!\n";
					return line;
				}
				art.movability = nums.get(3);
				
				String[] temps = line.split(" ",5);
				art.name = temps[temps.length-1];
				if(art.name.isEmpty()){
					message +="ERROR: Wrong map - Artifact "+art.ID+" has no name!";
					return line;
				}
				
				if(artifacts.isEmpty() == false && artifacts.get(artifacts.size() - 1).description.size() == 0){
					message += "ERROR - artifact with no description " + artifacts.get(artifacts.size() - 1).name + "\n";
					return line;
				}
				this.artifacts.add(art);
				//add the artifact to its original location Place
				Place.findPlaceByID(places, art.placeID).roomArtifacts.add(art);
			}
			
		}
	}
	
	/*add Keys to the map, ignoring nKeys
	 *input: variable line is the staring line of this section*/
	String addAllKeys(BufferedReader brf, String line){
		//read new lines and add new Keys
		while(true){
			try {
				line = brf.readLine().split("//")[0].replaceAll("\t", " ").trim();
			} catch (Exception e) {
//				message += "ERROR: Incomplete map, detected while reading KEYS!";
				return "";
			}
			if(line.isEmpty()) continue;

			if(line.startsWith("*")){
				if(artifacts.isEmpty()){
					message += "ERROR - added a desscription for keys when no keys added.\n";
					return line;
				}
				artifacts.get(artifacts.size()-1).description.add(line.replace("*","")); 
				continue;
			}
			
			if(this.isSectionStarter(line)) return line;
			
			List<Integer> nums;
			try{
				nums = parseIntegers(line);
			}
			catch(Exception e){
				message += "ERROR: could not parse integers in Key "+line+" !\n";
				return line;
			}
			if(nums.size()>=6){
				Key e = new Key();
				e.type = 1;
				//if(Key.findKeyIndexByID(keys,nums.get(0)) != -1){//duplicate ID
				//	message +="ERROR: Wrong map - duplicate Key ID "+e.ID+"!";
				//	return line;
				//}
				Artifact temp = Artifact.findArtifactByID(artifacts, nums.get(0));
				if(temp!=null && temp.type==1){//duplicate ID against other Keys
					message +="ERROR: Wrong map - duplicate Key ID "+nums.get(0)+"!";
					return line;
				}else{
					e.ID = nums.get(0);
				}
				if(this.isInPlaces(nums.get(1))){
					e.placeID = nums.get(1);
				}else{
					message += "ERROR: Wrong map - " +
							"check the location Place ID of Key "+e.ID+".";
					return line;
				}
				e.keyPattern = nums.get(2);
				e.masterCode = nums.get(3);
				e.value = nums.get(4);
				
				if(nums.get(5) < 0 || nums.get(5) > 1000){
					message += "ERROR: movability of key " + e.ID + " must be between 0 and 1000!";
					return line;
				}
				e.movability = nums.get(5);
				
				String[] temps = line.split(" ",7);
				e.name = temps[temps.length-1];
				if(e.name.isEmpty()){
					message +="ERROR: Wrong map - Key "+e.ID+" has no name!";
					return line;
				}
				//this.keys.add(e);
				
				if(artifacts.isEmpty() == false && artifacts.get(artifacts.size() - 1).description.size() == 0){
					message += "ERROR - artifact with no description " + artifacts.get(artifacts.size() - 1).name + "\n";
					return line;
				}
				this.artifacts.add(e);
				//add the artifact to its original location Place
				Place.findPlaceByID(places, e.placeID).roomArtifacts.add(e);
			}
			
		}
	}
	
	/*add Lights to the map, ignoring nLights
	 *input: variable line is the staring line of this section*/
	String addAllLights(BufferedReader brf, String line){
		//read new lines and add new Lights
		while(true){
			try {
				line = brf.readLine().split("//")[0].replaceAll("\t", " ").trim();
			} catch (Exception e) {//end of map file
				return "";//!=null, but isEmpty
			}
			if(line.isEmpty()) continue;
			
			if(line.startsWith("*")){
				if(artifacts.isEmpty()){
					message += "ERROR - added a description for lights when no lights added\n";
					return line;
				}
				artifacts.get(artifacts.size()-1).description.add(line.replace("*","")); 
				continue;
			}
			
			//Section LIGHTS comes last if in default order, then this line is never used
			if(this.isSectionStarter(line))	return line;
			
			List<Integer> nums;
			try{
				nums = parseIntegers(line);
			}
			catch(Exception e){
				message += "ERROR: could not parse integers in Lighting "+line+" !\n";
				return line;
			}
			if(nums.size()>=5){
				Light l = new Light();
				l.type = 2;
				//if(Light.findLightIndexByID(lights,nums.get(0)) != -1){//duplicate ID
				//	message +="ERROR: Wrong map - duplicate Light ID "+l.ID+"!";
				//	return line;
				//}
				Artifact temp = Artifact.findArtifactByID(artifacts, nums.get(0));
				if(temp!=null && temp.type==2){//duplicate ID with other Lights
					message +="ERROR: Wrong map - duplicate Light ID "+nums.get(0)+"!";
					return line;
				}else{
					l.ID = nums.get(0);
				}
				if(this.isInPlaces(nums.get(1))){
					l.placeID = nums.get(1);
				}else{
					message += "ERROR: Wrong map - " +
							"check the location Place ID of Light "+l.ID+".";
					return line;
				}
				if(nums.get(2) < 0 || nums.get(2) > 100){
					message += "ERROR: light ID " + l.ID + " light level must be between 0 and 100!\n";
					return line;
				}
				l.lightLevel = nums.get(2);
				l.value = nums.get(3);
				
				if(nums.get(4) < 0 || nums.get(4) > 1000){
					message += "ERROR: light " + l.ID + " must have a movability between 0 and 1000!\n";
					return line;
				}
				l.movability = nums.get(4);
				
				String[] temps = line.split(" ",6);
				l.name = temps[temps.length-1];
				if(l.name.isEmpty()){
					message +="ERROR: Wrong map - Light "+l.ID+" has no name!";
					return line;
				}
				//this.lights.add(l);
				
				if(artifacts.isEmpty() == false && artifacts.get(artifacts.size() - 1).description.size() == 0){
					message += "ERROR - artifact with no description " + artifacts.get(artifacts.size() - 1).name + "\n";
					return line;
				}
				this.artifacts.add(l);
				//add the artifact to its original location Place
				Place.findPlaceByID(places, l.placeID).roomArtifacts.add(l);
			}
			
		}
	}
	
	/*
	 * This will parse the actors from the gdf file
	 * There is error checking for each of the inputs except the question line
	 * see the actor_format.txt file for more information
	 */
	String addAllActors(BufferedReader brf, String line){
		while(true){
			try {
				line = brf.readLine().split("//")[0].replaceAll("\t", " ").trim();
			} catch (Exception e) {//end of map file
				return "";//!=null, but isEmpty
			}
			if(line.isEmpty()) continue;
			if(line.startsWith("*")){
				if(actors.isEmpty()){
					message += "ERROR - added question for actors when no actors parsed.\n";
					return line;
				}
				actors.get(actors.size()-1).question.add(line.replace("*","")); 
				//System.out.println(line);
				continue;
			}
			List<Integer> nums;
			try{
				nums = parseIntegers(line);
			}
			catch(Exception e){
				message += "ERROR: could not parse integers in Actor "+line+" !\n";
				return line;
			}
			if(nums.size() >= 2){
				Actor tempActor = new Actor();
				if(Actor.findActorIndexByID(actors, nums.get(0)) != -1){
					message += "ERROR - duplicate Actors " + nums.get(0);
					return line;
				}
				tempActor.ID = nums.get(0);
				if(this.isInPlaces(nums.get(1)))
					tempActor.placeID = nums.get(1);
				else{
					message += "ERROR - invalid Place ID for Actor " + tempActor.ID;
					return line;
				}
				String[] temps = line.split(" ",4);
				tempActor.name = temps[2];
				if(tempActor.name.isEmpty()){
					message += "ERROR - Actor " + tempActor.ID + " has no name";
					return line;
				}
				tempActor.answer = temps[3];
				if(tempActor.answer.isEmpty()){
					message += "ERROR - Actor " + tempActor.name + " has no answer";
					return line;
				}
				if(actors.isEmpty() == false && actors.get(actors.size() - 1).question.size() == 0){
					message += "ERROR - Actor parsed with no description " + actors.get(actors.size() - 1).name + "\n";
					return line;
				}
				
				actors.add(tempActor);
				//add actor to place, overwrite if >1 anounced
				Place.findPlaceByID(this.places,tempActor.placeID).actor = tempActor;
				
				//System.out.println("ID: " + tempActor.ID);
				//System.out.println("placeID: " + tempActor.placeID);
				//System.out.println("name: " + tempActor.name);
				//System.out.println("answer: " + tempActor.answer);
			}	
		}
	}
	
	/*Read numbers from one line of GDF file*/
	List<Integer> parseIntegers(String s){
		s= s.split("//")[0].trim();
		List<Integer> nums = new ArrayList<Integer>();
		Pattern p = Pattern.compile("-?[0-9]+");
		Matcher m = p.matcher(s);
		while (m.find()) {
			int temp = Integer.parseInt(m.group());
			nums.add(temp);
		}
		return nums;
	}
	
	
	int[] parseVersionNumber(String s){
		s= s.split("//")[0].trim();
		String[] ss = s.split("\\.",2);
		if(ss.length<2
				||ss[0].length()<1 || !Character.isDigit(ss[0].charAt(ss[0].length()-1))
				||ss[1].length()<1 || !Character.isDigit(ss[1].charAt(0))){ //not "int.int" format
			return null;
		}
			
		List<Integer> v = new ArrayList<Integer>();
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(ss[0]);
		while(m.find()){
			int temp = Integer.parseInt(m.group());
			v.add(temp);
		}
		
		p = Pattern.compile("[0-9]+");
		m = p.matcher(ss[1]);
		while(m.find()){
			int temp = Integer.parseInt(m.group());
			v.add(temp);
		}
		
		if(v.size()<2) return null;
		
		int[] version = new int[2];
		version[0] = v.get(0);
		version[1] = v.get(1);
		
		return version;
	}
	
	List<Double> parseDoubles(String s){
		s= s.split("//")[0].trim();
		List<Double> nums = new ArrayList<Double>();
		Pattern p = Pattern.compile("-?[0-9]+\\.?[0-9]*"); //TODO: read 3.10 as 3.1, 2 as 2.0
		Matcher m = p.matcher(s);
		while (m.find()) {
			double temp = Double.parseDouble(m.group());
			nums.add(temp);
		}
		return nums;
	}
	
	/*Change the default light level for every place.
	 *Default light level is indicated in LIGHTING for place with ID 0.
	 *Return: true if changed successfully, false if invalid light value*/
	boolean changeDefaultPlaceLightLevel(int lightLevel){
		if(lightLevel<0 | lightLevel>100) return false;
		
		for(int i=0;i<this.places.size();i++){
			if(this.places.get(i).lightLevel == this.defaultLighting)
				this.places.get(i).lightLevel = lightLevel;
		}
		return true;
	}

	/*check if a given placeID is in existing Places
	 * Note: Place 0 and Place 1 are valid but not in Places*/
	boolean isInPlaces(int id){
		if(Place.findPlaceIndexByID(places, id)>=0){
			return true;
		}
		return false;
	}
	
	/*check if a given placeID is valid*/
	boolean isValidPlaceID(int id){
		if(this.isInPlaces(id) || id==0 ||id==1){
			return true;
		}
		return false;
	}

	/*check if a given line is a starting line of a new section in the map*/
	boolean isSectionStarter(String line){
		line = line.trim();
		if(line.startsWith("PLACES")
				|line.startsWith("PATHS")
				|line.startsWith("LIGHTING")
				|line.startsWith("ARTIFACTS")
				|line.startsWith("KEYS")
				|line.startsWith("LIGHTS")
				|line.startsWith("ACTORS")) 
			return true;
		return false;
	}
	
	int findParsingLimit(String temp){
		try{
			return new Integer(temp).intValue();
		}
		catch(Exception e){
			return 0;
		}
	}
}
