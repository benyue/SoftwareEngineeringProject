

public class Token {

	public enum Direction {
		N, NORTH, S, SOUTH, E, EAST, W, WEST, 
		U, UP, D, DOWN, 
		NE,NORTHEAST, NW,NORTHWEST, SE,SOUTHEAST, SW,SOUTHWEST, 
		NNE,NORTHNORTHEAST, NNW, NORTHNORTHWEST, 
		SSE, SOUTHSOUTHEAST, SSW,SOUTHSOUTHWEST, 
		ENE, EASTNORTHEAST, ESE, EASTSOUTHEAST, 
		WNW, WESTNORTHWEST, WSW, WESTSOUTHWEST,
	}
	
	
	public enum Command {
		HELP, QUIT, EXIT, LOOK, GO, GET, DROP, USE, INVE, INVENTORY
	}
	
	String command;
	String optional;
	
	/*extract Token from the input string
	 * Condition: the input is checked as valid token; whitespace is allowed
	 * */
	static Token extractToken(String input){
		Token token = new Token(); //as long as it's "newed" here, the token is not "null"
		String[] tokenParts = input.trim().split(" ",2); 
		//allow space before and after
		if(tokenParts.length == 1){
			if(tokenParts[0].trim().equalsIgnoreCase("HELP")
					|| tokenParts[0].trim().equalsIgnoreCase("QUIT")
					|| tokenParts[0].trim().equalsIgnoreCase("EXIT")
					|| tokenParts[0].trim().equalsIgnoreCase("LOOK")
					|| tokenParts[0].trim().equalsIgnoreCase("GET")
					|| tokenParts[0].trim().equalsIgnoreCase("DROP")
					|| tokenParts[0].trim().equalsIgnoreCase("USE")
					|| tokenParts[0].trim().equalsIgnoreCase("INVE")
					|| tokenParts[0].trim().equalsIgnoreCase("INVENTORY")){
				token.command = tokenParts[0].trim();
				return token;//i.e. token.optional = null;
			}
		}else{ //tokenParts.length == 2
			if(((tokenParts[0].trim().equalsIgnoreCase("GO") 
					|| tokenParts[0].trim().equalsIgnoreCase("LOOK"))
						&& Token.isValidDirection(tokenParts[1].trim()))){ //optional is Dir
				token.command = tokenParts[0].trim().toUpperCase();
				if(Token.isValidDirection(tokenParts[1].trim()))
					token.optional = abbreviateDir(tokenParts[1].trim().toUpperCase());
				return token;
			}else if(tokenParts[0].trim().equalsIgnoreCase("LOOK")
					||tokenParts[0].trim().equalsIgnoreCase("GET")
					|| tokenParts[0].trim().equalsIgnoreCase("DROP")
					|| tokenParts[0].trim().equalsIgnoreCase("USE")){//optional is object, or HERE for LOOK
				token.command = tokenParts[0].trim().toUpperCase();
				token.optional = tokenParts[1].trim().toUpperCase();
				return token;
			}
		}
		return null; 
		//!"null" result is not applicable if the input is already checked,
		//or else it may cause NullPointerException trouble when called
	}
	
	/*change directions to its abbreviation if applicable
	 * also store directions in upper case
	 * */
	static String abbreviateDir(String dir) {
		switch (dir.toUpperCase()) {
			case "UP": dir = "U"; break;
			case "DOWN": dir = "D"; break;
	        case "NORTH": dir = "N"; break;
	        case "SOUTH": dir = "S"; break;
	        case "EAST": dir = "E"; break;
	        case "WEST": dir = "W"; break;
	        case "NORTHEAST": dir = "NE"; break; 
	        case "NORTHWEST": dir = "NW"; break; 
	        case "SOUTHEAST": dir = "SE"; break; 
	        case "SOUTHWEST": dir = "SW"; break; 
	        case "NORTHNORTHEAST": dir = "NNE"; break; 
	        case "NORTHNORTHWEST": dir = "NNW"; break; 
	        case "SOUTHSOUTHEAST": dir = "SSE"; break; 
	        case "SOUTHSOUTHWEST": dir = "SSW"; break; 
	        case "EASTNORTHEAST": dir = "ENE"; break; 
	        case "EASTSOUTHEAST": dir = "ESE"; break; 
	        case "WESTNORTHWEST": dir = "WNW"; break; 
	        case "WESTSOUTHWEST": dir = "WSW"; break;
		}
		return dir.toUpperCase();
	}
	
	// Extract the ArtifactName from user's input 
	// it can tolerate the "_", and any other non-word character
	//TODO:Cindy to Ma: I don't understand why converting those characters?
        static String extractOption(String line){
		return line.replaceAll("\\s+", "").replaceAll("\\W","")
				.replaceAll("_","").toUpperCase();
	}

	/* test if user's input string is valid token */
	static Boolean isValidToken(String input){
		String[] tokenParts = input.trim().split(" ",2); 
		if(tokenParts.length == 1){
			if(tokenParts[0].trim().equalsIgnoreCase("HELP")
					|| tokenParts[0].trim().equalsIgnoreCase("QUIT")
					|| tokenParts[0].trim().equalsIgnoreCase("EXIT")
					|| tokenParts[0].trim().equalsIgnoreCase("LOOK")
					|| tokenParts[0].trim().equalsIgnoreCase("GET")
					|| tokenParts[0].trim().equalsIgnoreCase("DROP")
					|| tokenParts[0].trim().equalsIgnoreCase("USE")
					|| tokenParts[0].trim().equalsIgnoreCase("INVE")
					|| tokenParts[0].trim().equalsIgnoreCase("INVENTORY")){
				return true;
			}
		}else if(tokenParts.length == 2){
			if(((tokenParts[0].trim().equalsIgnoreCase("GO") //v. + Direction
						|| tokenParts[0].trim().equalsIgnoreCase("LOOK"))
					&& Token.isValidDirection(tokenParts[1].trim()))
					
					|| tokenParts[0].trim().equalsIgnoreCase("LOOK") //LOOK HERE
					&& tokenParts[1].trim().equalsIgnoreCase("HERE")
					
					|| tokenParts[0].trim().equalsIgnoreCase("LOOK")//v. + object
					|| tokenParts[0].trim().equalsIgnoreCase("GET")
					|| tokenParts[0].trim().equalsIgnoreCase("DROP")
					|| tokenParts[0].trim().equalsIgnoreCase("USE")){
				return true;
			}
		}
		return false;
	}
	
	/*check if the input string is a valid Direction*/
	static boolean isValidDirection(String input) {
	    for (Direction d : Direction.values()) {
	        if (d.name().equalsIgnoreCase(input.trim())) { 
	        	//case and whitespace insensible
	            return true;
	        }
	    }
	    return false;
	}


	
	/*check if the input string is a valid command*/
	static boolean isValidCommand(String input) {
	    for (Command d : Command.values()) {
	        if (d.name().equalsIgnoreCase(input.trim())) { 
	        	//case  and whitespace insensible
	            return true;
	        }
	    }
	    return false;
	}
}
