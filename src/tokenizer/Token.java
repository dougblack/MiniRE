package tokenizer;
/**
 * Tokens identified according to a given grammar and scanned from a program
 * file using a table walker
 */
public class Token {
    private String id; // token identifier as given in a grammar file
	private String string; // literal token string from a program file
	private String file; // name of the file this token was found in
    private long index; // index of the first character of the token's string

    /**
     * Constructs a token with the identifier id, that represents the string
     * string that was found in file at the given index
     *
     * @param id The identifier for this token
     * @param string The literal string this token will represent
     * @param file The name of the file this token was found in
     * @param index Index of the first character of the token's string in a
     *          particular file
     */
	public Token(String id, String string, String file, long index) {
		this.id = id;
		this.string = string;
        this.file = file;
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

    /**
     * Sets this token's identifier
     * 
     * @param id The new identifier for this token
     */
	public void setId(String id) {
		this.id = id;
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
     * Sets the literal string this token represents
     * 
     * @param str The new literal string this token will represent
     */
	public void setString(String str) {
		string = str;
	}
	
    /**
     * Returns the name of the file this token was found in
     * 
     * @return The name of the file this token was found in
     */
	public String getFile() {
		return file;
	}

    /**
     * Overrides the generic toString method
     * 
     * @return This token's identifier and the literal string this token
     *          represents
     */
	public String toString() {
		return id + ": " + string;
	}

    /**
     * Checks if testId is the same as this token's id
     * 
     * @param testId An identifier to compare with this token's id
     * @return true if and only if testId is the same as id
     */
    public boolean equals(String testId) {
        return testId.equals(id);
    }

    /**
     * Checks if testId is not the same as this token's id
     * 
     * @param testId An identifier to compare with this token's id
     * @return true if and only if testId is not the same as id
     */
    public boolean notEquals(String testId) {
        return !this.equals(testId);
    }
    
    /**
     * Returns the index of the first character in string in the file it was
     * found in
     * 
     * @return The index of the first character in string in the file it was
     *          found in
     */
    public long getIndex() {
        return this.index;
    }
}
