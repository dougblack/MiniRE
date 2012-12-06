package tokenizer;
/**
 * Tokens identified according to a given grammar and scanned from a program
 * file using a table walker
 */
public class Token {
    private String id; // token identifier as given in a grammar file
	private String string; // literal token string from a program file
	
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
	
    /**
     * Returns this token's identifier
     * 
     * @return The identifier for this token
     */
	public String getId() {
		return id;
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
     * Overrides the generic toString method
     * 
     * @return This token's identifier and the literal string this token
     *          represents
     */
	public String toString() {
		return id + ": " + string;
	}
}
