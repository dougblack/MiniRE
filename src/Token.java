
public class Token {
    private String id;
	private String string;
	
	public Token(String id, String string) {
		this.id = id;
		this.string = string;
	}
	
	public String getId() {
		return id;
	}
	
	public String getString() {
		return string;
	}
	
	public String toString() {
		return id + ": " + string;
	}
}
