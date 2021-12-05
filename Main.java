
// import ANTLR's runtime libraries
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

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

		try {
			ParseTree tree = parser.compilationUnit(); // begin parsing at init rule
			System.out.println(tree.toStringTree(parser)); // print LISP-style tree
			System.out.println("Tudo ok");
		} catch (Error e) {
			System.out.println("Deu ruim!");
		}
	
	}
}