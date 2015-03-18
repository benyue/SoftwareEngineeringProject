import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(final String[] args) throws Exception {
		String filename = "";
		if(args.length > 0)
			filename = args[0];
		testEnvironment(filename);
//		testToken();
		return;
		
	}

	@SuppressWarnings("unused")
	private static void testToken() throws IOException {
		BufferedReader br 
			= new BufferedReader(new InputStreamReader(System.in));
		while(true){
			String testString = br.readLine();
			if(Token.isValidCommand(testString)) System.out.println("is valid command");
			if(Token.isValidDirection(testString)) System.out.println("is valid direction");
			if(Token.isValidToken(testString)){
				System.out.println("is valid token");
				Token token = Token.extractToken(testString);
				System.out.println("Command: "+token.command+", Direction: "+token.direction);
			}

		}
		
	}

	@SuppressWarnings("unused")
	private static void testEnvironment(String argFileName) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//System.out.println("Please input the environment file path:");
		String filename = argFileName;
		Environment map;

		while(true){
			//String filename = br.readLine();
			map = new Environment(filename);
			if(map.message.contains("ERROR")){
				System.out.println(map.message+"\n" +
						"Please input the correct environment file path:");
				filename = br.readLine();
				continue;
			}else{
				System.out.println(map.message+"\n");
				break;
			}
		}
		
//		test = new Environment("A:\\sixRooms-MorePlacesThanAnnounced.gdf");
//		System.out.println(test.message+"\n");
//		
//		test = new Environment("A:\\sixRooms-LessPlacesThanAnnounced.gdf");
//		System.out.println(test.message+"\n");
//		
//		test = new Environment("A:\\sixRooms-WrongPath.gdf");
//		System.out.println(test.message+"\n");
//		
//		test = new Environment("A:\\sixRooms-VacantPlaceName.gdf"); //no error reported
//		System.out.println(test.message+"\n");
//		
//		test = new Environment("A:\\NoSuchFile.gdf");
//		System.out.println(test.message+"\n");
		
		
		while(true){
			String testString = br.readLine();
			if(Token.isValidToken(testString)){
				Token token = Token.extractToken(testString);
//				System.out.println("Command: "+token.command+", Direction: "+token.direction);
				map.executeToken(token);
				System.out.println(map.message);
				
				if(map.Exit || map.Quit){
					break;
				}
			}else{
				System.out.println("Invalid Token. Type correct commands or HELP for instructions.\n");
			}
	
		}
		
		
		br.close();
	}

}
