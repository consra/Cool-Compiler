package cool.visitors;

import cool.nodes.Float;
import cool.structures.*;
import cool.utils.ClassParserCtx;
import cool.nodes.*;

import java.util.HashMap;

public class DefinitionPassVisitor implements ASTVisitor<Void> {
    public Scope currentScope = SymbolTable.globals;
    public static HashMap<String, ClassParserCtx> classes = new HashMap<>();

    @Override
    public Void visit(Program prog) {

        ClassSymbol objectSymbol = (ClassSymbol) currentScope.lookup("Object");
        ClassSymbol stringSymbol = (ClassSymbol) currentScope.lookup("String");
        ClassSymbol ioSymbol = (ClassSymbol) currentScope.lookup("IO");
        ClassSymbol intSymbol = (ClassSymbol) currentScope.lookup("Int");
        ClassSymbol boolSymbol = (ClassSymbol) currentScope.lookup("Bool");

        ClassParserCtx objectClassParserCtx = new ClassParserCtx("Object", null, null);
        objectClassParserCtx.setClassSymbol(objectSymbol);
        classes.put("Object", objectClassParserCtx);

        ClassParserCtx ioClassParserCtx = new ClassParserCtx("IO", null, null);
        ioClassParserCtx.setClassSymbol(ioSymbol);
        classes.put("IO", ioClassParserCtx);
        
        ClassParserCtx stringClassParserCtx = new ClassParserCtx("String", null, null);
        stringClassParserCtx.setClassSymbol(stringSymbol);
        classes.put("String", stringClassParserCtx);

        ClassParserCtx intClassParserCtx = new ClassParserCtx("Int", null, null);
        intClassParserCtx.setClassSymbol(intSymbol);
        classes.put("Int", intClassParserCtx);

        ClassParserCtx boolClassParserCtx = new ClassParserCtx("Bool", null, null);
        boolClassParserCtx.setClassSymbol(boolSymbol);
        classes.put("Bool", boolClassParserCtx);

        for (var stmt : prog.classes)
            stmt.accept(this);

        return null;
    }

    @Override
    public Void visit(Clas clas) {
        ClassParserCtx classParserCtx = new ClassParserCtx(clas.name.getText(), clas.ctx, clas.name);
        classes.put(clas.name.getText(), classParserCtx);

        Symbol classSymbol = currentScope.lookup(clas.name.getText());
        //if the class was redefined
        if (classSymbol instanceof ClassSymbol) {
            SymbolTable.addError(clas.ctx, clas.name,
                    "Class " + clas.name.getText() + " is redefined");
            return null;
        }

        // if the class is a basic class
        if (classSymbol instanceof TypeSymbol) {
            if (classSymbol.getName().equals("SELF_TYPE"))
                SymbolTable.addError(clas.ctx, clas.name,
                        "Class has illegal name SELF_TYPE");
            else
                SymbolTable.addError(clas.ctx, clas.name,
                        "Class " + clas.name.getText() + " is redefined");
            return null;
        }

        ClassSymbol newClassSymbol = new ClassSymbol(currentScope, null, clas.name.getText());
        classParserCtx.classSymbol = newClassSymbol;
        currentScope.add(newClassSymbol.getName(), newClassSymbol);
        Scope oldScope = currentScope;
        currentScope = newClassSymbol;
        currentScope.addId(newClassSymbol.getSelfSymbol());
        for (var def : clas.defs) {
            def.accept(this);
        }

        currentScope = oldScope;
        return null;
    }

    @Override
    public Void visit(Id id) {
//        // Căutăm simbolul în domeniul curent.
        var symbol = (IdSymbol) currentScope.lookupId(id.getToken().getText());
        if(symbol == null)
            return null;

        id.setScope(currentScope);

        // Atașăm simbolul nodului din arbore.
        id.setSymbol(symbol);

        return null;
    }

