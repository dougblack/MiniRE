package interpreter;



public class Main {

    public static void main(String args[]) {
        Parser parser = new Parser("src/tokenDef.txt", "src/script.txt");
        parser.parse();
    }

}