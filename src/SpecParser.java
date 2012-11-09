import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;



public class SpecParser {
		
	HashMap<String, String> specDefinitions;
	HashMap<String, NFA> specNFAs;
	
	public static void main (String args[]) {
		SpecParser sp = new SpecParser();
		sp.parseFile("src/spec.txt");
	}
	
	public SpecParser() {
		specDefinitions = new HashMap<String, String>();
		specNFAs = new HashMap<String, NFA>();
	}
	
	public HashMap<String, NFA> parseFile(String filename) {
		specDefinitions = readFile(filename);
		for (Map.Entry<String, String> specEntry : specDefinitions.entrySet()) {
			String entry = specEntry.getKey();
			String definition = specEntry.getValue();
			if (!definition.contains("$")) {
				//specNFAs.put(entry, new NFA(definition));
				System.out.println("Added definition for token \"" + entry + "\": " + definition);
			} else if (definition.contains("IN")) {
				System.out.println("Need to check inclusion for token \"" + entry + "\": " + definition);
			} else {
				System.out.println("Need to do substition for token \"" + entry + "\": " + definition);
			}
		}
		
		return null;
	}
	
	public HashMap<String, String> readFile(String filename)
	{
		HashMap<String, String> specDefinitions = new HashMap<String, String>();
		try{
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				String[] splitString = strLine.split(" ", 2);
				if (splitString.length > 1) {
					specDefinitions.put(splitString[0], splitString[1]);
					System.out.println(splitString[0] + ", " + splitString[1]);
				}
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Couldn't read file.");
			e.printStackTrace();
		}
		return specDefinitions;
	}
	
	
	
}