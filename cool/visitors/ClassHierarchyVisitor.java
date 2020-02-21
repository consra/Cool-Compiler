package cool.visitors;

import cool.nodes.*;
import cool.nodes.Float;
import cool.structures.SymbolTable;
import cool.utils.ClassParserCtx;
import cool.utils.InheritanceInspector;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ClassHierarchyVisitor implements ASTVisitor<Void> {
    public static LinkedHashMap<ClassParserCtx, ClassParserCtx> classHierarchy;
    @Override
    public Void visit(Id id) {
        return null;
    }

    @Override
    public Void visit(Int intt) {
        return null;
    }

    @Override
    public Void visit(Float float1) {
        return null;
    }

    @Override
    public Void visit(Bool bool1) {
        return null;
    }

    @Override
    public Void visit(If iff) {
        return null;
    }

    @Override
    public Void visit(MultDiv multDiv) {
        return null;
    }

    @Override
    public Void visit(PlusMinus plusMinus) {
        return null;
    }

    @Override
    public Void visit(Paren paren) {
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        return null;
    }

    @Override
    public Void visit(RelationalExpr rexpr) {
        return null;
    }

    @Override
    public Void visit(UnaryMinus unaryMinus) {
        return null;
    }

    @Override
    public Void visit(FunctionCall fCall) {
        return null;
    }

    @Override
    public Void visit(VarDef varDef) {
        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        return null;
    }

    @Override
    public Void visit(Program program) {
        classHierarchy = new LinkedHashMap<>();

        ClassParserCtx objectCtx = DefinitionPassVisitor.classes.get("Object");
        ClassParserCtx ioCtx = DefinitionPassVisitor.classes.get("IO");

        classHierarchy.put(objectCtx, null);
        classHierarchy.put(ioCtx, objectCtx);

        for (var cl: program.classes) {
            cl.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Clas clas) {
        ClassParserCtx classParserCtx = DefinitionPassVisitor.classes.get(clas.name.getText());
        if(clas.parent != null)
            classHierarchy.put(classParserCtx, DefinitionPassVisitor.classes.get(clas.parent.getText()));
        else {
            ClassParserCtx objectCtx = DefinitionPassVisitor.classes.get("Object");
            classHierarchy.put(classParserCtx, objectCtx);
        }
        ArrayList<ClassParserCtx> path = new InheritanceInspector(classHierarchy).detectCycle(classParserCtx);
        if(path != null) {
            for(int i = path.size() - 1; i >= 0; i--) {
                SymbolTable.addError(path.get(i).getCtx(), path.get(i).getToken(),
                        "Inheritance cycle for class " + path.get(i).getClassName());
            }
        }
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        return null;
    }

    @Override
    public Void visit(Str str) {
        return null;
    }

    @Override
    public Void visit(TldNode tldNode) {
        return null;
    }

    @Override
    public Void visit(NotNode notNode) {
        return null;
    }

    @Override
    public Void visit(IsvoidNode isvoidNode) {
        return null;
    }

    @Override
    public Void visit(InstanceNode instanceNode) {
        return null;
    }

    @Override
    public Void visit(WhileNode whileNode) {
        return null;
    }

    @Override
    public Void visit(MethodDispatch methodDispatch) {
        return null;
    }

    @Override
    public Void visit(LetNode letNode) {
        return null;
    }

    @Override
    public Void visit(CaseNode caseNode) {
        return null;
    }

    @Override
    public Void visit(BlockNode blockNode) {
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }
}
