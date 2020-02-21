package cool.visitors;

import cool.nodes.*;
import cool.nodes.Float;
import cool.structures.ClassSymbol;
import cool.structures.FunctionSymbol;
import cool.structures.IdSymbol;
import cool.structures.SymbolTable;
import cool.utils.ClassParserCtx;
import cool.utils.GenericPair;
import cool.utils.InheritanceInspector;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DispatchOffsetCalcVisitor implements ASTVisitor<ST> {
    HashMap<String, ClassParserCtx> classes = DefinitionPassVisitor.classes;
    LinkedHashMap<ClassParserCtx, ClassParserCtx> classHierarchy = ClassHierarchyVisitor.classHierarchy;
    HashMap<String, GenericPair<Integer, Integer>> tags;

    @Override
    public ST visit(Id id) {
        return null;
    }

    @Override
    public ST visit(Int intt) {
        return null;
    }

    @Override
    public ST visit(Float float1) {
        return null;
    }

    @Override
    public ST visit(Bool bool1) {
        return null;
    }

    @Override
    public ST visit(If iff) {
        return null;
    }

    @Override
    public ST visit(MultDiv multDiv) {
        return null;
    }

    @Override
    public ST visit(PlusMinus plusMinus) {
        return null;
    }

    @Override
    public ST visit(Paren paren) {
        return null;
    }

    @Override
    public ST visit(Assign assign) {
        return null;
    }

    @Override
    public ST visit(RelationalExpr rexpr) {
        return null;
    }

    @Override
    public ST visit(UnaryMinus unaryMinus) {
        return null;
    }

    @Override
    public ST visit(FunctionCall fCall) {
        return null;
    }

    @Override
    public ST visit(VarDef varDef) {
        return null;
    }

    @Override
    public ST visit(FuncDef funcDef) {
        return null;
    }

    @Override
    public ST visit(Program program) {
        ClassParserCtx objCtx = DefinitionPassVisitor.classes.get("Object");
        tags = new InheritanceInspector(classHierarchy).addTags(objCtx, 0);
        resolveOffsetForDefaults();
        for (ASTNode cl : program.classes)
            cl.accept(this);
        return null;
    }

    private void resolveOffset(Map.Entry<String, GenericPair<Integer, Integer>> entry) {
        ClassParserCtx currentCtx = classes.get(entry.getKey());
        ClassParserCtx parentCtx = classHierarchy.get(currentCtx);
        if(parentCtx != null)
            currentCtx.getMethodsName().addAll(parentCtx.getMethodsName());

        for(var entry2 : currentCtx.classSymbol.getSymbols().entrySet()) {
            var symbol = entry2.getValue();
            if(symbol instanceof FunctionSymbol) {
                int offset = 0;
                if(currentCtx.getMethodsName().size() > 0)
                    offset = currentCtx.getMethodsName().size() * 4;
                ((FunctionSymbol) symbol).offset = offset;
                currentCtx.getMethodsName().add(entry.getKey() + "." + symbol.getName());
            }
        }
    }

    public void resolveOffsetForDefaults() {
        var entrySet = new ArrayList<>(tags.entrySet());
        for(int i = 0; i < 2; i++) {
            var entry = entrySet.get(i);
            resolveOffset(entry);
        }

        for(int i = entrySet.size() - 3; i < entrySet.size(); i++) {
            var entry = entrySet.get(i);
            resolveOffset(entry);
        }
    }

    @Override
    public ST visit(Clas clas) {
        ClassParserCtx ctx = classes.get(clas.name.getText());
        ClassParserCtx parentCtx = classHierarchy.get(ctx);
        ClassSymbol classSymbol = ctx.classSymbol;
        String className = clas.name.getText();


        ctx.getMethodsName().addAll(parentCtx.getMethodsName());
        ctx.getAttributesName().addAll(parentCtx.getAttributesName());
        for(var entry : classSymbol.getSymbols().entrySet()) {
            var symbol = entry.getValue();
            if(symbol instanceof FunctionSymbol) {
                String completeMethodName = className + "." + ((FunctionSymbol) symbol).getName();
                String methodName = ((FunctionSymbol) symbol).getName();
                boolean flag = true;
                for(int i = 0; i < ctx.getMethodsName().size(); i++) {
                    String currentMethodName = ctx.getMethodsName().get(i).split("\\.")[1];
                    if(currentMethodName.equals(methodName)) {
                        ctx.getMethodsName().set(i, completeMethodName);
                        flag = false;
                        ((FunctionSymbol) symbol).offset = i * 4;
                    }
                }
                if(flag) {
                    ((FunctionSymbol) symbol).offset = ctx.getMethodsName().size() * 4;
                    ctx.getMethodsName().add(completeMethodName);
                }
            } else if(symbol instanceof IdSymbol) {
                if(SymbolTable.basicTypes.contains(((IdSymbol) symbol).getType().getName())) {
                    String typeName = ((IdSymbol) symbol).getType().getName();
                    String attrType = null;
                    if(typeName.equals("Bool"))
                        attrType = "bool_const0";
                    if(typeName.equals("String"))
                        attrType = "str_const0";
                    if(typeName.equals("Int"))
                        attrType = "int_const0";
                    ((IdSymbol) symbol).offset = ctx.getAttributesName().size() * 4;
                    ctx.getAttributesName().add(attrType);
                } else if(!symbol.getName().equals("self") || ((IdSymbol) symbol).getType().getName().equals("SELF_TYPE") ){
                    ((IdSymbol) symbol).offset = ctx.getAttributesName().size() * 4;
                    ctx.getAttributesName().add("0");
                }
            }
        }
        return null;
    }

    @Override
    public ST visit(Formal formal) {
        return null;
    }

    @Override
    public ST visit(Str str) {
        return null;
    }

    @Override
    public ST visit(TldNode tldNode) {
        return null;
    }

    @Override
    public ST visit(NotNode notNode) {
        return null;
    }

    @Override
    public ST visit(IsvoidNode isvoidNode) {
        return null;
    }

    @Override
    public ST visit(InstanceNode instanceNode) {
        return null;
    }

    @Override
    public ST visit(WhileNode whileNode) {
        return null;
    }

    @Override
    public ST visit(MethodDispatch methodDispatch) {
        return null;
    }

    @Override
    public ST visit(LetNode letNode) {
        return null;
    }

    @Override
    public ST visit(CaseNode caseNode) {
        return null;
    }

    @Override
    public ST visit(BlockNode blockNode) {
        return null;
    }

    @Override
    public ST visit(Type type) {
        return null;
    }
}
