package interpreter;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.util.Stack;
import java.util.ArrayList;


public class Parser {
	
	Token token;
	Tokenizer tkzr;
    Stack<SyntaxTreeNode> stack;

	public Parser(String miniRETokenSpec, String programFile) {
        stack = new Stack<SyntaxTreeNode>();
		tkzr = new Tokenizer(miniRETokenSpec, programFile);
        checkForReservedWords(tkzr.generateTokens());
		token = tkzr.peekToken();
	}

    /*
     * This method parses the token stream and builds a Syntax Tree.
     * <MiniRE-program> ::= begin <statement-list> end
     */
    public SyntaxTreeNode parse() {
        System.out.println("BUILDING TREE...");
        SyntaxTreeNode program = new SyntaxTreeNode("MiniRE-program");
        stack.push(program);
        expect("$BEGIN", "start: begin expected");
        stack.peek().addChild(statement_list());
        expect("$END", "start: end expected");
        stack.pop();

        System.out.println("TREE BUILT!");
        return program;
    }

    /*
     * <statement-list> ::= <statement><statement-list-tail> 
     */	
	public SyntaxTreeNode statement_list() {
        SyntaxTreeNode statementListNode = new SyntaxTreeNode("STATEMENT-LIST");
        stack.push(statementListNode);

        if (!(peek("$REPLACE") || peek("$RECREP") || peek("$PRINT")
                || peek("$ID"))) {
            error("start: statement-list expected");
        } else {
            statementListNode.addChild(statement());
            statementListNode.addChild(statement_list_tail());
        }
        stack.pop();
        return statementListNode;
	}
    
    /*
     * <statement-list-tail> ::= <statement><statement-list-tail> | <epsilon>
     */	
	public SyntaxTreeNode statement_list_tail() {

        SyntaxTreeNode statementListTailNode = new SyntaxTreeNode("STATEMENT-LIST-TAIL");
        stack.push(statementListTailNode);

        if (!(peek("$REPLACE") || peek("$RECREP") || peek("$PRINT")
                || peek("$ID"))) {
            statementListTailNode.addChild(epsilon());
        } else {
            statementListTailNode.addChild(statement());
            statementListTailNode.addChild(statement_list_tail());
        }
        stack.pop();
        return statementListTailNode;
	}

    public SyntaxTreeNode epsilon() {
        return new SyntaxTreeNode("EPSILON");

    }

    /*
     * Kinda done.
     * <statement> ::= replace REGEX with ASCII-STR in  <file-names> ;
     * <statement> ::= recursivereplace REGEX with ASCII-STR in  <file-names> ;
     * <statement> ::= ID = <statement-righthand> ;
     * <statement> ::= print ( <exp-list> ) ;
     */
	public SyntaxTreeNode statement() {
        SyntaxTreeNode statementNode = new SyntaxTreeNode("STATEMENT");
        stack.push(statementNode);
        if (accept("$REPLACE")) {
            expect("$REGEX", "statement: invalid REGEX");
            expect("$WITH", "statement: missing 'with'");
            expect("$ASCII-STR", "statement: invalid $ASCII-STR");
            expect("$IN", "statement: missing 'in'");
            check("$ASCII-STR", "statement: missing FILE-NAMES");
            while(peek("$ASCII-STR"))
                statementNode.addChild(file_names());
            expect("$SEMICOLON", "statement: missing SEMICOLON");
        } else if (accept("$RECREP")) {
            expect("$REGEX", "statement: invalid REGEX");
            expect("$WITH", "statement: missing 'with'");
            expect("$ASCII-STR", "statement: invalid $ASCII-STR");

            // Check that the recursive replace is not an infinite loop
            String regex = statementNode.children.get(1).value;
            String string = statementNode.children.get(3).value;

            regex = regex.substring(1, regex.length()-1);
            string = string.substring(1, string.length()-1);

            String tempFilename1 = FileOperations.createTempFile();
            FileOperations.writeFile(string, tempFilename1);

            if (Evaluator.find(regex, tempFilename1).strings().length > 0) {
                // throw some kind of error - the recursion is infinite
                error("statement: ininite recursivereplace");
            }

            expect("$IN", "statement: missing 'in'");
            check("$ASCII-STR", "statement: missing FILE-NAMES");
            while(peek("$ASCII-STR"))
                statementNode.addChild(file_names());
            expect("$SEMICOLON", "statement: missing SEMICOLON");
        } else if (accept("$PRINT")) {
            expect("$OPENPARENS", "statement: missing lparen");
            statementNode.addChild(exp_list());
            expect("$CLOSEPARENS", "statement: missing rparen");
            expect("$SEMICOLON", "statement: missing semicolon");
        } else if (accept("$ID")) {
            expect("$EQ", "statment: missing equal sign");
            statementNode.addChild(statement_righthand());
            expect("$SEMICOLON", "statement: missing semicolon");
        }
        stack.pop();
        return statementNode;
	}

