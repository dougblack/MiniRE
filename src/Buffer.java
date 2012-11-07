import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/**
 * 
 * @author qob
 *
 */
/*
 * Todo: Write lexer to accept buffered input
 * Lexer and Buffer act as the scanner-generator
 */
public class Buffer {
	private Scanner in;
	private BufferedReader input;
	private String buffer;
	//current position of the line
	private int column;
	//current line number
	private int row;
	
	private boolean peek;
	
	public Buffer(Scanner in) {												//public Buffer(DataInputStream in) {
		//this.input = new BufferedReader(new InputStreamReader(input));
		this.in = in;
		this.buffer = in.nextLine();
		this.column = 0;
		this.peek = false;
	} //constructor Buffer
	
	public char getNextChar() {
		//if not within the column
		if((this.column >= buffer.length()) || (buffer.length() == 0)) {
			return '\n';
		}
		if(peek) {
			this.peek = false;
			return buffer.charAt(column);
		}
		else {
			return this.buffer.charAt(column);
		}
	} //getNextChar
	
	public char peekNextChar() {
		if(this.column >= buffer.length()) {
			return '\n';
		}
		if(peek) {
			return buffer.charAt(column);
		}
		else {
			peek = true;
			return getNextChar();
		}
	} //peekNextChar
	
	//reset everything because the buffer is getting the next line of text
	public boolean getNextLine() {
		if(this.in.hasNextLine()) {
			this.buffer = in.nextLine();
			this.column = 0;
			this.peek = false;
			return true;
		}
		else {
			peek = false;
			return false;
		}
	} //getNextLine
} 
