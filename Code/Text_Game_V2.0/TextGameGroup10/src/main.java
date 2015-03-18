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
		loadMapAndPlayGame(filename);
//		testToken();
		return;
	}


	private static void loadMapAndPlayGame(String argFileName) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//System.out.println("Please input the environment file path:");
		String filename = argFileName;
		Environment env = new Environment();
		Game game = new Game();
		
		while(true){
			//String filename = br.readLine();
			env.initEnvironment(filename);
			if(env.message.contains("ERROR")){
				System.out.println(env.message+"\n" +
						"Please input the correct environment file path:");
				filename = br.readLine();
				continue;
			}else{
				System.out.println(env.message+"\n");
				break;
			}
		}
		
		game.currentPlace = env.places.get(0);
		game.previousPlace = game.currentPlace;
		
		while(true){
			String userInput = br.readLine();
			if(Token.isValidToken(userInput)){
				Token token = Token.extractToken(userInput);
//				System.out.println("Command: "+token.command+", Direction: "+token.optional);
				game.executeToken(env,token);
				System.out.println(game.message);
				
				if(game.Exit || game.Quit){
					break;
				}
			}else{
				System.out.println("Invalid Token. Type correct commands or HELP for instructions.\n");
			}
	
		}
		
		
		br.close();
	}

}
