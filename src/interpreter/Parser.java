package interpreter;
import tokenizer.Token;
import tokenizer.Tokenizer;



public class Parser {
	
	Token token;
	Tokenizer tkzr;
	public Parser(String miniRETokenSpec, String programFile) {
		tkzr = new Tokenizer(miniRETokenSpec, programFile);
		token = tkzr.peekToken();
	}

    public void parse() {
        expect("BEGIN", "start: begin expected");
        statement_list();
        expect("END", "start: end expected");
    }
	
	public void statement_list() {
		statement();
		statement_list_tail();
	}
	
	public void statement_list_tail() {

        if(peek("NULL"))
            return;

		statement();
		statement_list_tail();
	}

    /*
     * Not done.
     */
	public void statement() {
        if (accept("REPLACE")) {
            expect("REGEX", "statement: not valid REGEX");
            expect("WITH", "statement: missing 'width'");
            expect("ASCII-STR", "statement: not valid ASCII-STR");
            expect("IN", "statement: missing 'in'");
            check("ASCII-STR", "statement: missing FILE-NAMES");
            while(peek("ASCII-STR"))
                file_names();
        } else if (accept("RECURSIVEREPLACE")) {
            expect("REGEX", "statement: not valid REGEX");
            expect("WITH", "statement: missing 'width'");
            expect("ASCII-STR", "statement: not valid ASCII-STR");
            expect("IN", "statement: missing 'in'");
            check("ASCII-STR", "statement: missing FILE-NAMES");
            while(peek("ASCII-STR"))
                file_names();
        } else if (accept("PRINT")) {
            expect("LPAREN", "statement: missing lparen");
            exp_list();
            expect("RPAREN", "statement: missing rparen");
            expect("SEMICOLON", "statement: missing semi-colon");
        }
        else if (accept("ID")) {
            accept("ID");
            expect("ASSIGN", "statement: missing assign");
            if (accept("HASH")) {
                // signify number assign
                exp();
            } else if (accept("MAXFREQSTRING"))  {
                expect("LPAREN", "statement: missing LPAREN");
                take();
                expect("RPAREN", "statement: missing RPAREN");
            }
        } else {
            error("statement: malformed statement");
        }
	}

    public void file_names() {
        source_file();
        expect("PIPE-THING", "file_names: missing pipe-thing");
        destination_file();
    }

    public void source_file() {
        expect("ASCII-STR", "source_file: not a valid source file name");
    }

    public void destination_file() {
        expect("ASCII-STR", "source_file: not a valid destination file name");
    }

    public void exp_list() {
        if (!accept("NULL")) {
            exp();
            exp_list_tail();
        }
    }

    public void exp_list_tail() {
        if (accept("COMMA")) {
            exp();
            exp_list_tail();
        } else if (accept("BINOP")) {
            term();
            exp_list_tail();
        }
    }

    public void exp() {
        if (accept("ID")) {
            // pass
        } else if (accept("LPAREN")) {
            exp();
            expect("RPAREN", "exp: missing rparen");
        } else {
            term();
            exp_tail();
        }
    }

    public void exp_tail() {
        if (!accept("NULL")) {
            bin_op();
            term();
            exp_tail();
        }
    }

    public void term() {
        expect("FIND", "term: expected FIND");
        expect("REGEX", "term: expeccted REGEX");
        expect("IN", "term: missing 'in'");
        file_name();
    }

    public void file_name() {
        expect("ASCII-TERM", "file_name: not a valid file name");
    }

    public void bin_op() {
        if (accept("DIFF")) {
            // pass
        } else if (accept("UNION")) {
            // pass
        } else if (accept("INTERS")) {
            // pass;
        }
    }

	public void error(String failedGrammar) {
		System.out.println("error in " + failedGrammar);
		System.exit(0);
	}
	
    public boolean accept(String id) {
        if (token.equals(id)) {
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