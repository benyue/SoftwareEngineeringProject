import java.util.ArrayList;
import java.util.List;

public class TestParser {
	Environment env;
	
	public TestParser(){
		
	}
	
	public void testPlaces(Environment parsedPlaces){
		env = parsedPlaces;
		
		int numPlaces = env.places.size();
		System.out.println("Number of Places: " + numPlaces);
		for(int i = 0; i < numPlaces; i++){
			System.out.println("Place ID: " + env.places.get(i).ID);
			System.out.println("Place Name: " + env.places.get(i).name);
			int numDescriptions = env.places.get(i).description.size();
			System.out.println("Number of description lines: " + numDescriptions);
			for(int k = 0; k < numDescriptions; k++)
				System.out.println("Description " + k + ": " + env.places.get(i).description.get(k));
			System.out.println();
		}
	}
	
	public void testPaths(Environment parsedPaths){
		env = parsedPaths;
		
		int numPaths = 0;
		for(int i = 0; i < env.places.size(); i++){
			numPaths += env.places.get(i).outGoing.size();
			for(int k = 0; k < env.places.get(i).outGoing.size(); k++){
				//No path ID checked!!
				System.out.println("Source: " + env.places.get(i).ID);
				System.out.println("Direction: " + env.places.get(i).outGoing.get(k).direction);
				System.out.println("Destination: " + env.places.get(i).outGoing.get(k).outNeighborID);
				System.out.println("Lock Pattern: " + env.places.get(i).outGoing.get(k).lockPattern);
			}
			System.out.println();
		}
		System.out.println("Number of Paths: " + numPaths);
	}
	
	public void testLighting(Environment parsedLighting){
		env = parsedLighting;
		
		for(int i = 0; i < env.places.size(); i++){
			System.out.println("\n" + env.places.get(i).name);
			System.out.println("Light Level: " + env.places.get(i).lightLevel);
		}
	}
	
	public void testArtifact(Environment parsedArtifact){
		env = parsedArtifact;
		for(int i = 0; i < env.artifacts.size(); i++){
			System.out.println("ID: " + env.artifacts.get(i).ID);
			System.out.println("Place ID: " + env.artifacts.get(i).placeID);
			System.out.println("value: " + env.artifacts.get(i).value);
			System.out.println("movability: " + env.artifacts.get(i).movability);
			System.out.println("name: " + env.artifacts.get(i).name);
			for(int k = 0; k < env.artifacts.get(i).description.size(); k++)
				System.out.println(env.artifacts.get(i).description.get(k));
		}
	}
}
