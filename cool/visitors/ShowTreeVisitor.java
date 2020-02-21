package cool.visitors;

import cool.nodes.Float;
import cool.structures.*;
import cool.nodes.*;

public class ShowTreeVisitor implements ASTVisitor<Void> {
    int indent = 0;

    @Override
    public Void visit(FunctionCall fCall) {
        boolean increaseIndent = false;
        if(fCall.dot == null) {
            increaseIndent = true;
            printIndent("implicit dispatch");
        }

        if(increaseIndent)
            indent++;

        printIndent(fCall.id.getToken().getText());
        for(var param : fCall.params) {
            param.accept(this);
        }

        if(increaseIndent)
            indent--;

        return null;
    }

    @Override
    public Void visit(Id id) {
        printIndent(id.token.getText());
        return null;
    }

    public Void visit(BlockNode blockNode) {
        printIndent("block");
        indent++;
        for(var expr : blockNode.exprs) {
            expr.accept(this);
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }

    @Override
    public Void visit(CaseNode caseNode) {
        printIndent(caseNode.getToken().getText());
        indent++;
        caseNode.cond.accept(this);
        for(var cse : caseNode.cases) {
            printIndent("case branch");
            indent++;
            cse.getKey().accept(this);
            cse.getValue().accept(this);
            indent--;
        }
        indent--;
        return null;
    }

    public Void visit(LetNode letNode) {
        printIndent(letNode.getToken().getText());
        indent++;
        for(var def : letNode.defs) {
            printIndent("local");
            def.accept(this);
        }
        letNode.expr.accept(this);
        indent--;
        return null;
    }
    @Override
    public Void visit(WhileNode whileNode) {
        printIndent(whileNode.getToken().getText());
        indent++;
        whileNode.cond.accept(this);
        whileNode.body.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(MethodDispatch methodDispatch) {
        printIndent(methodDispatch.dot.getText());
        indent++;
        methodDispatch.dispatcher.accept(this);
        if(methodDispatch.type != null)
            printIndent(methodDispatch.type.getText());
        methodDispatch.methCall.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(IsvoidNode in) {
        printIndent(in.getToken().getText());
        indent++;
        in.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(InstanceNode node) {
        printIndent(node.n.getText());
        indent++;
        printIndent(node.type.getText());
        indent--;
        return null;
    }

    @Override
    public Void visit(Str str) {
        printIndent(str.getToken().getText());
        return null;
    }

    @Override
    public Void visit(TldNode tldNode) {
        printIndent(tldNode.getToken().getText());
        indent++;
        tldNode.arithmetic_expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(NotNode notNode) {
        printIndent(notNode.getToken().getText());
        indent++;
        notNode.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Int intt) {
        printIndent(intt.getToken().getText());
        return null;
    }

    @Override
    public Void visit(Float float1) {
        printIndent(float1.getToken().getText());
        return null;
    }

    @Override
    public Void visit(Bool bool1) {
        printIndent(bool1.getToken().getText());
        return null;
    }

    @Override
    public Void visit(If iff) {
        printIndent(iff.getToken().getText());
        indent++;
        iff.cond.accept(this);
        iff.thenBranch.accept(this);
        iff.elseBranch.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(MultDiv multDiv) {
        printIndent(multDiv.getToken().getText());
        indent++;
        multDiv.expr1.accept(this);
        multDiv.expr2.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(PlusMinus plusMinus) {
        printIndent(plusMinus.getToken().getText());
        indent++;
        plusMinus.expr1.accept(this);
        plusMinus.expr2.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Paren paren) {
        printIndent("Paren");
        indent++;
        paren.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        printIndent(assign.getToken().getText());
        indent++;
        assign.id.accept(this);
        assign.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(RelationalExpr rexpr) {
        printIndent(rexpr.getToken().getText());
        indent++;
        rexpr.expr1.accept(this);
        rexpr.expr2.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(UnaryMinus unaryMinus) {
        printIndent(unaryMinus.token.getText());
        indent++;
        unaryMinus.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(VarDef varDef) {
        if(varDef.vardefType.equals("class"))
            printIndent("attribute");

        indent++;
        printIndent(varDef.id.token.getText());
        printIndent(varDef.type.token.getText());
        if(varDef.initValue != null)
            varDef.initValue.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        printIndent("method");
        indent++;
        String txt = funcDef.id.token.getText();
        printIndent(txt);
        for(var formal : funcDef.formals) {
            printIndent("formal");
            indent++;
            formal.accept(this);
            indent--;
        }
        printIndent(funcDef.type.token.getText());
        funcDef.body.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Program program) {
        printIndent("program");
        indent++;
        for(var c : program.classes) {
            c.accept(this);
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(Clas clas) {
        printIndent("class");
        indent += 1;
        printIndent(clas.name.getText());
        if(clas.parent != null)
            printIndent(clas.parent.getText());

        for(var def : clas.defs) {
            def.accept(this);
        }
        indent -= 1;
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        printIndent(formal.id.token.getText());
        printIndent(formal.type.token.getText());
        return null;
    }

    void printIndent(String str) {
        for (int i = 0; i < indent; i++)
            System.out.print("  ");
        System.out.println(str);
    }
}
