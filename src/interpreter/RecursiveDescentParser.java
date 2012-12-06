package interpreter;
import tokenizer.Token;
import tokenizer.Tokenizer;



public class RecursiveDescentParser {
	
	Token token;
	Tokenizer tkzr;
	public RecursiveDescentParser(String miniRETokenSpec, String programFile) {
		tkzr = new Tokenizer(miniRETokenSpec, programFile);
		
		token = tkzr.peekToken();
		if (token.getId() != "BEGIN")
			System.out.println("ERROR.");
		
		token = tkzr.peekToken();
		statement_list();
		
		if (token.getId() != "END")
			System.out.println("ERROR.");
	}
	
	public void statement_list() {
		statement();
		statement_list_tail();
	}
	
	public void statement_list_tail() {
		token = tkzr.peekToken();
		if (token.toString().equals("NULL: MATCH")) {
			tkzr.matchToken(new Token("NULL", "MATCH"));
			return;
		}
		
		statement();
		statement_list_tail();
	}
	
	public void statement() {
		String ID_name = "";
		if (!tkzr.matchToken(new Token("ID", "NULL"))) {
			error("STATEMENT");
		}
		if (!tkzr.matchToken(new Token("ASSIGN", "NULL"))) {
			error("STATEMENT");
		}
		token = tkzr.peekToken();
		if (token.getId().equals("#")) {
			tkzr.matchToken(new Token("HASH", "NULL"));
			exp();
		}
	}
	
	public void error(String failedGrammar) {
		System.out.println("ERROR in " + failedGrammar);
		System.exit(0);
	}
	
	
	
}