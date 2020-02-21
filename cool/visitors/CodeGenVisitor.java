package cool.visitors;

import cool.compiler.Compiler;
import cool.nodes.Float;
import cool.structures.*;
import cool.utils.ClassParserCtx;
import cool.utils.GenericPair;
import cool.utils.InheritanceInspector;
import cool.utils.Pair;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import cool.nodes.*;

import java.util.*;


public class CodeGenVisitor implements ASTVisitor<ST>{
    DefaultScope globals = (DefaultScope) SymbolTable.globals;
    static STGroupFile templates = new STGroupFile("cool/visitors/cgen.stg");
    HashMap<String, GenericPair<Integer, Integer>> tags;
    HashMap<String, ClassParserCtx> classes = DefinitionPassVisitor.classes;
    LinkedHashMap<ClassParserCtx, ClassParserCtx> classHierarchy = ClassHierarchyVisitor.classHierarchy;

    LinkedHashMap<Integer, String> declaredInts = new LinkedHashMap<>();
    int numberDeclaredInts = 0;
    LinkedHashMap<String, String> declaredStrings = new LinkedHashMap<>();
    int numberDeclaredStrings = 0;

    int dispatched = 0;
    ST programST;

    ST declaredStringsST;
    ST declaredIntsST;
    ST declaredBoolST;
    ST classNameTable;
    ST classObjTable;
    ST classProtObj;
    ST dispTables;
    ST objInit;
    ST mainSection;

    static int count = 0;

    String defaultString = "str_const0";
    String defaultBool = "bool_const0";
    String defaultInt = "int_const0";
    int ifCount = 0;
    int isVoidCount = 0;
    int notCount = 0;
    int equalCount = 0;
    int compareCount = 0;
    int whileCount = 0;
    int caseCount = 0;
    int caseBranchCount = 0;

    @Override
    public ST visit(Int val) {
        String s = val.getToken().getText();
        String intLiteral = getIntLiteral(Integer.parseInt(s));
        ST loadInt = templates.getInstanceOf("sequence");
        loadInt.add("e", "   la      $a0 " + intLiteral);
        return loadInt;
    }

    @Override
    public ST visit(Float float1) {
        return null;
    }

    @Override
    public ST visit(Bool val) {
        String s = val.getToken().getText();
        String boolLiteral = "bool_const0";
        if(s.equals("true"))
            boolLiteral = "bool_const1";

        ST loadBool = templates.getInstanceOf("sequence");
        loadBool.add("e", "   la      $a0 " + boolLiteral);
        return loadBool;
    }

    @Override
    public ST visit(UnaryMinus uMinus) {
        var st = templates.getInstanceOf("uMinus");
        st.add("e1", uMinus.expr.accept(this)).add("dStr", uMinus.debugStr);
        return st;
    }

    @Override
    public ST visit(FunctionCall fCall) {
        ST methodDispatchST = templates.getInstanceOf("methodDispatch");
        String[] paths = Compiler.glFileName.split("/");
        String fileName = paths[paths.length - 1];
        String fileNameLiteral = getStrLiteral(fileName);
        for(int i = fCall.params.size() - 1; i >= 0; i--) {
            ST paramST = fCall.params.get(i).accept(this);
            ST stackSave = templates.getInstanceOf("saveOnStack");
            stackSave.add("loadedVar", paramST);
            methodDispatchST.add("args", stackSave);
        }

        if(fCall.methodDispatch != null)
            methodDispatchST.add("previousDispatch", fCall.methodDispatch.dispatcher.accept(this));
        else
            methodDispatchST.add("previousDispatch", "   move    $a0 $s0");

        int line = fCall.start.getLine();
        String dispatchedLabel = "distpatch" + dispatched++;

        methodDispatchST.add("instr", "   bnez    $a0 " + dispatchedLabel);
        methodDispatchST.add("instr", "   la      $a0 " + fileNameLiteral);
        methodDispatchST.add("instr", "   li      $t1 " + line);
        methodDispatchST.add("dispatchLabel", dispatchedLabel);

        if(fCall.methodDispatch == null || fCall.methodDispatch.type == null)
            methodDispatchST.add("dispatchTable", "   lw      $t1 8($a0)");
        else
            methodDispatchST.add("dispatchTable", "   la      $t1 " + fCall.methodDispatch.type.getText()
                    + "_dispTab");

        methodDispatchST.add("offset", fCall.functionSymbol.offset);
        return methodDispatchST;
    }

