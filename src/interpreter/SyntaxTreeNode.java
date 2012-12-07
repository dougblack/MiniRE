package interpreter;

import tokenizer.Token;

import java.util.ArrayList;
import java.util.Stack;

public class SyntaxTreeNode {

    Token token;
    ArrayList<SyntaxTreeNode> children;
    int lineNumber;
    String id;
    String value;
    String nodeType;

    /**
     * This constructor is used when a terminal was enountered.
     * @param nodeToken the token of the terminal
     * @param tokenLineNumber this is probably deprecated.
     */
    public SyntaxTreeNode(Token nodeToken, int tokenLineNumber) {
        this.children = new ArrayList<SyntaxTreeNode>();
        this.token = nodeToken;
        this.lineNumber = tokenLineNumber;
        this.id = token.getId();
        this.value = token.getString();
    }

    public SyntaxTreeNode(String nodeType) {
        this.children = new ArrayList<SyntaxTreeNode>();
        this.nodeType = nodeType;
    }

    public void addChild(SyntaxTreeNode newChild) {
        children.add(newChild);
    }

    public void printLevels() {
        Stack<SyntaxTreeNode> nextLevel = new Stack<SyntaxTreeNode>();
        nextLevel.add(this);
        int level = 0;
        while (!nextLevel.empty()) {
            System.out.println("=========LEVEL " + level + "==========");
            Stack<SyntaxTreeNode> nextLevelTemp = new Stack<SyntaxTreeNode>();
            while (!nextLevel.empty()) {
                SyntaxTreeNode node = nextLevel.pop();
                if (node.id != null)
                    System.out.println("Node: " + node.id + "," + node.value);
                else
                    System.out.println("Node: " + node.nodeType);

                for (int i = node.children.size()-1; i >= 0; i--) {
                    nextLevelTemp.push(node.children.get(i));
                }
            }
            level++;
            nextLevel = nextLevelTemp;
        }
    }

}
