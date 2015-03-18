import java.io.BufferedReader;
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
		System.out.println(filename);
		loadMapAndPlayGame(filename);
		return;
	}


	private static void loadMapAndPlayGame(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//System.out.println("Please input the environment file path:");
		Environment env = new Environment();

		while(true){
			//String filename = br.readLine();
			env.initEnvironment(filename);
			if(env.message.contains("ERROR")){
				System.out.println(env.message+"\n" +
						"Please input the correct environment file path:");
				filename = br.readLine();
				env = new Environment();
				continue;
			}else{
				System.out.println(env.message+"\n");
				break;
			}
		}
		
		Game game = new Game();
		game.currentPlace = env.places.get(0);
		game.previousPlace = game.currentPlace;
		
		while(true){
			String userInput = br.readLine();
			Token token = Token.extractToken(userInput);
			if(token==null){
				System.out.println("Invalid Token. Type correct commands or HELP for instructions.\n");
				continue;
			}

//			game.executeToken(env,token);
			game.executeTokenLimitingVersion(env,token);//another version control design
			System.out.println(game.message);
			
			if(game.Exit || game.Quit) break;
		}
		
		br.close();
	}

}
