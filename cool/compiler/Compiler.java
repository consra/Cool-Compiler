package cool.compiler;

import cool.visitors.*;
import cool.structures.SymbolTable;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import cool.lexer.*;
import cool.parser.*;

import java.io.*;
public class Compiler {
    // Annotates class nodes with the names of files where they are defined.
    public static String glFileName = null;

    public static ParseTreeProperty<String> fileNames = new ParseTreeProperty<>();

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("No file(s) given");
            return;
        }
        
        CoolLexer lexer = null;
        CommonTokenStream tokenStream = null;
        CoolParser parser = null;
        ParserRuleContext globalTree = null;
        
        // True if any lexical or syntax errors occur.
        boolean lexicalSyntaxErrors = false;
        
        // Parse each input file and build one big parse tree out of
        // individual parse trees.
        for (var fileName : args) {
            var input = CharStreams.fromFileName(fileName);
            glFileName = fileName;
            // Lexer
            if (lexer == null)
                lexer = new CoolLexer(input);
            else
                lexer.setInputStream(input);

            // Token stream
            if (tokenStream == null)
                tokenStream = new CommonTokenStream(lexer);
            else
                tokenStream.setTokenSource(lexer);

            // Parser
            if (parser == null)
                parser = new CoolParser(tokenStream);
            else
                parser.setTokenStream(tokenStream);
            
            // Customized error listener, for including file names in error
            // messages.
            int got_error = 0;
            var errorListener = new BaseErrorListener() {
                public boolean errors = false;

                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg,
                                        RecognitionException e) {
                    String newMsg = "\"" + new File(fileName).getName() + "\", line " +
                                        line + ":" + (charPositionInLine) + ", ";

                    Token token = (Token)offendingSymbol;
                    if (token.getType() == CoolLexer.ERROR)
                        newMsg += "Lexical error: " + token.getText();
                    else
                        newMsg += "Syntax error: " + msg;

                    System.err.println(newMsg);
                    errors = true;
                }
            };

            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            // Actual parsing
            var tree = parser.program();
            if (globalTree == null)
                globalTree = tree;
            else
                // Add the current parse tree's children to the global tree.
                for (int i = 0; i < tree.getChildCount(); i++)
                    globalTree.addAnyChild(tree.getChild(i));

            //System.out.println(tree.toStringTree(parser));
            // Stop before semantic analysis phase, in case errors occurred.
            // Record any lexical or syntax errors.
            lexicalSyntaxErrors |= errorListener.errors;
            if (lexicalSyntaxErrors) {
                System.err.println("Compilation halted");
                return;
            }

            // TODO Print tree
            var astConstructionVisitor = new SintanticVisitor();
            var astPrintingVisitor = new ShowTreeVisitor();

            var ast = astConstructionVisitor.visit(tree);
            //ast.accept(astPrintingVisitor);

            // Annotate class nodes with file names, to be used later
            // in semantic error messages.
            for (int i = 0; i < tree.getChildCount(); i++) {
                var child = tree.getChild(i);
                // The only ParserRuleContext children of the program node
                // are class nodes.
                if (child instanceof ParserRuleContext)
                    fileNames.put(child, fileName);
            }

            // Populate global scope.
            SymbolTable.defineBasicClasses();

            // Semantic analysis
            var definitionVisitor = new DefinitionPassVisitor();
            ast.accept(definitionVisitor);

            var classHierarchyVisitor = new ClassHierarchyVisitor();
            ast.accept(classHierarchyVisitor);

            var resolutionVisitor = new ResolutionPassVisitor();
            ast.accept(resolutionVisitor);

            var dispatchOffsetCalcVisitor = new DispatchOffsetCalcVisitor();
            ast.accept(dispatchOffsetCalcVisitor);

            var codeGeneratorVisitor = new CodeGenVisitor();
            var t = ast.accept(codeGeneratorVisitor);

            System.out.println(t.render());

            DefinitionPassVisitor.classes.clear();
            ClassHierarchyVisitor.classHierarchy.clear();
            SymbolTable.globals = null;
        }
    }
}
