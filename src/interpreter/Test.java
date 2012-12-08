package interpreter;

import java.util.ArrayList;

import tokenizer.*;

public class Test {

	public static void main(String args[]){
		
		System.out.println(Evaluator.union("hello hellox", "hello world"));
		System.out.println(Evaluator.maxfreqstring("hello hello world World world"));
		//System.out.println(Evaluator.replace("1939434 asta","'[1-9]* [a-z]'", "tosa"));

		Tokenizer d = new Tokenizer("/home/alazar/git/MiniRE/src/interpreter/gramm.txt", "/home/alazar/git/MiniRE/src/interpreter/test.txt");
        d.generateTokens();
        ArrayList<Token> tokens = d.getTokens();

        System.out.println();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).getId() + ": " +
                tokens.get(i).getString());
        }
        

	}
}