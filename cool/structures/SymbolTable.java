package cool.structures;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

import cool.nodes.*;
import cool.utils.CustomToken;
import cool.utils.GenericPair;
import cool.utils.SemanticError;
import org.antlr.v4.runtime.*;

import cool.compiler.Compiler;
import cool.parser.CoolParser;

public class SymbolTable {
    public static Scope globals;
    public static ArrayList<SemanticError> errors = new ArrayList<>();
    private static boolean semanticErrors;
    public static TreeSet<String> basicTypes = new TreeSet<>();
    public static void defineBasicClasses() {
        globals = new DefaultScope(null);
        semanticErrors = false;

        globals.add("Float", TypeSymbol.FLOAT);
        globals.add("SELF_TYPE", TypeSymbol.SELF_TYPE);

        createIOClass();
        createObjectClass();
        createStringClass();
        createIntClass();
        createBoolClass();

        basicTypes.add("Int");
        basicTypes.add("String");
        basicTypes.add("Bool");
    }

    public static FuncDef createFuncDef(String id, String type, ArrayList<GenericPair<String, String>> input_formals) {
        CustomToken idToken = new CustomToken(id);
        CustomToken typeToken = new CustomToken(type);
        LinkedList<Definition> formals = new LinkedList<>();
        if(input_formals == null)
            return new FuncDef(formals, null, new Id(idToken, null),
                    new Type(typeToken), null, null);

        for(var pair : input_formals) {
            CustomToken formalIdToken = new CustomToken(pair.getKey());
            CustomToken formalTypeToken = new CustomToken(pair.getValue());
            formals.add(new Formal(new Id(formalIdToken, null), new Type(formalTypeToken),
                    null, "func", null));
        }

        return new FuncDef(formals, null, new Id(idToken, null), new Type(typeToken), null, null);
    }

    public static void createIntClass() {
        ClassSymbol objectClass = new ClassSymbol(globals, null, "Int");
        globals.add("Int", objectClass);
    }

    public static void createBoolClass() {
        ClassSymbol objectClass = new ClassSymbol(globals, null, "Bool");
        globals.add("Bool", objectClass);
    }

    public static void createObjectClass() {
        ClassSymbol objectClass = new ClassSymbol(globals, null, "Object");

        FunctionSymbol abortMethod = new FunctionSymbol(objectClass, "abort", null);
        abortMethod.setType(objectClass);
        abortMethod.setFuncDef(createFuncDef("abort", "Object", null));

        FunctionSymbol type_nameMethod = new FunctionSymbol(objectClass, "type_name", null);
        type_nameMethod.setType(TypeSymbol.STRING);
        type_nameMethod.setFuncDef(createFuncDef("type_name", "String", null));

        FunctionSymbol copyMethod = new FunctionSymbol(objectClass, "copy", null);
        copyMethod.setType(TypeSymbol.SELF_TYPE);
        copyMethod.setFuncDef(createFuncDef("copy", "SELF_TYPE", null));

        objectClass.symbols.put(abortMethod.getName() + "_method", abortMethod);
        objectClass.symbols.put(type_nameMethod.getName() + "_method", type_nameMethod);
        objectClass.symbols.put(copyMethod.getName() + "_method", copyMethod);

        globals.add("Object", objectClass);
    }

