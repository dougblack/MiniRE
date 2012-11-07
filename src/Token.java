/**
 * 
 * @author qob
 *
 */
public class Token {
    //public static final int semicolon       = 0;
    //public static final int period          = 1;
    //public static final int plusop          = 2;
    //public static final int minusop         = 3;
    //public static final int timesop         = 4;
    //public static final int divideop        = 5;
    //public static final int assignmentop    = 6;
    //public static final int lparen          = 7;
    //public static final int rparen          = 8;
    //public static final int letter          = 9;
    //public static final int notop           = 10;
    //public static final int escape          = 11;
    //public static final int number          = 12;
	/*
	 * Since we're writing a scanner generator, we need to scan the file for token types so we can generate the tokens as well.
	 * minusop should be renamed to dash for [a-z] 
	 * timesop should be renamed to star 
	 * divideop should be slash
	 * 
	 */
	
	private TokenType 	tokenType;
	private String 		tokenValue;
	
	public Token(TokenType tokenType, String tokenValue) {
		this.tokenType = tokenType;
		this.tokenValue = tokenValue;
	}
	
	public TokenType getType() {
		return this.tokenType;
	}
	
	public String getValue() {
		return this.tokenValue;
	}
	
	public void setType(TokenType tokenType) {
		this.tokenType = tokenType;
	}
	
	public void setValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}
	
	public String toString() {
		return "Type: " + this.tokenType + " Value: " + this.tokenValue;
	}
}
