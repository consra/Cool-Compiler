package cool.nodes;


import cool.structures.FunctionSymbol;
import cool.structures.TypeSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class FunctionCall extends Expression {
    public LinkedList<Expression> params;
    public Id id;
    public Token dot = null;
    public Token start;
    public MethodDispatch methodDispatch;
    public ParserRuleContext ctx;
    public TypeSymbol dispatcherType;
    public FunctionSymbol functionSymbol;
    public FunctionCall(LinkedList<Expression> params, Id id, Token dot, Token start, MethodDispatch methodDispatch, ParserRuleContext ctx) {
        super(start);
        this.params = params;
        this.dot = dot;
        this.id = id;
        this.start = start;
        this.methodDispatch = methodDispatch;
        this.ctx = ctx;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

