package checker;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import ast.AST;


import tools.*;

import parser.CLexer;
import code.CodeGen;
import code.Interpreter;
import parser.CParser;

public class Main {
	public static void main(String[] args) throws Exception {

		// Cria um CharStream que lê os caracteres de um arquivo.
		CharStream input = CharStreams.fromFileName(args[0]);

		// create a lexer that feeds off of input CharStream
		CLexer lexer = new CLexer(input);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// create a parser that feeds off the tokens buffer
		CParser parser = new CParser(tokens);
		parser.setErrorHandler(new NewBailErrorStrategy());
		parser.addErrorListener(new ThrowingErrorListener());

		try {
			System.out.println("Iniciando teste");
			ParseTree tree = parser.compilationUnit(); // begin parsing at init rule
			SemanticChecker checker = new SemanticChecker();
            
			checker.visit(tree);

			AST.printDot(checker.getAST());
		
			// Interpreter inter = new Interpreter(checker.getVarTable(), checker.getFuncTable());
			// inter.visit(checker.getAST());
			// System.out.println("--------------------------------");
			// System.out.println("Mapeamento das variáveis na memória");
			// inter.printMap();
			// System.out.println("Revisão sintática realizada com êxito.");

            CodeGen codeGen = new CodeGen(checker.getVarTable(), checker.getFuncTable());
		    codeGen.execute(checker.getAST());
			//checker.printTables();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

