package cool.visitors;

import cool.nodes.Float;
import cool.parser.CoolParser;
import cool.structures.*;
import cool.nodes.*;
import cool.utils.ClassParserCtx;
import cool.utils.CustomToken;
import cool.utils.InheritanceInspector;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ResolutionPassVisitor implements ASTVisitor<TypeSymbol> {
    LinkedHashMap<ClassParserCtx, ClassParserCtx> classHierarchy = ClassHierarchyVisitor.classHierarchy;
    public Scope currentScope = SymbolTable.globals;

    @Override
    public TypeSymbol visit(Program prog) {
        ClassParserCtx objectCtx = DefinitionPassVisitor.classes.get("Object");
        ClassParserCtx stringCtx = DefinitionPassVisitor.classes.get("String");
        ClassParserCtx intCtx = DefinitionPassVisitor.classes.get("Int");
        ClassParserCtx boolCtx = DefinitionPassVisitor.classes.get("Bool");

        classHierarchy.put(stringCtx, objectCtx);
        classHierarchy.put(intCtx, objectCtx);
        classHierarchy.put(boolCtx, objectCtx);

        for (var cl: prog.classes) {
            cl.accept(this);
        }
        return null;
    }

    @Override
    public TypeSymbol visit(Clas clas) {
        Symbol parentScope = null;
        if(clas.parent != null) {
            parentScope = currentScope.lookup(clas.parent.getText());
            if(parentScope == null) {
                SymbolTable.addError(clas.ctx, clas.parent,
                        "Class " + clas.name.getText() + " has undefined parent " + clas.parent.getText());
            } else if (!(parentScope instanceof ClassSymbol)) {
                SymbolTable.addError(clas.ctx, clas.parent,
                        "Class " + clas.name.getText() + " has illegal parent " + clas.parent.getText());
            } else {
                ((ClassSymbol) currentScope.lookup(clas.name.getText())).setExtendedClassScope((ClassSymbol) parentScope);
            }
        }

        Scope oldScope = currentScope;
        String className = clas.name.getText();
        if(!(currentScope.lookup(className) instanceof ClassSymbol))
            return  null;

        ClassSymbol newScope = (ClassSymbol) currentScope.lookup(className);
        currentScope = newScope;

        for(var def : clas.defs) {
            def.accept(this);
        }

        currentScope = oldScope;
        return null;
    }

    @Override
    public TypeSymbol visit(Id id) {
        if(id.getToken().getText().equals("self")) {
            var auxScope = currentScope;
            while(!(auxScope instanceof ClassSymbol)) {
                auxScope = auxScope.getParent();
            }

            return (TypeSymbol) auxScope;
        }

        IdSymbol idSymbol = id.getSymbol();
        if(idSymbol == null) {
            var classScope = currentScope;
            while(!(classScope instanceof ClassSymbol))
                classScope = classScope.getParent();

            String className = getTypeOfSelf().getName();
            ClassParserCtx classCtx = DefinitionPassVisitor.classes.get(className);
            idSymbol = new InheritanceInspector(classHierarchy).searchAttribute(classCtx, id.getToken().getText());
            if(idSymbol == null) {
                SymbolTable.addError(id.ctx, id.token, "Undefined identifier " +  id.token.getText());
                return null;
            }

            id.setScope(currentScope);
            id.setSymbol(idSymbol);
        }

        return idSymbol.getType();
    }

    public TypeSymbol getTypeOfSelf() {
        CustomToken newToken = new CustomToken("self");
        return  (ClassSymbol) new Id(newToken, null).accept(this);
    }

    public TypeSymbol visitClassVardef(VarDef varDef) {
        var id = varDef.id;
        var type = varDef.type;
        varDef.className = ((ClassSymbol)currentScope).getName();
        // Căutăm tipul variabilei.
        var typeSymbol = currentScope.lookup(type.getToken().getText());

        var lookUpResult = ((ClassSymbol) currentScope).isIdInExtendedScope(id.token.getText());
        if(lookUpResult) {
            SymbolTable.addError(varDef.ctx, varDef.id.token, "Class " +
                    ((ClassSymbol)currentScope).getName() +
                    " redefines inherited attribute " +  varDef.id.token.getText());
            return null;
        }

        if(varDef.throwedErrorInDefinitonVisitor)
            return null;


        // Semnalăm eroare dacă nu există.
        if (typeSymbol == null || (!(typeSymbol instanceof TypeSymbol) && !(typeSymbol instanceof ClassSymbol))) {
            SymbolTable.addError(varDef.ctx, type.token, "Class " +
                    ((ClassSymbol)currentScope).getName() +
                    " has attribute " +  varDef.id.token.getText() + " with undefined type " + type.token.getText());
            return null;
        }
        id.getSymbol().setType((TypeSymbol) typeSymbol);

        if (varDef.initValue != null) {
            var varType  = varDef.id.getSymbol().getType();
            var initType = varDef.initValue.accept(this);

            var idType  = varDef.id.getSymbol().getType();
            var exprType = varDef.initValue.accept(this);

            // TODO 5: Verificăm dacă expresia de inițializare are tipul potrivit
            // cu cel declarat pentru variabilă.

            if (idType == null || exprType == null)
                return null;

            if(exprType.isSelfType)
                exprType = TypeSymbol.SELF_TYPE;

            if(idType.getName().equals(exprType.getName()))
                return idType;

            boolean idTypeIsBasicType = SymbolTable.basicTypes.contains(idType.getName());
            if(idTypeIsBasicType && !idType.getName().equals(exprType.getName())) {
                SymbolTable.addError(varDef.ctx,  varDef.initValue.getToken(), "Type " + exprType.getName() +
                        " of initialization expression of attribute " +
                        varDef.id.getToken().getText() + " is incompatible with declared type " + idType.getName());
                return null;
            }

            if(!idTypeIsBasicType) {
                if(idType.getName().equals("SELF_TYPE") && exprType.getName().equals("self"))
                    return getTypeOfSelf();

                boolean subtypeCheck = true;
                if(idType.getName().equals("SELF_TYPE"))
                    idType = getTypeOfSelf();

                if(subtypeCheck) {
                    ClassParserCtx ancestorClass = DefinitionPassVisitor.classes.get(idType.getName());
                    ClassParserCtx childClass = DefinitionPassVisitor.classes.get(exprType.getName());
                    subtypeCheck = new InheritanceInspector(classHierarchy).isSubtype(ancestorClass, childClass);
                }

                if(!subtypeCheck) {
                    var thirdChild = (CoolParser.ExprContext)varDef.ctx.getChild(2);
                    SymbolTable.addError(varDef.ctx, thirdChild.getStart(), "Type " + exprType.getName() +
                            " of initialization expression of attribute " +
                            varDef.id.getToken().getText() + " is incompatible with declared type " + idType.getName());
                    return null;
                }
            }
        }

        return null;
    }

    public TypeSymbol visitLetVardef(VarDef varDef) {
        var id = varDef.id;
        var type = varDef.type;

        // Căutăm tipul variabilei.
        var typeSymbol = currentScope.lookup(type.getToken().getText());

        // Semnalăm eroare dacă nu există.
        if (typeSymbol == null || (!(typeSymbol instanceof TypeSymbol) && !(typeSymbol instanceof ClassSymbol))) {
            SymbolTable.addError(varDef.ctx, type.token, "Let variable " + id.getSymbol().getName() + " " +
                    "has undefined type " + type.token.getText());
            return null;
        }
        id.getSymbol().setType((TypeSymbol) typeSymbol);
        if (varDef.initValue != null) {
            var idType  = varDef.id.getSymbol().getType();
            var exprType = varDef.initValue.accept(this);

            // TODO 5: Verificăm dacă expresia de inițializare are tipul potrivit
            // cu cel declarat pentru variabilă.

            if (idType == null || exprType == null)
                return null;

            if(idType.getName().equals(exprType.getName()))
                return idType;

            boolean idTypeIsBasicType = SymbolTable.basicTypes.contains(idType.getName());
            if(idTypeIsBasicType && !idType.getName().equals(exprType.getName())) {
                SymbolTable.addError(varDef.ctx,  varDef.initValue.getToken(), "Type " + exprType.getName() +
                        " of initialization expression of identifier " +
                        varDef.id.getToken().getText() + " is incompatible with declared type " + idType.getName());
                return null;
            }

            if(!idTypeIsBasicType) {
                ClassParserCtx ancestorClass = DefinitionPassVisitor.classes.get(idType.getName());
                ClassParserCtx childClass = DefinitionPassVisitor.classes.get(exprType.getName());
                boolean subtypeCheck = new InheritanceInspector(classHierarchy).isSubtype(ancestorClass, childClass);
                if(!subtypeCheck) {
                    var thirdChild = (CoolParser.ExprContext)varDef.ctx.getChild(2);
                    SymbolTable.addError(varDef.ctx, thirdChild.getStart(), "Type " + exprType.getName() +
                            " of initialization expression of identifier " +
                            varDef.id.getToken().getText() + " is incompatible with declared type " + idType.getName());
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    public TypeSymbol visit(VarDef varDef) {
        if(varDef.vardefType.equals("class"))
            return visitClassVardef(varDef);
        if(varDef.vardefType.equals("let"))
            return visitLetVardef(varDef);

        return null;
    }

    boolean exitsType(Symbol typeSymbol) {
        if (typeSymbol == null || (!(typeSymbol instanceof TypeSymbol) && !(typeSymbol instanceof ClassSymbol))) {
            return false;
        }
        return true;
    }

    @Override
    public TypeSymbol visit(FuncDef funcDef) {
        var id   = funcDef.id;
        var type = funcDef.type;

        // Căutăm tipul funcției.
        var typeSymbol = (TypeSymbol) currentScope.lookup(type.getToken().getText());
        var functionSymbol = (FunctionSymbol) id.getScope();

        currentScope = functionSymbol;
        String className = ((ClassSymbol) currentScope.getParent()).getName();
        String methodName = ((FunctionSymbol) currentScope).getName();
        funcDef.className = className;

        // Semnalăm eroare dacă nu există.
        if (!exitsType(typeSymbol)) {
            SymbolTable.addError(funcDef.ctx, type.token, "Method " +
                    functionSymbol.getName() + " of class " + ((ClassSymbol) currentScope).getName() +
                            " has undefined return type " + type.token.getText());
            return null;
        }
        String name = id.token.getText();
        // Reținem informația de tip în cadrul simbolului aferent funcției.
        functionSymbol.setType(typeSymbol);

        for (var formal : funcDef.formals) {
            formal.accept(this);
        }

        if(((ClassSymbol) currentScope.getParent()).isMethodInExtendedScope(funcDef.id.token.getText())) {
            FunctionSymbol overridedMethodSymbol = (FunctionSymbol)
                    ((ClassSymbol) currentScope.getParent()).lookupOnlyParentClassMethod(funcDef.id.token.getText());
            FuncDef overridedMethod = overridedMethodSymbol.getFuncDef();
            if(overridedMethod.formals.size() != funcDef.formals.size()) {
                SymbolTable.addError(funcDef.ctx, funcDef.id.token, "Class " + className + " overrides method "
                        + methodName + " with different number of formal parameters");
                return null;
            }

            for(int i = 0; i < funcDef.formals.size(); i++) {
                String funcDefFormalType = ((Formal)funcDef.formals.get(i)).id.symbol.getType().getName();
                String overrideFormalType = ((Formal)overridedMethod.formals.get(i)).id.symbol.getType().getName();
                if(!funcDefFormalType.equals(overrideFormalType)) {
                    SymbolTable.addError(funcDef.ctx, ((Formal)funcDef.formals.get(i)).ctx.stop,
                            "Class " + className + " overrides method "
                            + methodName + " but changes type of formal parameter "
                            + ((Formal)funcDef.formals.get(i)).id.symbol.getName() + " from " + overrideFormalType
                            + " to " + funcDefFormalType);
                    return null;
                }
            }

            String overridedMethodReturnType = overridedMethod.id.getSymbol().getType().getName();
            String currentMethodReturnType = funcDef.id.getSymbol().getType().getName();
            if(!overridedMethodReturnType.equals(currentMethodReturnType)) {
                SymbolTable.addError(funcDef.ctx,  funcDef.type.token, "Class " + className + " overrides method "
                        + methodName + " but changes return type from " + overridedMethodReturnType + " to " +
                        currentMethodReturnType);
                return null;
            }

        }
        var idType = funcDef.id.getSymbol().getType();
        var exprType = funcDef.body.accept(this);

        currentScope = currentScope.getParent();
        if (exprType == null || idType == null)
            return null;

        if(idType.getName().equals(exprType.getName()))
            return idType;

        boolean idTypeIsBasicType = SymbolTable.basicTypes.contains(idType.getName());
        if(idTypeIsBasicType == false)
            idTypeIsBasicType = SymbolTable.basicTypes.contains(exprType.getName());
        if(idTypeIsBasicType && (!idType.getName().equals(exprType.getName()) && !idType.getName().equals("Object"))) {
            SymbolTable.addError(funcDef.ctx,  funcDef.body.getToken(), "Type " + exprType.getName() + " of " +
                    "the body of method " + funcDef.id.getToken().getText() + " is incompatible with declared return type "
                            + idType.getName());
            return null;
        }

        if(!idTypeIsBasicType) {
            ClassParserCtx ancestorClass = DefinitionPassVisitor.classes.get(idType.getName());
            ClassParserCtx childClass = DefinitionPassVisitor.classes.get(exprType.getName());
            if(ancestorClass == null || childClass == null)
                return null;
            boolean subtypeCheck = new InheritanceInspector(classHierarchy).isSubtype(ancestorClass, childClass);
            if(!subtypeCheck) {
                SymbolTable.addError(funcDef.ctx, funcDef.body.getToken(), "Type " + exprType.getName() + " of " +
                        "the body of method " + funcDef.id.getToken().getText() + " is incompatible with declared return type "
                        + idType.getName());
                return null;
            }
        }

        return null;
    }

    @Override
    public TypeSymbol visit(FunctionCall call) {
        FunctionSymbol functionSymbol = null;
        TypeSymbol returnType = null;
        if(call.methodDispatch != null && call.methodDispatch.type != null) {
            TypeSymbol dispType = call.methodDispatch.dispatcher.accept(this);
            call.dispatcherType = dispType;
            String castName = call.methodDispatch.type.getText();
            ClassSymbol classScope = (ClassSymbol) currentScope.lookup(castName);
            if((functionSymbol = (FunctionSymbol) classScope.lookupMethod(call.id.getToken().getText())) == null) {
                SymbolTable.addError(call.ctx, call.id.getToken(),  "Undefined method " + call.id.getToken().getText() +
                        " in class " + classScope.getName());
                return null;
            }
            call.functionSymbol = functionSymbol;
            var searchedMethodTypeName = functionSymbol.getFuncDef().type.getToken().getText();
            var searchedMethodType = currentScope.lookup(searchedMethodTypeName);

            if(searchedMethodType.getName().equals("SELF_TYPE")) {
                returnType = dispType;
                returnType.isSelfType = true;
            } else {
                returnType = (TypeSymbol) searchedMethodType;
            }
            for(int i = 0; i < call.params.size(); i++) {
                var currentParamCall = call.params.get(i).accept(this);
            }

        } else {
            TypeSymbol callerType = null;
            if(call.methodDispatch == null) {
                CustomToken newToken = new CustomToken("self");
                callerType = new Id(newToken, null).accept(this);
            }else {
                callerType = call.methodDispatch.dispatcher.accept(this);
            }

            call.dispatcherType = callerType;
            if(callerType == null)
                return null;
            ClassParserCtx startingClass = DefinitionPassVisitor.classes.get(callerType.getName());
            if(callerType.getName().equals("SELF_TYPE"))
                startingClass = DefinitionPassVisitor.classes.get(getTypeOfSelf().getName());

            FunctionSymbol searchedMethod =
                    new InheritanceInspector(classHierarchy).searchMethod(startingClass, call.id.getToken().getText());
            call.functionSymbol = searchedMethod;
            if(searchedMethod == null) {
                SymbolTable.addError(call.ctx, call.id.getToken(),"Undefined method " + call.id.getToken().getText() +
                        " in class " + callerType.getName());
                return null;
            }

            if(call.params.size() != searchedMethod.getFuncDef().formals.size()) {
                SymbolTable.addError(call.ctx, call.id.getToken(),  "Method " + call.id.getToken().getText() + " of class " +
                        callerType.getName() + " is applied to wrong number of arguments");
                return null;
            }

            var searchedMethodTypeName = searchedMethod.getFuncDef().type.getToken().getText();
            var searchedMethodType = currentScope.lookup(searchedMethodTypeName);
            if(searchedMethodType.getName().equals("SELF_TYPE")) {
                returnType = callerType;
                returnType.isSelfType = true;
            } else
                returnType = (TypeSymbol) searchedMethodType;
            for(int i = 0; i < call.params.size(); i++) {
                var currentParamCall = call.params.get(i).accept(this);
                var currentParamDef = searchedMethod.getFuncDef().formals.get(i).accept(this);
                Formal formalCtx = (Formal) searchedMethod.getFuncDef().formals.get(i);

                if(currentParamCall.getName().equals(currentParamDef.getName()))
                    continue;

                boolean containsBasicType = SymbolTable.basicTypes.contains(currentParamCall.getName())
                        || SymbolTable.basicTypes.contains(currentParamDef.getName());

                if(containsBasicType) {
                    SymbolTable.addError(call.ctx, call.params.get(i).getToken(),  "In call to method " + call.id.getToken().getText() + " of class " +
                            callerType.getName() + ", actual type "  + currentParamCall.getName() + " of formal parameter "
                            + formalCtx.id.getToken().getText() +  " is incompatible with declared type " + formalCtx.type.getToken().getText());
                } else {
                    ClassParserCtx childClass = DefinitionPassVisitor.classes.get(currentParamCall.getName());
                    if(childClass == null)
                        childClass = DefinitionPassVisitor.classes.get(getTypeOfSelf().getName());
                    ClassParserCtx ancestorClass = DefinitionPassVisitor.classes.get(currentParamDef.getName());
                    boolean subtypeCheck = new InheritanceInspector(classHierarchy).isSubtype(ancestorClass, childClass);
                    if(!subtypeCheck) {
                        SymbolTable.addError(call.ctx, call.params.get(i).getToken(),  "In call to method " + call.id.getToken().getText() + " of class " +
                                callerType.getName() + ", actual type "  + currentParamCall.getName() + " of formal parameter "
                                + formalCtx.id.getToken().getText() +  " is incompatible with declared type " + formalCtx.type.getToken().getText());
                    }
                }
            }
        }

       return returnType;
    }

    @Override
    public TypeSymbol visit(Assign assign) {
        if(assign.id.getToken().getText().equals("self")) {
            SymbolTable.addError(assign.ctx,  assign.id.token, "Cannot assign to self");
            return null;
        }

        var idType   = assign.id.accept(this);
        var exprType = assign.expr.accept(this);

        // TODO 5: Verificăm dacă expresia cu care se realizează atribuirea
        // are tipul potrivit cu cel declarat pentru variabilă.

        if (idType == null || exprType == null)
            return null;

        if(idType.getName().equals(exprType.getName()))
            return idType;

        boolean idTypeIsBasicType = SymbolTable.basicTypes.contains(idType.getName()) ||
                SymbolTable.basicTypes.contains(exprType.getName());
        if(idTypeIsBasicType && !idType.getName().equals(exprType.getName())) {
            SymbolTable.addError(assign.ctx,  assign.expr.getToken(), "Type " + exprType.getName() + " of " +
                    "assigned expression is incompatible with declared type " + idType.getName() + " of identifier " +
                    assign.id.getToken().getText());
            return null;
        }

        if(!idTypeIsBasicType) {
            boolean subtypeCheck = true;
            if(idType.getName().equals("SELF_TYPE") && exprType.getName().equals(getTypeOfSelf().getName())) {
                return getTypeOfSelf();
            }

            if(idType.getName().equals("SELF_TYPE")) {
                subtypeCheck = false;
            }
            if(exprType.getName().equals("self")) {
                exprType = getTypeOfSelf();
                subtypeCheck = false;
            }

            if(subtypeCheck) {
                ClassParserCtx ancestorClass = DefinitionPassVisitor.classes.get(idType.getName());
                ClassParserCtx childClass = DefinitionPassVisitor.classes.get(exprType.getName());
                subtypeCheck = new InheritanceInspector(classHierarchy).isSubtype(ancestorClass, childClass);
            }

            if(!subtypeCheck) {
                var thirdChild = assign.expr.getToken();
                SymbolTable.addError(assign.ctx, thirdChild, "Type " + exprType.getName() + " of " +
                        "assigned expression is incompatible with declared type " + idType.getName() + " of identifier " +
                        assign.id.getToken().getText());
                return null;
            }
        }

        return exprType;
    }

    @Override
    public TypeSymbol visit(If iff) {
        var condType = iff.cond.accept(this);

        if(!condType.getName().equals("Bool")) {
            SymbolTable.addError(iff.ctx, iff.cond.token, "If condition has type " + condType.getName()
                    + " instead of Bool");
        }

        var thenType = iff.thenBranch.accept(this);
        var elseType = iff.elseBranch.accept(this);
        if(elseType != null && thenType != null && elseType.getName().equals(thenType.getName()))
            return elseType;

        if(SymbolTable.basicTypes.contains(thenType.getName()) || SymbolTable.basicTypes.contains(elseType.getName()))
            return TypeSymbol.OBJECT;

        ArrayList<ClassParserCtx> classes = new ArrayList<>();
        if(thenType.getName().equals("self"))
            thenType = getTypeOfSelf();
        if(elseType.getName().equals("self"))
            elseType = getTypeOfSelf();

        ClassParserCtx cl1 = DefinitionPassVisitor.classes.get(thenType.getName());
        ClassParserCtx cl2 = DefinitionPassVisitor.classes.get(elseType.getName());
        if(cl1 == null || cl2 == null)
            return null;

        classes.add(cl1);
        classes.add(cl2);
        String commonAncestor = new InheritanceInspector(classHierarchy).getCommonAncestor(classes);

        return new TypeSymbol(commonAncestor);
    }

    @Override
    public TypeSymbol visit(Paren paren) {
        return null;
    }

    @Override
    public TypeSymbol visit(Type type) {
        return null;
    }

    public TypeSymbol visitFuncFormal(Formal formal) {
        var id = formal.id;
        var type = formal.type;

        // Căutăm tipul variabilei.
        var typeSymbol = currentScope.lookup(type.getToken().getText());

        if(currentScope instanceof ClassSymbol)
            return (TypeSymbol) typeSymbol;

        String className = ((ClassSymbol) currentScope.getParent()).getName();
        String methodName = ((FunctionSymbol) currentScope).getName();

        // Semnalăm eroare dacă nu există.
        if (!exitsType(typeSymbol)) {
            SymbolTable.addError(formal.ctx, formal.ctx.stop, "Method " + methodName + " of class " + className +
                    " has formal parameter " + id.getSymbol().getName() + " with undefined type " + type.token.getText());
            return null;
        }

        if (typeSymbol.getName().equals("SELF_TYPE")) {
            SymbolTable.addError(formal.ctx, formal.ctx.stop, "Method " + methodName + " of class " + className +
                    " has formal parameter " + id.getSymbol().getName() + " with illegal type SELF_TYPE");
            return null;
        }

        // Reținem informația de tip în cadrul simbolului aferent
        // variabilei
        if(id.getSymbol() != null)
            id.getSymbol().setType((TypeSymbol) typeSymbol);

        return (TypeSymbol) typeSymbol;
    }

    public TypeSymbol visitCaseFormal(Formal formal) {

        var id = formal.id;
        var type = formal.type;

        // Căutăm tipul variabilei.
        var typeSymbol = currentScope.lookup(type.getToken().getText());

        // Semnalăm eroare dacă nu există.
        if (!exitsType(typeSymbol)) {
            SymbolTable.addError(formal.ctx, formal.ctx.stop, "Case variable " + id.getSymbol().getName() +
                    " has undefined type " + type.token.getText());
            return null;
        }

        if (typeSymbol.getName().equals("SELF_TYPE")) {
            SymbolTable.addError(formal.ctx, formal.ctx.stop, "Case variable " +
                    id.getSymbol().getName() + " has illegal type SELF_TYPE");
            return null;
        }

        id.getSymbol().setType((TypeSymbol) typeSymbol);
        return (TypeSymbol) typeSymbol;
    }

    public TypeSymbol visitVardefFormal(Formal formal) {
        return null;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        if(formal.formalType.equals("func"))
            return visitFuncFormal(formal);
        if(formal.formalType.equals("case"))
            return visitCaseFormal(formal);
        if(formal.formalType.equals("vardef"))
            return visitFuncFormal(formal);

        return null;
    }

    @Override
    public TypeSymbol visit(TldNode tldNode) {
        var exprType = tldNode.arithmetic_expr.accept(this);

        if (exprType != null && !exprType.getName().equals("Int")) {
            SymbolTable.addError(tldNode.ctx, tldNode.ctx.stop, "Operand of " +
                    tldNode.getToken().getText() + " has type " + exprType.getName() + " instead of Int");
            return null;
        }

        return  exprType;
    }

    @Override
    public TypeSymbol visit(NotNode notNode) {
        var exprType = notNode.expr.accept(this);

        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.

        if (exprType != null && !exprType.getName().equals("Bool")) {
            SymbolTable.addError(notNode.ctx, notNode.ctx.stop, "Operand of " +
                    notNode.getToken().getText() + " has type " + exprType.getName() + " instead of Bool");
            return null;
        }

        return  exprType;
    }

    @Override
    public TypeSymbol visit(IsvoidNode isvoidNode) {
        return isvoidNode.expr.accept(this);
    }

    @Override
    public TypeSymbol visit(InstanceNode instanceNode) {
        var typeSymbol = currentScope.lookup(instanceNode.type.getText());
        instanceNode.selfTypeValue = getTypeOfSelf().getName();
        if(typeSymbol == null) {
            SymbolTable.addError(instanceNode.ctx, instanceNode.type, "new is used with undefined type " +
                    instanceNode.type.getText());
            return null;
        }
        return (TypeSymbol) typeSymbol;
    }

    @Override
    public TypeSymbol visit(WhileNode whileNode) {
        var condType = whileNode.cond.accept(this);
        if(condType == null)
            return null;

        if(!condType.getName().equals(TypeSymbol.BOOL.getName())) {
            SymbolTable.addError(whileNode.ctx, whileNode.cond.getToken(), "While condition has type " +
                    condType.getName() + " instead of Bool");
            return TypeSymbol.OBJECT;
        }

        whileNode.body.accept(this);
        return TypeSymbol.OBJECT;
    }

    @Override
    public TypeSymbol visit(MethodDispatch methodDispatch) {
        if(methodDispatch.type != null) {
            if(methodDispatch.type.getText().equals("SELF_TYPE"))
                return null;
            Symbol classSymbol = currentScope.lookup(methodDispatch.type.getText());
            if (classSymbol == null || !(classSymbol instanceof ClassSymbol)) {
                SymbolTable.addError(methodDispatch.ctx, methodDispatch.type, "Type " +
                        methodDispatch.type.getText() + " of static dispatch is undefined");
                return null;
            }

            var exprType = methodDispatch.type.getText();
            var dispatcherType = methodDispatch.dispatcher.accept(this);
            if(dispatcherType.getName().equals("SELF_TYPE"))
                dispatcherType = getTypeOfSelf();
            ClassParserCtx ancestorClass =  DefinitionPassVisitor.classes.get(exprType);
            ClassParserCtx childClass = DefinitionPassVisitor.classes.get(dispatcherType.getName());
            boolean subtypeCheck = new InheritanceInspector(classHierarchy).isSubtype(ancestorClass, childClass);
            if(!subtypeCheck) {
                SymbolTable.addError(methodDispatch.ctx, methodDispatch.type, "Type " +
                        exprType + " of static dispatch is not a superclass of type " + dispatcherType.getName());
                return null;
            }
        }

        return methodDispatch.methCall.accept(this);
    }

    @Override
    public TypeSymbol visit(LetNode letNode) {
        for(var def : letNode.defs) {
            def.accept(this);
        }
        return letNode.expr.accept(this);
    }

    private TypeSymbol containsBasicType(ArrayList<TypeSymbol> types) {
        TypeSymbol lastType = types.get(0);
        boolean allSameType = true;
        boolean hasBasicType = SymbolTable.basicTypes.contains(types.get(0).getName());
        for(int i = 1; i < types.size(); i++) {
            if (SymbolTable.basicTypes.contains(types.get(i).getName()))
                hasBasicType = true;

            if(!lastType.getName().equals(types.get(i).getName()))
                allSameType = false;

            lastType = types.get(i);
        }

        if(allSameType)
            return lastType;

        if(hasBasicType)
            return TypeSymbol.OBJECT;

        return null;
    }

    @Override
    public TypeSymbol visit(CaseNode caseNode) {
        var condType = caseNode.cond.accept(this);
        ArrayList<TypeSymbol> returnTypes = new ArrayList<>();
        ArrayList<TypeSymbol> nodeTypes = new ArrayList<>();
         for(var node : caseNode.cases) {
            nodeTypes.add(node.getKey().accept(this));
            returnTypes.add(node.getValue().accept(this));
        }
         for(var returnType : returnTypes) {
             if (returnType == null)
                 return null;
         }
        var type = containsBasicType(returnTypes);
        if(type != null)
            return type;

        ArrayList<ClassParserCtx> classes = new ArrayList<>();
        for(var t : returnTypes) {
            classes.add(DefinitionPassVisitor.classes.get(t.getName()));
        }

        String commonAncestor = new InheritanceInspector(classHierarchy).getCommonAncestor(classes);
        return new TypeSymbol(commonAncestor);
    }

    @Override
    public TypeSymbol visit(BlockNode blockNode) {
        for(int i = 0; i < blockNode.exprs.size() - 1; i++) {
            blockNode.exprs.get(i).accept(this);
        }
        return blockNode.exprs.get(blockNode.exprs.size() - 1).accept(this);
    }

    // Operații aritmetice.
    @Override
    public TypeSymbol visit(UnaryMinus uMinus) {
        var exprType = uMinus.expr.accept(this);

        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.

        if (exprType != null && !exprType.getName().equals("Int")) {
            SymbolTable.addError(uMinus.ctx, uMinus.ctx.start, "Operand of " +
                    uMinus.getToken().getText() + " has type " + exprType.getName() + " instead of Int");
            return null;
        }

        return exprType;
    }

    @Override
    public TypeSymbol visit(MultDiv multDiv) {
        var type1 = multDiv.expr1.accept(this);
        if(type1 != null && !type1.getName().equals("Int")) {
            SymbolTable.addError(multDiv.ctx, multDiv.ctx.start, "Operand of " +
                    multDiv.getToken().getText() + " has type " +  type1.getName() + " instead of Int");
            return null;
        }

        var type2 = multDiv.expr2.accept(this);
        if(type2 != null && !type2.getName().equals("Int")) {
            SymbolTable.addError(multDiv.ctx, multDiv.ctx.stop, "Operand of " +
                    multDiv.getToken().getText() + " has type " +  type2.getName() + " instead of Int");
            return null;
        }

        if(type1 != null && type2 != null)
            return TypeSymbol.INT;

        return null;
    }

    @Override
    public TypeSymbol visit(PlusMinus plusMinus) {
        var type1 = plusMinus.expr1.accept(this);
        if(type1 != null && !type1.getName().equals("Int")) {
            SymbolTable.addError(plusMinus.ctx, plusMinus.ctx.start, "Operand of " +
                    plusMinus.getToken().getText() + " has type " +  type1.getName() + " instead of Int");
            return null;
        }

        var type2 = plusMinus.expr2.accept(this);
        if(type2 != null && !type2.getName().equals("Int")) {
            SymbolTable.addError(plusMinus.ctx, plusMinus.ctx.stop, "Operand of " +
                    plusMinus.getToken().getText() + " has type " +  type2.getName() + " instead of Int");
            return null;
        }

        if(type1 != null && type2 != null)
            return TypeSymbol.INT;
        return null;
    }

    TypeSymbol checkBinaryOpTypes(Token token, Expression e1, Expression e2) {
        var type1 = e1.accept(this);
        var type2 = e2.accept(this);

        if (type1 == null || type2 == null)
            return null;

        if (type1 == TypeSymbol.INT && type2 == TypeSymbol.INT)
            return type1;

        // CPLANGPARSEREQUAL
        if (token.getType() == '=' &&
                type1 == TypeSymbol.BOOL && type2 == TypeSymbol.BOOL)
            return type1;

        ASTVisitor.error(token,
                "Operands of " + token.getText() + " have incompatible types");

        return null;
    }

    public TypeSymbol visitEqualExpr(RelationalExpr relExpr) {
        var type1 = relExpr.expr1.accept(this);
        if(type1 == null)
            return null;

        var type2 = relExpr.expr2.accept(this);
        if(type2 == null)
            return null;

        boolean basicTypePresent = (SymbolTable.basicTypes.contains(type1.getName())) ||
                                   (SymbolTable.basicTypes.contains(type2.getName()));
        if(!type1.getName().equals(type2.getName()) && basicTypePresent) {
            SymbolTable.addError(relExpr.ctx, relExpr.getToken(), "Cannot compare " +
                    type1.getName() + " with " +  type2.getName());
        }
        return TypeSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(RelationalExpr relExpr) {
        if(relExpr.getToken().getText().equals("="))
            return visitEqualExpr(relExpr);

        var type1 = relExpr.expr1.accept(this);
        if(type1 != null && !type1.getName().equals("Int")) {
            SymbolTable.addError(relExpr.ctx, relExpr.ctx.stop, "Operand of " +
                    relExpr.getToken().getText() + " has type " +  type1.getName() + " instead of Int");
            return null;
        }

        var type2 = relExpr.expr2.accept(this);
        if(type2 != null && !type2.getName().equals("Int")) {
            SymbolTable.addError(relExpr.ctx, relExpr.ctx.stop, "Operand of " +
                    relExpr.getToken().getText() + " has type " +  type2.getName() + " instead of Int");
            return null;
        }

        if(type1 != null && type2 != null)
            return TypeSymbol.BOOL;
        return null;
    }


    // Tipurile de bază
    @Override
    public TypeSymbol visit(Int intt) {
        return TypeSymbol.INT;
    }

    @Override
    public TypeSymbol visit(Float float1) {
        return TypeSymbol.FLOAT;
    }

    @Override
    public TypeSymbol visit(Bool bool) {
        return TypeSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(Str str) {
        if(str.getToken().getText().equals("true") || str.getToken().getText().equals("false"))
            return TypeSymbol.BOOL;

        return TypeSymbol.STRING;
    }
};