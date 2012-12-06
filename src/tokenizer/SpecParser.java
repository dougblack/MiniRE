package tokenizer;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class SpecParser {

	HashMap<String, String> specDefinitions;
	HashMap<String, DFA> specDFAs;
    ArrayList<String> orderedDefs;

	public static void main(String args[]) {
		SpecParser sp = new SpecParser();
		sp.parseFile("spec.txt");
		for (Map.Entry<String, String> specEntry : sp.specDefinitions.entrySet()) {
			System.out.println("Entry: " + specEntry.getKey() + ". Value: " + specEntry.getValue());
		}
        /*for (String id : sp.specDFAs.keySet()) {
			System.out.println("Id: " + id);
            sp.specDFAs.get(id).printStructure();
		}*/
	}

	public SpecParser() {
		specDefinitions = new HashMap<String, String>();
		specDFAs = new HashMap<String, DFA>();
        orderedDefs = new ArrayList<String>();
	}

	/**
	 * This method runs through the HashMap and reduces each definition to just
	 * straight regex and then builds DFAs from NFAs from that regex. It requires
	 * substituting in the regex from other token definitions and calculating
	 * inclusion principles. (i.e. [^A-Z] IN $ALPHA).
	 * 
	 * @param filename
	 * @return
	 */
	public HashMap<String, DFA> parseFile(String filename) {
        HashMap<String, String> replacements = new HashMap<String, String>();
		specDefinitions = readFile(filename);
		for (int i = 0; i < orderedDefs.size(); i++) {
            String id = orderedDefs.get(i);
			String definition = specDefinitions.get(id);
			if (definition.equals("-")) {
				definition = "\\-";
                System.out.println("SPEC: " + id);
				specDefinitions.put(id, definition);
			}
			if (!definition.contains("$")) {
                System.out.println("SPEC: " + id);
				specDFAs.put(id, new DFA(new NFA(definition)));
                replacements.put(id, definition);
			} else {
                for (String defined : replacements.keySet()) {
                    definition = definition.replace(defined, replacements.get(defined));
                }
                String entry = id;
			    String target = "";
			    String excludeRule = "";
			    if (definition.contains(" IN ")) {
				    String[] splitDefinition = definition.split(" IN ", 2);
				    excludeRule = splitDefinition[0].trim();
				    target = splitDefinition[1].trim();

				    String tokenDefinition = target;
				    if (tokenDefinition.contains("-") && !(tokenDefinition.equals("\\-"))) {
					    int startIndex = 0;
					    int dashIndex = tokenDefinition.indexOf("-", startIndex);
					    while (dashIndex != -1) {
						    char rangeStart = tokenDefinition.charAt(dashIndex - 1);
						    char rangeEnd = tokenDefinition.charAt(dashIndex + 1);

						    if (excludeRule.contains("-")) {
							    int excludeDashIndex = excludeRule.indexOf("-");
							    char excludeStart = excludeRule.charAt(excludeDashIndex - 1);
							    char excludeEnd = excludeRule.charAt(excludeDashIndex + 1);
							    if (rangeIsInRange(excludeStart, excludeEnd, rangeStart, rangeEnd)) {
								    String replaceString = excludeRange(excludeStart, excludeEnd, rangeStart, rangeEnd);
								    definition = tokenDefinition.replace(rangeStart + "-" + rangeEnd,
										    replaceString);
								    specDefinitions.put(entry, definition);
                                    System.out.println("SPEC: " + id);
								    specDFAs.put(entry, new DFA(new NFA(definition)));

							    }
						    } else {
							    char excludeChar = excludeRule.charAt(excludeRule.indexOf("^") + 1);
							    if (charIsInRange(excludeChar, rangeStart, rangeEnd)) {
								    String replaceString = excludeChar(excludeChar, rangeStart, rangeEnd);
								    definition = tokenDefinition.replace(rangeStart + "-" + rangeEnd,
										    replaceString);
								    specDefinitions.put(entry, definition);
                                    System.out.println("SPEC: " + id);
								    specDFAs.put(entry, new DFA(new NFA(definition)));
							    }
						    }

						    dashIndex = tokenDefinition.indexOf("-", dashIndex + 1);
					    }
				    }
			    }
                //System.out.println(id + "  " + definition);
                replacements.put(id, definition);
                System.out.println("SPEC: " + id);
			    specDFAs.put(id, new DFA(new NFA(definition)));
				specDefinitions.put(id, definition);
            }
		}
		return specDFAs;
	}

	/*
	 * This method grabs lines and splits them into (token/spec name,
	 * definition).
	 */
	public HashMap<String, String> readFile(String filename) {
		HashMap<String, String> specDefinitions = new HashMap<String, String>();
		try {
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				String[] splitString = strLine.split(" ", 2);
				if (splitString.length > 1 && !splitString[0].contains("%%")) {
					specDefinitions.put(splitString[0], splitString[1]);
                    orderedDefs.add(splitString[0]);
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

	public static boolean charIsInRange(char c, char rangeStart, char rangeEnd) {
		return (c >= rangeStart && c <= rangeEnd);
	}

	public static boolean rangeIsInRange(char testStart, char testEnd, char rangeStart, char rangeEnd) {
		return (charIsInRange(testStart, rangeStart, rangeEnd) && charIsInRange(testEnd, rangeStart, rangeEnd));
	}

	public String excludeChar(char c, char rangeStart, char rangeEnd) {
		String fixedRange = "";
		if (c == rangeStart) {
			fixedRange = "" + Character.valueOf((char) (rangeStart + 1)) + "-" + rangeEnd;
		} else if (c == rangeEnd) {
			fixedRange = rangeStart + "-" + Character.valueOf((char) (rangeEnd - 1));
		} else if (charIsInRange(c, rangeStart, rangeEnd)) {
			fixedRange = rangeStart + "-" + Character.valueOf((char) (c - 1)) + Character.valueOf((char) (c + 1)) + "-"
					+ rangeEnd;
		}
		return fixedRange;
	}

	public String excludeRange(char excludeStart, char excludeEnd, char rangeStart, char rangeEnd) {
		String fixedRange = "";
		
		// Ranges are the same
		if (excludeStart == rangeStart && excludeEnd == rangeEnd) {
			return fixedRange;
		}
		// Left contained
		else if (excludeStart <= rangeStart && excludeEnd < rangeEnd) {
			fixedRange = "" + Character.valueOf((char) (excludeEnd + 1)) + "-" + rangeEnd; 
		} 
		// Right contained
		else if (excludeStart > rangeStart && excludeEnd >= rangeEnd) {
			fixedRange = "" + rangeStart + "-" + Character.valueOf((char) (excludeStart -1));
		}
		// Fully contained
		else if (excludeStart > rangeStart && excludeEnd < rangeEnd) {
			fixedRange = "" + Character.valueOf((char) (rangeStart-1)) + "-" + Character.valueOf((char) (excludeStart+1)) + excludeEnd + "-" + rangeEnd;
		}
		return fixedRange;
	}	

}
