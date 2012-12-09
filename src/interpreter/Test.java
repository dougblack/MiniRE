package interpreter;

import interpreter.Evaluator;

import java.io.IOException;
import java.util.ArrayList;

import tokenizer.*;

public class Test {

	public static void main(String args[]) {
		
		//System.out.println(Evaluator.find("([A-Za-z])*ment([A-Za-z])*", "input1.txt"));
		//System.out.println(Evaluator.find("(a)*", "input1.txt"));

        //StringList list1 = Evaluator.find(".*//.*", "input2.txt");
        /*System.out.println(list1);
        System.out.println(list1.maxfreqstring());
        System.out.println(list1.length());
        
        StringList list2 = Evaluator.find("&&&", "input2.txt");
        System.out.println(list2);
        System.out.println(list2.maxfreqstring());
        System.out.println(list2.length());

        Evaluator.replace("(A|a)utomata", "machina", "input2.txt", "output2.txt");

        Evaluator.recursivereplace("AutomataAutomata", "Automata", "input2.txt", "output1.txt");

        Evaluator.recursivereplace("(A|a)utomata", "machina", "input2.txt", "output2.txt");
        
        Evaluator.recursivereplace("", "", "input2.txt", "output3.txt");

        Evaluator.recursivereplace("GraphGraph", "Graph", "input2.txt", "output4.txt");*/

        Evaluator.replace("\\*\\*", "*", "input2.txt", "output5.txt");
        Evaluator.recursivereplace("\\*\\*", "*", "input2.txt", "output6.txt");
        Evaluator.recursivereplace("/\\*/", "/**/", "output6.txt", "output7.txt");

	}
}