    public Void visitLetVardef(VarDef varDef) {
        // La definirea unei variabile, creăm un nou simbol.
        // Adăugăm simbolul în domeniul de vizibilitate curent.

        if (varDef.initValue != null)
            varDef.initValue.accept(this);

        var id = varDef.id;
        var type = varDef.type;

        var symbol = new IdSymbol(id.getToken().getText());
        symbol.idType = "local";

        // Atașăm simbolul nodului din arbore.
        id.setSymbol(symbol);
        id.setScope(currentScope);

        if (id.token.getText().equals("self")) {
            SymbolTable.addError(varDef.ctx, varDef.id.token, "Let variable has illegal name self");
            return null;
        }

        // Semnalăm eroare dacă există deja variabila în scope-ul curent.
        if (!currentScope.addId(symbol)) {
        }

        // Tipul unei definiții ca instrucțiune în sine nu este relevant.
        return null;
    }

    public Void visitClassVardef(VarDef varDef) {
        // La definirea unei variabile, creăm un nou simbol.
        // Adăugăm simbolul în domeniul de vizibilitate curent.

        var id = varDef.id;
        var type = varDef.type;

        var symbol = new IdSymbol(id.getToken().getText());
        symbol.idType = "attribute";

        // Atașăm simbolul nodului din arbore.
        id.setSymbol(symbol);
        id.setScope(currentScope);

        if (id.token.getText().equals("self")) {
            SymbolTable.addError(varDef.ctx, varDef.id.token, "Class " +
                    ((ClassSymbol) currentScope).getName() +
                    " has attribute with illegal" + " name self");
            varDef.throwedErrorInDefinitonVisitor = true;
            return null;
        }

        // Semnalăm eroare dacă există deja variabila în scope-ul curent.
        if (!currentScope.addId(symbol)) {
            SymbolTable.addError(varDef.ctx, varDef.id.token, "Class " +
                    ((ClassSymbol) currentScope).getName() +
                    " redefines attribute " + varDef.id.token.getText());
            varDef.throwedErrorInDefinitonVisitor = true;
            return null;
        }

        if (varDef.initValue != null)
            varDef.initValue.accept(this);

        // Tipul unei definiții ca instrucțiune în sine nu este relevant.
        return null;
    }

    @Override
    public Void visit(VarDef varDef) {
        if(varDef.vardefType.equals("let")) {
            return visitLetVardef(varDef);
        }
        if(varDef.vardefType.equals("class")) {
            return visitClassVardef(varDef);
        }

        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        var id = funcDef.id;
        var type = funcDef.type;

        var functionSymbol = new FunctionSymbol(currentScope, id.getToken().getText(), funcDef);
        currentScope = functionSymbol;
        id.setScope(currentScope);
        String name = id.token.getText();
        // Verificăm faptul că o funcție cu același nume nu a mai fost
        // definită până acum.
        id.setSymbol(functionSymbol);
        id.setScope(currentScope);

        if (!currentScope.getParent().addMethod(functionSymbol)) {
            SymbolTable.addError(funcDef.ctx, funcDef.id.token, "Class " +
                    ((ClassSymbol) currentScope.getParent()).getName() + " redefines method " + id.getSymbol().getName());
            currentScope = currentScope.getParent();
            return null;
        }

        int offset = 0;
        for (var formal : funcDef.formals) {
            formal.accept(this);
            ((Formal) formal).id.getSymbol().offset = offset;
            offset += 4;
        }

        funcDef.body.accept(this);

        currentScope = currentScope.getParent();
        return null;
    }

    public Void visitFormalFunc(Formal formal) {
        var id = formal.id;
        var type = formal.type;

        var symbol = new IdSymbol(id.getToken().getText());
        symbol.idType = "parameter";

        String className = ((ClassSymbol) currentScope.getParent()).getName();
        String methodName = ((FunctionSymbol) currentScope).getName();
        id.setSymbol(symbol);
        id.setScope(currentScope);

        if(id.getSymbol().getName().equals("self")) {
            SymbolTable.addError(formal.ctx, formal.ctx.start, "Method " + methodName + " of class " + className +
                    " has formal parameter with illegal name " + id.getSymbol().getName());

            return null;
        }

        // Verificăm dacă parametrul deja există în scope-ul curent.
        if (!currentScope.addId(symbol)) {
            SymbolTable.addError(formal.ctx, formal.ctx.start, "Method " + methodName + " of class " + className +
                    " redefines formal parameter " + id.getSymbol().getName());
            return null;
        }

        return null;
    }

