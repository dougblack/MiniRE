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

/**
 * Generates token archetypes from a file of token definitions.
 */
public class SpecParser {

	HashMap<String, String> specDefinitions;
	HashMap<String, DFA> specDFAs;
    ArrayList<String> orderedDefs;
    ArrayList<String> characterClasses;
    boolean inTokens;

	public static void main(String args[]) {
		SpecParser sp = new SpecParser();
		sp.parseFile("token_spec.txt");
		for (Map.Entry<String, String> specEntry : sp.specDefinitions.entrySet()) {
			System.out.println("Entry: " + specEntry.getKey() + ". Value: " + specEntry.getValue());
		}
        /*for (String id : sp.specDFAs.keySet()) {
			System.out.println("Id: " + id);
            sp.specDFAs.get(id).printStructure();
		}*/
	}

    /**
     * Creates a new SpecParser and sets it up to be ready to parse a file of
     * token definitions
     */
	public SpecParser() {
		specDefinitions = new HashMap<String, String>();
		specDFAs = new HashMap<String, DFA>();
        orderedDefs = new ArrayList<String>();
        characterClasses = new ArrayList<String>();
        inTokens = false;
	}

	/**
	 * This method runs through the HashMap and reduces each definition to just
	 * straight regex and then builds DFAs from NFAs from that regex. It requires
	 * substituting in the regex from other token definitions and calculating
	 * inclusion principles. (i.e. [^A-Z] IN $ALPHA).
	 * 
	 * @param filename A file containing token definitions
	 * @return A mapping of token identifiers to DFAs built from each particular
     *          token's regex definition
	 */
	public HashMap<String, DFA> parseFile(String filename) {
        HashMap<String, String> replacements = new HashMap<String, String>();
		specDefinitions = readFile(filename);
		for (int i = 0; i < orderedDefs.size(); i++) {
            String identifier = orderedDefs.get(i);
			String definition = specDefinitions.get(identifier);

			if (!definition.contains("$")) {
                // The definition does not contain any other token names;
                // e.g., $FirstName = [A-Z]([a-z])*
                if (definition.equals("-")) {
                    // Escape the '-'
				    definition = "\\-";
				    specDefinitions.put(identifier, definition);
			    }
				specDFAs.put(identifier, new DFA(new NFA(definition), 0));
                replacements.put(identifier, definition);
			} else {
                // The definition contains other token names;
                // e.g., $Number = ($DIGIT)*
                for (String defined : replacements.keySet()) {
                    // Replace the token names in the definition;
                    // e.g., if $DIGIT = ([0-9])*, $Number becomes (([0-9]))*
                    definition = definition.replace(defined, replacements.get(defined));
                }
                String entry = identifier;
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
                                    // The syntax is correct; the excluded range of characters is a subset of the second range;
                                    // e.g., [^C-E] in [A-Z] - every character from C to E is within the range [A-Z]
                                    // Convert a definition like the above to [A-BF-Z]
								    String replaceString = excludeRange(excludeStart, excludeEnd, rangeStart, rangeEnd);

								    definition = tokenDefinition.replace(rangeStart + "-" + rangeEnd,
										    replaceString);
								    specDefinitions.put(entry, definition);
								    specDFAs.put(entry, new DFA(new NFA(definition), 1));
							    }
						    } else {
							    char excludeChar = excludeRule.charAt(excludeRule.indexOf("^") + 1);
							    if (charIsInRange(excludeChar, rangeStart, rangeEnd)) {
								    String replaceString = excludeChar(excludeChar, rangeStart, rangeEnd);
								    definition = tokenDefinition.replace(rangeStart + "-" + rangeEnd,
										    replaceString);
								    specDefinitions.put(entry, definition);
								    specDFAs.put(entry, new DFA(new NFA(definition), 1));
							    }
						    }
						    dashIndex = tokenDefinition.indexOf("-", dashIndex + 1);
					    }
				    }
			    } // end if, where the definition initially looked similar to [^BC] IN [A-Z]
                //System.out.println(identifier + "  " + definition);
                replacements.put(identifier, definition);
                // TODO: The space problem in ranges is passed on to NFA.java below,
                // as a definition like [A-Z a-z] will make it to this point in the code
			    specDFAs.put(identifier, new DFA(new NFA(definition), 1));
				specDefinitions.put(identifier, definition);
            } // end else, where all tokens initially had definitions containing other tokens
		}
        // Remove any character classes from the list of Tokens
        for (String characterClass : characterClasses) {
            specDFAs.remove(characterClass);
        }
		return specDFAs;
	}

	/*
	 * This method splits lines into (token/spec name, definition).
     * 
	 * @param filename A file containing token definitions
	 * @return A mapping of token identifiers to their particular regexes
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
				if (!inTokens && splitString.length > 1 && !splitString[0].contains("%%")) {
                    characterClasses.add(splitString[0]);
					specDefinitions.put(splitString[0], splitString[1]);
                    orderedDefs.add(splitString[0]);
				} else if (strLine.contains("%% TOKENS")) {
                    inTokens = true;
                } else if (inTokens) {
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

    /**
     * Determines if c has an ASCII value no greater than rangeStart and no less
     * than rangeEnd
     * 
	 * @param c A char
	 * @param rangeStart A char that is no greater than rangeEnd
	 * @param rangeEnd A char that is no less than rangeStart
	 * @return true if and only if c has an ASCII value no greater than
     *          rangeStart and no less than rangeEnd
	 */
	public static boolean charIsInRange(char c, char rangeStart, char rangeEnd) {
		return (c >= rangeStart && c <= rangeEnd);
	}

    /**
     * Determines if all characters from testStart to testEnd on an ASCII chart
     * are no greater than rangeStart and no less than rangeEnd
     * 
	 * @param testStart A char that is no greater than testEnd
	 * @param testEnd A char that is no less than testStart
	 * @param rangeStart A char that is no greater than rangeEnd
	 * @param rangeEnd A char that is no less than rangeStart
	 * @return true if and only if all characters from testStart to testEnd on
     *          an ASCII chart are no greater than rangeStart and no less than
     *          rangeEnd
	 */
	public static boolean rangeIsInRange(char testStart, char testEnd, char rangeStart, char rangeEnd) {
		return (charIsInRange(testStart, rangeStart, rangeEnd) && charIsInRange(testEnd, rangeStart, rangeEnd));
	}

    /**
     * Returns a range of characters with char c removed, represented as a
     * string like "a-df-z", given e, a, z as parameters
     * 
	 * @param c A char
	 * @param rangeStart A char that is no greater than rangeEnd
	 * @param rangeEnd A char that is no less than rangeStart
	 * @return a range of characters with char c removed, in a string
	 */
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

	/**
     * Returns a range of characters with all chars from excludeStart to
     * excludeEnd removed, represented as a string like "a-dp-z", given
     * e, o, a, z as parameters
     * 
	 * @param excludeStart A char that is no greater than excludeEnd
	 * @param excludeEnd A char that is no less than excludeStart
	 * @param rangeStart A char that is no greater than rangeEnd
	 * @param rangeEnd A char that is no less than rangeStart
	 * @return a range of characters with all chars from excludeStart to
     *          excludeEnd removed, in a string
	 */
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
