package cool.nodes;

import org.antlr.v4.runtime.Token;

public class Float extends Expression {
    public Float(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