    public ST visitPlus(PlusMinus expr) {
        var st = templates.getInstanceOf("arithmeticExpr");
        st.add("e1", expr.expr1.accept(this))
                .add("e2",  expr.expr2.accept(this))
                .add("expr", "   add     $t1 $t1 $t2");

        return st;
    }

    public ST visitMinus(PlusMinus expr) {
        var st = templates.getInstanceOf("arithmeticExpr");
        st.add("e1", expr.expr1.accept(this))
                .add("e2",  expr.expr2.accept(this))
                .add("expr", "   sub     $t1 $t1 $t2");

        return st;
    }

    @Override
    public ST visit(PlusMinus expr) {
        if(expr.getToken().getText().equals("+"))
            return visitPlus(expr);
        else
            return visitMinus(expr);
    }

    @Override
    public ST visit(Paren paren) {
        return paren.expr.accept(this);
    }

    public ST visitMult(MultDiv expr) {
        var st = templates.getInstanceOf("arithmeticExpr");
        st.add("e1", expr.expr1.accept(this))
                .add("e2",  expr.expr2.accept(this))
                .add("expr", "   mul    $t1 $t1 $t2");

        return st;
    }

    public ST visitDiv(MultDiv expr) {
        var st = templates.getInstanceOf("arithmeticExpr");
        st.add("e1", expr.expr1.accept(this))
                .add("e2",  expr.expr2.accept(this))
                .add("expr", "   div    $t1 $t1 $t2");

        return st;
    }

    @Override
    public ST visit(MultDiv expr) {
        if(expr.getToken().getText().equals("*")) {
            return visitMult(expr);
        } else {
            return visitDiv(expr);
        }
    }

    public ST visitLower(RelationalExpr expr) {
        var st = templates.getInstanceOf("compare");
        st.add("e1", expr.expr1.accept(this))
                .add("e2",  expr.expr2.accept(this))
                .add("unique", compareCount++)
                .add("jump", "blt");

        return st;
    }

    public ST visitLowerEqual(RelationalExpr expr) {
        var st = templates.getInstanceOf("compare");
        st.add("e1", expr.expr1.accept(this))
                .add("e2",  expr.expr2.accept(this))
                .add("unique", compareCount++)
                .add("jump", "ble");

        return st;
    }

    public ST visitEqual(RelationalExpr expr) {
        var st = templates.getInstanceOf("equal");
        st.add("e1", expr.expr1.accept(this))
                .add("e2",  expr.expr2.accept(this))
                .add("unique", equalCount++);

        return st;
    }

    @Override
    public ST visit(RelationalExpr expr) {
        if(expr.getToken().getText().equals("<"))
            return visitLower(expr);
        else if(expr.getToken().getText().equals("<="))
            return visitLowerEqual(expr);
        else
            return visitEqual(expr);
    }

    @Override
    public ST visit(If iff) {
        var st = templates.getInstanceOf("if");
        st.add("cond", iff.cond.accept(this))
                .add("b1",  iff.thenBranch.accept(this))
                .add("b2",  iff.elseBranch.accept(this))
                .add("unique", ifCount++);

        return st;
    }

    @Override
    public ST visit(Assign assign) {
        ST assignST = templates.getInstanceOf("assign");
        ST assginedExpr = assign.expr.accept(this);
        ST storeST = templates.getInstanceOf("sequence");
        assignST.add("assignedExpr", assginedExpr);
        
        int offset = ((Id) assign.id).getSymbol().offset + 12;
        if(((Id) assign.id).getSymbol().idType.equals("attribute"))
            storeST.add("e", "sw      $a0 " + offset + "($s0)");
        else if (((Id) assign.id).getSymbol().idType.equals("local"))
            storeST.add("e", "sw      $a0 " + ((Id) assign.id).getSymbol().offset + "($fp)");
        else
            storeST.add("e", "sw      $a0 " + offset + "($fp)");
        assignST.add("store", storeST);

        return assignST;
    }

