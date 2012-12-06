package interpreter;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.util.Stack;


public class Parser {
	
	Token token;
	Tokenizer tkzr;
    Stack<SyntaxTreeNode> stack;
    int lineNumber = 0;

	public Parser(String miniRETokenSpec, String programFile) {
        stack = new Stack<SyntaxTreeNode>();
		tkzr = new Tokenizer(miniRETokenSpec, programFile);
        tkzr.generateTokens();
		token = tkzr.peekToken();
	}

    public SyntaxTreeNode parse() {
        SyntaxTreeNode program = new SyntaxTreeNode("PROGRAM");
        stack.push(program);
        expect("$BEGIN", "start: begin expected");
        program.addChild(statement_list());
        expect("$END", "start: end expected");
        stack.pop();
        return program;
    }
	
	public SyntaxTreeNode statement_list() {
        SyntaxTreeNode statementListNode = new SyntaxTreeNode("STATEMENT-LIST");
        stack.push(statementListNode);
		statementListNode.addChild(statement());
		statementListNode.addChild(statement_list_tail());
        stack.pop();
        return statementListNode;
	}
	
	public SyntaxTreeNode statement_list_tail() {

        SyntaxTreeNode statementListTailNode = new SyntaxTreeNode("STATEMENT-LIST-TAIL");
        stack.push(statementListTailNode);
        if(peek("NULL"))
            return null;

		statementListTailNode.addChild(statement());
		statementListTailNode.addChild(statement_list_tail());
        stack.pop();
        return statementListTailNode;
	}

    /*
     * Not done.
     */
	public SyntaxTreeNode statement() {
        SyntaxTreeNode statementNode = new SyntaxTreeNode("STATEMENT");
        stack.push(statementNode);
        if (accept("$REPLACE")) {
            expect("$REGEX", "statement: not valid REGEX");
            expect("$WITH", "statement: missing 'width'");
            expect("$ASCII-STR", "statement: not valid ASCII-STR");
            expect("$IN", "statement: missing 'in'");
            check("ASCII-STR", "statement: missing FILE-NAMES");
            while(peek("ASCII-STR"))
                statementNode.addChild(file_names());
        } else if (accept("$RECURSIVEREPLACE")) {
            expect("$REGEX", "statement: not valid REGEX");
            expect("$WITH", "statement: missing 'width'");
            expect("$ASCII-STR", "statement: not valid ASCII-STR");
            expect("$IN", "statement: missing 'in'");
            check("ASCII-STR", "statement: missing FILE-NAMES");
            while(peek("ASCII-STR"))
                statementNode.addChild(file_names());
        } else if (accept("$PRINT")) {
            expect("$LPAREN", "statement: missing lparen");
            statementNode.addChild(exp_list());
            expect("$RPAREN", "statement: missing rparen");
            expect("$SEMICOLON", "statement: missing semi-colon");
        } else if (accept("$ID")) {
            expect("$ASSIGN", "statement: missing assign");
            if (accept("$HASH")) {
                statementNode.addChild(exp());
            } else if (accept("$MAXFREQSTRING"))  {
                expect("$LPAREN", "statement: missing LPAREN");
                expect("$ID", "statement: missing ID");
                expect("$RPAREN", "statement: missing RPAREN");
            }
        } else {
            error("statement: malformed statement");
        }
        stack.pop();
        return statementNode;
	}

    public SyntaxTreeNode file_names() {
        SyntaxTreeNode fileNamesNode = new SyntaxTreeNode("FILE-NAMES");
        stack.push(fileNamesNode);
        fileNamesNode.addChild(source_file());
        expect("$PIPE-THING", "file_names: missing pipe-thing");
        fileNamesNode.addChild(destination_file());
        stack.pop();
        return fileNamesNode;
    }

    public SyntaxTreeNode source_file() {
        SyntaxTreeNode sourceFileNode = new SyntaxTreeNode("SOURCE-FILE");
        stack.push(sourceFileNode);
        expect("$ASCII-STR", "source_file: not a valid source file name");
        stack.pop();
        return sourceFileNode;
    }

    public SyntaxTreeNode destination_file() {
        SyntaxTreeNode destinationFileNode = new SyntaxTreeNode("DESTINATION-FILE");
        stack.push(destinationFileNode);
        expect("$ASCII-STR", "source_file: not a valid destination file name");
        stack.pop();
        return destinationFileNode;
    }

    public SyntaxTreeNode exp_list() {
        SyntaxTreeNode expListNode = new SyntaxTreeNode("EXP-LIST");
        stack.push(expListNode);
        if (!accept("$NULL")) {
            expListNode.addChild(exp());
            expListNode.addChild(exp_list_tail());
        }
        stack.pop();
        return expListNode;
    }

    public SyntaxTreeNode exp_list_tail() {
        SyntaxTreeNode expListTailNode = new SyntaxTreeNode("EXP-LIST-TAIL");
        stack.push(expListTailNode);
        if (accept("$COMMA")) {
            expListTailNode.addChild(exp());
            expListTailNode.addChild(exp_list_tail());
        } else if (accept("$BINOP")) {
            expListTailNode.addChild(term());
            expListTailNode.addChild(exp_list_tail());
        }
        stack.pop();
        return expListTailNode;
    }

    public SyntaxTreeNode exp() {
        SyntaxTreeNode expNode = new SyntaxTreeNode("EXP");
        stack.push(expNode);
        if (accept("$ID")) {
            // pass
        } else if (accept("$LPAREN")) {
            expNode.addChild(exp());
            expect("$RPAREN", "exp: missing rparen");
        } else {
            expNode.addChild(term());
            expNode.addChild(exp_tail());
        }
        stack.pop();
        return expNode;
    }

    public SyntaxTreeNode exp_tail() {
        SyntaxTreeNode expTailNode = new SyntaxTreeNode("EXP-TAIL");
        stack.push(expTailNode);
        if (!accept("$NULL")) {
            expTailNode.addChild(bin_op());
            expTailNode.addChild(term());
            expTailNode.addChild(exp_tail());
        }
        stack.pop();
        return expTailNode;
    }

    public SyntaxTreeNode term() {
        SyntaxTreeNode termNode = new SyntaxTreeNode("TERM");
        stack.push(termNode);
        expect("$FIND", "term: expected FIND");
        expect("$REGEX", "term: expeccted REGEX");
        expect("$IN", "term: missing 'in'");
        termNode.addChild(file_name());
        stack.pop();
        return termNode;
    }

    public SyntaxTreeNode file_name() {
        SyntaxTreeNode fileNameNode = new SyntaxTreeNode("FILE-NAME");
        stack.push(fileNameNode);
        expect("$ASCII-TERM", "file_name: not a valid file name");
        stack.pop();
        return fileNameNode;

    }

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

	public void error(String failedGrammar) {
		System.out.println("error in " + failedGrammar);
		System.exit(0);
	}
	
    public boolean accept(String id) {
        if (token.equals(id)) {
            SyntaxTreeNode newNode = new SyntaxTreeNode(token, lineNumber);
            stack.peek().addChild(newNode);
            token = tkzr.peekToken();
            return true;
        }
        return false;
    }

    public boolean peek(String id) {
        return token.equals(id);
    }

    public void expect(String id, String grammar) {
        if (accept(id))
            return;
        error(grammar);
    }

    public void check(String id, String grammar) {
        if (token.equals(id)) {
            return;
        }
        error(grammar);
    }

    public String take() {
        String ret = token.getString();
        token = tkzr.peekToken();
        return ret;
    }
	
}