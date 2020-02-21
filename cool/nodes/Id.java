package cool.nodes;

import cool.structures.IdSymbol;
import cool.structures.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

// Identificatori
public class Id extends Expression {
    public  IdSymbol symbol;
    public  Scope scope;
    public ParserRuleContext ctx;
    public String debugName = null;
    public Id(Token token, ParserRuleContext ctx) {
        super(token);
        this.token = token;
        this.ctx = ctx;
        this.debugName = token.getText();
    }

    public IdSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(IdSymbol symbol) {
        this.symbol = symbol;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