    public static void createIOClass() {
        ClassSymbol ioClass = new ClassSymbol(globals, null, "IO");

        FunctionSymbol out_string = new FunctionSymbol(ioClass, "out_string", null);
        out_string.setType(TypeSymbol.SELF_TYPE);
        ArrayList<GenericPair<String, String>> formals = new ArrayList<>();
        formals.add(new GenericPair<String, String>("x", "String"));
        IdSymbol out_stringSymbol =  new IdSymbol("x");
        out_stringSymbol.setType(TypeSymbol.SELF_TYPE);
        out_string.add("x", out_stringSymbol);
        out_string.setFuncDef(createFuncDef("out_string", "SELF_TYPE", formals));

        FunctionSymbol out_int = new FunctionSymbol(ioClass, "out_int", null);
        out_int.setType(TypeSymbol.SELF_TYPE);
        formals = new ArrayList<GenericPair<String, String>>();
        formals.add(new GenericPair<String, String>("x", "Int"));
        IdSymbol out_intSymbol = new IdSymbol("x");
        out_intSymbol.setType(TypeSymbol.SELF_TYPE);
        out_int.add("x", out_intSymbol);
        out_int.setFuncDef(createFuncDef("out_int", "SELF_TYPE", formals));

        FunctionSymbol in_string = new FunctionSymbol(ioClass, "in_string", null);
        in_string.setType(TypeSymbol.STRING);
        in_string.setFuncDef(createFuncDef("in_string", "String", null));

        FunctionSymbol in_int = new FunctionSymbol(ioClass, "in_int", null);
        in_int.setType(TypeSymbol.INT);
        in_int.setFuncDef(createFuncDef("in_int", "Int", null));

        ioClass.symbols.put(out_string.getName() + "_method", out_string);
        ioClass.symbols.put(out_int.getName() + "_method", out_int);
        ioClass.symbols.put(in_string.getName() + "_method", in_string);
        ioClass.symbols.put(in_int.getName() + "_method", in_int);

        globals.add("IO", ioClass);
    }

    public static void createStringClass() {
        ClassSymbol stringClass = new ClassSymbol(globals, null, "String");
        FunctionSymbol length = new FunctionSymbol(stringClass, "length", null);
        length.setType(TypeSymbol.INT);
        length.setFuncDef(createFuncDef("length", "Int", null));

        FunctionSymbol concat = new FunctionSymbol(stringClass, "concat", null);
        concat.setType(TypeSymbol.STRING);
        var formals = new ArrayList<GenericPair<String, String>>();
        formals.add(new GenericPair<String, String>("s", "String"));
        IdSymbol concatIdSymbol = new IdSymbol("s");
        concatIdSymbol.setType(TypeSymbol.STRING);
        concat.add("s", concatIdSymbol);
        concat.setFuncDef(createFuncDef("concat", "String", formals));

        FunctionSymbol substr = new FunctionSymbol(stringClass, "substr", null);
        substr.setType(TypeSymbol.STRING);
        formals = new ArrayList<GenericPair<String, String>>();
        formals.add(new GenericPair<String, String>("i", "Int"));
        formals.add(new GenericPair<String, String>("l", "Int"));
        substr.add("i", new IdSymbol("i"));
        substr.add("l", new IdSymbol("l"));
        substr.setFuncDef(createFuncDef("concat", "String", formals));

        stringClass.symbols.put(length.getName() + "_method", length);
        stringClass.symbols.put(concat.getName() + "_method", concat);
        stringClass.symbols.put(substr.getName() + "_method", substr);

        globals.add("String", stringClass);
    }


    /**
     * Displays a semantic error message.
     *
     * @param ctx Used to determine the enclosing class context of this error,
     *            which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void addError(ParserRuleContext ctx, Token info, String str) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();

        errors.add(new SemanticError(str, ctx, info));
        semanticErrors = true;
    }

    public static void printErrors() {
//        errors.sort((semanticError, t1) -> {
//            if(semanticError.getToken().getLine() == t1.getToken().getLine())
//                return Integer.compare(semanticError.getToken().getCharPositionInLine(),
//                        t1.getToken().getCharPositionInLine());
//            return Integer.compare(semanticError.getToken().getLine(), t1.getToken().getLine());
//        });

        errors.forEach(semanticError -> {
            error(semanticError.getCtx(), semanticError.getToken(), semanticError.getErrorMsg());
        });
    }
    /**
     * Displays a semantic error message.
     *
     * @param ctx Used to determine the enclosing class context of this error,
     *            which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void error(ParserRuleContext ctx, Token info, String str) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();

        String message = "\"" + new File(Compiler.fileNames.get(ctx)).getName()
                + "\", line " + info.getLine()
                + ":" + (info.getCharPositionInLine() + 1)
                + ", Semantic error: " + str;

        System.err.println(message);

        semanticErrors = true;
    }

    public static String getFileName(ParserRuleContext ctx) {
        return new File(Compiler.fileNames.get(ctx)).getName();
    }

    public static void error(String str) {
        String message = "Semantic error: " + str;

        System.err.println(message);

        semanticErrors = true;
    }

    public static boolean hasSemanticErrors() {
        return semanticErrors;
    }
}