    public Void visitFormalVardef(Formal formal) {

        return null;
    }

    public Void visitFormalCase(Formal formal) {
        var id = formal.id;
        var type = formal.type;

        var symbol = new IdSymbol(id.getToken().getText());
        id.setSymbol(symbol);
        id.setScope(currentScope);

        if(id.getSymbol().getName().equals("self")) {
            SymbolTable.addError(formal.ctx, formal.ctx.start, "Case variable has illegal name self");
            return null;
        }

        // Verificăm dacă parametrul deja există în scope-ul curent.
        if (!currentScope.addId(symbol)) {
//            SymbolTable.addError(formal.ctx, formal.ctx.start, "Method " + methodName + " of class " + className +
//                    " redefines formal " + id.getSymbol().getName());
            return null;
        }

        return null;
    }

    @Override
    public Void visit(Formal formal) {
        if(formal.formalType.equals("func"))
            return visitFormalFunc(formal);
        if(formal.formalType.equals("vardef"))
            return visitFormalVardef(formal);
        if(formal.formalType.equals("case"))
            return visitFormalCase(formal);

        System.out.println("Undefined formal");
        return null;
    }

    @Override
    public Void visit(Str str) {
        return null;
    }

    @Override
    public Void visit(TldNode tldNode) {
        tldNode.arithmetic_expr.accept(this);
        return null;
    }

    @Override
    public Void visit(NotNode notNode) {
        notNode.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(IsvoidNode isvoidNode) {
        isvoidNode.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(InstanceNode instanceNode) {
        return null;
    }

    @Override
    public Void visit(WhileNode whileNode) {
        whileNode.cond.accept(this);
        whileNode.body.accept(this);
        return null;
    }

    @Override
    public Void visit(MethodDispatch methodDispatch) {
        methodDispatch.dispatcher.accept(this);
        if(methodDispatch.type != null) {
            String castName = methodDispatch.type.getText();
            if (castName.equals("SELF_TYPE")) {
                SymbolTable.addError(methodDispatch.ctx,
                        methodDispatch.type, "Type of static dispatch cannot be SELF_TYPE");
                return null;
            }
        }

        methodDispatch.methCall.accept(this);
        return null;
    }

    @Override
    public Void visit(LetNode letNode) {
        var letScope = new LetScope(currentScope);
        currentScope = letScope;
        int offset = - 4;
        for(var def : letNode.defs) {
            def.accept(this);
            ((VarDef) def).id.getSymbol().offset = offset;
            offset -= 4;
        }

        letNode.expr.accept(this);
        currentScope = currentScope.getParent();
        return null;
    }


    @Override
    public Void visit(CaseNode caseNode) {
        var caseScope = new CaseScope(currentScope);
        currentScope = caseScope;
        caseNode.cond.accept(this);
        for(var formal : caseNode.cases) {
            formal.getKey().accept(this);
            formal.getValue().accept(this);
        }
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public Void visit(BlockNode blockNode) {
        for(var expr : blockNode.exprs) {
            expr.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(FunctionCall call) {
        var id = call.id;
        for (var arg: call.params) {
            arg.accept(this);
        }
        id.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        assign.id.accept(this);
        assign.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(If iff) {
        iff.cond.accept(this);
        iff.thenBranch.accept(this);
        iff.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visit(MultDiv multDiv) {
        multDiv.expr1.accept(this);
        multDiv.expr2.accept(this);
        return null;
    }

    @Override
    public Void visit(PlusMinus plusMinus) {
        plusMinus.expr1.accept(this);
        plusMinus.expr2.accept(this);
        return null;
    }

    @Override
    public Void visit(Paren paren) {
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }

    // Operații aritmetice.
    @Override
    public Void visit(UnaryMinus uMinus) {
        uMinus.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(RelationalExpr relational) {
        relational.expr1.accept(this);
        relational.expr2.accept(this);
        return null;
    }

    // Tipurile de bază
    @Override
    public Void visit(Int intt) {
        return null;
    }

    @Override
    public Void visit(Float float1) {
        return null;
    }

    @Override
    public Void visit(Bool bool) {
        return null;
    }
}