    public ST visitClassVardef(VarDef varDef) {
        if(varDef.initValue == null)
            return null;

        ST attrST = templates.getInstanceOf("sequence");
        String attrType = varDef.type.getToken().getText();

        ST initValueST = varDef.initValue.accept(this);
        if(SymbolTable.basicTypes.contains(attrType)) {
            initValueST.add("e", "sw      $a0 " + varDef.offset + "($s0)");
        } else {
            initValueST.add("e", "sw      $a0 " + varDef.offset + "($s0)");
        }
        return initValueST;
    }

    public ST visitLetVardef(VarDef varDef) {
        ST resultST = templates.getInstanceOf("sequence");
        String type = varDef.id.getSymbol().getType().getName();

        if(varDef.initValue == null) {
            if(type.equals("String")) {
                resultST.add("e", "   la      $a0 " + defaultString);
            } else if(type.equals("Int")) {
                resultST.add("e", "   la      $a0 " + defaultInt);
            } else if (type.equals("Bool")) {
                resultST.add("e", "   la      $a0 " + defaultBool);
            } else {
                resultST.add("e", "   move      $a0 $zero");
            }
        } else {
            return varDef.initValue.accept(this);
        }

        return resultST;
    }

    @Override
    public ST visit(VarDef varDef) {
        if(varDef.vardefType.equals("class")) {
            return visitClassVardef(varDef);
        } else {
            return visitLetVardef(varDef);
        }
    }

    @Override
    public ST visit(FuncDef funcDef) {
        ST funcDefST = templates.getInstanceOf("funcDef");
        funcDefST.add("name", funcDef.className + "." + funcDef.id.getToken().getText());
        ST bodyST = funcDef.body.accept(this);
        funcDefST.add("body", bodyST);
        funcDefST.add("free", "addiu   $sp $sp " + funcDef.formals.size() * 4);
        mainSection.add("e", funcDefST);
        return null;
    }

    @Override
    public ST visit(Id id) {
        ST seq = templates.getInstanceOf("sequence");
        if(id.getToken().getText().equals("self"))
            seq.add("e", "move    $a0 $s0");
        else {
            if(id.getSymbol().idType.equals("attribute"))
                seq.add("e", "   lw      $a0 " + (id.getSymbol().offset + 12) + "($s0)");
            else if(id.getSymbol().idType.equals("parameter"))
                seq.add("e", "   lw      $a0 " + (id.getSymbol().offset + 12) + "($fp)");
            else if(id.getSymbol().idType.equals("local"))
                seq.add("e", "   lw      $a0 " + (id.getSymbol().offset) + "($fp) # aici");
            else
                seq.add("e", "   lw      $a0 " + (id.getSymbol().offset + 12) + "($s0)");
        }
        return seq;
    }

    public ST visitCaseFormal(Formal formal) {
        ST branchST = templates.getInstanceOf("branchcase");
        var node = tags.get(formal.type.getToken().getText());
        branchST.add("min", node.getKey());
        branchST.add("max", node.getValue());
        return branchST;
    }

    @Override
    public ST visit(Formal formal) {
        if(formal.formalType.equals("case"))
            return visitCaseFormal(formal);

        return null;
    }

    @Override
    public ST visit(Str str) {
        String s = str.getToken().getText();
        String strLiteral = getStrLiteral(s);
        ST loadStr = templates.getInstanceOf("sequence");
        loadStr.add("e", "   la      $a0 " + strLiteral);
        return loadStr;
    }

    @Override
    public ST visit(TldNode tldNode) {
        ST tldNodeST = templates.getInstanceOf("neg");
        tldNodeST.add("load", tldNode.arithmetic_expr.accept(this));
        return tldNodeST;
    }

    @Override
    public ST visit(NotNode notNode) {
        ST notST = templates.getInstanceOf("not");
        notST.add("loads", notNode.expr.accept(this));
        notST.add("unique", notCount++);
        return notST;
    }

    @Override
    public ST visit(IsvoidNode isvoidNode) {
        ST isvoidST = templates.getInstanceOf("isVoid");
        isvoidST.add("loads", isvoidNode.expr.accept(this));
        isvoidST.add("unique", isVoidCount++);
        return isvoidST;
    }

