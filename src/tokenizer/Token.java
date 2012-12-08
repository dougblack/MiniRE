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

     * @param The new identifier for this token
     */
	public void setId(String ID) {
		id=ID;
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

     * @param The new literal string this token will represent
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

    public boolean equals(String testId) {
        return testId.equals(id);
    }

    public boolean notEquals(String testId) {
        return !this.equals(testId);
    }
    
    //accessors
    public int getStart(){return this.start;}
    public int getEnd() {return this.end;}
    public int getRow(){return this.row;}
    
}