    /**
     * <statement-righthand> ::= <exp> | # <exp> | maxfreqstring (ID)
     */
    public SyntaxTreeNode statement_righthand() {
        SyntaxTreeNode statementRighthandNode = new SyntaxTreeNode("STATEMENT-RIGHTHAND");
        stack.push(statementRighthandNode);
        if (accept("$HASH")) {
            statementRighthandNode.addChild(exp());
        } else if (accept("$MAXFREQ")) {
            expect("$OPENPARENS", "statement: missing OPENPARENS");
            expect("$ID", "statement: missing ID");
            expect("$CLOSEPARENS", "statement: missing CLOSEPARENS");
        } else if (peek("$ID") || peek("$FIND")) {
            statementRighthandNode.addChild(exp());
        } else {
            error("statement: missing statement-righthand"); 
        }
        stack.pop();
        return statementRighthandNode;
    }

    /*
     * <file-names> ::=  <source-file>  >!  <destination-file>
     */
    public SyntaxTreeNode file_names() {
        SyntaxTreeNode fileNamesNode = new SyntaxTreeNode("FILE-NAMES");
        stack.push(fileNamesNode);
        fileNamesNode.addChild(source_file());
        expect("$GRTNOT", "file_names: missing pipe-thing");
        fileNamesNode.addChild(destination_file());

        // Check that the source-file and destination-file are distinct
        if (fileNamesNode.children.get(0).children.get(0).value.equals(
            fileNamesNode.children.get(2).children.get(0).value)) {

            error("error in file-names: source-file and destination-file " +
                "are the same");
        }
        stack.pop();
        return fileNamesNode;
    }

    /*
     * <source-file> ::= ASCII-STR  
     */
    public SyntaxTreeNode source_file() {
        SyntaxTreeNode sourceFileNode = new SyntaxTreeNode("SOURCE-FILE");
        stack.push(sourceFileNode);
        expect("$ASCII-STR", "source_file: not a valid source file name");
        stack.pop();
        return sourceFileNode;
    }

    /*
     * <destination-file> ::= ASCII-STR
     */
    public SyntaxTreeNode destination_file() {
        SyntaxTreeNode destinationFileNode = new SyntaxTreeNode("DESTINATION-FILE");
        stack.push(destinationFileNode);
        expect("$ASCII-STR", "source_file: not a valid destination file name");
        stack.pop();
        return destinationFileNode;
    }
    
    /*
     * <exp-list> ::= <exp> <exp-list-tail>
     */
    public SyntaxTreeNode exp_list() {
        SyntaxTreeNode expListNode = new SyntaxTreeNode("EXP-LIST");
        stack.push(expListNode);
        expListNode.addChild(exp());
        expListNode.addChild(exp_list_tail());
        stack.pop();
        return expListNode;
    }
    /*
     * <exp-list-tail> ::= , <exp> <exp-list-tail> | <epsilon>
     */
    public SyntaxTreeNode exp_list_tail() {
        SyntaxTreeNode expListTailNode = new SyntaxTreeNode("EXP-LIST-TAIL");
        stack.push(expListTailNode);
        if (accept("$COMMA")) {
            expListTailNode.addChild(exp());
            expListTailNode.addChild(exp_list_tail());
        } else {
            expListTailNode.addChild(epsilon());
        }
        stack.pop();
        return expListTailNode;
    }
    
    /*
     * <exp> ::= ID  | ( <exp> ) 
     * <exp> ::=  <term> <exp-tail>
     */
    public SyntaxTreeNode exp() {
        SyntaxTreeNode expNode = new SyntaxTreeNode("EXP");
        stack.push(expNode);
        if (accept("$ID")) {
            // pass
        } else if (accept("$OPENPARENS")) {
            expNode.addChild(exp());
            expect("$CLOSEPARENS", "exp: missing rparen");
        } else {
            expNode.addChild(term());
            expNode.addChild(exp_tail());
        }
        stack.pop();
        return expNode;
    }
    /*
     * <exp-tail> ::= <bin-op> <term> <exp-tail> | <epsilon>
     */
    public SyntaxTreeNode exp_tail() {
        SyntaxTreeNode expTailNode = new SyntaxTreeNode("EXP-TAIL");
        stack.push(expTailNode);
        if (peek("$UNION") || peek("$INTERS") || peek("$DIFF")) {
            expTailNode.addChild(bin_op());
            expTailNode.addChild(term());
            expTailNode.addChild(exp_tail());
        } else {
            expTailNode.addChild(epsilon());
        }
        stack.pop();
        return expTailNode;
    }

