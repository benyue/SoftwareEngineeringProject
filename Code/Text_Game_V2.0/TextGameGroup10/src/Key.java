
public class Key extends Artifact{
	int keyPattern; 
	int masterCode; 
	// Divide lockPattern by 10^masterCode, then check if lockPattern == keyPattern 
	// Example: keyPattern 52 with masterCode 2 works all locks 5200 to 5299

}