package cool.nodes;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;

public class BlockNode extends Expression {
    public ArrayList<Expression> exprs;
    public Token start;

    public BlockNode(ArrayList<Expression> exprs, Token token, Token start) {
        super(token);
        this.exprs = exprs;
        this.start = start;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