    /*
     * <term> ::=  find REGEX in  <file-name>  
     */
    public SyntaxTreeNode term() {
        SyntaxTreeNode termNode = new SyntaxTreeNode("TERM");
        stack.push(termNode);
        expect("$FIND", "term: expected FIND");
        expect("$REGEX", "term: expected REGEX");
        expect("$IN", "term: missing 'in'");
        termNode.addChild(file_name());
        stack.pop();
        return termNode;
    }

    /*
     * <file-name> ::=  ASCII-STR
     */
    public SyntaxTreeNode file_name() {
        SyntaxTreeNode fileNameNode = new SyntaxTreeNode("FILE-NAME");
        stack.push(fileNameNode);
        expect("$ASCII-STR", "file_name: not a valid file name");
        stack.pop();
        return fileNameNode;

    }
    
    /*
     * <bin-op> ::=  diff | union | inters
     */
    public SyntaxTreeNode bin_op() {
        SyntaxTreeNode binOpNode = new SyntaxTreeNode("BIN-OP");
        stack.push(binOpNode);
        if (accept("$DIFF")) {
            // pass
        } else if (accept("$UNION")) {
            // pass
        } else if (accept("$INTERS")) {
            // pass;
        }
        stack.pop();
        return binOpNode;
    }

    /*
     * Emits an error message and quits the program.
     */
	public void error(String failedGrammar) {
        SyntaxTreeNode last;// = stack.pop();
        System.out.println("error in " + failedGrammar + " at index " +
            token.getIndex() + " in file " + token.getFile());
        if (!stack.isEmpty()) {
            last = stack.pop();
            System.out.println("current rule " + last.nodeType);
            System.out.println("current token: " + token.getId() + " - " + token.getString() );
        }
	    System.exit(0);
	}

    /*
     * Accepts the given token, adds it to the tree, and
     * grabs the next token.
     */	
    public boolean accept(String id) {
        if (token.equals(id)) {
            tkzr.consumeToken();
            SyntaxTreeNode newNode = new SyntaxTreeNode(token);
            System.out.println("Accepted " + token);
            stack.peek().addChild(newNode);
            try {
                token = tkzr.peekToken();
            } catch (IndexOutOfBoundsException ioob) {
                // done parsing.
            }
            return true;
        }
        return false;
    }

    /*
     * Checks if the next token is of the type id.
     */
    public boolean peek(String id) {
        return token.equals(id);
    }

    /*
     * Returns an error if the next token is not the one
     * that was expected.
     */
    public void expect(String id, String grammar) {
        if (accept(id))
            return;
        error(grammar);
    }

    /*
     * Same as expect, except that it doesn't consume the token.
     */
    public void check(String id, String grammar) {
        if (token.equals(id)) {
            return;
        }
        error(grammar);
    }

    /*
     * This returns the value of the next token unconditionally.
     */
    public String take() {
        String ret = token.getString();
        token = tkzr.peekToken();
        return ret;
    }

    /**
     * Ensures the reserved words in the MiniRE language are associated with
     * their respective tokens rather than the ID token
     */
    public void checkForReservedWords(ArrayList<Token> tokens) {
        if (tokens == null) {
            return;
        }
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getId().equals("$ID")) {
                tokens.set(i, checkIfReservedWord(tokens.get(i)));
            }
        }
    }

    /**
     * Ensures the reserved words in the MiniRE language are associated with
     * their respective tokens rather than the ID token
     *
     * @param token A token
     * @return The same token, with a new id if the string matched a keyword
     */
    public Token checkIfReservedWord(Token t) {
        if (t.getString().equals("begin")) {
            t.setId("$BEGIN");
        } else if (t.getString().equals("end")) {
            t.setId("$END");
        } else if (t.getString().equals("find")) {
            t.setId("$FIND");
        } else if (t.getString().equals("with")) {
            t.setId("$WITH");
        } else if (t.getString().equals("in")) {
            t.setId("$IN");
        } else if (t.getString().equals("print")) {
            t.setId("$PRINT");
        } else if (t.getString().equals("replace")) {
            t.setId("$REPLACE");
        } else if (t.getString().equals("recursivereplace")) {
            t.setId("$RECREP");
        } else if (t.getString().equals("inters")) {
            t.setId("$INTERS");
        } else if (t.getString().equals("union")) {
            t.setId("$UNION");
        } else if (t.getString().equals("diff")) {
            t.setId("$DIFF");
        } else if (t.getString().equals("maxfreqstring")) {
            t.setId("$MAXFREQ");
        }
        return t;
    }
	
}
