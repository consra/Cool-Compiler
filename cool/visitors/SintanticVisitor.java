package cool.visitors;

import cool.parser.CoolParser;
import cool.parser.CoolParserBaseVisitor;
import cool.utils.Pair;
import org.antlr.v4.runtime.Token;
import cool.nodes.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class SintanticVisitor extends CoolParserBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitId(CoolParser.IdContext ctx) {
        return new Id(ctx.ID().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitInt(CoolParser.IntContext ctx) {
        return new Int(ctx.INT().getSymbol());
    }

    @Override
    public ASTNode visitIf(CoolParser.IfContext ctx) {
        return new If((Expression)ctx.cond.accept(this),
                (Expression)ctx.thenBranch.accept(this),
                (Expression)ctx.elseBranch.accept(this),
                ctx.start, ctx);
    }

    public ASTNode visitMethDispatch(CoolParser.MethDispatchContext ctx) {
        LinkedList<Expression> params = new LinkedList<>();
        for(var expr : ctx.args) {
            params.add((Expression)expr.accept(this));
        }
        var funcCall = new FunctionCall(params, new Id(ctx.ID().getSymbol(), null),
                ctx.DOT().getSymbol(), ctx.start, null, ctx);
        Token type = null;
        if(ctx.TYPE() != null)
            type = ctx.TYPE().getSymbol();

        ASTNode methodDispatch = new MethodDispatch((Expression)ctx.dis.accept(this),
                funcCall,
                type,
                ctx.DOT().getSymbol(), ctx.start, ctx);
        funcCall.methodDispatch = (MethodDispatch) methodDispatch;
        return methodDispatch;
    }

    @Override
    public ASTNode visitMultDiv(CoolParser.MultDivContext ctx) {
        Token token;
        if(ctx.DIV() == null)
            token = ctx.MULT().getSymbol();
        else
            token = ctx.DIV().getSymbol();
        return new MultDiv((Expression)ctx.left.accept(this),
                (Expression)ctx.right.accept(this),
                token, ctx);
    }

    @Override
    public ASTNode visitPlusMinus(CoolParser.PlusMinusContext ctx) {
        Token token = null;
        if(ctx.PLUS() == null)
            token = ctx.MINUS().getSymbol();
        else
            token = ctx.PLUS().getSymbol();
        return new PlusMinus((Expression)ctx.left.accept(this),
                (Expression)ctx.right.accept(this),
                token, ctx);
    }

    @Override
    public ASTNode visitParen(CoolParser.ParenContext ctx) {
        return ctx.e.accept(this);
    }

    @Override
    public ASTNode visitAssign(CoolParser.AssignContext ctx) {
        return new Assign((Expression)ctx.e.accept(this),
                new Id(ctx.name, null),
                ctx.ASSIGN().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitWhile(CoolParser.WhileContext ctx) {
        return new WhileNode((Expression)ctx.cond.accept(this),
                (Expression)ctx.body.accept(this),
                ctx.WHILE().getSymbol(), ctx.start, ctx);
    }
    @Override
    public ASTNode visitIsvoid(CoolParser.IsvoidContext ctx) {
        return new IsvoidNode((Expression)ctx.expr().accept(this),
                ctx.ISVOID().getSymbol());
    }

    @Override
    public ASTNode visitInstance(CoolParser.InstanceContext ctx) {
        return new InstanceNode(ctx.NEW().getSymbol(), ctx.TYPE().getSymbol(), ctx.start, ctx);
    }

    @Override
    public ASTNode visitRelational(CoolParser.RelationalContext ctx) {
        Token token = null;
        if(ctx.LE() != null)
            token = ctx.LE().getSymbol();
        if(ctx.LT() != null)
            token = ctx.LT().getSymbol();
        if(ctx.EQUAL() != null)
            token = ctx.EQUAL().getSymbol();

        return new RelationalExpr((Expression)ctx.left.accept(this),
                (Expression)ctx.right.accept(this),
                token, ctx);
    }

    @Override
    public ASTNode visitUnaryMinus(CoolParser.UnaryMinusContext ctx) {
        return new UnaryMinus((Expression)ctx.e.accept(this),
                ctx.MINUS().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitBlocks(CoolParser.BlocksContext ctx) {
        ArrayList<Expression> exprs = new ArrayList<>();
        for(var expr : ctx.exprs) {
            exprs.add((Expression)expr.accept(this));
        }
        return new BlockNode(exprs, ctx.LBRACE().getSymbol(), ctx.start);
    }

    @Override
    public ASTNode visitString(CoolParser.StringContext ctx) {
        return new Str(ctx.start);
    }

    @Override
    public ASTNode visitCall(CoolParser.CallContext ctx) {
        LinkedList<Expression> params = new LinkedList<>();
        for(var expr : ctx.args) {
            params.add((Expression)expr.accept(this));
        }
        return new FunctionCall(params, new Id(ctx.ID().getSymbol(), null), null, ctx.start, null, ctx);
    }

    @Override
    public ASTNode visitFuncDef(CoolParser.FuncDefContext ctx) {
        Definition def = null;
        LinkedList<Definition> formals = new LinkedList<>();
        for(var param : ctx.formals) {
            formals.add((Definition) param.accept(this));
        }

        return new FuncDef(formals, (Expression) ctx.body.accept(this),
                new Id(ctx.name, null), new Type(ctx.TYPE().getSymbol()), ctx.start, ctx);
    }

    @Override
    public ASTNode visitVarDef(CoolParser.VarDefContext ctx) {
        String varDefType = null;
        if(ctx.parent instanceof CoolParser.LetContext)
            varDefType = "let";
        else if(ctx.parent instanceof CoolParser.ClasContext)
            varDefType = "class";

        Expression expr = null;
        if(ctx.vardef().init != null)
            expr = (Expression) ctx.vardef().init.accept(this);

        Token token = ctx.vardef().formal().name;
        return new VarDef(new Id(ctx.vardef().formal().name, null), new Type(ctx.vardef().formal().type),
                expr, varDefType, ctx.vardef().start, ctx.vardef());
    }

    @Override
    public ASTNode visitVardef(CoolParser.VardefContext ctx) {
        String varDefType = null;
        if(ctx.parent instanceof CoolParser.LetContext)
            varDefType = "let";
        else if(ctx.parent instanceof CoolParser.ClasContext)
            varDefType = "class";

        Expression expr = null;
        if(ctx.init != null)
            expr = (Expression) ctx.init.accept(this);

        Token token = ctx.formal().name;
        return new VarDef(new Id(ctx.formal().name, null), new Type(ctx.formal().type),
                expr, varDefType, ctx.start, ctx);
    }

    @Override
    public ASTNode visitLet(CoolParser.LetContext ctx) {
        ArrayList<Definition> defs = new ArrayList<>();
        Expression expr = (Expression)ctx.expr().accept(this);
        defs.add((Definition)ctx.first.accept(this));
        for(var def : ctx.vardefs) {
            defs.add((Definition)def.accept(this));
        }

        return new LetNode(defs, expr, ctx.LET().getSymbol(), ctx.start);
    }

    @Override
    public ASTNode visitClas(CoolParser.ClasContext ctx) {
        LinkedList<Definition> defs = new LinkedList<>();
        for(var def : ctx.definition()) {
            defs.add((Definition)def.accept(this));
        }
        return new Clas(ctx.name, ctx.parent, defs, ctx.start, ctx);
    }

    @Override
    public ASTNode visitBool(CoolParser.BoolContext ctx) {
        return new Bool(ctx.BOOL().getSymbol());
    }

    @Override
    public ASTNode visitTld(CoolParser.TldContext ctx) {
        return new TldNode((Expression)ctx.expr().accept(this),
                ctx.TLD().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitNot(CoolParser.NotContext ctx) {
        return new NotNode((Expression)ctx.expr().accept(this),
                ctx.NOT().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitCase(CoolParser.CaseContext ctx) {
        ArrayList<Pair> cases = new ArrayList<>();
        Expression cond = (Expression)ctx.cond.accept(this);
        for(int i = 0; i < ctx.formals.size(); i++) {
            CoolParser.FormalContext formal = ctx.formals.get(i);
            CoolParser.ExprContext expr = ctx.exprs.get(i);
            Pair pair = new Pair(formal.accept(this), expr.accept(this));
            cases.add(pair);
        }

        return new CaseNode(cond, cases, ctx.CASE().getSymbol(), ctx.start);
    }

    @Override
    public ASTNode visitFormal(CoolParser.FormalContext ctx) {
        Formal newFormal = null;
        String type = null;
        if(ctx.parent instanceof CoolParser.CaseContext)
            type = "case";
        else if(ctx.parent instanceof CoolParser.FuncDefContext)
            type = "func";
        else if(ctx.parent instanceof CoolParser.VarDefContext)
            type = "vardef";

        return new Formal(new Id(ctx.name, null), new Type(ctx.type), ctx.start, type, ctx);
    }

    @Override
    public ASTNode visitProgram(CoolParser.ProgramContext ctx) {
        LinkedList<ASTNode> classes = new LinkedList<>();

        for(var c : ctx.clas()) {
            classes.add((ASTNode)c.accept(this));
        }

        return new Program(classes, ctx.start);
    }
}