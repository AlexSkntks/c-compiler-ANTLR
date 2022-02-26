package ast;

import java.util.ArrayList;
import java.util.List;

import tables.VarTable;
// Implementação dos nós da AST.
public class AST {

	// Todos os campos são finais para simplificar, assim não precisa de getter/setter.
	// Note que não há union em Java, então aquele truque de ler
	// e/ou escrever o campo com formatos diferentes não funciona aqui.
	// Os campos 'data' NÃO ficam sincronizados!
	public  final NodeKind kind;
	public  final int intData;
    public  final char charData;
	public  final float floatData;

    public String text;

	private final List<AST> children; // Privado para que a manipulação da lista seja controlável.

	// private AST(){}

	// Construtor completo para poder tornar todos os campos finais.
	// Privado porque não queremos os dois campos 'data' preenchidos ao mesmo tempo.
	private AST(NodeKind kind, int intData, float floatData, char charData, String text) {
		this.kind = kind;
		this.intData = intData;
        this.charData = charData;
		this.floatData = floatData;
        this.text = text;
		this.children = new ArrayList<AST>();
	}

	// Cria o nó com um dado inteiro.
	public AST(NodeKind kind, int intData, String value) {
		this(kind, intData, 0.0f, ' ', value);
	}

	// Cria o nó com um dado float.
	public AST(NodeKind kind, float floatData, String value) {
		this(kind, 0, floatData, ' ', null);
	}

    // Cria o nó com um dado char.
	public AST(NodeKind kind, char charData, String value) {
		this(kind, 0, 0.0f, charData, null);
	}

    public AST(NodeKind kind){
		this(kind, 0, "");//Talvez precise trocar o 0
	}
	
	//Adiciona um campo texto de informações extras ao nó
	public void addInfo(String info){
		this.text = info;
	}

	// Adiciona um novo filho ao nó.
	public void addChild(AST child) {
		// A lista cresce automaticamente, então nunca vai dar erro ao adicionar.
		this.children.add(child);
	}

	// Retorna o filho no índice passado.
	// Não há nenhuma verificação de erros!
	public AST getChild(int idx) {
		// Claro que um código em produção precisa testar o índice antes para
		// evitar uma exceção.
	    return this.children.get(idx);
	}

	public String getText(){
		return this.text;
	}

	public NodeKind getNodeKind(){
		return this.kind;
	}
	
	// Cria um nó e pendura todos os filhos passados como argumento.
	public static AST newSubtree(NodeKind kind, AST... children) {
		AST node = new AST(kind, 0, "");
	    for (AST child: children) {
	    	node.addChild(child);
	    }
	    return node;
	}

	// Variáveis internas usadas para geração da saída em DOT.
	// Estáticas porque só precisamos de uma instância.
	private static int nr;
	// private static VarTable vt;

	// Imprime recursivamente a codificação em DOT da subárvore começando no nó atual.
	// Usa stderr como saída para facilitar o redirecionamento, mas isso é só um hack.
	private int printNodeDot() {
		int myNr = nr++;

	    System.err.printf("node%d[label=\"", myNr);

	    if (this.kind.toString() != "no_type") {
	    	System.err.printf("(%s) ", this.kind.toString());
	    }
	    
	    if (NodeKind.hasData(this.kind)) {
	        if (this.kind == NodeKind.FLOAT_VAL_NODE) {
	        	System.err.printf("%.2f", this.floatData);
	        } else if (this.kind == NodeKind.STR_VAL_NODE) {
	        	System.err.printf("@%d", this.intData);
	        } else if(this.kind == NodeKind.INT_VAL_NODE) {
	        	System.err.printf("%d", this.intData);
	        } else {
	        	System.err.printf("%c", this.charData);

            }
	    }

	    System.err.printf("\"];\n");

	    for (int i = 0; i < this.children.size(); i++) {
	        int childNr = this.children.get(i).printNodeDot();
	        System.err.printf("node%d -> node%d;\n", myNr, childNr);
	    }
	    return myNr;
	}

	// Imprime a árvore toda em stderr.
	public static void printDot(AST tree) {
	    nr = 0;
	    // vt = table;
	    System.err.printf("digraph {\ngraph [ordering=\"out\"];\n");
	    tree.printNodeDot();
	    System.err.printf("}\n");
	}
    
}
