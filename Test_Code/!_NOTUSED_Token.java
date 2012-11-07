/*
 * Token.tostring(int) to get the token itself
 */
public class Token {
    //[a-zA-Z]
    //[a-z]
    //^

    public static final int percent         = 0;
    public static final int period          = 1;
    public static final int plusop          = 2;
    public static final int minusop         = 3;
    public static final int timesop         = 4;
    public static final int divideop        = 5;
    public static final int assignmentop    = 6;
    public static final int lparen          = 7;
    public static final int rparen          = 8;
    public static final int letter          = 9;
    public static final int notop           = 10;
    public static final int escape          = 11;
    public static final int number          = 12;


    private static String[] spelling = {
        "%", ".", "+", "-", "*", "/", "=", "(", ")", "letter", "notop", "escape", "number"};

    //returns the actual text
    public static String toString (int i) {
        if (i < 0 || i > spelling.length - 1) {
            return "";
        }
        return spelling[i];
    }
}