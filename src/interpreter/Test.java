package interpreter;

import interpreter.Evaluator;

import java.io.IOException;
import java.util.ArrayList;

import tokenizer.*;

public class Test {

	public static void main(String args[]) throws IOException{
		
		System.out.println(Evaluator.union("hello hellox", "hello world"));
		System.out.println(Evaluator.maxfreqstring("hello hello world World world"));
		//System.out.println(Evaluator.replace("1939434 asta","'[1-9]* [a-z]'", "tosa"));

		/*Tokenizer d = new Tokenizer("/home/alazar/git/MiniRE/src/interpreter/gramm.txt", "/home/alazar/git/MiniRE/src/interpreter/test.txt");
        d.generateTokens();
        ArrayList<Token> tokens = d.getTokens();

        System.out.println();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).getId() + ": " +
                tokens.get(i).getString());
        }
		
		Tokenizer d = new Tokenizer("ment", "([A-Za-z])*ment([A-Za-z])*", "/home/alazar/git/MiniRE/src/input2.txt");
        d.generateTokens();
        ArrayList<Token> tokens = d.getTokens();

        System.out.println();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).getId() + ": " +
                tokens.get(i).getString() + " at line " +
                tokens.get(i).getRow() + ", column " +
                tokens.get(i).getStart());
        }
        */
		
		System.out.println(Evaluator.find("'([A-Za-z])*ment([A-Za-z])*'", "/home/alazar/git/MiniRE/src/input2.txt"));
        System.out.println(Evaluator.replace("'brown'","red", "/home/alazar/git/MiniRE/src/interpreter/test.txt", "/home/alazar/git/MiniRE/src/interpreter/output2.txt"));
        System.out.println(Evaluator.minus("The quick brown fox", "The quick yellow dogs"));

	}
}