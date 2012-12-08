package interpreter;

import interpreter.Evaluator;

import java.io.IOException;
import java.util.ArrayList;

import tokenizer.*;

public class Test {

	public static void main(String args[]) throws IOException{
		
		//System.out.println(Evaluator.find("([A-Za-z])*ment([A-Za-z])*", "src/input1.txt"));
		//System.out.println(Evaluator.find("(a)*", "src/input1.txt"));

        StringList list1 = Evaluator.find(".*//.*", "src/input2.txt");
        System.out.println(list1);
        System.out.println(list1.maxfreqstring());
        System.out.println(list1.length());
        
        StringList list2 = Evaluator.find("&&&", "src/input2.txt");
        System.out.println(list2);
        System.out.println(list2.maxfreqstring());
        System.out.println(list2.length());

        Evaluator.replace("(A|a)utomata", "machina", "src/input2.txt", "src/output2.txt");

        //Evaluator.recursivereplace("ev", "ve", "src/input2.txt", "src/output2.txt");

	}
}
