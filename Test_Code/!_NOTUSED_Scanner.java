import java.io.*;
public class Scanner {
    private final int READ_FROM_FILE = 0;
    
    private char ch = ' ';
    private char ident = ' ';
    private int digit = 0;
    private Buffer buffer;
    public int token;

    public Scanner (DataInputStream in) {
        buffer = new Buffer(in);
        token = Token.semicolon;
    } 


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


    public int number () {
        return digit;
    }


    public char letter () {
        return ident;
    }


    public void match (int which) {
        token = getToken();
        if (token != which) {
            error("Invalid token " + Token.toString(token) +
                  "-- expecting " + Token.toString(which));
            System.exit(1);
        }
    }


    public void error (String msg) {
        System.err.println(msg);
        System.exit(1);
    } // error


    private int getNumber () {
        int rslt = 0;
        do {
            rslt = rslt * 10 + Character.digit(ch, 10);
            ch = buffer.get();
        } while (Character.isDigit(ch));
        return rslt;
    } // getNumber

} // Scanner

