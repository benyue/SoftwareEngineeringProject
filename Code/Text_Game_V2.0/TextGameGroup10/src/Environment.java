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

	String message = "";
	int defaultLighting = 50;
	
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
					if(places.size()>0){ //fine enough to allow game to start
						message += "Input commands to wander around and find the exit.\n" +
							"Type HELP for instructions at any time.\n" +
							"Let's begin!";
					}else{
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
			switch (lineHead){
				case "GDF":
					message = "Game environment: "+line+"\n";
					continue;//continue to the outer while loop
				case "PLACES": 
					tempLine = this.addAllPlaces(brf,line);
					//even in called function, brf updates location globally, great!
					continue;
				case "PATHS": 
					tempLine = this.addAllPathsAsPlaceOuts(brf,line);
					continue;
				case "LIGHTING": 
					tempLine = this.addAllLightingsAsLightLevels(brf,line);
					continue;
				case "ARTIFACTS":
					tempLine = this.addAllArtifacts(brf,line);
					continue;
				case "KEYS":
					tempLine = this.addAllKeys(brf,line);
					continue;
				case "LIGHTS":
					tempLine = this.addAllLights(brf,line);
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
				places.get(places.size()-1) //get index
					.description.add(line.replace("*","")); 
				continue;
			}
			
			if(this.isSectionStarter(line)) return line;
			
			List<Integer> nums = parseIntegers(line);
			if(nums.size()>0){
				Place tempPlace = new Place();
				if(Place.findPlaceIndexByID(places, nums.get(0)) != -1){//duplicate ID
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
			
			List<Integer> nums = parseIntegers(line);
			if(nums.size() == 4){// no need to check duplicate ID
				//add outgoing info for source place
				int index = Place.findPlaceIndexByID(places, nums.get(1));
				if(index < 0){//the source Place does not exist
					message += "ERROR: Wrong map - " +
							"check the source of Path ID "+nums.get(0)+".";
					return line;
				}
				
				PlaceOut po = new PlaceOut();
				
				if(!this.isValidPlaceID(Math.abs(nums.get(2)))){
					message += "ERROR: Wrong map - " +
							"check the destination of Path ID "+nums.get(0)+".";
					return line;
				}else{
					po.outNeighborID = Math.abs(nums.get(2));//non-negative only
				}
				po.lockPattern = nums.get(3);
				
				String dir = line.replaceAll("[0-9]+","").replaceAll("-","").trim();
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
			}
		}
	}
	
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
			
			List<Integer> nums = parseIntegers(line);
			if(nums.size()==3){
				int placeID = nums.get(1);
				if(nums.get(2)<0 || nums.get(2)>100){
					//error tolerated, this line will be ignored
//					message += "ERROR: wrong lightLevel in LIGHTING "+nums.get(0)+"!";
					continue;
				}
				if(placeID==0){
					changeDefaultLighting = true;
					newDefaultLighting = nums.get(2);
				}else if(this.isInPlaces(placeID)){
					Place.findPlaceByID(places, placeID).lightLevel = nums.get(2);
				}
				//invalid-PlaceID-error tolerated and ignored
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
				artifacts.get(artifacts.size()-1).description.add(line.replace("*","")); 
				continue;
			}
			
			if(this.isSectionStarter(line)) return line;
			
			List<Integer> nums = parseIntegers(line);
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
				art.movability = nums.get(3);
				
				String[] temps = line.split(" ",5);
				art.name = temps[temps.length-1];
				if(art.name.isEmpty()){
					message +="ERROR: Wrong map - Artifact "+art.ID+" has no name!";
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
			
			//if(line.startsWith("*")){
			//	keys.get(keys.size()-1).description.add(line.replace("*","")); 
			//	continue;
			//}
			
			if(line.startsWith("*")){
				artifacts.get(artifacts.size()-1).description.add(line.replace("*","")); 
				continue;
			}
			
			if(this.isSectionStarter(line)) return line;
			
			List<Integer> nums = parseIntegers(line);
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
				e.movability = nums.get(5);
				
				String[] temps = line.split(" ",7);
				e.name = temps[temps.length-1];
				if(e.name.isEmpty()){
					message +="ERROR: Wrong map - Key "+e.ID+" has no name!";
					return line;
				}
				//this.keys.add(e);
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
				artifacts.get(artifacts.size()-1).description.add(line.replace("*","")); 
				continue;
			}
			//if(line.startsWith("*")){
			//	lights.get(lights.size()-1).description.add(line.replace("*","")); 
			//	continue;
			//}
			
			//Section LIGHTS comes last if in default order, then this line is never used
			if(this.isSectionStarter(line))	return line;
			
			List<Integer> nums = parseIntegers(line);
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
				l.lightLevel = nums.get(2);
				l.value = nums.get(3);
				l.movability = nums.get(4);
				
				String[] temps = line.split(" ",6);
				l.name = temps[temps.length-1];
				if(l.name.isEmpty()){
					message +="ERROR: Wrong map - Light "+l.ID+" has no name!";
					return line;
				}
				//this.lights.add(l);
				this.artifacts.add(l);
				//add the artifact to its original location Place
				Place.findPlaceByID(places, l.placeID).roomArtifacts.add(l);
			}
			
		}
	}
	
	/*Read positive numbers from one line of GDF file*/
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
				|line.startsWith("LIGHTS")) 
			return true;
		return false;
	}
}
