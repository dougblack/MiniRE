package interpreter;

import tokenizer.Token;

import java.util.ArrayList;

public class SyntaxTreeNode {

    Token token;
    ArrayList<SyntaxTreeNode> children;
    SyntaxTreeNode sibling;
    int lineNumber;
    String id;
    String value;
    String nodeType;

    public SyntaxTreeNode(Token nodeToken, int tokenLineNumber) {
        children = new ArrayList<SyntaxTreeNode>();
        token = nodeToken;
        lineNumber = tokenLineNumber;
        id = token.getId();
        value = token.getString();
    }

    public SyntaxTreeNode(String nodeType) {
        children = new ArrayList<SyntaxTreeNode>();
        this.nodeType = nodeType;
    }

    public void addChild(SyntaxTreeNode newChild) {
        children.add(newChild);
    }

    public void setSibling(SyntaxTreeNode newSibling) {
        sibling = newSibling;
    }

}
