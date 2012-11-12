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
	
	/**
	 * This method runs through the HashMap and reduces each definition to just straight regex
	 * and then builds NFA's out of that regex. It requires substituting in the regex from other
	 * token definitions and calculating inclusion principles. (i.e. [^A-Z] IN $ALPHA).
	 * @param filename
	 * @return
	 */
	public HashMap<String, NFA> parseFile(String filename) {
		specDefinitions = readFile(filename);
		for (Map.Entry<String, String> specEntry : specDefinitions.entrySet()) {
			String entry = specEntry.getKey();
			String definition = specEntry.getValue();
			if (!definition.contains("$")) {
				specNFAs.put(entry, new NFA(definition));
				System.out.println("Added definition for token \"" + entry + "\": " + definition);
			} 
		}
		
		for (Map.Entry<String, String> specEntry : specDefinitions.entrySet()) {
			String entry = specEntry.getKey();
			String definition = specEntry.getValue();
			String target = "";
			String excludeRule = "";
			String targetDefinition = "";
			if (definition.contains("IN")) {
				String[] splitDefinition = definition.split(" IN ", 2);
				excludeRule = splitDefinition[0].trim();
				target = splitDefinition[1].trim();
				
				// sanity check
				assert(specNFAs.containsKey(target));
				targetDefinition = specDefinitions.get(target);
				if (targetDefinition.contains("-")) {
					
				} else {
					
				}
				
			}
		}
		
		for (Map.Entry<String, String> specEntry : specDefinitions.entrySet()) {
			String entry = specEntry.getKey();
			String definition = specEntry.getValue();
			if (!definition.contains("IN")) {
				int tokenStart = -1;
				int tokenEnd = -1;
				boolean inToken = false;
				char currentChar;
				String token = "";
				for (int i=0; i < definition.length(); i++) {
					currentChar = definition.charAt(i);
					if (currentChar == '$') {
						inToken = true;
						tokenStart = i;
						token = token+"$";
					} else if (inToken && Character.isLetter(currentChar)) {
						token = token+=currentChar;
					} else if (inToken) {
						inToken = false;
						tokenEnd = i;
						System.out.println(definition);
						if (specNFAs.containsKey(token)) 
							System.out.println("After replacement: " + definition.replace(token, specDefinitions.get(token)));
						token ="";
					}
				}
			}
		}
		
		return null;
	}
	
	
	/*
	 * This method grabs lines and splits them into (token/spec name, definition). 
	 */
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