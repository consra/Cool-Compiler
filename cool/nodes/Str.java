package cool.nodes;

import org.antlr.v4.runtime.Token;

public class Str extends Expression {
    public Str(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

