//package tokenizer;
/**
 * Tokens identified according to a given grammar and scanned from a program
 * file using a table walker
 */
public class Token {
    private String id; // token identifier as given in a grammar file
	private String string; // literal token string from a program file
	
	public int line;
	
	private int start;
	private  int end;
	private int row;
    /**
     * Constructs a token with id as its identifier and string as the literal
     * string it represents
     * 
     * @param id The identifier for this token; from a list in a grammar file
     * @param string The literal string this token represents
     */
	public Token(String id, String string) {
		this.id = id;
		this.string = string;
	}
	
	public Token(String id, String string, int start, int end, int row) {
		this.id = id;
		this.string = string;
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
