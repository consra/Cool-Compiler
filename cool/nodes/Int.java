package cool.nodes;

import org.antlr.v4.runtime.Token;

// Literali întregi
public class Int extends Expression {
    public Int(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

