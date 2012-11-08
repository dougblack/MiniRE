import java.io.IOException;
import java.util.*;


public class RDP {
	private char array[][];
	private GetChar _char;
	public RDP(GetChar _char){
		
		this._char = _char;
	}
	
	private static class ParseError extends Exception {
		   ParseError(String message) {
		      super(message);
		   }
		}
	
	private boolean reg_ex() throws IOException{
		
		if(!rexp())
			return false;
		return true;
		
	}
	
	private boolean rexp() throws IOException{
		
		if(!rexp1())
			return false;
		if(!rexpp())   //rexp'
			return false;
		return true;
	}
	
	private boolean rexp1() throws IOException{
		if(!rexp2())
			return false;
		if(!rexp1p())
			return false;
		return true;
		
		//or eps
	}
	
	private boolean UNION() throws IOException{
		
		if(_char.getNextChar()=='|')
			return true;
		return false;
	}
	private boolean rexpp() throws IOException{
		
		if(!UNION())
			return false;
		if(!rexp1())
			return false;
		if(!rexpp())
			return false;
		return true;
		//or eps
	}
	
	private boolean rexp1p() throws IOException{
		if(!rexp2())
			return false;
		if(!rexp1p())
			return false;
		return true;
		//or eps
	}
	
	private boolean getRPar() throws IOException{
		if(_char.getNextChar()==')')
			return true;
		return false;
	}

	private boolean getLPar() throws IOException{
		if(_char.getNextChar()=='(')
			return true;
		return false;
	}
	
	private boolean RE_CHAR() throws IOException{
		char ch = _char.getNextChar();
		if((ch+"").matches("[a-zA-Z\\*+?|[]().'\""))
			return true;
		return false;
	}
	
	private boolean CLS_CHAR() throws IOException{
		
		char ch = _char.getNextChar();
		if((ch+"").matches("[a-zA-Z^-[]"))
			return true;
		return false;
	}
	private boolean rexp2() throws IOException{
		if(getLPar()){
			
			if(rexp())
				return true;
			if(getRPar())
				return true;
			if(rexp2_tail())
				return true;
			return false;
		}
		else if(RE_CHAR()){
			if(rexp2_tail())
				return true;
		}
		else
			if(rexp3())
				return true;
		return false;
		
	}
	
	private boolean rexp2_tail() throws IOException{
		
		char ch = _char.getNextChar();
		if(ch=='*')
			return true;
		if(ch =='+' /*|| eps*/)
			return true;
		/*if eps return true
		 * 
		 */
		return false;
			//throw new ParseError("*|+|Eps Error");
	}
	
	private boolean rexp3() throws IOException{
		return char_class(); // or eps
	}
	
	private boolean charIn(char ch, char array[][]){
		
		for(int x=0; x<array.length; x++)
			for(int y=0; y<array[x].length; y++)
				if(array[x][y] == ch)
					return true;
		return false;
	}
	
	private boolean char_class() throws IOException{
		
		char ch = _char.getNextChar();
		if (ch=='.')
			return true;
		if (char_class1())
			return true;
		if (charIn(ch,array))
			return true;
		return false;
	}
	
	private boolean char_class1() throws IOException{
		if(char_set_list())
			return true;
		if(exclude_set())
			return true;
		return false;
	}
	
	private boolean char_set_list() throws IOException{
		
		if (char_set())
			return true;
		if(char_set_list())
			return true;
		return false;
	}
	
	private boolean char_set_tail() throws IOException{
		
		if(CLS_CHAR())
			return true;
		// if eps return true
		return false;
	}
	
	private boolean char_set() throws IOException																																																																											{
		
		if(!CLS_CHAR())
			return false;
		if(!char_set_tail())
			return false;
		return true;
	}
	
	private boolean exclude_set() throws IOException{
		char ch = _char.getNextChar();
		if(ch!='^')
			return false;
		if(!char_set())
			return false;
		ch = _char.getNextChar();
		if(ch!=']')
			return false;
		ch = _char.getNextChar();
		if(ch!='I')
			return false;
		if(ch!='N')
			return false;
		
		if(!exclude_set_tail())
			return false;
		return true;
	}
	
	private boolean exclude_set_tail() throws IOException{
		
		if (_char.getNextChar()=='['){
		if(!char_set())
			return false;
		if(_char.getNextChar()!=']')
			return false;
		
		return true;
		}
		
		if(charIn(_char.getNextChar(),array))
			return true;
		
		return false;
		
	}
}
