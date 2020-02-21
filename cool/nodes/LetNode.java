package cool.nodes;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;

public class LetNode extends Expression {
    public Token let;
    public ArrayList<Definition> defs;
    public Expression expr;
    public Token start;
    public LetNode(ArrayList<Definition> defs, Expression expr, Token let, Token start) {
        super(let);
        this.defs = defs;
        this.let = let;
        this.expr = expr;
        this.start = start;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

