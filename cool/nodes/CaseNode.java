package cool.nodes;

import cool.utils.Pair;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;

public class CaseNode extends Expression {
    public Expression cond;
    public ArrayList<Pair> cases;
    public Token start;
    public CaseNode(Expression cond, ArrayList<Pair> cases, Token token, Token start) {
        super(token);
        this.cond = cond;
        this.cases = cases;
        this.start = start;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