    @Override
    public ST visit(InstanceNode instanceNode) {
        ST newInstanceST = templates.getInstanceOf("sequence");
        String type = instanceNode.type.getText();
        if(type.equals("SELF_TYPE")) {
            newInstanceST = templates.getInstanceOf("newSelfType");
            return newInstanceST;
        }

        newInstanceST.add("e", "   la      $a0 " + type + "_protObj");
        ClassParserCtx parentCtx = classHierarchy.get(classes.get(type));
        newInstanceST.add("e", "   jal     Object.copy");
        if(parentCtx != null)
            newInstanceST.add("e", "   jal     " + type + "_init");
        else
            newInstanceST.add("e", "   jal     " + "Object" + "_init");

        return newInstanceST;
    }

    @Override
    public ST visit(WhileNode whileNode) {
        ST whileST = templates.getInstanceOf("while");
        whileST.add("cond", whileNode.cond.accept(this));
        whileST.add("body", whileNode.body.accept(this));
        whileST.add("unique", whileCount++);
        return whileST;
    }

    @Override
    public ST visit(MethodDispatch methodDispatch) {
        return methodDispatch.methCall.accept(this);
    }

    @Override
    public ST visit(LetNode letNode) {
        ST letST = templates.getInstanceOf("let");
        ST initST = templates.getInstanceOf("sequence");
        ST bodyST = templates.getInstanceOf("sequence");
        ST freeST = templates.getInstanceOf("sequence");

        initST.add("e", "   addiu   $sp $sp -" + letNode.defs.size() * 4);
        int offset = -4;
        for(var def : letNode.defs) {
            initST.add("e", def.accept(this));
            initST.add("e", "   sw      $a0 " + offset + "($fp)");
            offset -= 4;
        }
        bodyST.add("e", letNode.expr.accept(this));

        freeST.add("e", "addiu   $sp $sp " + letNode.defs.size() * 4);
        letST.add("init", initST);
        letST.add("body", bodyST);
        letST.add("free", freeST);

        return letST;
    }

