package cool.nodes;

import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class Program extends ASTNode {
    public LinkedList<ASTNode> classes;
    public Token start;
    public Program(LinkedList<ASTNode> classes, Token start) {
        super(start);
        this.classes = classes;
        this.start = start;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

