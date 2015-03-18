import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

public class testGamePlay {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		testTokenExtraction();
//		testParseDouble();
		testVersion();
	}
	
	static void testTokenExtraction() throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			String userInput = br.readLine();
			Token token = Token.extractToken(userInput);
			if(token == null){
				System.out.println("ERROR: Invalid Token.");
				continue;
			}
			System.out.println("Command:"+token.command+", Optional:"+token.optional);
		}
	}
	
	static void testParseDouble(){
		Environment e = new Environment();
		List<Double> d = e.parseDoubles("3.10 +3.1 -2.0 357 0.123");
		// Pattern.compile("-?[0-9]+\\.?[0-9]*"); //TODO: read 3.10 as 3.1, 357 as 357.0
		for(Double dd: d){
			System.out.println(dd);
		}
	}
	
	static void testVersion() throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			int[] d = new Environment().parseVersionNumber(br.readLine()); 
			if(d==null) {System.out.println(d);continue;}
			for(Integer dd: d)
				System.out.println(dd);
		}
	}
		

}
