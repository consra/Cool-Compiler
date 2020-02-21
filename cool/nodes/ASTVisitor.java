package cool.nodes;

import org.antlr.v4.runtime.Token;

public interface ASTVisitor<T> {
    T visit(Id id);
    T visit(Int intt);
    T visit(Float float1);
    T visit(Bool bool1);
    T visit(If iff);
    T visit(MultDiv multDiv);
    T visit(PlusMinus plusMinus);
    T visit(Paren paren);
    T visit(Assign assign);
    T visit(RelationalExpr rexpr);
    T visit(UnaryMinus unaryMinus);
    T visit(FunctionCall fCall);
    T visit(VarDef varDef);
    T visit(FuncDef funcDef);
    T visit(Program program);
    T visit(Clas clas);
    T visit(Formal formal);
    T visit(Str str);
    T visit(TldNode tldNode);
    T visit(NotNode notNode);
    T visit(IsvoidNode isvoidNode);
    T visit(InstanceNode instanceNode);
    T visit(WhileNode whileNode);
    T visit(MethodDispatch methodDispatch);
    T visit(LetNode letNode);
    T visit(CaseNode caseNode);
    T visit(BlockNode blockNode);
    T visit(Type type);

    public static void error(Token token, String message) {
        System.err.println("line " + token.getLine()
                + ":" + (token.getCharPositionInLine() + 1)
                + ", " + message);
    }
}
