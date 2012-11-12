import java.util.Scanner;
/**
 * 
 * @author qob
 * Takes inputs from buffer and uses it to generate tokens
 * Todo: Define token
 */
public class Lexer {
	private Buffer bufferedInput;
	private boolean peek;
	private Token current;
	private char token = ' ';
	private Token retrievedToken = null;
	
	public Lexer(Scanner scan) {
		this.bufferedInput = new Buffer(scan);
		this.peek = false;
		this.current = null;
	}
	
	public Token getNextToken() {
		if(peek) {
			this.peek = false;
			return this.current;
		}
		else {
			this.current = generateToken();
			return this.current;
		}
	}
	
	public Token peekNextToken() {
		if(peek) { 
			return this.current;
		}
		else {
			this.current = getNextToken(); 
			this.peek = true;
			return this.current;
		}
	}
	
	/*
	 * Use this to generate tokens
	 * "%", ".", "+", "-", "*", "/", "=", "(", ")", "letter", "notop", "escape", "number"
	 */
	public Token generateToken() {
		this.token = bufferedInput.getNextChar();
		this.retrievedToken = null;
		
		switch(this.token) {
			case '%':
				
		}
		return current;
	}
	//from my prev code
	/*
	public int getToken () {
        while (Character.isWhitespace(ch)) {
            ch = buffer.get();
        }
        if (Character.isLetter(ch)) {
            //ident = Character.toLowerCase(ch);
            ident = ch;
            ch = buffer.get();
            token = Token.letter;
        }
        else if (Character.isDigit(ch)) {
            digit = getNumber();
            token = Token.number;
        } 
        else {
            switch (ch) {
                case ';' : 
                    ch = buffer.get();
                    System.out.println(token);
                    break;

                case '.' : 
                    ch = buffer.get();
                    token = Token.period;
                    break;

                case '+' : 
                    ch = buffer.get();
                    token = Token.plusop;
                    break;

                case '-' : 
                    ch = buffer.get();
                    token = Token.minusop;
                    break;

                case '*' : 
                    ch = buffer.get();
                    token = Token.timesop;
                    break;

                case '/' : 
                    ch = buffer.get();
                    token = Token.divideop;
                    break;

                case '=' : 
                    ch = buffer.get();
                    token = Token.assignmentop;
                    break;

                case '(' : 
                    ch = buffer.get();
                    token = Token.lparen;
                    break;

                case ')' : 
                    ch = buffer.get();
                    token = Token.rparen;
                    break;
                    
                case '^' : 
                    ch = buffer.get();
                    token = Token.notop;
                    break;
                    
                case '\\' :
                    ch = buffer.get();
                    token = Token.escape;
                    break;
                    
                default : error ("Illegal character " + ch );
                    break;
            } 
        } 
        return token;
    } 
	*/
}