    @Override
    public ST visit(CaseNode caseNode) {
        ST caseST = templates.getInstanceOf("case");
        String[] paths = Compiler.glFileName.split("/");
        String fileName = paths[paths.length - 1];
        String fileNameLiteral = getStrLiteral(fileName);

        caseST.add("file_name", fileNameLiteral);
        caseST.add("line", caseNode.start.getLine());
        caseST.add("cond", caseNode.cond.accept(this));
        caseST.add("unique", caseCount);

        caseNode.cases.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair pair, Pair t1) {
                Formal f1 = (Formal) pair.getKey();
                Formal f2 = (Formal) t1.getKey();
                var ctx1 = tags.get(f1.type.getToken().getText());
                var ctx2 = tags.get(f2.type.getToken().getText());
                if( (ctx1.getValue() - ctx1.getKey()) == (ctx2.getValue() - ctx2.getKey())) {
                    return (-1) * Integer.compare(ctx1.getKey(), ctx2.getKey());
                } else {
                    return Integer.compare(ctx1.getValue() - ctx1.getKey(), ctx2.getValue() - ctx2.getKey());
                }
            }
        });

        var condSymbol = ((Id) caseNode.cond).symbol;
        for(var cs : caseNode.cases) {
            ((Formal)cs.getKey()).id.getSymbol().idType = condSymbol.idType;
            ((Formal)cs.getKey()).id.getSymbol().offset = condSymbol.offset;
            ST caseBranchST = cs.getKey().accept(this);
            caseBranchST.add("expr", cs.getValue().accept(this));
            caseBranchST.add("unique", caseCount);
            caseBranchST.add("nr", caseBranchCount++);
            caseST.add("branches", caseBranchST);
        }

        caseCount++;
        return caseST;
    }

    @Override
    public ST visit(BlockNode blockNode) {
        ST blockNodeST = templates.getInstanceOf("blockNode");
        for(var instr : blockNode.exprs) {
            blockNodeST.add("instr", instr.accept(this));
        }
        return blockNodeST;
    }

    @Override
    public ST visit(Type type) {
        return null;
    }

    public void createDistTable(Map.Entry<String, GenericPair<Integer, Integer>> entry) {
        ClassParserCtx currentCtx = classes.get(entry.getKey());
        var dispTable = templates.getInstanceOf("table");
        dispTable.add("name", entry.getKey() + "_dispTab");
        for(var methodName : currentCtx.getMethodsName()){
            dispTable.add("entry", methodName);
        }

        dispTables.add("e", dispTable);
    }

    public void createDispTablesForDefaults() {
        var entrySet = new ArrayList<>(tags.entrySet());
        for(int i = 0; i < 2; i++) {
            var entry = entrySet.get(i);
            createDistTable(entry);
        }
    }

    public void createDispTablesForBasics() {
        var entrySet = new ArrayList<>(tags.entrySet());
        for(int i = entrySet.size() - 3; i < entrySet.size(); i++) {
            var entry = entrySet.get(i);
            createDistTable(entry);
        }
    }

    private void resolveBasicType(String className) {
        ClassParserCtx ctx = classes.get(className);
        ClassParserCtx parentCtx = classHierarchy.get(ctx);
        ClassSymbol classSymbol = ctx.classSymbol;
        int size = 3;

        //saving the name
        String savedStrLiteral = getStrLiteral(className);

        //adding literal to class_nameTab
        classNameTable.add("entry", savedStrLiteral);

        //adding to class_objTab
        classObjTable.add("entry", className + "_protObj");
        classObjTable.add("entry", className + "_init");

        ST initMethod = templates.getInstanceOf("initMethod");
        initMethod.add("name", className);
        if(parentCtx != null)
            initMethod.add("parentInit", "jal     " + parentCtx.getClassName() + "_init");

        objInit.add("e", initMethod);
    }

    private void resolveBasicTypes() {
        createDispTablesForDefaults();
        var entrySet = new ArrayList<>(tags.entrySet());
        for(int i = 0; i < 2; i++) {
            var entry = entrySet.get(i);
            resolveBasicType(entry.getKey());
        }
    }

    private void resolveBasicTypes2() {
        createDispTablesForBasics();
        var entrySet = new ArrayList<>(tags.entrySet());
        for(int i = entrySet.size() - 3; i < entrySet.size(); i++) {
            var entry = entrySet.get(i);
            resolveBasicType(entry.getKey());
        }
    }

    private String getIntLiteral(Integer intLiteral) {
        if(declaredInts.containsKey(intLiteral))
            return declaredInts.get(intLiteral);

        String label = "int_const" + numberDeclaredInts;
        ST className = templates.getInstanceOf("int");
        className.add("label", label);
        className.add("field", tags.get("Int").getValue());
        className.add("field", 4);
        className.add("field", "Int_dispTab");
        className.add("field", intLiteral);
        declaredInts.put(intLiteral, label);
        declaredIntsST.add("e", className);
        numberDeclaredInts++;
        return label;
    }

    private String getStrLiteral(String name) {
        if(declaredStrings.containsKey(name))
            return declaredStrings.get(name);

        String label = "str_const" + numberDeclaredStrings;
        ST className = templates.getInstanceOf("string");
        int size = 4 + (name.length() + 1) / 4
                + ((name.length() + 1) % 4 == 0 ? 0 : 1);

        className.add("label", label );
        className.add("field", tags.get("String").getKey());
        className.add("field", size);
        className.add("str", "\"" + name.replace("\t", "\\t")
                                                     .replace("\\", "\\\\")
                                                     .replace("\n", "\\n")
                                                      + "\"");
        String intLabel = getIntLiteral(name.length());
        className.add("len", intLabel);

        declaredStrings.put(name, label);
        declaredStringsST.add("e", className);
        numberDeclaredStrings++;
        return label;
    }

    @Override
    public ST visit(Program program) {
        ClassParserCtx objCtx = DefinitionPassVisitor.classes.get("Object");
        tags = new InheritanceInspector(classHierarchy).addTags(objCtx, 0);

        declaredStringsST = templates.getInstanceOf("sequence");
        declaredIntsST    = templates.getInstanceOf("sequence");
        declaredBoolST    = templates.getInstanceOf("sequence");
        classNameTable    = templates.getInstanceOf("table");
        classObjTable     = templates.getInstanceOf("table");
        classProtObj      = templates.getInstanceOf("sequence");
        dispTables        = templates.getInstanceOf("sequence");
        objInit           = templates.getInstanceOf("sequence");
        mainSection       = templates.getInstanceOf("sequence");
        programST         = templates.getInstanceOf("program");

        classNameTable.add("name", "class_nameTab");
        classObjTable.add("name", "class_objTab");

        programST.add("intTag", tags.get("Int").getKey());
        programST.add("stringTag", tags.get("String").getKey());
        programST.add("boolTag", tags.get("Bool").getKey());

        // empty string
        getStrLiteral("");

        {
            String boolConst = "bool_const";
            ST bool1 = templates.getInstanceOf("bool");
            bool1.add("label", boolConst + 0);
            bool1.add("tag", tags.get("Bool").getKey());
            bool1.add("val", 0);
            declaredBoolST.add("e", bool1);

            ST bool2 = templates.getInstanceOf("bool");
            bool2.add("label", boolConst + 1);
            bool2.add("tag", tags.get("Bool").getKey());
            bool2.add("val", 1);
            declaredBoolST.add("e", bool2);
        }

        var basicTypesProtoObj = templates.getInstanceOf("basicInheritableTypesProtoObj");
        classProtObj.add("e", basicTypesProtoObj);

        resolveBasicTypes();
        ArrayList<ASTNode> classes = new ArrayList<>(program.classes);
        classes.sort(new Comparator<ASTNode>() {
            @Override
            public int compare(ASTNode astNode, ASTNode t1) {
                Clas cl1 = (Clas) astNode;
                Clas cl2 = (Clas) t1;
                int tag1 = tags.get(cl1.name.getText()).getKey();
                int tag2 = tags.get(cl2.name.getText()).getKey();
                return Integer.compare(tag1, tag2);
            }
        });

        for (ASTNode cl : classes)
            cl.accept(this);
        resolveBasicTypes2();
        basicTypesProtoObj = templates.getInstanceOf("basicNonInheritableTypesProtoObj");
        basicTypesProtoObj.add("intTag", tags.get("Int").getKey());
        basicTypesProtoObj.add("stringTag", tags.get("String").getKey());
        basicTypesProtoObj.add("boolTag", tags.get("Bool").getKey());
        classProtObj.add("e", basicTypesProtoObj);

        programST.add("data", declaredStringsST);
        programST.add("data", declaredIntsST);
        programST.add("data", declaredBoolST);
        programST.add("data", classNameTable);
        programST.add("data", classObjTable);
        programST.add("data", classProtObj);
        programST.add("data", dispTables);
        programST.add("objInit", objInit);
        programST.add("mainSection", mainSection);

        return programST;
    }

    @Override
    public ST visit(Clas clas) {
        ClassParserCtx ctx = classes.get(clas.name.getText());
        ClassParserCtx parentCtx = classHierarchy.get(ctx);
        String className = clas.name.getText();

        //saving the name
        String savedStrLiteral = getStrLiteral(className);

        //adding literal to class_nameTab
        classNameTable.add("entry", savedStrLiteral);

        //adding to class_objTab
        classObjTable.add("entry", className + "_protObj");
        classObjTable.add("entry", className + "_init");

        int size = 3 + ctx.getAttributesName().size();
        //creating the protObj
        var protObj = templates.getInstanceOf("prototypeObj");
        protObj.add("name", className);
        protObj.add("field", tags.get(className).getKey());
        protObj.add("field", size);
        for(var def : ctx.getAttributesName()) {
            protObj.add("attributes", def);
        }
        classProtObj.add("e", protObj);

        //addind to disp tables
        var dispTable = templates.getInstanceOf("table");
        dispTable.add("name", className + "_dispTab");
        for(var methodName : ctx.getMethodsName()){
            dispTable.add("entry", methodName);
        }
        dispTables.add("e", dispTable);

        // create init method
        ST initMethod = templates.getInstanceOf("initMethod");
        initMethod.add("name", className);
        if(parentCtx != null)
            initMethod.add("parentInit", "jal     " + parentCtx.getClassName() + "_init");

        int offset = 12;
        if(parentCtx != null)
            offset = parentCtx.highestOffset;

        for(var def : clas.defs) {
            if(def instanceof VarDef) {
                ((VarDef) def).offset = offset;
                offset += 4;
            }

            ST attrST = def.accept(this);
            if(attrST != null)
                initMethod.add("attributes", attrST);
        }

        ctx.highestOffset = offset;
        objInit.add("e", initMethod);
        return null;
    }
}