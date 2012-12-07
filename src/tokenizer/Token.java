package tokenizer;
/**
 * Tokens identified according to a given grammar and scanned from a program
 * file using a table walker
 */
public class Token {
    private String id; // token identifier as given in a grammar file
	private String string; // literal token string from a program file
    private int index; // index of the first character of the token's string in a file
	
    /**
     * Constructs a token with id as its identifier, string as the literal
     * string it represents, and index as the first character of the string
     * in a particular file
     * 
     * @param id The identifier for this token; from a list in a grammar file
     * @param string The literal string this token represents
     * @param index Index of the first character of the token's string in a
     *          particular file
     */
	public Token(String id, String string, int index) {
		this.id = id;
		this.string = string;
        this.index = index;
	}
	
    /**
     * Returns this token's identifier
     * 
     * @return The identifier for this token
     */
	public String getId() {
		return id;
	}

    public void setId(String newId) {
        this.id = newId;
    }
	
    /**
     * Returns the literal string this token represents
     * 
     * @return The literal string this token represents
     */
	public String getString() {
		return string;
	}
	
    /**
     * Returns the index of the first character of this token's string in a file
     * 

     * @return The index of the first character of the token's string in a file

     */
	public int getIndex() {
		return index;
	}
	
    /**
     * Overrides the generic toString method
     * 
     * @return This token's identifier, the literal string this token
     *          represents, and the index of the first character of this
     *          token in a particular file
     */
	public String toString() {
		return id + ": " + string + " @" + index;
	}

    public boolean equals(String testId) {
        return testId.equals(id);
    }

    public boolean notEquals(String testId) {
        return !this.equals(testId);
    }
}
