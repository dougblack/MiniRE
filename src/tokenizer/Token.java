package tokenizer;
/**
 * Tokens identified according to a given grammar and scanned from a program
 * file using a table walker
 */
public class Token {
    private String id; // token identifier as given in a grammar file
	private String string; // literal token string from a program file
	private String file; // name of the file this token was found in
	private int start; // column this token was found on in a file
	private int end; // column of the last character in this token's string
	private int row; // line this token was found on in a file

    /**
     * Constructs a token with the identifier id, that represents the string
     * string that was found in file on line row. The first and last characters
     * occurred in columns start and end, respectively.
     *
     * @param id The identifier for this token
     * @param string The literal string this token will represent
     * @param file The name of the file this token was found in
     * @param start The column of the first character in string, from line row
     * @param end column of the last character in string
     * @param row line in the file that this string appeared on
     */
	public Token(String id, String string, String file, int start, int end, int row) {
		this.id = id;
		this.string = string;
        this.file = file;
		this.start = start;
		this.end = end;
		this.row = row;
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
     * Returns the column of the first character in string
     * 
     * @return The column of the first character in string
     */
    public int getStart() {
        return this.start;
    }

    /**
     * Returns the column of the last character in string
     * 
     * @return The column of the last character in string
     */
    public int getEnd() {
        return this.end;
    }

    /**
     * Returns the line in this string appeared on in its file
     * 
     * @return The line in this string appeared on in its file
     */
    public int getRow() {
        return this.row;
    }
    
}
