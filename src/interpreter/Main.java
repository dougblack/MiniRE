package interpreter;

import java.util.Scanner;
import java.io.File;

/**
 * Interprets and runs MiniRE scripts
 */
public class Main {

    /**
     * Prompts for, then interprets and runs a MiniRE script
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String args[]) {
        Scanner scan = new Scanner(System.in);
        String filename;

        System.out.print("\nEnter the name of a MiniRE script to run: ");
        filename = scan.nextLine();


        if (filename == null || filename.trim().length() < 1) {
            System.out.println("No script was given.");
        } else if (filename.contains(".txt")) {
            filename = filename.trim();

            if (!(new File(filename)).exists()) {
		        System.out.println("Could not locate file " + filename);
                return;
            }
            System.out.println("-------------------------------------------\n" +
                "\tRunning " + filename +
                "\n-------------------------------------------\n");

            Parser parser = new Parser("token_spec.txt", filename);
            SyntaxTreeNode head = parser.parse();
            //head.printLevels();
		    System.out.println();
		    System.out.println("Starting evaluation.");
		    Evaluator evaluator = new Evaluator();
		    evaluator.eval(head);
		    System.out.println("Finished evaluation.");
        } else {
            System.out.println("Only .txt files are accepted.");
        }
    }


}
